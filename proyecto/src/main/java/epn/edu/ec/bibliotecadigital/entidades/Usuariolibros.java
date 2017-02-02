/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital.entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author santi
 */
@Entity
@Table(name = "usuariolibros")
@NamedQueries({
    @NamedQuery(name = "Usuariolibros.findAll", query = "SELECT u FROM Usuariolibros u"),
    @NamedQuery(name = "Usuariolibros.findByIdusuariolibros", query = "SELECT u FROM Usuariolibros u WHERE u.idusuariolibros = :idusuariolibros"),
    @NamedQuery(name = "Usuariolibros.findByFecha", query = "SELECT u FROM Usuariolibros u WHERE u.fecha = :fecha"),
    @NamedQuery(name = "Usuariolibros.findByAccion", query = "SELECT u FROM Usuariolibros u WHERE u.accion = :accion")})
public class Usuariolibros implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idusuariolibros")
    private Integer idusuariolibros;
    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Basic(optional = false)
    @Column(name = "accion")
    private Character accion;
    @JoinColumn(name = "codigolibro", referencedColumnName = "codigolibro")
    @ManyToOne
    private Libro codigolibro;
    @JoinColumn(name = "nombrecuenta", referencedColumnName = "nombrecuenta")
    @ManyToOne
    private Usuario nombrecuenta;

    public Usuariolibros() {
    }

    public Usuariolibros(Integer idusuariolibros) {
        this.idusuariolibros = idusuariolibros;
    }

    public Usuariolibros(Integer idusuariolibros, Date fecha, Character accion) {
        this.idusuariolibros = idusuariolibros;
        this.fecha = fecha;
        this.accion = accion;
    }

    public Integer getIdusuariolibros() {
        return idusuariolibros;
    }

    public void setIdusuariolibros(Integer idusuariolibros) {
        this.idusuariolibros = idusuariolibros;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Character getAccion() {
        return accion;
    }

    public void setAccion(Character accion) {
        this.accion = accion;
    }

    public Libro getCodigolibro() {
        return codigolibro;
    }

    public void setCodigolibro(Libro codigolibro) {
        this.codigolibro = codigolibro;
    }

    public Usuario getNombrecuenta() {
        return nombrecuenta;
    }

    public void setNombrecuenta(Usuario nombrecuenta) {
        this.nombrecuenta = nombrecuenta;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idusuariolibros != null ? idusuariolibros.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuariolibros)) {
            return false;
        }
        Usuariolibros other = (Usuariolibros) object;
        if ((this.idusuariolibros == null && other.idusuariolibros != null) || (this.idusuariolibros != null && !this.idusuariolibros.equals(other.idusuariolibros))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "epn.edu.ec.bibliotecadigital.entidades.Usuariolibros[ idusuariolibros=" + idusuariolibros + " ]";
    }
    
}
