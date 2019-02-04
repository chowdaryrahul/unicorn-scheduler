package com.pebble.unicornschedulerapp.dto;

public class IndexDTO {
	private Long index;

	public IndexDTO(Long index) {
		super();
		this.index = index;
	}

	/**
	 * @return the index
	 */
	public Long getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(Long index) {
		this.index = index;
	}
	
}
