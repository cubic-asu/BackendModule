package com.mirror.capstoneglass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.client.util.DateTime;
import com.tour.capstoneglass.Location;
import com.google.api.services.mirror.model.NotificationConfig;
import com.tour.capstoneglass.*;

public class Card {

	public static TimelineItem createLocationCard(Location loc, String html){
		
		TimelineItem timelineItem = new TimelineItem()
		.setHtml(html)
		.setDisplayTime(new DateTime(new Date()))
		.setNotification(new NotificationConfig().setLevel("Default"))
		.setId(loc.loc_id);

		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		timelineItem.setLocation(new com.google.api.services.mirror.model.Location()
						.setLatitude(loc.latitude)
						.setLongitude(loc.longitude)
						.setDisplayName(loc.name));

		menuItemList.add(new MenuItem().setAction("NAVIGATE"));	
		menuItemList.add(new MenuItem().setAction("DELETE"));
		timelineItem.setMenuItems(menuItemList);
		return timelineItem;
	}
	
	public static TimelineItem createWorldCard(World w){
		TimelineItem timelineItem = new TimelineItem()
		.setHtml(w.toCard())
		.setDisplayTime(new DateTime(new Date()))
		.setNotification(new NotificationConfig().setLevel("Default"));

		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		menuItemList.add(new MenuItem().setAction("DELETE"));
		timelineItem.setMenuItems(menuItemList);
		return timelineItem;
	}
	
	public static TimelineItem createMapCard(double latitude, double longitude, World w){
		String html = "<article><figure><img src='glass://map?w=240&h=360&marker=0;";
		
		for (com.tour.capstoneglass.Location l : w.unlocked_locations)
		{
			html += String.valueOf(l.latitude) + "," + String.valueOf(l.longitude)+ "&marker=0;";
		}
		html += String.valueOf(latitude) + "," + String.valueOf(longitude); //current glass location
		
		html +=	"height='360' width='240'></figure><section><div class='text-auto-size'>" +
				"<p>Multiple Markers</p></div></section></article>";
		
		
		TimelineItem timelineItem = new TimelineItem()
		.setHtml(html)
		.setDisplayTime(new DateTime(new Date()))
		.setNotification(new NotificationConfig().setLevel("Default"));

		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		menuItemList.add(new MenuItem().setAction("DELETE"));
		timelineItem.setMenuItems(menuItemList);
		return timelineItem;
	}
	
}