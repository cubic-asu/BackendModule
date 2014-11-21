package com.tour.capstoneglass;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class World
{
	//String constants representing column names
	public final static String EntityName = "World";
	public final static String ColWorldId = "world_id";
	public final static String ColUserId = "user_id";
	public final static String ColName = "name";
	public final static String ColDescription = "description";
	public final static String ColUnlockedLocations = "unlocked_locations";
	public final static String ColAllLocations = "all_locations";
	
	//Attributes
	public String world_id;
	public String user_id;
	public String name;
	public String description;
	public ArrayList<Location> unlocked_locations;
	public HashMap<String, Location> all_locations;
	
	
	//Constructors
	public World()
	{	
		world_id = "";
		user_id = "";
		name = "";
		description = "";
		unlocked_locations = new ArrayList<Location>();
		all_locations = new HashMap<String,Location>();
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
	
	public World(String wId, String uId, String wName, String wDescription, ArrayList<Location> unlockedLocations, HashMap<String, Location> allLocations)
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
		all_locations = new HashMap<String, Location>();
		
		ArrayList<String> locationIds  = getLocationsArrayList(e.getProperty(ColUnlockedLocations));
		getLocations(locationIds);	
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
		w.setProperty(ColUnlockedLocations, unlocked_locations);
		//No need to set the all_loctions property as this is not stored in the datastore
		
		return w;
	}
	
	private ArrayList<String> getLocationsArrayList(Object obj)
	{
		/**
		 * Function will take an Object instance and attempt to cast to an ArrayList<String>. 
		 * Used to retrieve an list of location ids. 
		 */
		ArrayList<String> a = new ArrayList<String>();
		if (obj instanceof ArrayList)
		{
			ArrayList<?> temp = (ArrayList<?>)obj;
			for (int i = 0; i < temp.size(); i++)
				if (temp.get(i) instanceof String)
					a.add((String)temp.get(i));
		}
		return a;
	}
	
	
	private void getLocations(ArrayList<String> a)
	{
		/**
		 * Function to populate unlocked_locations as well as all_locations  
		 */
		
		for(String str : a)
		{
			Location l = new Location(str);
			if (!l.equals(null))
			{
				unlocked_locations.add(l);
				all_locations.put(str, l);
				getLocationsRecursive(l.locations_to_unlock);	//recursively add locations to all_locations
			}
		}
	}
	
	
	private void getLocationsRecursive(ArrayList<String> a)
	{
		/**
		 * Recursive function to populate all reachable locations in the current World.
		 * Uses a depth first approach. 
		 */
		
		//Base Case ArrayList size = 0
		if (a.size() == 0)		
			return;
		else
		{
			for(String str : a)
			{
				Location l = new Location(str);
				if (l != null)
				{
					all_locations.put(str, l);
					getLocationsRecursive(l.locations_to_unlock);
				}
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
	
	public String toString()
	{
		String s = String.format("%s: %s\n", ColWorldId, world_id);
		s += String.format("%s: %s\n", ColUserId, user_id);
		s += String.format("%s: %s\n", ColName, name);
		s += String.format("%s: %s\n", ColDescription, description);
		
		s += String.format("%s: {", ColUnlockedLocations);
		boolean firsttime = true;
		for (int i = 0; i < unlocked_locations.size(); i++)
		{
			Location l = unlocked_locations.get(i);
			if (firsttime)
			{
				 
				s += l.loc_id;
				firsttime = false;
			}
			else
				s += ", " + l.loc_id;
		}
		
		s += "}\n";
		s += String.format("%s: {", ColAllLocations);
		
		firsttime = true;
		
		for (String locationId : all_locations.keySet())
		{
			if (firsttime)
			{
				s += locationId;
				firsttime = false;
			}
			else
				s += ", " + locationId;
		}
		
		s += "}\n";
		
		return s;
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
