package com.pebble.unicornschedulerapp.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.pebble.unicornschedulerapp.entities.Unicorn;

@Transactional
public abstract class CommonJPADAO<T extends Serializable> {

	private Class<T> clazz;

	@PersistenceContext
	EntityManager entityManager;

	public final void setClazz(Class<T> clazzToSet) {
		this.clazz = clazzToSet;
	}

	public T findOne(long id) {
		return entityManager.find(clazz, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return entityManager.createQuery("from " + clazz.getName()).getResultList();
	}
	

	public Unicorn findUnicornBytaskId(Long id){
	
		try {
			return (Unicorn) entityManager.createNamedQuery(Unicorn.findById).setParameter(Unicorn.p_task_id, id).getSingleResult();	
		}
		catch(Exception e) {
			return null;
		}		
	}

	public void create(T entity) {
		entityManager.persist(entity);
	}

	public T update(T entity) {
		return entityManager.merge(entity);
	}

	public void delete(T entity) {
		entityManager.remove(entity);
	}

	public void deleteById(long entityId) {
		T entity = findOne(entityId);
		delete(entity);
	}
	
}
