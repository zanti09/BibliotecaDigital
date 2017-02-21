/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.examenfinal.controladores;

import epn.edu.ec.examenfinal.controladores.exceptions.NonexistentEntityException;
import epn.edu.ec.examenfinal.entidades.Disponibilidad;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import epn.edu.ec.examenfinal.entidades.Usuario;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author santi
 */
public class DisponibilidadJpaController implements Serializable {

    public DisponibilidadJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Disponibilidad disponibilidad) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario nombrecuenta = disponibilidad.getNombrecuenta();
            if (nombrecuenta != null) {
                nombrecuenta = em.getReference(nombrecuenta.getClass(), nombrecuenta.getNombreusuario());
                disponibilidad.setNombrecuenta(nombrecuenta);
            }
            em.persist(disponibilidad);
            if (nombrecuenta != null) {
                nombrecuenta.getDisponibilidadList().add(disponibilidad);
                nombrecuenta = em.merge(nombrecuenta);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Disponibilidad disponibilidad) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Disponibilidad persistentDisponibilidad = em.find(Disponibilidad.class, disponibilidad.getIddisponibilidad());
            Usuario nombrecuentaOld = persistentDisponibilidad.getNombrecuenta();
            Usuario nombrecuentaNew = disponibilidad.getNombrecuenta();
            if (nombrecuentaNew != null) {
                nombrecuentaNew = em.getReference(nombrecuentaNew.getClass(), nombrecuentaNew.getNombreusuario());
                disponibilidad.setNombrecuenta(nombrecuentaNew);
            }
            disponibilidad = em.merge(disponibilidad);
            if (nombrecuentaOld != null && !nombrecuentaOld.equals(nombrecuentaNew)) {
                nombrecuentaOld.getDisponibilidadList().remove(disponibilidad);
                nombrecuentaOld = em.merge(nombrecuentaOld);
            }
            if (nombrecuentaNew != null && !nombrecuentaNew.equals(nombrecuentaOld)) {
                nombrecuentaNew.getDisponibilidadList().add(disponibilidad);
                nombrecuentaNew = em.merge(nombrecuentaNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = disponibilidad.getIddisponibilidad();
                if (findDisponibilidad(id) == null) {
                    throw new NonexistentEntityException("The disponibilidad with id " + id + " no longer exists.");
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
            Disponibilidad disponibilidad;
            try {
                disponibilidad = em.getReference(Disponibilidad.class, id);
                disponibilidad.getIddisponibilidad();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The disponibilidad with id " + id + " no longer exists.", enfe);
            }
            Usuario nombrecuenta = disponibilidad.getNombrecuenta();
            if (nombrecuenta != null) {
                nombrecuenta.getDisponibilidadList().remove(disponibilidad);
                nombrecuenta = em.merge(nombrecuenta);
            }
            em.remove(disponibilidad);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Disponibilidad> findDisponibilidadEntities() {
        return findDisponibilidadEntities(true, -1, -1);
    }

    public List<Disponibilidad> findDisponibilidadEntities(int maxResults, int firstResult) {
        return findDisponibilidadEntities(false, maxResults, firstResult);
    }

    private List<Disponibilidad> findDisponibilidadEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Disponibilidad.class));
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

    public Disponibilidad findDisponibilidad(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Disponibilidad.class, id);
        } finally {
            em.close();
        }
    }

    public int getDisponibilidadCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Disponibilidad> rt = cq.from(Disponibilidad.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
