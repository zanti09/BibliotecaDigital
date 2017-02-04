/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital.servidor;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Daniela Ramos
 */
public class ServerRunnable implements Runnable {

    protected Socket clientSocket;
    protected Server server;

    public ServerRunnable(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        boolean error = false;
        try {

            DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());
            String accion = dataIn.readUTF();
            switch (accion) {
                case "enviar":
                    DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
                    File file = new File("C:\\Computacion Distribuida1\\index1.txt");
                    dataOut.writeUTF(file.getName());
                    OutputStream out = clientSocket.getOutputStream();
                    try {
                        byte[] bytes = new byte[64 * 1024];
                        InputStream in = new FileInputStream(file);

                        int count;
                        while ((count = in.read(bytes)) > 0) {
                            out.write(bytes, 0, count);
                        }
                        in.close();
                    } finally {
                        IOUtils.closeQuietly(out);
                    }
                    break;

            }
            dataIn.close();

        } catch (IOException e) {
            e.printStackTrace();
            error = true;
        } finally {
            try {
                server.finishDebitProcesor(error);
            } catch (Exception ex) {
                Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
