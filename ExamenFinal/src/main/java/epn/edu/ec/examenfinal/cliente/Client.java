/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.examenfinal.cliente;

import epn.edu.ec.examenfinal.aplicacion.MainApplication;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author santi
 */
public class Client extends Thread {

    private final int port;
    private final String serverIP;
    private Socket clientSocket;
    private final String accion;
    private final String[] params;

    public Client(int port, String serverIP, String accion, String... params) {
        this.port = port;
        this.serverIP = serverIP;
        this.accion = accion;
        this.params = params;
    }

    @Override
    public void run() {
        try {
            clientSocket = new Socket(InetAddress.getByName(serverIP), port);
            DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
            dataOut.writeUTF(accion);//accion
            dataOut.writeUTF(params[0]);//nombre de usuario
            dataOut.writeUTF(getHash(params[1], "SHA1"));//contrasena

            switch (accion) {
                case "registrar":

                    break;
                case "autenticar":

                    break;
            }
            dataIn.close();
            dataOut.close();
        } catch (IOException e) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void conect(MainApplication mainApplication) {
        try {
            clientSocket = new Socket(InetAddress.getByName(serverIP), port);
            DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
            dataOut.writeUTF(accion);//accion
            dataOut.writeUTF(params[0]);//nombre de usuario
            dataOut.writeUTF(getHash(params[1], "SHA1"));//contrasena

            switch (accion) {
                case "registrar":
                    if (dataIn.readBoolean()) {
                        System.out.println("Usuario ya registrado\n");
                        mainApplication.registrarUsuario();
                        break;
                    }
                    System.out.println("Usuario registrado\n");
                    break;
                case "autenticar":
                    if (dataIn.readBoolean()) {
                        System.out.println("Usuario ya registrado\n");
                        mainApplication.iniciar();
                        break;
                    }
                    if (dataIn.readBoolean()) {
                        System.out.println("Contraseña incorrecta\n");
                        mainApplication.iniciar();
                        break;
                    }
                    System.out.println("Ingreso exitoso\n");
                    break;
            }
            dataIn.close();
            dataOut.close();
        } catch (IOException e) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static String getHash(String txt, String hashType) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance(hashType);
            byte[] array = md.digest(txt.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
