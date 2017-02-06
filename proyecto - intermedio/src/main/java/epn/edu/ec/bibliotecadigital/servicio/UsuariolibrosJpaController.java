/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital.servicio;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import epn.edu.ec.bibliotecadigital.entidades.Libro;
import epn.edu.ec.bibliotecadigital.entidades.Usuario;
import epn.edu.ec.bibliotecadigital.entidades.Usuariolibros;
import epn.edu.ec.bibliotecadigital.servicio.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author santi
 */
public class UsuariolibrosJpaController implements Serializable {

    public UsuariolibrosJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuariolibros usuariolibros) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Libro codigolibro = usuariolibros.getCodigolibro();
            if (codigolibro != null) {
                codigolibro = em.getReference(codigolibro.getClass(), codigolibro.getCodigolibro());
                usuariolibros.setCodigolibro(codigolibro);
            }
            Usuario nombrecuenta = usuariolibros.getNombrecuenta();
            if (nombrecuenta != null) {
                nombrecuenta = em.getReference(nombrecuenta.getClass(), nombrecuenta.getNombrecuenta());
                usuariolibros.setNombrecuenta(nombrecuenta);
            }
            em.persist(usuariolibros);
            if (codigolibro != null) {
                codigolibro.getUsuariolibrosList().add(usuariolibros);
                codigolibro = em.merge(codigolibro);
            }
            if (nombrecuenta != null) {
                nombrecuenta.getUsuariolibrosList().add(usuariolibros);
                nombrecuenta = em.merge(nombrecuenta);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuariolibros usuariolibros) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuariolibros persistentUsuariolibros = em.find(Usuariolibros.class, usuariolibros.getIdusuariolibros());
            Libro codigolibroOld = persistentUsuariolibros.getCodigolibro();
            Libro codigolibroNew = usuariolibros.getCodigolibro();
            Usuario nombrecuentaOld = persistentUsuariolibros.getNombrecuenta();
            Usuario nombrecuentaNew = usuariolibros.getNombrecuenta();
            if (codigolibroNew != null) {
                codigolibroNew = em.getReference(codigolibroNew.getClass(), codigolibroNew.getCodigolibro());
                usuariolibros.setCodigolibro(codigolibroNew);
            }
            if (nombrecuentaNew != null) {
                nombrecuentaNew = em.getReference(nombrecuentaNew.getClass(), nombrecuentaNew.getNombrecuenta());
                usuariolibros.setNombrecuenta(nombrecuentaNew);
            }
            usuariolibros = em.merge(usuariolibros);
            if (codigolibroOld != null && !codigolibroOld.equals(codigolibroNew)) {
                codigolibroOld.getUsuariolibrosList().remove(usuariolibros);
                codigolibroOld = em.merge(codigolibroOld);
            }
            if (codigolibroNew != null && !codigolibroNew.equals(codigolibroOld)) {
                codigolibroNew.getUsuariolibrosList().add(usuariolibros);
                codigolibroNew = em.merge(codigolibroNew);
            }
            if (nombrecuentaOld != null && !nombrecuentaOld.equals(nombrecuentaNew)) {
                nombrecuentaOld.getUsuariolibrosList().remove(usuariolibros);
                nombrecuentaOld = em.merge(nombrecuentaOld);
            }
            if (nombrecuentaNew != null && !nombrecuentaNew.equals(nombrecuentaOld)) {
                nombrecuentaNew.getUsuariolibrosList().add(usuariolibros);
                nombrecuentaNew = em.merge(nombrecuentaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuariolibros.getIdusuariolibros();
                if (findUsuariolibros(id) == null) {
                    throw new NonexistentEntityException("The usuariolibros with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuariolibros usuariolibros;
            try {
                usuariolibros = em.getReference(Usuariolibros.class, id);
                usuariolibros.getIdusuariolibros();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuariolibros with id " + id + " no longer exists.", enfe);
            }
            Libro codigolibro = usuariolibros.getCodigolibro();
            if (codigolibro != null) {
                codigolibro.getUsuariolibrosList().remove(usuariolibros);
                codigolibro = em.merge(codigolibro);
            }
            Usuario nombrecuenta = usuariolibros.getNombrecuenta();
            if (nombrecuenta != null) {
                nombrecuenta.getUsuariolibrosList().remove(usuariolibros);
                nombrecuenta = em.merge(nombrecuenta);
            }
            em.remove(usuariolibros);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuariolibros> findUsuariolibrosEntities() {
        return findUsuariolibrosEntities(true, -1, -1);
    }

    public List<Usuariolibros> findUsuariolibrosEntities(int maxResults, int firstResult) {
        return findUsuariolibrosEntities(false, maxResults, firstResult);
    }

    private List<Usuariolibros> findUsuariolibrosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuariolibros.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Usuariolibros findUsuariolibros(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuariolibros.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuariolibrosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuariolibros> rt = cq.from(Usuariolibros.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
