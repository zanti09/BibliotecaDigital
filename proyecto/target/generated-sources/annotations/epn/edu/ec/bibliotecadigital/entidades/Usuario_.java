package epn.edu.ec.bibliotecadigital.entidades;

import epn.edu.ec.bibliotecadigital.entidades.Usuariolibros;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-02-04T10:42:05")
@StaticMetamodel(Usuario.class)
public class Usuario_ { 

    public static volatile SingularAttribute<Usuario, String> nombrecuenta;
    public static volatile SingularAttribute<Usuario, String> apellidos;
    public static volatile SingularAttribute<Usuario, String> clave;
    public static volatile SingularAttribute<Usuario, String> nombres;
    public static volatile SingularAttribute<Usuario, Boolean> activo;
    public static volatile ListAttribute<Usuario, Usuariolibros> usuariolibrosList;

}