package com.pebble.unicornschedulerapp.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pebble.unicornschedulerapp.commons.FetchType;
import com.pebble.unicornschedulerapp.commons.TaskType;
import com.pebble.unicornschedulerapp.entities.Unicorn;

@Service
public class UnicornPriortizer{

	public List<Unicorn> priortizeQueue(List<Unicorn> unicornList, FetchType type){
		List<Unicorn> MGMTList = new ArrayList<Unicorn>();
		List<Unicorn> VIPList = new ArrayList<Unicorn>();
		List<Unicorn> PRIORList = new ArrayList<Unicorn>();
		List<Unicorn> NORMALList = new ArrayList<Unicorn>();

		if (!unicornList.isEmpty()) {
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
		
		if (!MGMTList.isEmpty()) {

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
			
			if (type.equals(FetchType.PRIORITY)) {
				return MGMTList;
			}
			
		}
		
		List<Unicorn> collectiveList = new ArrayList<>();		

		if (!VIPList.isEmpty()) {
			long currentTime = new Date().getTime();
			
			VIPList.forEach( f -> f.setRank((int) Math.floor(Math.max (3, (currentTime - f.getDatetime().getTime()) * Math.log((currentTime - f.getDatetime().getTime()))))));
			
			collectiveList.addAll(VIPList);
		}
		if (!PRIORList.isEmpty()) {
			long currentTime = new Date().getTime();
			PRIORList.forEach( f -> f.setRank((int) Math.floor(Math.max (3, 2 * ((currentTime - f.getDatetime().getTime())) * Math.log((currentTime - f.getDatetime().getTime()))))));
			collectiveList.addAll(PRIORList);
		}
		if (!NORMALList.isEmpty()) {			
			NORMALList.forEach( f -> f.setRank((int) (new Date().getTime() - f.getDatetime().getTime())));
			collectiveList.addAll(NORMALList);
		}
		
		collectiveList.sort((Unicorn ob1, Unicorn ob2)-> ob2.getRank() - ob1.getRank());
		
		
		List<Unicorn> finalList = new ArrayList<>();
		
		if (!MGMTList.isEmpty())
			finalList.addAll(MGMTList);
		if (!collectiveList.isEmpty())
			finalList.addAll(collectiveList);
		
		return finalList;
	}
}
