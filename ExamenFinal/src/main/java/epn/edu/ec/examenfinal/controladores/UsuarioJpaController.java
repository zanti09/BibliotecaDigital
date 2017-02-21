/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.examenfinal.controladores;

import epn.edu.ec.examenfinal.controladores.exceptions.NonexistentEntityException;
import epn.edu.ec.examenfinal.controladores.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import epn.edu.ec.examenfinal.entidades.Disponibilidad;
import epn.edu.ec.examenfinal.entidades.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author santi
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        if (usuario.getDisponibilidadList() == null) {
            usuario.setDisponibilidadList(new ArrayList<Disponibilidad>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Disponibilidad> attachedDisponibilidadList = new ArrayList<Disponibilidad>();
            for (Disponibilidad disponibilidadListDisponibilidadToAttach : usuario.getDisponibilidadList()) {
                disponibilidadListDisponibilidadToAttach = em.getReference(disponibilidadListDisponibilidadToAttach.getClass(), disponibilidadListDisponibilidadToAttach.getIddisponibilidad());
                attachedDisponibilidadList.add(disponibilidadListDisponibilidadToAttach);
            }
            usuario.setDisponibilidadList(attachedDisponibilidadList);
            em.persist(usuario);
            for (Disponibilidad disponibilidadListDisponibilidad : usuario.getDisponibilidadList()) {
                Usuario oldNombrecuentaOfDisponibilidadListDisponibilidad = disponibilidadListDisponibilidad.getNombrecuenta();
                disponibilidadListDisponibilidad.setNombrecuenta(usuario);
                disponibilidadListDisponibilidad = em.merge(disponibilidadListDisponibilidad);
                if (oldNombrecuentaOfDisponibilidadListDisponibilidad != null) {
                    oldNombrecuentaOfDisponibilidadListDisponibilidad.getDisponibilidadList().remove(disponibilidadListDisponibilidad);
                    oldNombrecuentaOfDisponibilidadListDisponibilidad = em.merge(oldNombrecuentaOfDisponibilidadListDisponibilidad);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuario(usuario.getNombreusuario()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getNombreusuario());
            List<Disponibilidad> disponibilidadListOld = persistentUsuario.getDisponibilidadList();
            List<Disponibilidad> disponibilidadListNew = usuario.getDisponibilidadList();
            List<Disponibilidad> attachedDisponibilidadListNew = new ArrayList<Disponibilidad>();
            for (Disponibilidad disponibilidadListNewDisponibilidadToAttach : disponibilidadListNew) {
                disponibilidadListNewDisponibilidadToAttach = em.getReference(disponibilidadListNewDisponibilidadToAttach.getClass(), disponibilidadListNewDisponibilidadToAttach.getIddisponibilidad());
                attachedDisponibilidadListNew.add(disponibilidadListNewDisponibilidadToAttach);
            }
            disponibilidadListNew = attachedDisponibilidadListNew;
            usuario.setDisponibilidadList(disponibilidadListNew);
            usuario = em.merge(usuario);
            for (Disponibilidad disponibilidadListOldDisponibilidad : disponibilidadListOld) {
                if (!disponibilidadListNew.contains(disponibilidadListOldDisponibilidad)) {
                    disponibilidadListOldDisponibilidad.setNombrecuenta(null);
                    disponibilidadListOldDisponibilidad = em.merge(disponibilidadListOldDisponibilidad);
                }
            }
            for (Disponibilidad disponibilidadListNewDisponibilidad : disponibilidadListNew) {
                if (!disponibilidadListOld.contains(disponibilidadListNewDisponibilidad)) {
                    Usuario oldNombrecuentaOfDisponibilidadListNewDisponibilidad = disponibilidadListNewDisponibilidad.getNombrecuenta();
                    disponibilidadListNewDisponibilidad.setNombrecuenta(usuario);
                    disponibilidadListNewDisponibilidad = em.merge(disponibilidadListNewDisponibilidad);
                    if (oldNombrecuentaOfDisponibilidadListNewDisponibilidad != null && !oldNombrecuentaOfDisponibilidadListNewDisponibilidad.equals(usuario)) {
                        oldNombrecuentaOfDisponibilidadListNewDisponibilidad.getDisponibilidadList().remove(disponibilidadListNewDisponibilidad);
                        oldNombrecuentaOfDisponibilidadListNewDisponibilidad = em.merge(oldNombrecuentaOfDisponibilidadListNewDisponibilidad);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = usuario.getNombreusuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getNombreusuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<Disponibilidad> disponibilidadList = usuario.getDisponibilidadList();
            for (Disponibilidad disponibilidadListDisponibilidad : disponibilidadList) {
                disponibilidadListDisponibilidad.setNombrecuenta(null);
                disponibilidadListDisponibilidad = em.merge(disponibilidadListDisponibilidad);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
