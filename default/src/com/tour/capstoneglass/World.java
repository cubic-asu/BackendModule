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
	public final static String EntityName = "world";
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
	public String map_id;
	
	
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
		Filter worldFilter = new FilterPredicate("world_id", FilterOperator.EQUAL,
				id);
		Query q = new Query("world").addSort("name", SortDirection.ASCENDING);
		q.setFilter(worldFilter);
		PreparedQuery pq = dss.prepare(q);

		for (Entity result : pq.asIterable()) {
			updateWorldFromEntity(result);

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
		
		all_locations = new ArrayList<Location>();
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
			Query q = new Query("location").addSort("name", SortDirection.ASCENDING);
			q.setFilter(worldFilter);
			PreparedQuery pq = dss.prepare(q);
			for (Entity result : pq.asIterable()) {
				
				Location temploc = new Location((String)result.getProperty("loc_id"), 
						(String)result.getProperty("name"),
						(String)result.getProperty("description"),
						Double.parseDouble((String) result.getProperty("latitude")),
						Double.parseDouble((String) result.getProperty("longitude")), 
						Integer.parseInt((String) result.getProperty("unlock_threshold")), 
						Boolean.parseBoolean((String)result.getProperty("visited")), 
						(boolean)result.getProperty("locked"), 
						(ArrayList<String>)result.getProperty("locations_to_unlock"),
						(ArrayList<String>)result.getProperty("locations_to_lock"));
					all_locations.add(temploc);
					if((boolean)result.getProperty("locked") == false){
						unlocked_locations.add(temploc);
					}
			}
			
	}
	
	public Location getLocationByName(String loc_name){
		for(int i=0; i < all_locations.size(); i++){
			if(all_locations.get(i).name.equalsIgnoreCase(loc_name)){
				return all_locations.get(i);
			}
		}
		return null;
	}
	
	public void addUnlockedLocations(ArrayList<String> loc_names){
	
		for(int i=0; i < loc_names.size(); i++){
			Location loc = getLocationByName(loc_names.get(i));
			unlocked_locations.add(loc);
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
	
	public void setMapId(String id){
		map_id = id;
	}
	
	
	public String toString(boolean formatAsHtml)
	{
		String str = this.toString();
		
		if (formatAsHtml)
			str = str.replaceAll("\n", "<br>\n");
		
		return str;
		
	}
	
	public String toCard(){
		
		String html = "<article><section><div>" +
				"<p style='text-align:center;'>" + name + "</p>" +
				"<p style='font-size:0.5em'>"+ description + "</p></div>" +
				"</section>" +
				"<footer><div>Tap to Start</div>" +
				"</footer></article>";
		
		return html;
	}
	
	
}
