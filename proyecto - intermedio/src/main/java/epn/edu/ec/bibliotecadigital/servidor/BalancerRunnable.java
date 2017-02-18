/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital.servidor;

import epn.edu.ec.bibliotecadigital.DisponibilidadJpaController;
import epn.edu.ec.bibliotecadigital.entidades.Disponibilidad;
import epn.edu.ec.bibliotecadigital.entidades.Usuario;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Daniela Ramos
 */
public class BalancerRunnable implements Runnable {

    protected Socket clientSocket;
    protected boolean disponible;
    private Object[] infoServer = new Object[2];
    private final List<Object[]> servers = new ArrayList<>();
    private Disponibilidad disponibilidad;
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("epn.edu.ec_BibliotecaDigital_jar_1.0-SNAPSHOTPU");

    public BalancerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
        servers.add(new Object[]{"127.0.0.1", 8888});
        servers.add(new Object[]{"127.0.0.1", 9999});
    }

    @Override
    public void run() {
        try {
            DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
            String nombreUsuario = dataIn.readUTF();
            System.out.println("nombreUsuario" + nombreUsuario);
            disponibilidad = new Disponibilidad();
            disponibilidad.setNombrecuenta(new Usuario(nombreUsuario));
            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
            encontrarServidor();
            dataOut.writeUTF((String) infoServer[0]);
            dataOut.writeInt((int) infoServer[1]);
            disponibilidad.setRealizado(true);
            new DisponibilidadJpaController(emf).create(disponibilidad);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void encontrarServidor() {
        Socket clientSocketServer;
        String ipServidorUno = (String) servers.get(0)[0];
        int puertoServidorUno = (int) servers.get(0)[1];
        String ipServidorDos = (String) servers.get(1)[0];
        int puertoServidorDos = (int) servers.get(1)[1];

        try {
            clientSocketServer = new Socket(ipServidorUno, puertoServidorUno);
            infoServer[0] = "127.0.0.1";
            infoServer[1] = puertoServidorUno;
        } catch (IOException ex) {
            System.out.println("Servidor en dirección: " + ipServidorUno + ":" + puertoServidorUno + " no disponile");
            disponibilidad.setRealizado(false);
            new DisponibilidadJpaController(emf).create(disponibilidad);
//            Logger.getLogger(BalancerRunnable.class.getName()).log(Level.SEVERE, null, ex);
            try {
                clientSocketServer = new Socket(ipServidorDos, puertoServidorDos);
                infoServer[0] = ipServidorDos;
                infoServer[1] = puertoServidorDos;
            } catch (IOException ex1) {
                System.out.println("Servidor en dirección: " + ipServidorDos + ":" + puertoServidorDos + " no disponile");
                disponibilidad.setRealizado(false);
                new DisponibilidadJpaController(emf).create(disponibilidad);
                encontrarServidor();
//                Logger.getLogger(BalancerRunnable.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

}
