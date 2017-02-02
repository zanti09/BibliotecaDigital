package epn.edu.ec.bibliotecadigital.entidades;

import epn.edu.ec.bibliotecadigital.entidades.Libro;
import epn.edu.ec.bibliotecadigital.entidades.Usuario;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-01-26T15:33:07")
@StaticMetamodel(Usuariolibros.class)
public class Usuariolibros_ { 

    public static volatile SingularAttribute<Usuariolibros, Character> accion;
    public static volatile SingularAttribute<Usuariolibros, Usuario> nombrecuenta;
    public static volatile SingularAttribute<Usuariolibros, Date> fecha;
    public static volatile SingularAttribute<Usuariolibros, Integer> idusuariolibros;
    public static volatile SingularAttribute<Usuariolibros, Libro> codigolibro;

}