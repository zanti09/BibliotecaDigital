/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Daniela Ramos
 */
public class BalancerRunnable implements Runnable {

    protected Socket clientSocket;
    protected boolean disponible;
    private Object[] infoServer = new Object[2];

    public BalancerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
            dataOut.writeUTF("balanceador");
            verificarServidorDisponible();
            dataOut.writeUTF((String) infoServer[0]);
            dataOut.writeInt((int) infoServer[1]);
//            System.out.println(disponible);
//            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
//            
//            if (disponible) {
//                dataOut.writeUTF("8888");
//            } else {
//                dataOut.writeUTF("9999");
//            }
//            dataOut.close();
//            Socket clientSocketServer = new Socket(InetAddress.getByName("127.0.0.1"), 8888);
//            DataOutputStream dataOut = new DataOutputStream(clientSocketServer.getOutputStream());
//            dataOut.writeUTF("verificarEstado");
//            DataInputStream dataIn = new DataInputStream(clientSocketServer.getInputStream());  
//            boolean disponibleServerOne = Boolean.parseBoolean(dataIn.readUTF());
//            if (disponibleServerOne) {
//                dataOut = new DataOutputStream(clientSocket.getOutputStream());
//                dataOut.writeUTF(String.valueOf(disponibleServerOne));
//            } else {
//                clientSocketServer = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
//                dataIn = new DataInputStream(clientSocketServer.getInputStream());
//                disponibleServerOne = Boolean.parseBoolean(dataIn.readUTF());
//                if (disponibleServerOne) {
//                    dataOut = new DataOutputStream(clientSocket.getOutputStream());
//                    dataOut.writeUTF(String.valueOf(disponibleServerOne));
//                } else {
//
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verificarServidorDisponible() {
        try {
            Socket clientSocketServer = new Socket("127.0.0.1", 8888);
            DataInputStream dataIn = new DataInputStream(clientSocketServer.getInputStream());
            if (dataIn.readBoolean()) {
                clientSocketServer = new Socket("127.0.0.1", 9999);
                dataIn = new DataInputStream(clientSocketServer.getInputStream());
                if (dataIn.readBoolean()) {
                    Thread.sleep(5000);
                    verificarServidorDisponible();
                } else {
                    infoServer[0] = "127.0.0.1";
                    infoServer[1] = 9999;
                    dataIn.close();
                }
            } else {
                infoServer[0] = "127.0.0.1";
                infoServer[1] = 8888;
                dataIn.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(BalancerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(BalancerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
