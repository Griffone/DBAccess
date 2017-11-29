/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.integration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

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
}
