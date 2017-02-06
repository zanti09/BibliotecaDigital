/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital.cliente;

import epn.edu.ec.bibliotecadigital.entidades.Libro;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author santi
 */
public class Client extends Thread {

    private int portBalancer;
    private String serverIP;
    private Socket clientSocketBalancer;
    private String accion;
    private Object[] params;

    public Client(int port, String serverIP, String accion) {
        this.portBalancer = port;
        this.serverIP = serverIP;
        this.accion = accion;
    }

    public Client(int port, String serverIP, String accion, Object... params) {
        this.portBalancer = port;
        this.serverIP = serverIP;
        this.accion = accion;
        this.params = params;
    }

    @Override
    public void run() {
        try {

//            clientSocketBalancer = new Socket(InetAddress.getByName(serverIP), portBalancer);
//            DataInputStream dataInBalancer = new DataInputStream(clientSocketBalancer.getInputStream());
//            int portServer = Integer.parseInt(dataInBalancer.readUTF());
//            clientSocketBalancer.close();
            Socket clientSocket = new Socket(InetAddress.getByName(serverIP), portBalancer);
            DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
            dataOut.writeUTF(accion);
            dataOut.writeUTF((String) params[0]);
            InputStream in;
            DataInputStream dataIn;
            switch (accion) {
                case "bajar":
                    dataOut = new DataOutputStream(clientSocket.getOutputStream());
                    dataOut.writeUTF((String) params[1]);
                    dataIn = new DataInputStream(clientSocket.getInputStream());
                    boolean encontrado = dataIn.readBoolean();
                    if (!encontrado) {
                        System.out.println("Libro con el código: " + params[1] + " no encontrado");
                        break;
                    }
                    String fileName = dataIn.readUTF();
                    System.out.println("Descargando libro " + fileName + " con código " + params[1] + " en la carpeta Donwloads");
                    String home = System.getProperty("user.home");

                    in = clientSocket.getInputStream();
                    try {
                        FileOutputStream out = new FileOutputStream(new File(home + "\\Downloads\\" + fileName));
                        byte[] bytes = new byte[64 * 1024];

                        int count;
                        while ((count = in.read(bytes)) > 0) {
                            out.write(bytes, 0, count);
                        }
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        IOUtils.closeQuietly(in);
                    }
                    break;
                case "subir":
                    dataOut = new DataOutputStream(clientSocket.getOutputStream());
                    dataOut.writeUTF(((File) params[1]).getName());
                    OutputStream out = clientSocket.getOutputStream();
                    try {
                        byte[] bytes = new byte[64 * 1024];
                        in = new FileInputStream((File) params[1]);

                        int count;
                        while ((count = in.read(bytes)) > 0) {
                            out.write(bytes, 0, count);
                        }
                        in.close();
                    } finally {
                        IOUtils.closeQuietly(out);
                    }
                    break;
                case "obtenerLista":
                    ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());
                    System.out.println("Libros disponibles: \n");
                    List<Libro> libros=(List<Libro>) inFromClient.readObject();
                    System.out.println("\tCódigo\tNommbre\n");
                    for (Libro lbr : libros) {
                        System.out.println("\t"+lbr.getCodigolibro() + "\t" + lbr.getNombre());
                    }
                    inFromClient.close();
                    break;
                case "verificar":

                    break;
            }
            dataOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void subirArchivo() throws IOException {

    }

    public static void main(String[] args) {
        Client client = new Client(7777, "192.168.43.195", "enviar");
        client.start();
    }

}
