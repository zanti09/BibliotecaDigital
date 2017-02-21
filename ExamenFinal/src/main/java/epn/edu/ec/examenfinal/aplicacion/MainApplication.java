/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.examenfinal.aplicacion;

import epn.edu.ec.examenfinal.cliente.Client;
import java.util.Scanner;

/**
 *
 * @author santi
 */
public class MainApplication {
    private static final String INGRESE_NOMBRE_USUARIO = "Nombre de usuario: ";
    private static final String INGRESE_CLAVE = "Contraseña: ";
    private final Scanner entradaEscaner = new Scanner(System.in);
    
    public static void main(String[] args) {
        new MainApplication().iniciar();
    }
    
    public void iniciar() {
        System.out.println("Seleccione una opción:\n"
                + "\t1 Ingresar al sistema\n"
                + "\t2 Registrarse\n"
                + "\t3 Salir\n"
                + "Opción: ");
        String opcionIncial = entradaEscaner.nextLine();
        switch (opcionIncial) {
            case "1":
                autenticar();
                break;
            case "2":
                registrarUsuario();
                iniciar();
                break;
            case "3":
                System.out.println("Saliendo");
                break;
            default:
                System.out.println("No es una opción correcta");
                iniciar();
                break;
        }
        
    }
    
    public void autenticar() {
        System.out.print(INGRESE_NOMBRE_USUARIO);
        String nombreUsuario = entradaEscaner.nextLine();
        
        System.out.print(INGRESE_CLAVE);
        String contrasena = entradaEscaner.nextLine();
        
        Client client=new Client(8888, "192.168.100.83", "autenticar", nombreUsuario,contrasena);
        client.conect(this);
        
    }
    
    public void registrarUsuario() {
        System.out.print(INGRESE_NOMBRE_USUARIO);
        String nombreUsuario = entradaEscaner.nextLine();
        System.out.print(INGRESE_CLAVE);
        String contrasena = entradaEscaner.nextLine();
        Client client=new Client(8888, "192.168.100.83", "registrar", nombreUsuario,contrasena);
        client.conect(this);
    }

}
