package epn.edu.ec.bibliotecadigital.entidades;

import epn.edu.ec.bibliotecadigital.entidades.Usuariolibros;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-01-26T15:33:07")
@StaticMetamodel(Libro.class)
public class Libro_ { 

    public static volatile SingularAttribute<Libro, Integer> codigolibro;
    public static volatile SingularAttribute<Libro, String> nombre;
    public static volatile ListAttribute<Libro, Usuariolibros> usuariolibrosList;

}