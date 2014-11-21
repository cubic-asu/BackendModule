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
import com.tour.capstoneglass.*;

public class TourUpdater implements Runnable{

		String id;
		DatastoreService dss;
		Key k;
		double currLat;
		double currLon;
		String timeStamp;
		int radius = 5; //needs to be changed for each location
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
			if(distance <= radius){
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
			    	  
			    	  //updates the location distance for each card
			    	  //updateLocationDistance();
			    	  
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
				TimelineItem timelineItem = timeline.get(loc.loc_id).execute();
				String html = loc.toUnlockedCard();
				timelineItem.setHtml(html);
				timeline.update(loc.loc_id, timelineItem).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//change location state in the database
			loc.visited = true;
			
			//deletes this location card from the timeline
			/*
			try {
				timeline.delete(loc.loc_id);
			} catch (IOException e) {
				e.printStackTrace();
			}
			*/
			
			//adds recently unlocked cards to the timeline
			
		}
		
}
