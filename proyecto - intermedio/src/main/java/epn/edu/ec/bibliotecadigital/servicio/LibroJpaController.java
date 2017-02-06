/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epn.edu.ec.bibliotecadigital.servicio;

import epn.edu.ec.bibliotecadigital.entidades.Libro;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import epn.edu.ec.bibliotecadigital.entidades.Usuariolibros;
import epn.edu.ec.bibliotecadigital.servicio.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * @author santi
 */
public class LibroJpaController implements Serializable {

    public LibroJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Libro libro) {
        if (libro.getUsuariolibrosList() == null) {
            libro.setUsuariolibrosList(new ArrayList<Usuariolibros>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Usuariolibros> attachedUsuariolibrosList = new ArrayList<Usuariolibros>();
            for (Usuariolibros usuariolibrosListUsuariolibrosToAttach : libro.getUsuariolibrosList()) {
                usuariolibrosListUsuariolibrosToAttach = em.getReference(usuariolibrosListUsuariolibrosToAttach.getClass(), usuariolibrosListUsuariolibrosToAttach.getIdusuariolibros());
                attachedUsuariolibrosList.add(usuariolibrosListUsuariolibrosToAttach);
            }
            libro.setUsuariolibrosList(attachedUsuariolibrosList);
            em.persist(libro);
            for (Usuariolibros usuariolibrosListUsuariolibros : libro.getUsuariolibrosList()) {
                Libro oldCodigolibroOfUsuariolibrosListUsuariolibros = usuariolibrosListUsuariolibros.getCodigolibro();
                usuariolibrosListUsuariolibros.setCodigolibro(libro);
                usuariolibrosListUsuariolibros = em.merge(usuariolibrosListUsuariolibros);
                if (oldCodigolibroOfUsuariolibrosListUsuariolibros != null) {
                    oldCodigolibroOfUsuariolibrosListUsuariolibros.getUsuariolibrosList().remove(usuariolibrosListUsuariolibros);
                    oldCodigolibroOfUsuariolibrosListUsuariolibros = em.merge(oldCodigolibroOfUsuariolibrosListUsuariolibros);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Libro libro) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Libro persistentLibro = em.find(Libro.class, libro.getCodigolibro());
            List<Usuariolibros> usuariolibrosListOld = persistentLibro.getUsuariolibrosList();
            List<Usuariolibros> usuariolibrosListNew = libro.getUsuariolibrosList();
            List<Usuariolibros> attachedUsuariolibrosListNew = new ArrayList<Usuariolibros>();
            for (Usuariolibros usuariolibrosListNewUsuariolibrosToAttach : usuariolibrosListNew) {
                usuariolibrosListNewUsuariolibrosToAttach = em.getReference(usuariolibrosListNewUsuariolibrosToAttach.getClass(), usuariolibrosListNewUsuariolibrosToAttach.getIdusuariolibros());
                attachedUsuariolibrosListNew.add(usuariolibrosListNewUsuariolibrosToAttach);
            }
            usuariolibrosListNew = attachedUsuariolibrosListNew;
            libro.setUsuariolibrosList(usuariolibrosListNew);
            libro = em.merge(libro);
            for (Usuariolibros usuariolibrosListOldUsuariolibros : usuariolibrosListOld) {
                if (!usuariolibrosListNew.contains(usuariolibrosListOldUsuariolibros)) {
                    usuariolibrosListOldUsuariolibros.setCodigolibro(null);
                    usuariolibrosListOldUsuariolibros = em.merge(usuariolibrosListOldUsuariolibros);
                }
            }
            for (Usuariolibros usuariolibrosListNewUsuariolibros : usuariolibrosListNew) {
                if (!usuariolibrosListOld.contains(usuariolibrosListNewUsuariolibros)) {
                    Libro oldCodigolibroOfUsuariolibrosListNewUsuariolibros = usuariolibrosListNewUsuariolibros.getCodigolibro();
                    usuariolibrosListNewUsuariolibros.setCodigolibro(libro);
                    usuariolibrosListNewUsuariolibros = em.merge(usuariolibrosListNewUsuariolibros);
                    if (oldCodigolibroOfUsuariolibrosListNewUsuariolibros != null && !oldCodigolibroOfUsuariolibrosListNewUsuariolibros.equals(libro)) {
                        oldCodigolibroOfUsuariolibrosListNewUsuariolibros.getUsuariolibrosList().remove(usuariolibrosListNewUsuariolibros);
                        oldCodigolibroOfUsuariolibrosListNewUsuariolibros = em.merge(oldCodigolibroOfUsuariolibrosListNewUsuariolibros);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = libro.getCodigolibro();
                if (findLibro(id) == null) {
                    throw new NonexistentEntityException("The libro with id " + id + " no longer exists.");
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
            Libro libro;
            try {
                libro = em.getReference(Libro.class, id);
                libro.getCodigolibro();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The libro with id " + id + " no longer exists.", enfe);
            }
            List<Usuariolibros> usuariolibrosList = libro.getUsuariolibrosList();
            for (Usuariolibros usuariolibrosListUsuariolibros : usuariolibrosList) {
                usuariolibrosListUsuariolibros.setCodigolibro(null);
                usuariolibrosListUsuariolibros = em.merge(usuariolibrosListUsuariolibros);
            }
            em.remove(libro);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Libro> findLibroEntities() {
        return findLibroEntities(true, -1, -1);
    }

    public List<Libro> findLibroEntities(int maxResults, int firstResult) {
        return findLibroEntities(false, maxResults, firstResult);
    }

    private List<Libro> findLibroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Libro.class));
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

    public Libro findLibro(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Libro.class, id);
        } finally {
            em.close();
        }
    }

    public int getLibroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Libro> rt = cq.from(Libro.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public List<Libro> findLibroByNombre(String nombre){
        EntityManager em=getEntityManager();
        TypedQuery<Libro> query = em.createNamedQuery("Libro.findByNombre", Libro.class);
        query.setParameter("nombre", nombre);
        return query.getResultList();
    }
}
