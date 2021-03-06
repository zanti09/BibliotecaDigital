/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital;

import epn.edu.ec.bibliotecadigital.cliente.Client;
import epn.edu.ec.bibliotecadigital.entidades.Libro;
import epn.edu.ec.bibliotecadigital.entidades.Usuario;
import epn.edu.ec.bibliotecadigital.entidades.Usuariolibros;
import epn.edu.ec.bibliotecadigital.servicio.LibroJpaController;
import epn.edu.ec.bibliotecadigital.servicio.UsuarioJpaController;
import epn.edu.ec.bibliotecadigital.servicio.UsuariolibrosJpaController;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author santi
 */
public class UserApplication {

    private static final String INGRESE_NOMBRE_USUARIO = "Nombre de usuario: ";
    private static final String INGRESE_CLAVE = "Clave: ";
    private static final String INGRESE_NOMBRES = "Nombres: ";
    private static final String INGRESE_APELLIDOS = "Apellidos: ";
    private static final String INGRESE_RUTA_ARCHIVO = "Ubicaión del archivo: ";

    private final UsuarioJpaController controladorUsuario;
    private final LibroJpaController controladorLibro;
    private final UsuariolibrosJpaController controladorUsuariosLibro;

    private Usuario usr;
    private Libro lbr;
    private Usuariolibros usrLbr;

    private final Scanner entradaEscaner = new Scanner(System.in);

    public UserApplication() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("epn.edu.ec_BibliotecaDigital_jar_1.0-SNAPSHOTPU");
        emf.createEntityManager();
        controladorUsuario = new UsuarioJpaController(emf);
        controladorLibro = new LibroJpaController(emf);
        controladorUsuariosLibro = new UsuariolibrosJpaController(emf);
    }

    public static void main(String[] args) throws Exception {
        UserApplication ua = new UserApplication();
        ua.iniciar();
    }

    private void iniciar() {
        System.out.println("Seleccione una opción:\n"
                + "\t1 Ingresar al sistema\n"
                + "\t2 Registrarse\n"
                + "\t3 Salir\n");
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

    private void registrarUsuario() {
        System.out.print(INGRESE_NOMBRE_USUARIO);
        String nombreCuenta = entradaEscaner.nextLine();
        usr = controladorUsuario.findUsuario(nombreCuenta);
        if (usr != null) {
            System.err.println("\nUsuario ya existe, intente nuevamente\n");
            registrarUsuario();
            return;
        }
        System.out.print(INGRESE_NOMBRES);
        String nombres = entradaEscaner.nextLine();
        System.out.print(INGRESE_APELLIDOS);
        String apellidos = entradaEscaner.nextLine();
        System.out.print(INGRESE_CLAVE);
        String clave = entradaEscaner.nextLine();

        usr = new Usuario(nombreCuenta, nombres, apellidos, clave, true);
        try {
            controladorUsuario.create(usr);
        } catch (Exception ex) {
            Logger.getLogger(UserApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.print("\nRegistro guardado\n");
    }

    private void autenticar() {
        System.out.print(INGRESE_NOMBRE_USUARIO);
        String nombreCuenta = entradaEscaner.nextLine();
        usr = controladorUsuario.findUsuario(nombreCuenta);
        if (usr == null) {
            System.err.println("\nUsuario no existe\n");
            iniciar();
            return;
        }
        System.out.print(INGRESE_CLAVE);
        String clave = entradaEscaner.nextLine();
        if (!clave.equals(usr.getClave())) {
            System.err.println("\nClave incorrecta\n");
            autenticar();
            return;
        }
        System.out.println("\nIngreso  exitoso\n");
        mostrarMenuPrincipal();
    }

    private void mostrarMenuPrincipal() {
        System.out.println("Seleccione una opción:\n"
                + "\t1 Cargar Libro\n"
                + "\t2 Descargar Libro\n"
                + "\t3 Salir\n");
        String opcionIncial = entradaEscaner.nextLine();
        switch (opcionIncial) {
            case "1":
                subirLibro();
                break;
            case "2":
                mostrarMenuBajarLibro();
                break;
            case "3":
                System.out.println("Saliendo");
                iniciar();
                break;
            default:
                System.out.println("No es una opción correcta");
                mostrarMenuPrincipal();
                break;
        }
    }

    private void subirLibro() {
        System.out.print(INGRESE_RUTA_ARCHIVO);
        String path = entradaEscaner.nextLine();
        //File file=new File(path);
        String[] arrRutaArchivo = path.split("\\\\");
        String nombreArchivo = arrRutaArchivo[arrRutaArchivo.length - 1];
        File f = new File(path);
        if (!f.exists() || f.isDirectory()) {
            System.out.println("\nRuta incorrecta\n");
            subirLibro();
            return;
        }
        if (!controladorLibro.findLibroByNombre(nombreArchivo).isEmpty()) {
            System.out.println("\nLibro ya registrado\n");
            subirLibro();
            return;
        }

        Client client = new Client(7777, "192.168.100.14", "subir",  usr.getNombrecuenta(),f);
        client.start();
        mostrarMenuPrincipal();
    }
    
    private void mostrarMenuBajarLibro(){
        System.out.println("Seleccione una opción:\n"
                + "\t1 Mostrar lista de libros\n"
                + "\t2 Descargar Libro\n"
                + "\t3 Salir\n");
        String opcionIncial = entradaEscaner.nextLine();
        switch (opcionIncial) {
            case "1":
                obtenerListaLibros();
                break;
            case "2":
                bajarLibro();
                break;
            case "3":
                mostrarMenuPrincipal();
                break;
            default:
                System.out.println("No es una opción correcta");
                mostrarMenuBajarLibro();
                break;
        }
    }

    private void bajarLibro() {

        System.out.println("\nIngrese los códigos de los libros separados por una coma: ");
        String codigosLibros[] = entradaEscaner.nextLine().split(",");
        for (String cod : codigosLibros) {
            try {
                Integer.parseInt(cod);
            } catch (Exception e) {
                System.out.println("Código: " + cod + " no es numérico, ingrese solo números");
                continue;
            }
            Client client = new Client(7777, "192.168.100.14", "bajar", usr.getNombrecuenta(), cod);
            client.start();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UserApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        mostrarMenuPrincipal();

    }

    private void obtenerListaLibros() {
        Client client = new Client(7777, "192.168.100.14", "obtenerLista",usr.getNombrecuenta());
        client.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UserApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        mostrarMenuBajarLibro();
    }

}
