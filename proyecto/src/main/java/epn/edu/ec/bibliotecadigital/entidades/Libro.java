/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital.entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author santi
 */
@Entity
@Table(name = "libro")
@NamedQueries({
    @NamedQuery(name = "Libro.findAll", query = "SELECT l FROM Libro l"),
    @NamedQuery(name = "Libro.findByCodigolibro", query = "SELECT l FROM Libro l WHERE l.codigolibro = :codigolibro"),
    @NamedQuery(name = "Libro.findByNombre", query = "SELECT l FROM Libro l WHERE l.nombre = :nombre")})
public class Libro implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "codigolibro")
    private Integer codigolibro;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(mappedBy = "codigolibro")
    private List<Usuariolibros> usuariolibrosList;

    public Libro() {
    }

    public Libro(Integer codigolibro) {
        this.codigolibro = codigolibro;
    }

    public Libro(Integer codigolibro, String nombre) {
        this.codigolibro = codigolibro;
        this.nombre = nombre;
    }

    public Integer getCodigolibro() {
        return codigolibro;
    }

    public void setCodigolibro(Integer codigolibro) {
        this.codigolibro = codigolibro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Usuariolibros> getUsuariolibrosList() {
        return usuariolibrosList;
    }

    public void setUsuariolibrosList(List<Usuariolibros> usuariolibrosList) {
        this.usuariolibrosList = usuariolibrosList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigolibro != null ? codigolibro.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Libro)) {
            return false;
        }
        Libro other = (Libro) object;
        if ((this.codigolibro == null && other.codigolibro != null) || (this.codigolibro != null && !this.codigolibro.equals(other.codigolibro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "epn.edu.ec.bibliotecadigital.entidades.Libro[ codigolibro=" + codigolibro + " ]";
    }
    
}
