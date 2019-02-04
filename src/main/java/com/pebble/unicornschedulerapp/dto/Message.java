package com.pebble.unicornschedulerapp.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class Message {

	@NotNull
	private final Long id;
	@NotNull
	private final Date timestamp;
	
	public Message(Long id, Date timestamp) {
		super();
		this.id = id;
		this.timestamp = timestamp;
	}
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	
}
