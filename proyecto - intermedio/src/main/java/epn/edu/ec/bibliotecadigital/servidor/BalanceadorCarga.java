/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital.servidor;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;

/**
 *
 * @author Daniela Ramos
 */
public class BalanceadorCarga extends Thread {

    private ServerSocket ser;
    private int port;

    private boolean disponible;

    public BalanceadorCarga(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            setDisponible(true);
            File carpetaCompartida = new File("C:\\Computacion Distribuida");
            carpetaCompartida.mkdir();
            for (File file : carpetaCompartida.listFiles()) {
                file.delete();
            }
            ser = new ServerSocket(port);
            while (true) {
                Socket clientSocket = ser.accept();
                new Thread(new BalancerRunnable(clientSocket, isDisponible())).start();
                if (disponible) {
                    setDisponible(false);
                } else {
                    setDisponible(true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

}
