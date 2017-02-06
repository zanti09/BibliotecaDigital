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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Daniela Ramos
 */
public class Server extends Thread {

    private ServerSocket ser;
    private int port;
    private int count = 0;
    private int errorCount = 0;
    private boolean monitor = false;
    private final int maxThreads =1;
    private boolean disponible=true;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            File carpetaCompartida = new File("C:\\Computacion Distribuida");
            carpetaCompartida.mkdir();
            ser = new ServerSocket(port);
            while (true) {
                //                Socket clientSocket = ser.accept();
                //                new Thread(new ServerRunnable(clientSocket, this)).start()
                proccesRequest();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void finishDebitProcesor(boolean pError)
            throws Exception {
        while (this.monitor) {
            this.wait();
        }
        this.monitor = true;
        try {
            this.count--;
            if (pError) {
                this.errorCount++;
            }

            System.out.println(
                    "Numero de Errores: " + this.errorCount
                    + " Pendientes: " + this.count);
        } finally {
            this.monitor = false;
            this.notifyAll();
        }
    }

    private synchronized void proccesRequest()
            throws Exception {
        while (this.monitor || (this.count > this.maxThreads)) {
            if (this.count > this.maxThreads) {
                System.out.println(
                        "Numero de Hilos Levantados para tranferencias en maximo "
                        + this.maxThreads);
            }
            this.wait();
        }
        this.monitor = true;
        try {

            Socket clientSocket = ser.accept();
            new Thread(new ServerRunnable(clientSocket, this)).start();
            this.count++;
            System.out.println(
                    "Numero de Hilos Levantados " + count);
        } finally {
            this.monitor = false;
            this.notifyAll();
        }
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public static void main(String[] args) {

        Server server = new Server(8888);
        server.start();

//        BalanceadorCarga balanceador = new BalanceadorCarga(7777);
//        balanceador.start();

////        Server2 server2 = new Server2(9999);
//        server2.start();
    }

}
