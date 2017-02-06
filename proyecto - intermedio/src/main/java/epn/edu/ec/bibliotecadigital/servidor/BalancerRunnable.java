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

/**
 *
 * @author Daniela Ramos
 */
public class BalancerRunnable implements Runnable {

    protected Socket clientSocket;
    
    protected boolean disponible;

    public BalancerRunnable(Socket clientSocket, boolean disponible) {
        this.clientSocket = clientSocket;
        this.disponible = disponible;
    }

    @Override
    public void run() {
        try {
//            System.out.println(disponible);
//            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
//            
//            if (disponible) {
//                dataOut.writeUTF("8888");
//            } else {
//                dataOut.writeUTF("9999");
//            }
//            dataOut.close();

            Socket clientSocketServer = new Socket(InetAddress.getByName("127.0.0.1"), 8888);
            DataOutputStream dataOut = new DataOutputStream(clientSocketServer.getOutputStream());
            dataOut.writeUTF("verificarEstado");
            DataInputStream dataIn = new DataInputStream(clientSocketServer.getInputStream());  
            boolean disponibleServerOne = Boolean.parseBoolean(dataIn.readUTF());
            if (disponibleServerOne) {
                dataOut = new DataOutputStream(clientSocket.getOutputStream());
                dataOut.writeUTF(String.valueOf(disponibleServerOne));
            } else {
                clientSocketServer = new Socket(InetAddress.getByName("127.0.0.1"), 9999);
                dataIn = new DataInputStream(clientSocketServer.getInputStream());
                disponibleServerOne = Boolean.parseBoolean(dataIn.readUTF());
                if (disponibleServerOne) {
                    dataOut = new DataOutputStream(clientSocket.getOutputStream());
                    dataOut.writeUTF(String.valueOf(disponibleServerOne));
                } else {

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
