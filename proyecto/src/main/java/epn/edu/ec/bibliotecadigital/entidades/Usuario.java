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
@Table(name = "usuario")
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findByNombrecuenta", query = "SELECT u FROM Usuario u WHERE u.nombrecuenta = :nombrecuenta"),
    @NamedQuery(name = "Usuario.findByNombres", query = "SELECT u FROM Usuario u WHERE u.nombres = :nombres"),
    @NamedQuery(name = "Usuario.findByApellidos", query = "SELECT u FROM Usuario u WHERE u.apellidos = :apellidos"),
    @NamedQuery(name = "Usuario.findByClave", query = "SELECT u FROM Usuario u WHERE u.clave = :clave"),
    @NamedQuery(name = "Usuario.findByActivo", query = "SELECT u FROM Usuario u WHERE u.activo = :activo")})
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "nombrecuenta")
    private String nombrecuenta;
    @Basic(optional = false)
    @Column(name = "nombres")
    private String nombres;
    @Basic(optional = false)
    @Column(name = "apellidos")
    private String apellidos;
    @Basic(optional = false)
    @Column(name = "clave")
    private String clave;
    @Basic(optional = false)
    @Column(name = "activo")
    private boolean activo;
    @OneToMany(mappedBy = "nombrecuenta")
    private List<Usuariolibros> usuariolibrosList;

    public Usuario() {
    }

    public Usuario(String nombrecuenta) {
        this.nombrecuenta = nombrecuenta;
    }

    public Usuario(String nombrecuenta, String nombres, String apellidos, String clave, boolean activo) {
        this.nombrecuenta = nombrecuenta;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.clave = clave;
        this.activo = activo;
    }

    public String getNombrecuenta() {
        return nombrecuenta;
    }

    public void setNombrecuenta(String nombrecuenta) {
        this.nombrecuenta = nombrecuenta;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
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
        hash += (nombrecuenta != null ? nombrecuenta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.nombrecuenta == null && other.nombrecuenta != null) || (this.nombrecuenta != null && !this.nombrecuenta.equals(other.nombrecuenta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "epn.edu.ec.bibliotecadigital.entidades.Usuario[ nombrecuenta=" + nombrecuenta + " ]";
    }
    
}
