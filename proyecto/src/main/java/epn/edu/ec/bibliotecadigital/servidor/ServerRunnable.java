/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital.servidor;

import epn.edu.ec.bibliotecadigital.DisponibilidadJpaController;
import epn.edu.ec.bibliotecadigital.entidades.Disponibilidad;
import epn.edu.ec.bibliotecadigital.entidades.Libro;
import epn.edu.ec.bibliotecadigital.entidades.Usuario;
import epn.edu.ec.bibliotecadigital.entidades.Usuariolibros;
import epn.edu.ec.bibliotecadigital.servicio.LibroJpaController;
import epn.edu.ec.bibliotecadigital.servicio.UsuariolibrosJpaController;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Daniela Ramos
 */
public class ServerRunnable implements Runnable {

    protected Socket clientSocket;
    protected Server server;
    EntityManagerFactory emf;
    Disponibilidad disponibilidad;

    public ServerRunnable(Socket clientSocket, Server server) {
        emf = Persistence.createEntityManagerFactory("epn.edu.ec_BibliotecaDigital_jar_1.0-SNAPSHOTPU");
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        boolean error = false;
        try {

            DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
            String accion = dataIn.readUTF();
            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
            OutputStream out;
            Libro lbr;
            String nombreUsuario = dataIn.readUTF();
            disponibilidad=new Disponibilidad();
            disponibilidad.setNombrecuenta(new Usuario(nombreUsuario));
            disponibilidad.setRealizado(false);
            switch (accion) {
                case "bajar":

                    String codArchivo = dataIn.readUTF();
                    dataOut = new DataOutputStream(clientSocket.getOutputStream());

                    lbr = new LibroJpaController(emf).findLibro(Integer.parseInt(codArchivo));
                    if (lbr == null) {
                        dataOut.writeBoolean(false);
                        break;
                    }
                    dataOut.writeBoolean(true);

                    //File file = new File("C:\\Computacion Distribuida\\" + lbr.getNombre());
                    dataOut.writeUTF(lbr.getNombre());
                    out = clientSocket.getOutputStream();
                    try {
                        byte[] bytes = new byte[64 * 1024];
                        InputStream in = new ByteArrayInputStream(lbr.getArchivo());

                        int count;
                        while ((count = in.read(bytes)) > 0) {
                            out.write(bytes, 0, count);
                        }
                        Usuariolibros usrLbr = new Usuariolibros();
                        usrLbr.setFecha(Calendar.getInstance().getTime());
                        usrLbr.setAccion('B');
                        usrLbr.setCodigolibro(lbr);
                        usrLbr.setNombrecuenta(new Usuario(nombreUsuario));
                        new UsuariolibrosJpaController(emf).create(usrLbr);
                        in.close();
                        disponibilidad.setRealizado(true);
                    } finally {
                        IOUtils.closeQuietly(out);
                    }
                    break;
                case "subir":
                    dataIn = new DataInputStream(clientSocket.getInputStream());
                    String fileName = dataIn.readUTF();
                    InputStream in = clientSocket.getInputStream();
                    try {
                        out = new FileOutputStream("C:\\Computacion Distribuida\\" + fileName);
                        byte[] bytes = new byte[64 * 1024];

                        int count;
                        while ((count = in.read(bytes)) > 0) {
                            out.write(bytes, 0, count);
                        }
                        out.close();
                        lbr = new Libro();
                        lbr.setNombre(fileName);
                        lbr.setArchivo(IOUtils.toByteArray(new FileInputStream("C:\\Computacion Distribuida\\" + fileName)));

                        new LibroJpaController(emf).create(lbr);
                        Usuariolibros usrLbr = new Usuariolibros();
                        usrLbr.setFecha(Calendar.getInstance().getTime());
                        usrLbr.setAccion('S');
                        usrLbr.setCodigolibro(lbr);
                        usrLbr.setNombrecuenta(new Usuario(nombreUsuario));
                        new UsuariolibrosJpaController(emf).create(usrLbr);
                        disponibilidad.setRealizado(true);
                    } finally {
                        IOUtils.closeQuietly(in);
                    }
                    break;
                case "obtenerLista":
                    ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
                    outToServer.writeObject(new LibroJpaController(emf).findLibroEntities());
                    disponibilidad.setRealizado(true);
                    break;
                case "verificarEstado":
                    dataOut.writeUTF(String.valueOf(server.isDisponible()));
                    break;
            }
            dataIn.close();

        } catch (IOException e) {
            disponibilidad.setRealizado(false);            
            e.printStackTrace();
            error = true;
        } finally {
            new DisponibilidadJpaController(emf).create(disponibilidad);
            try {
                server.finishDebitProcesor(error);
            } catch (Exception ex) {
                Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
