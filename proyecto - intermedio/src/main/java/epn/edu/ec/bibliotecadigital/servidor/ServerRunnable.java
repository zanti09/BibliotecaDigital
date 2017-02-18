/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital.servidor;

import epn.edu.ec.bibliotecadigital.entidades.Libro;
import epn.edu.ec.bibliotecadigital.entidades.Usuario;
import epn.edu.ec.bibliotecadigital.entidades.Usuariolibros;
import epn.edu.ec.bibliotecadigital.servicio.LibroJpaController;
import epn.edu.ec.bibliotecadigital.servicio.UsuariolibrosJpaController;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;
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
    private boolean ocupado;

    public ServerRunnable(Socket clientSocket, Server server) {
        emf = Persistence.createEntityManagerFactory("epn.edu.ec_BibliotecaDigital_jar_1.0-SNAPSHOTPU");
        this.clientSocket = clientSocket;
        this.server = server;
    }

    public ServerRunnable(Socket clientSocket, Server server, boolean ocupado) {
        emf = Persistence.createEntityManagerFactory("epn.edu.ec_BibliotecaDigital_jar_1.0-SNAPSHOTPU");
        this.clientSocket = clientSocket;
        this.server = server;
        this.ocupado = ocupado;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
            OutputStream out;
            String accion = dataIn.readUTF();
            Libro lbr;
            String nombreUsuario = dataIn.readUTF();
            System.out.println("nombreUsuario" + nombreUsuario);
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
                        actualizarLibrosEnServidores(fileName);
                    } finally {
                        IOUtils.closeQuietly(in);
                    }
                    break;
                case "obtenerLista":
                    ObjectOutputStream outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
                    outToServer.writeObject(new LibroJpaController(emf).findLibroEntities());
                    outToServer.close();
                    break;
                case "verificarEstado":
                    dataOut.writeUTF(String.valueOf(server.isDisponible()));
                    break;
                case "actualizar":
                    dataIn = new DataInputStream(clientSocket.getInputStream());
                    String fileNameFromServer = dataIn.readUTF();
                    in = clientSocket.getInputStream();
                    try {
                        out = new FileOutputStream("C:\\Computacion Distribuida\\" + fileNameFromServer);
                        byte[] bytes = new byte[64 * 1024];

                        int count;
                        while ((count = in.read(bytes)) > 0) {
                            out.write(bytes, 0, count);
                            System.err.println(count);
                        }
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        IOUtils.closeQuietly(in);
                    }

            }
            dataIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void actualizarLibrosEnServidores(String fileName) {
        try {
            Socket socket = new Socket("192.168.100.14", 8888);
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            dataOut.writeUTF("actualizar");
            dataOut.writeUTF("servidor");
            dataOut.writeUTF(fileName);
            OutputStream out = socket.getOutputStream();
            try {
                byte[] bytes = new byte[64 * 1024];
                InputStream in = new FileInputStream("C:\\Computacion Distribuida\\" + fileName);

                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
                in.close();
            } finally {
                IOUtils.closeQuietly(out);
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
