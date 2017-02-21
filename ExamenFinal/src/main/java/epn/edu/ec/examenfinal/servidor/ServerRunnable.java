/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.examenfinal.servidor;

import epn.edu.ec.examenfinal.controladores.DisponibilidadJpaController;
import epn.edu.ec.examenfinal.controladores.UsuarioJpaController;
import epn.edu.ec.examenfinal.entidades.Disponibilidad;
import epn.edu.ec.examenfinal.entidades.Usuario;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Daniela Ramos
 */
public class ServerRunnable implements Runnable {

    protected Socket clientSocket;
    EntityManagerFactory emf;
    private UsuarioJpaController usuarioController;
    private DisponibilidadJpaController disponibilidadController;

    public ServerRunnable(Socket clientSocket) {
        emf = Persistence.createEntityManagerFactory("epn.edu.ec_ExamenFinal_jar_1.0-SNAPSHOTPU");
        this.clientSocket = clientSocket;
        this.usuarioController = new UsuarioJpaController(emf);
        this.disponibilidadController = new DisponibilidadJpaController(emf);
    }

    @Override
    public void run() {
        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setFecha(Calendar.getInstance().getTime());
        try {
            Usuario usr;
            DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
            String accion = dataIn.readUTF();
            String nombreUsuario = dataIn.readUTF();
            String contrasena = dataIn.readUTF();

            disponibilidad.setNombrecuenta(new Usuario(nombreUsuario));
            switch (accion) {
                case "registrar":
                    usr = usuarioController.findUsuario(nombreUsuario);
                    if (usr != null) {
                        dataOut.writeBoolean(true);
                        break;
                    }
                    dataOut.writeBoolean(false);
                    usr = new Usuario();
                    usr.setContrasena(contrasena);
                    usr.setNombreusuario(nombreUsuario);
                    usuarioController.create(usr);
                    break;
                case "autenticar":
                    usr = usuarioController.findUsuario(nombreUsuario);
                    if (usr == null) {
                        dataOut.writeBoolean(true);
                        break;
                    }
                    dataOut.writeBoolean(false);
                    if (!usr.getContrasena().equals(contrasena)) {
                        dataOut.writeBoolean(true);
                        break;
                    }
                    dataOut.writeBoolean(false);
                    break;
            }

            disponibilidad.setConectado(true);

            dataIn.close();
            dataOut.close();
        } catch (IOException e) {
            Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, e);
            disponibilidad.setConectado(false);

        } catch (Exception ex) {
            Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
            disponibilidad.setConectado(false);
        }
        disponibilidadController.create(disponibilidad);
    }

}
