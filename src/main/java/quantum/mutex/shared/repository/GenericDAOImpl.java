/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.shared.repository;


import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;




/**
 *
 * @author florent
 * @param <E>
 * @param <ID>
 */
public abstract class GenericDAOImpl<E, ID> implements GenericDAO<E, ID>{
    
    @PersistenceContext
    protected EntityManager em;
    
    protected Class<E> entityClass;
    
    protected GenericDAOImpl(Class<E> entityClass){
        this.entityClass = entityClass;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
    
    public EntityManager getEntityManager(){
        return this.em;
    }
    
    @Override
    public Optional<E> makePersistent(E entity) {
        return Optional.of(em.merge(entity));
    }

    @Override
    public Optional<E> findById(ID id) {
       return  Optional.of(em.find(entityClass, id));
    }

    @Override
    public Optional<E> findReferenceById(ID id) {
       return  Optional.of(em.getReference(entityClass, id)); 
    }
    
    @Override
    public Optional<E> makeTransient(E entity) {
        em.remove(em.merge(entity));
        em.flush();
        return Optional.of(entity);
    }

    @Override
    public Long getCount(){
        CriteriaQuery<Long> c =
                em.getCriteriaBuilder().createQuery(Long.class);
        c.select(em.getCriteriaBuilder().count(c.from(entityClass)));
        return em.createQuery(c).getSingleResult();
    }

    @Override
    public List<E> findAll(){
        CriteriaQuery<E> c =
                em.getCriteriaBuilder().createQuery(entityClass);
        c.select(c.from(entityClass));
        return em.createQuery(c).getResultList();
    }

}
