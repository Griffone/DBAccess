/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.integration;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import server.model.AccountModel;
import server.model.FileModel;

/**
 * A wrapper for all interactions between the database and the server
 * 
 * @author Griffone
 */
public class DataAccessObject {
    
    private static final String ENTITY_MANAGER_NAME = "fs_persistence_unit";
    private final EntityManagerFactory emf;
    private final ThreadLocal<EntityManager> local_em = new ThreadLocal();
    
    
    public DataAccessObject() {
        emf = Persistence.createEntityManagerFactory(ENTITY_MANAGER_NAME);
    }
    
    public void createAccount(AccountModel account) {
        try {
            EntityManager em = beginTransaction();
            em.persist(account);
        } finally {
            commitTransaction();
        }
    }
    
    public AccountModel findAccount(String username, boolean endTransaction) {
        if (username == null)
            return null;
        
        try {
            EntityManager em = beginTransaction();
            try {
                Query q = em.createNamedQuery("findAccountByName", AccountModel.class);
                q.setParameter("name", username);
                AccountModel result = (AccountModel) q.getSingleResult();
                return result;
            } catch (NoResultException ex) {
                return null;
            }
        } finally {
            if (endTransaction)
                commitTransaction();
        }
    }
    
    public boolean deleteAccount(String username) {
        if (username == null)
            return false;
        
        try {
            EntityManager em = beginTransaction();
            Query q = em.createNamedQuery("deleteAccountByName", AccountModel.class);
            q.setParameter("name", username);
            if (q.executeUpdate() > 0) {
                q = em.createNamedQuery("deleteFileByOwnerName", FileModel.class);
                q.setParameter("name", username);
                q.executeUpdate();
                return true;
            } else
                return false;
        } finally {
            commitTransaction();
        }
    }
    
    public void createFile(FileModel file) {
        EntityManager em = beginTransaction();
        em.persist(file);
        commitTransaction();
    }
    
    public FileModel findFile(String name, boolean endTransaction) {
        if (name == null)
            return null;
        
        try {
            EntityManager em = beginTransaction();
            try {
                Query q = em.createNamedQuery("findFileByName", FileModel.class);
                q.setParameter("name", name);
                return (FileModel) q.getSingleResult();
            } catch (NoResultException ex) {
                return null;
            }
        } finally {
            if (endTransaction)
                commitTransaction();
        }
    }
    
    public List<FileModel> getFiles() {
        try {
            EntityManager em = beginTransaction();
            Query q = em.createNamedQuery("getFiles", FileModel.class);
            return (List<FileModel>) q.getResultList();
        } finally {
            commitTransaction();
        }
    }
    
    public boolean deleteFile(String name) {
        if (name == null)
            return false;
        
        try {
            EntityManager em = beginTransaction();
            Query q = em.createNamedQuery("deleteFileByName", FileModel.class);
            q.setParameter("name", name);
            return q.executeUpdate() > 0;
        } finally {
            commitTransaction();
        }
    }
    
    public void updateEntity() {
        commitTransaction();
    }
    
    /**
     * Create a transaction for an entity manager specific to the calling thread
     * 
     * @return 
     */
    private EntityManager beginTransaction() {
        EntityManager em = emf.createEntityManager();
        local_em.set(em);
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive())
            transaction.begin();
        return em;
    }
    
    /**
     * Finish the transaction for an entity manager specific to the calling thread
     */
    private void commitTransaction() {
        local_em.get().getTransaction().commit();
    }
    
    private void rollbackTransaction() {
        local_em.get().getTransaction().rollback();
    }
}
