package com.pebble.unicornschedulerapp.dto;

import java.util.Date;

public class IntervalMessage {

	private Date timestamp;

	public IntervalMessage() {}
	public IntervalMessage(Date timestamp) {
		super();
		this.timestamp = timestamp;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
