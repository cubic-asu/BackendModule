package com.mirror.capstoneglass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.Mirror.Timeline.Get;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.RequestLogs;
import com.google.apphosting.api.logservice.LogServicePb.RequestLog;
import com.tour.capstoneglass.*;

public class TourUpdater implements Runnable{

		String id;
		DatastoreService dss;
		Key k;
		double currLat;
		double currLon;
		String timeStamp;
		World w; 
		Timeline timeline;
		
		TourUpdater(String id, World w, Timeline timeline){
			this.id = "capstoneglass2014@gmail.com";
			dss  = DatastoreServiceFactory.getDatastoreService();
			k = KeyFactory.createKey("Location Update", this.id);
			this.w = w;
			this.timeline = timeline;
		}
		
		private boolean isNearby(double currLat, double currLon,Location location) {
			boolean isNearby;
			double latitude = location.latitude;
			double longitude = location.longitude;
			double distance = Distance.getDistance(currLat, currLon, latitude,longitude);
			if(distance <= location.unlock_threshold){
				isNearby = true;
			}else{
				isNearby = false;
			}
			return isNearby;
		}
		
		
		@Override
		public void run() {
			// TODO Auto-generated method 
			try {
			      while (true) {
			    	  Entity e = dss.get(k);
			    	  currLat = (double)e.getProperty("Latitude");
			    	  currLon = (double)e.getProperty("Longitude");
			    	  timeStamp = (String)e.getProperty("Timestamp");
			    	  
			    	  
			    	  //checks for unlocked locations
			        for(Location loc : w.unlocked_locations){
			        	if(isNearby(currLat, currLon, loc)){
			        		updateLocationCards(loc);
			        		break;
			        	}
			        }
			        Thread.sleep(5000);
			      }
			    } catch (InterruptedException ex) {
			      throw new RuntimeException("Interrupted in loop:", ex);
			    }catch (EntityNotFoundException ex){
			    	 throw new RuntimeException("Entity Not Found:", ex);
			    }
		}
		
		public void updateLocationCards(Location loc){
			
			//modify the location card to display its unlocked content, including its description
			try {
				TimelineItem timelineItem = timeline.get(loc.timeline_id).execute();
				String html = loc.toUnlockedCard();
				timelineItem.setHtml(html);
				timeline.update(timelineItem.getId(), timelineItem).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			loc.visited = true;
			addNewlyUnlockedCards(loc.locations_to_unlock);
			w.addUnlockedLocations(loc.locations_to_unlock);
			retireLocationCards(loc.locations_to_retire);
			
			
			
			//update map card
			String map_html = Card.updateMapCard(currLat,currLon, w);
			try {
				TimelineItem timelineItem = timeline.get(w.map_id).execute();
				timelineItem.setHtml(map_html);
				timeline.update(timelineItem.getId(), timelineItem).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void addNewlyUnlockedCards(ArrayList<String> unlocked_locations){
			for(int i=0; i < unlocked_locations.size(); i++){
				Location l = w.getLocationByName(unlocked_locations.get(i));
				try {
					TimelineItem locationcard = Card.createLocationCard(l, l.toCard(currLat, currLon));
					locationcard.setBundleId(w.world_id);
					TimelineItem timelineid = timeline.insert(locationcard).execute();
					l.timeline_id = timelineid.getId();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void retireLocationCards(ArrayList<String> retired_locations){
			//modify the location card to show it is retired
			for(int i=0; i < retired_locations.size(); i++){
				Location loc = w.getLocationByName(retired_locations.get(i));
				try {
					TimelineItem timelineItem = timeline.get(loc.timeline_id).execute();
					String html = loc.toRetiredCard();
					timelineItem.setHtml(html);
					timeline.update(timelineItem.getId(), timelineItem).execute();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
}
