package com.pebble.unicornschedulerapp.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.pebble.unicornschedulerapp.dto.TaskType;


@Entity
@NamedQueries({
	@NamedQuery(name = Unicorn.findById, query = "SELECT unicorn from Unicorn unicorn where unicorn.taskId = :p_task_id")})
public class Unicorn implements Serializable {
	
	public static final String findById = "Unicorn.findById";
	
	public static final String p_task_id = "p_task_id";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private long id;
	@Column(unique=true)
	private long taskId;
	private Date datetime;
	private TaskType type;
	private int rank;
	
	public Unicorn() {}
	
	public Unicorn(long id, Date datetime, TaskType type, int rank) {
		super();
		this.taskId = id;
		this.datetime = datetime;
		this.type = type;
		this.rank = rank;
	}

	/**
	 * @return the taskId
	 */
	public long getTaskId() {
		return taskId;
	}
	/**
	 * @param id2 the taskId to set
	 */
	public void setTaskId(long id2) {
		this.taskId = id2;
	}
	/**
	 * @return the datetime
	 */
	public Date getDatetime() {
		return datetime;
	}
	/**
	 * @param datetime the datetime to set
	 */
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}

	/**
	 * @return the type
	 */
	public TaskType getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(TaskType type) {
		this.type = type;
	}


	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}


	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Unicorn [id=" + id + ", taskId=" + taskId + ", datetime=" + datetime + ", type=" + type + ", rank="
				+ rank + "]";
	}
	
}
