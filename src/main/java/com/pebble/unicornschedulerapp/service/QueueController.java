package com.pebble.unicornschedulerapp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pebble.unicornschedulerapp.commons.FetchType;
import com.pebble.unicornschedulerapp.commons.TaskType;
import com.pebble.unicornschedulerapp.dao.OrderDao;
import com.pebble.unicornschedulerapp.dto.IndexDTO;
import com.pebble.unicornschedulerapp.dto.IntervalMessage;
import com.pebble.unicornschedulerapp.dto.Message;
import com.pebble.unicornschedulerapp.dto.ResMessage;
import com.pebble.unicornschedulerapp.entities.Unicorn;

@RestController
public class QueueController {

	private static final Long maxx = 9223372036854775807L;

	@Autowired
	OrderDao orderDao;
	
	@Autowired
	UnicornPriortizer unicornPriortizer;

	@PostMapping(path = "/order", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResMessage receiveOrder(@Valid @RequestBody Message message) {

		
		long id = message.getId();
		Date messageTime = message.getTimestamp();

		Unicorn unicorn = orderDao.findUnicornBytaskId(id);
		
		if ((id < 1 || id > maxx) || unicorn == null )

		if ((id % 3 == 0) && (id % 5 == 0)) {

			unicorn = new Unicorn(id, messageTime, TaskType.MGMT, 1);

			unicorn = orderDao.update(unicorn);

			return new ResMessage(unicorn.getTaskId(), "Pass");
		} else if (id % 5 == 0) {
			unicorn = new Unicorn(id, messageTime, TaskType.VIP, 2);

			unicorn = orderDao.update(unicorn);

			return new ResMessage(unicorn.getTaskId(), "Pass");
		}

		else if (id % 3 == 0) {
			unicorn = new Unicorn(id, messageTime, TaskType.PRIOR, 3);

			unicorn = orderDao.update(unicorn);

			return new ResMessage(unicorn.getTaskId(), "Pass");
		} else {
			unicorn = new Unicorn(id, messageTime, TaskType.NORMAL, 4);

			unicorn = orderDao.update(unicorn);

			return new ResMessage(unicorn.getTaskId(), "Pass");
		}
		else
			return new ResMessage(0, "Already exists/ID NOT VALID");
	}

	@GetMapping(path = "/retrieve", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Message getPriorityTask() {

		List<Unicorn> unicornList = orderDao.findAll();
		
		List<Unicorn> priortizedList = new ArrayList<Unicorn>();
		
		Unicorn topTask;

		if (!unicornList.isEmpty())
			priortizedList = unicornPriortizer.priortizeQueue(unicornList, FetchType.PRIORITY);
		
		if (!priortizedList.isEmpty()) {
			topTask = priortizedList.get(0);

			orderDao.delete(topTask);			//Dequeue
			
			return new Message(topTask.getTaskId(), topTask.getDatetime());
		}

		return new Message(null, null);

	}

	@GetMapping(path = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResMessage getOrder(@PathVariable(value = "id", required = true) Long id) {

		Unicorn unicorn = orderDao.findUnicornBytaskId(id);

		if (unicorn != null)
			return new ResMessage(unicorn.getTaskId(), "Found");
		else
			return new ResMessage(0, "Not Found");

	}

	@GetMapping(path = "/retrieve/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Message> getAllOrders() {

		List<Unicorn> unicornList = orderDao.findAll();
		List<Unicorn> priortizedList = new ArrayList<Unicorn>();		

		if (!unicornList.isEmpty())
			priortizedList = unicornPriortizer.priortizeQueue(unicornList, FetchType.FULL);
		
		List<Message> resultList = new ArrayList<Message>();
		
		if (!priortizedList.isEmpty()) {
			for (Unicorn unicorn : priortizedList) {
				resultList.add(new Message(unicorn.getTaskId(),unicorn.getDatetime()));
			}
		}
			return resultList;
	}
	
	@DeleteMapping(path = "/remove/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResMessage removeOrder(@PathVariable(value = "id", required = true) Long id) {
		
		Unicorn unicorn = orderDao.findUnicornBytaskId(id);

		if (unicorn != null) {
			orderDao.delete(unicorn);
			return new ResMessage(unicorn.getTaskId(), "DELETED");
		}else {
			return new ResMessage(id, "NOT FOUND");
		}		
	}
	
	@GetMapping(path = "/locate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody IndexDTO getTaskLocation(@PathVariable(value = "id", required = true) Long id) {

		
		Unicorn unicorn = orderDao.findUnicornBytaskId(id);
		
		if(unicorn != null) {
			List<Unicorn> unicornList = orderDao.findAll();
			List<Unicorn> priortizedList = new ArrayList<Unicorn>();		

			if (!unicornList.isEmpty())
				priortizedList = unicornPriortizer.priortizeQueue(unicornList, FetchType.FULL);
			int index = -1;
			if (!priortizedList.isEmpty()) {
				index = priortizedList.indexOf(unicorn);
			}
			return new IndexDTO((long)(index));
		}else {
			return new IndexDTO(-1L);
		}
		
	}
	
	@PostMapping(path = "/retrieve/waittime", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResMessage getTaskLocation(@RequestBody IntervalMessage message) {

		List<Unicorn> unicornList = orderDao.findAll();
		
		Long totalWaitTime = 0L;
		if(unicornList != null) {
			for (Unicorn u: unicornList) {
				totalWaitTime = totalWaitTime + (message.getTimestamp().getTime() - u.getDatetime().getTime());
			}			
			return new ResMessage((totalWaitTime / unicornList.size()), "Wait Time Seconds") ;
		}
		
		return new ResMessage(0, "EMPTY");
	}
}
