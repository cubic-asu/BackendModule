package com.mirror.capstoneglass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.util.DateTime;
import com.google.api.services.mirror.Mirror.Timeline;
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

public class TourBuilder {

	public TourBuilder(String pid, World w, HttpServletRequest req, Timeline timeline) throws IOException{

		//create world card
		TimelineItem worldcard = Card.createWorldCard(w);
		worldcard.setBundleId(w.world_id);
		worldcard.setIsBundleCover(true);
		timeline.insert(worldcard).execute();
		
		
		
		
		//get current location of the glass
		Key k = KeyFactory.createKey("Location Update", pid);
		DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
		Entity e;
		double currLat = 0.0;
		double currLon = 0.0;
		try {
			e = dss.get(k);
			
			currLat = (double)e.getProperty("Latitude");
	  	    currLon = (double)e.getProperty("Longitude");
		} catch (EntityNotFoundException e1) {
			e1.printStackTrace();
		}
		
		//create map card
				TimelineItem mapcard = Card.createMapCard(currLat,currLon,w);
				mapcard.setBundleId(w.world_id);
				timeline.insert(mapcard).execute();
  	    
		//create location cards
		for (com.tour.capstoneglass.Location l : w.unlocked_locations)
		{
			TimelineItem locationcard = Card.createLocationCard(l, l.toCard(currLat, currLon));
			locationcard.setBundleId(w.world_id);
			TimelineItem timelineid = timeline.insert(locationcard).execute();
			l.timeline_id = timelineid.getId();
		}
		
		
		
	}
	
}
