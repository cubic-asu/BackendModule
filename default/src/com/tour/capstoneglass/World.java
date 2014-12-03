package com.tour.capstoneglass;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.mirror.capstoneglass.Card;

public class World
{
	//String constants representing column names
	public final static String EntityName = "World";
	public final static String ColWorldId = "world_id";
	public final static String ColUserId = "user_id";
	public final static String ColName = "name";
	public final static String ColDescription = "description";
	
	//Attributes
	public String world_id;
	public String user_id;
	public String name;
	public String description;
	public ArrayList<Location> unlocked_locations;
	public ArrayList<Location> all_locations;
	
	
	//Constructors
	public World()
	{	
		world_id = "";
		user_id = "";
		name = "";
		description = "";
		unlocked_locations = new ArrayList<Location>();
		all_locations = new ArrayList<Location>();
	}
	
	public World(String id)
	{
		DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
		Key k = KeyFactory.createKey(EntityName, id);
		
		try
		{
			Entity e = dss.get(k);
			updateWorldFromEntity(e);			
		}
		catch (EntityNotFoundException err)
		{
			//TODO: How will we handle errors??
			System.out.println("Enable to Retrieve Entity from Key<br>");
			System.out.println(err.toString());
			
		}
	}
	
	public World(String wId, String uId, String wName, String wDescription, ArrayList<Location> unlockedLocations, ArrayList<Location> allLocations)
	{
		world_id = wId;
		user_id = uId;
		name = wName;
		description = wDescription;
		unlocked_locations = unlockedLocations;
		all_locations = allLocations;
	}
	
	public World(Entity e)
	{	
		updateWorldFromEntity(e);
	}
	
	
	private void updateWorldFromEntity(Entity e)
	{
		/**
		 * Update all World attributes from Entity object. 
		 */
		//Check to make sure the Entity is of the appropriate kind
		if (!(e.getKind().equals(EntityName)))
			return;
		
		world_id = (String)e.getProperty(ColWorldId);
		user_id = (String)e.getProperty(ColUserId);
		name = (String)e.getProperty(ColName);
		description = (String)e.getProperty(ColDescription);
		
		unlocked_locations = new ArrayList<Location>();
		getLocations((String)e.getProperty(ColWorldId));
	}
	
	public Entity toEntity()
	{
		/**
		 * This function will take the current World object and create a Entity for storage in the Cloud Datastore
		 */
		Entity w = new Entity(EntityName, world_id);
		w.setProperty(ColUserId, user_id);
		w.setProperty(ColName, name);
		w.setProperty(ColDescription, description);
		//No need to set the all_loctions property as this is not stored in the datastore
		
		return w;
	}
	
	
	
	
	private void getLocations(String worldName)throws IllegalArgumentException
	{
			
			DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
			Filter worldFilter =   new FilterPredicate("world_name",FilterOperator.EQUAL, worldName);
			Query q = new Query("Location").addSort("name", SortDirection.ASCENDING);
			q.setFilter(worldFilter);
			PreparedQuery pq = dss.prepare(q);
			for (Entity result : pq.asIterable()) {
				
				Location temploc = new Location((String)result.getProperty("loc_id"), 
						(String)result.getProperty("name"),
						(String)result.getProperty("description"),
						(double)result.getProperty("latitude"),
						(double)result.getProperty("longitude"), 
						(int)result.getProperty("unlock_threshold"), 
						(boolean)result.getProperty("visited"), 
						(boolean)result.getProperty("locked"), 
						(ArrayList<String>)result.getProperty("locations_to_unlock"),
						(ArrayList<String>)result.getProperty("locations_to_lock"));
					all_locations.add(temploc);
					if((boolean)result.getProperty("locked") == false){
						unlocked_locations.add(temploc);
					}
			}
			
	}
	
	
	
	public boolean addUpdateDataStore()
	{
		boolean success = false;
		
		if (!world_id.equals(null) && !world_id.equals(""))
		{
			
			DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
			dss.put(this.toEntity());
			//TODO: Verify update was correct.  
			success = true;
		}
	
		return success;
		
	}
	
	
	public String toString(boolean formatAsHtml)
	{
		String str = this.toString();
		
		if (formatAsHtml)
			str = str.replaceAll("\n", "<br>\n");
		
		return str;
		
	}
	
	public String toCard(){
		String html = "<article><figure>" +
				"<h1 class='text-auto-size'>" + name + "</h1><br/>" +
				"<p class='text-auto-size'>" + description + "</p>" +
				"</article></figure>";
		
		return html;
	}
	
	
}
