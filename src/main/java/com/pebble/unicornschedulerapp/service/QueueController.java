package com.pebble.unicornschedulerapp.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pebble.unicornschedulerapp.dao.OrderDao;
import com.pebble.unicornschedulerapp.dto.Greeting;
import com.pebble.unicornschedulerapp.dto.Message;
import com.pebble.unicornschedulerapp.dto.ResMessage;
import com.pebble.unicornschedulerapp.dto.TaskType;
import com.pebble.unicornschedulerapp.entities.Unicorn;

@RestController
public class QueueController {

	@Autowired
	OrderDao orderDao;

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	@RequestMapping("/greeting")
	public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

	@PostMapping(path = "/order", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResMessage receiveOrder(@Valid @RequestBody Message message) {

		long id = message.getId();
		Date messageTime = message.getTimestamp();

		Unicorn unicorn = orderDao.findUnicornBytaskId(id);
		
		if (unicorn == null)

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
			return new ResMessage(0, "Already exists");
	}

	@GetMapping(path = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResMessage getPriorityTask() {

		List<Unicorn> unicornList = orderDao.findAll();

		List<Unicorn> MGMTList = new ArrayList<Unicorn>();
		List<Unicorn> VIPList = new ArrayList<Unicorn>();
		List<Unicorn> PRIORList = new ArrayList<Unicorn>();
		List<Unicorn> NORMALList = new ArrayList<Unicorn>();

		if (unicornList.size() > 0) {
			for (Unicorn data : unicornList) {
				if (data.getType().equals(TaskType.MGMT))
					MGMTList.add(data);
				if (data.getType().equals(TaskType.VIP))
					VIPList.add(data);
				if (data.getType().equals(TaskType.PRIOR))
					PRIORList.add(data);
				if (data.getType().equals(TaskType.NORMAL))
					NORMALList.add(data);
			}
		}
		
		System.out.println("Normal List Size: " + NORMALList.size());

		if (MGMTList.size() > 0) {

			MGMTList.sort(new Comparator<Unicorn>() {
				public int compare(Unicorn ob1, Unicorn ob2) {
					if (ob1.getDatetime().before(ob2.getDatetime())) {
						return -1;
					} else if (ob1.getDatetime().after(ob2.getDatetime())) {
						return 1;
					} else {
						return 0;
					}
				}
			});

			return new ResMessage(MGMTList.get(0).getTaskId(), "Fetched");
		}
		
		List<Unicorn> collectiveList = new ArrayList<>();		

		if (VIPList.size() > 0) {
			long currentTime = new Date().getTime();
			
			VIPList.forEach( f -> f.setRank((int) Math.floor(Math.max (3, (currentTime - f.getDatetime().getTime()) * Math.log((currentTime - f.getDatetime().getTime()))))));
			
			collectiveList.addAll(VIPList);
		}
		if (PRIORList.size() > 0) {
			long currentTime = new Date().getTime();
			PRIORList.forEach( f -> f.setRank((int) Math.floor(Math.max (3, 2 * ((currentTime - f.getDatetime().getTime())) * Math.log((currentTime - f.getDatetime().getTime()))))));
			collectiveList.addAll(PRIORList);
		}
		if (NORMALList.size() > 0) {			
			
			NORMALList.forEach( f -> System.out.println(" " + (new Date().getTime() - f.getDatetime().getTime())));
			NORMALList.forEach( f -> f.setRank((int) (new Date().getTime() - f.getDatetime().getTime())));
			collectiveList.addAll(NORMALList);
		}
		
		collectiveList.sort((Unicorn ob1, Unicorn ob2)-> ob2.getRank() - ob1.getRank());
		
		System.out.println(collectiveList.toString());
		
		if (collectiveList.size() > 0) {
			return new ResMessage(collectiveList.get(0).getTaskId(), "Fetched");
		}

		return new ResMessage(0, "Nothing Found");

	}

	@GetMapping(path = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResMessage getOrder(@PathVariable(value = "id", required = true) Long id) {

		Unicorn unicorn = orderDao.findUnicornBytaskId(id);

		if (unicorn != null)
			return new ResMessage(unicorn.getTaskId(), "Found");
		else
			return new ResMessage(0, "Not Found");

	}

	@GetMapping(path = "/get/all", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Unicorn> getAllOrders() {

		List<Unicorn> resultSet = orderDao.findAll();
		resultSet.sort((Unicorn ob1, Unicorn ob2)-> ob1.getRank() - ob2.getRank());
		return resultSet;
	}
}
