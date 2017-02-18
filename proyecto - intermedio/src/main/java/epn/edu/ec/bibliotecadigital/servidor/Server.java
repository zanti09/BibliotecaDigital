/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital.servidor;

import epn.edu.ec.bibliotecadigital.entidades.Libro;
import epn.edu.ec.bibliotecadigital.servicio.LibroJpaController;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Daniela Ramos
 */
public class Server extends Thread {

    private ServerSocket ser;
    private int port;
    public int count = 0;
    private int errorCount = 0;
    private boolean monitor = false;
    private final int maxThreads = 1;
    private boolean disponible = true;
    public boolean ocupado = false;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("epn.edu.ec_BibliotecaDigital_jar_1.0-SNAPSHOTPU");
            File carpetaLibros = new File("C:\\Computacion Distribuida");
            carpetaLibros.mkdir();
            List<Libro> lstLibros = new LibroJpaController(emf).findLibroEntities();
            byte[] bytes = new byte[64 * 1024];
            for (Libro lbr : lstLibros) {
                InputStream in = new ByteArrayInputStream(lbr.getArchivo());
                FileOutputStream out = new FileOutputStream(new File("C:\\Computacion Distribuida" + lbr.getNombre()));
                int contador;
                while ((contador = in.read(bytes)) > 0) {
                    out.write(bytes, 0, contador);
                }
                in.close();
                out.close();
            }
            ser = new ServerSocket(port);
            while (true) {
                Socket clientSocket = ser.accept();
                new Thread(new ServerRunnable(clientSocket, this)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void process() {
        try {
            Socket clientSocket = ser.accept();
            new Thread(new ServerRunnable(clientSocket, this)).start();
            count++;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void finishProcesor(boolean pError)
            throws Exception {
        System.out.println("count: " + count);
//        while (this.monitor) {
//            this.wait();
//        }
//        this.monitor = true;
        try {
            this.count--;
            System.out.println("count: " + count);
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
//        while (this.monitor || (this.count > this.maxThreads)) {

        Socket clientSocket = ser.accept();
        System.out.println("count: " + count);
        if (this.count >= this.maxThreads) {
            System.out.println(
                    "Numero de Hilos Levantados para tranferencias en maximo "
                    + this.maxThreads);
            ocupado = true;
            new Thread(new ServerRunnable(clientSocket, this, ocupado)).start();
            this.count++;
            return;
        }
//            this.wait();
//        }
        this.monitor = true;
        try {
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
