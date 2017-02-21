/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.examenfinal.entidades;

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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author santi
 */
@Entity
@Table(name = "disponibilidad")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Disponibilidad.findAll", query = "SELECT d FROM Disponibilidad d"),
    @NamedQuery(name = "Disponibilidad.findByIddisponibilidad", query = "SELECT d FROM Disponibilidad d WHERE d.iddisponibilidad = :iddisponibilidad"),
    @NamedQuery(name = "Disponibilidad.findByConectado", query = "SELECT d FROM Disponibilidad d WHERE d.conectado = :conectado"),
    @NamedQuery(name = "Disponibilidad.findByFecha", query = "SELECT d FROM Disponibilidad d WHERE d.fecha = :fecha")})
public class Disponibilidad implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "iddisponibilidad")
    private Integer iddisponibilidad;
    @Basic(optional = false)
    @Column(name = "conectado")
    private boolean conectado;
    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @JoinColumn(name = "nombrecuenta", referencedColumnName = "nombreusuario")
    @ManyToOne
    private Usuario nombrecuenta;

    public Disponibilidad() {
    }

    public Disponibilidad(Integer iddisponibilidad) {
        this.iddisponibilidad = iddisponibilidad;
    }

    public Disponibilidad(Integer iddisponibilidad, boolean conectado, Date fecha) {
        this.iddisponibilidad = iddisponibilidad;
        this.conectado = conectado;
        this.fecha = fecha;
    }

    public Integer getIddisponibilidad() {
        return iddisponibilidad;
    }

    public void setIddisponibilidad(Integer iddisponibilidad) {
        this.iddisponibilidad = iddisponibilidad;
    }

    public boolean getConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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
        hash += (iddisponibilidad != null ? iddisponibilidad.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Disponibilidad)) {
            return false;
        }
        Disponibilidad other = (Disponibilidad) object;
        if ((this.iddisponibilidad == null && other.iddisponibilidad != null) || (this.iddisponibilidad != null && !this.iddisponibilidad.equals(other.iddisponibilidad))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "epn.edu.ec.examenfinal.entidades.Disponibilidad[ iddisponibilidad=" + iddisponibilidad + " ]";
    }
    
}
