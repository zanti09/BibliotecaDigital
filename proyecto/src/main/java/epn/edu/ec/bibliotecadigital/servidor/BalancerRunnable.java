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
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Daniela Ramos
 */
public class BalancerRunnable implements Runnable{
    protected Socket clientSocket;
   
   protected boolean disponible;
   

    public BalancerRunnable(Socket clientSocket, boolean disponible) {
        this.clientSocket = clientSocket;
        this.disponible = disponible;
    }

    @Override
    public void run() {
        try {
                System.out.println(disponible);
            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
             
            if (disponible) {
                dataOut.writeUTF("8888");
            }else{
               
                dataOut.writeUTF("9999");
            }
            dataOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    
}
