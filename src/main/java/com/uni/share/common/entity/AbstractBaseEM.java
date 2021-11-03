package com.uni.share.common.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.uni.share.common.types.AbstractBaseBE;

/**
 * Abstract base entity manager
 *
 * @author Felix Rottler
 */
public abstract class AbstractBaseEM<T extends AbstractBaseBE> {


    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Empty constructor
     */
    public AbstractBaseEM() {

    }


    protected abstract Class<T> getEntityClass();


    /**
     * Finds a entity of the given type by its id.
     *
     * @param id the id of the entity.
     * @return an optional of the entity.
     */
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(entityManager.find(getEntityClass(), id));
    }


    /**
     * Find a list of entities by their id's
     *
     * @param ids the id's of the entities
     * @return a collection of entites
     */
    public List<T> findByIds(Collection<Long> ids) {
        //TODO
        return Collections.emptyList();
    }


    /**
     * Persist a given entity
     *
     * @param entity the entity to persist
     * @return the persisted entity
     */
    public T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }


    /**
     * Update/Merge a given entity
     *
     * @param entity the entity to merge
     * @return the merged entity
     */
    public T merge(T entity) {
        entityManager.merge(entity);
        entityManager.flush();
        return entity;


    }


    /**
     * Deletes a entity by its id
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        T entity = entityManager.find(getEntityClass(), id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }


    /**
     * Returns the entity manager.
     *
     * @return the entity manager.
     */
    protected EntityManager getEntityManager() {
        return entityManager;
    }


    protected JPAQuery<T> query() {
        return new JPAQuery<>(this.getEntityManager());
    }


    protected JPAQueryFactory queryFactory() {
        return new JPAQueryFactory(this.getEntityManager());
    }

}
