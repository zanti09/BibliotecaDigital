/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import epn.edu.ec.bibliotecadigital.entidades.Usuariolibros;
import java.util.ArrayList;
import java.util.List;
import epn.edu.ec.bibliotecadigital.entidades.Disponibilidad;
import epn.edu.ec.bibliotecadigital.entidades.Usuario;
import epn.edu.ec.bibliotecadigital.exceptions.NonexistentEntityException;
import epn.edu.ec.bibliotecadigital.exceptions.PreexistingEntityException;
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
        if (usuario.getUsuariolibrosList() == null) {
            usuario.setUsuariolibrosList(new ArrayList<Usuariolibros>());
        }
        if (usuario.getDisponibilidadList() == null) {
            usuario.setDisponibilidadList(new ArrayList<Disponibilidad>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Usuariolibros> attachedUsuariolibrosList = new ArrayList<Usuariolibros>();
            for (Usuariolibros usuariolibrosListUsuariolibrosToAttach : usuario.getUsuariolibrosList()) {
                usuariolibrosListUsuariolibrosToAttach = em.getReference(usuariolibrosListUsuariolibrosToAttach.getClass(), usuariolibrosListUsuariolibrosToAttach.getIdusuariolibros());
                attachedUsuariolibrosList.add(usuariolibrosListUsuariolibrosToAttach);
            }
            usuario.setUsuariolibrosList(attachedUsuariolibrosList);
            List<Disponibilidad> attachedDisponibilidadList = new ArrayList<Disponibilidad>();
            for (Disponibilidad disponibilidadListDisponibilidadToAttach : usuario.getDisponibilidadList()) {
                disponibilidadListDisponibilidadToAttach = em.getReference(disponibilidadListDisponibilidadToAttach.getClass(), disponibilidadListDisponibilidadToAttach.getIddisponibilidad());
                attachedDisponibilidadList.add(disponibilidadListDisponibilidadToAttach);
            }
            usuario.setDisponibilidadList(attachedDisponibilidadList);
            em.persist(usuario);
            for (Usuariolibros usuariolibrosListUsuariolibros : usuario.getUsuariolibrosList()) {
                Usuario oldNombrecuentaOfUsuariolibrosListUsuariolibros = usuariolibrosListUsuariolibros.getNombrecuenta();
                usuariolibrosListUsuariolibros.setNombrecuenta(usuario);
                usuariolibrosListUsuariolibros = em.merge(usuariolibrosListUsuariolibros);
                if (oldNombrecuentaOfUsuariolibrosListUsuariolibros != null) {
                    oldNombrecuentaOfUsuariolibrosListUsuariolibros.getUsuariolibrosList().remove(usuariolibrosListUsuariolibros);
                    oldNombrecuentaOfUsuariolibrosListUsuariolibros = em.merge(oldNombrecuentaOfUsuariolibrosListUsuariolibros);
                }
            }
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
            if (findUsuario(usuario.getNombrecuenta()) != null) {
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
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getNombrecuenta());
            List<Usuariolibros> usuariolibrosListOld = persistentUsuario.getUsuariolibrosList();
            List<Usuariolibros> usuariolibrosListNew = usuario.getUsuariolibrosList();
            List<Disponibilidad> disponibilidadListOld = persistentUsuario.getDisponibilidadList();
            List<Disponibilidad> disponibilidadListNew = usuario.getDisponibilidadList();
            List<Usuariolibros> attachedUsuariolibrosListNew = new ArrayList<Usuariolibros>();
            for (Usuariolibros usuariolibrosListNewUsuariolibrosToAttach : usuariolibrosListNew) {
                usuariolibrosListNewUsuariolibrosToAttach = em.getReference(usuariolibrosListNewUsuariolibrosToAttach.getClass(), usuariolibrosListNewUsuariolibrosToAttach.getIdusuariolibros());
                attachedUsuariolibrosListNew.add(usuariolibrosListNewUsuariolibrosToAttach);
            }
            usuariolibrosListNew = attachedUsuariolibrosListNew;
            usuario.setUsuariolibrosList(usuariolibrosListNew);
            List<Disponibilidad> attachedDisponibilidadListNew = new ArrayList<Disponibilidad>();
            for (Disponibilidad disponibilidadListNewDisponibilidadToAttach : disponibilidadListNew) {
                disponibilidadListNewDisponibilidadToAttach = em.getReference(disponibilidadListNewDisponibilidadToAttach.getClass(), disponibilidadListNewDisponibilidadToAttach.getIddisponibilidad());
                attachedDisponibilidadListNew.add(disponibilidadListNewDisponibilidadToAttach);
            }
            disponibilidadListNew = attachedDisponibilidadListNew;
            usuario.setDisponibilidadList(disponibilidadListNew);
            usuario = em.merge(usuario);
            for (Usuariolibros usuariolibrosListOldUsuariolibros : usuariolibrosListOld) {
                if (!usuariolibrosListNew.contains(usuariolibrosListOldUsuariolibros)) {
                    usuariolibrosListOldUsuariolibros.setNombrecuenta(null);
                    usuariolibrosListOldUsuariolibros = em.merge(usuariolibrosListOldUsuariolibros);
                }
            }
            for (Usuariolibros usuariolibrosListNewUsuariolibros : usuariolibrosListNew) {
                if (!usuariolibrosListOld.contains(usuariolibrosListNewUsuariolibros)) {
                    Usuario oldNombrecuentaOfUsuariolibrosListNewUsuariolibros = usuariolibrosListNewUsuariolibros.getNombrecuenta();
                    usuariolibrosListNewUsuariolibros.setNombrecuenta(usuario);
                    usuariolibrosListNewUsuariolibros = em.merge(usuariolibrosListNewUsuariolibros);
                    if (oldNombrecuentaOfUsuariolibrosListNewUsuariolibros != null && !oldNombrecuentaOfUsuariolibrosListNewUsuariolibros.equals(usuario)) {
                        oldNombrecuentaOfUsuariolibrosListNewUsuariolibros.getUsuariolibrosList().remove(usuariolibrosListNewUsuariolibros);
                        oldNombrecuentaOfUsuariolibrosListNewUsuariolibros = em.merge(oldNombrecuentaOfUsuariolibrosListNewUsuariolibros);
                    }
                }
            }
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
                String id = usuario.getNombrecuenta();
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
                usuario.getNombrecuenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<Usuariolibros> usuariolibrosList = usuario.getUsuariolibrosList();
            for (Usuariolibros usuariolibrosListUsuariolibros : usuariolibrosList) {
                usuariolibrosListUsuariolibros.setNombrecuenta(null);
                usuariolibrosListUsuariolibros = em.merge(usuariolibrosListUsuariolibros);
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
