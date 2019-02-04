package com.pebble.unicornschedulerapp.dao;

import org.springframework.stereotype.Repository;

import com.pebble.unicornschedulerapp.entities.Unicorn;

@Repository
public class OrderDao extends CommonJPADAO<Unicorn>{

	public OrderDao() {
		setClazz(Unicorn.class);
	}
	
	
	
}
