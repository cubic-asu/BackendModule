package com.tour.capstoneglass;

import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.tour.capstoneglass.Distance;


public class Location {
	//String constants representing datastore property names
	public final static String EntityName = "Location";
	public final static String ColLocId = "loc_id";
	public final static String ColName = "name";
	public final static String ColDescription = "description";
	public final static String ColLatitude = "latitude";
	public final static String ColLongitude = "longitude";
	public final static String ColUnlockThreshold = "unlock_threshold";
	public final static String ColVisited = "visited";
	public final static String ColLocked = "locked";	//TODO: Is this needed in our implementation???
	public final static String ColLocationsToUnlock = "locations_to_unlock";
	public final static String ColLocationsToRetire = "locations_to_retire";
	
	//Attributes
	public String loc_id;
	public String name;
	public String description;
	public double latitude;
	public double longitude;
	public int unlock_threshold;
	public boolean visited;
	public boolean locked;
	public ArrayList<String> locations_to_unlock;
	public ArrayList<String> locations_to_retire;
	
	
	//Constructors
	public Location()
	{
		loc_id = "";
		name = "";
		description = "";
		latitude = 0.0;
		longitude = 0.0;
		unlock_threshold =  0;
		visited = false;
		locked = true;	
		locations_to_unlock = new ArrayList<String>();
		locations_to_retire = new ArrayList<String>();
	}
	
	public Location(String id)
	{
		/**
		 * Takes a World id as input and returns a Location Object. 
		 */
		DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
		Key k = KeyFactory.createKey(EntityName, id);
		
		try
		{
			Entity e = dss.get(k);
			updateLocationFromEntity(e);	
		}
		catch (EntityNotFoundException err)
		{
			//TODO: How will we handle errors?? 
			System.out.println("Enable to Retrieve Entity from Key<br>");
		}
	}
	
	public Location(String locId, String locName, String locDescription, double locLatitude, double locLongitude
			, int unlockThreshold, boolean locVisited, boolean locLocked
			, ArrayList<String> locationsToUnlock, ArrayList<String> locationsToRetire)
	{
		loc_id = locId;
		name = locName;
		description = locDescription;
		latitude = locLatitude;
		longitude = locLongitude;
		unlock_threshold = unlockThreshold;
		visited = locVisited;
		locked = locLocked;	
		locations_to_unlock = locationsToUnlock;
		locations_to_retire = locationsToRetire;
	}
	
	public Entity toEntity()
	{
		/**
		 * Function creates and returns a Entity of Kind Location. Used to store the Location  
		 */
		Entity e = new Entity("Location", loc_id);
		e.setProperty(ColLocId, loc_id);
		e.setProperty(ColName, name);
		e.setProperty(ColDescription, description);
		e.setProperty(ColLatitude, latitude);
		e.setProperty(ColLongitude, longitude);
		e.setProperty(ColUnlockThreshold, unlock_threshold);
		e.setProperty(ColVisited, false);
		e.setProperty(ColLocked, true);
		e.setProperty(ColLocationsToUnlock, locations_to_unlock);
		e.setProperty(ColLocationsToRetire, locations_to_retire);
		
		return e;	
	}

	private void updateLocationFromEntity(Entity e) 
	{
		if (!(e.getKind().equals(EntityName)))
			return;
		
		loc_id = (String)e.getProperty(ColLocId);
		name = (String)e.getProperty(ColName);
		description = (String)e.getProperty(ColDescription);
		latitude = (double)e.getProperty(ColLatitude);
		longitude = (double)e.getProperty(ColLongitude);
		
		int i = ((int) (long) e.getProperty(ColUnlockThreshold));
		unlock_threshold = i;
		visited = (boolean)e.getProperty(ColVisited);
		locked = (boolean)e.getProperty(ColLocked);	
		locations_to_unlock = getLocationsArrayList(e.getProperty(ColLocationsToUnlock));
		locations_to_retire = getLocationsArrayList(e.getProperty(ColLocationsToRetire));	
	}

	private ArrayList<String> getLocationsArrayList(Object obj)
	{
		/**
		 * Function will take an Object instance and construct and return an ArrayList<String> of locations ids. 
		 * Performs type checking for ArrayList and also String. 
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


	public boolean addUpdateDataStore()
	{
		/**
		 * Function will add the current Location object to the datastore if it does not already exist. If
		 * the Location Entity already exists it will be updated in the datastore   
		 */
		boolean success = false;
		
		if (!loc_id.equals(null) && !loc_id.equals(""))
		{
			DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
			dss.put(this.toEntity());
			//TODO: Verify update was successful.  
			success = true;
		}
	
		return success;
	}
	
	
	public static Entity createEntity(String locId, String locName, String locDescription, double locLatitude, double locLongitude
			, long unlockThreshold, boolean locVisited, boolean locLocked
			, ArrayList<String> locationsToUnlock, ArrayList<String> locationsToRetire)
	{
		Entity e = new Entity(EntityName, locId);
		e.setProperty(ColLocId, locId);
		e.setProperty(ColName, locName);
		e.setProperty(ColDescription, locDescription);
		e.setProperty(ColLatitude, locLatitude);
		e.setProperty(ColLongitude, locLongitude);
		e.setProperty(ColUnlockThreshold, unlockThreshold);
		e.setProperty(ColVisited, locVisited);
		e.setProperty(ColLocked, locLocked);
		e.setProperty(ColLocationsToUnlock, locationsToUnlock);
		e.setProperty(ColLocationsToRetire, locationsToRetire);
		
		return e;
	}
	
	/**
	 * This function will return a string representing all of the location attributes
	 * @return Returns a string representing all of the location attributes
	 */
	public String toString()
	{
		String s = String.format("%s: %s\n", ColLocId, loc_id);
		s += String.format("%s: %s\n", ColName, name);
		s += String.format("%s: %s\n", ColDescription, description);
		s += String.format("%s: %f\n", ColLatitude , latitude);
		s += String.format("%s: %f\n", ColLongitude, longitude);
		s += String.format("%s: %d\n", ColUnlockThreshold, (int)unlock_threshold);
		s += String.format("%s: %s\n", ColVisited, ( visited ? "TRUE" : "FALSE"));
		s += String.format("%s: %s\n", ColLocked, (locked ? "TRUE" : "FALSE"));
		
		s += String.format("%s: {", ColLocationsToUnlock);
		boolean firsttime = true;
		for (int i = 0; i < locations_to_unlock.size(); i++)
		{
			if (firsttime)
			{
				s += locations_to_unlock.get(i);
				firsttime = false;
			}
			else
				s += ", " + locations_to_unlock.get(i);
		}
		
		s += "}\n";
		s += String.format("%s: {\n", ColLocationsToRetire);
		
		firsttime = true;
		
		for (String locationId : locations_to_retire)
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
	
	/**
	 * This function will return a string representing all of the location attributes with HTML <br> tags
	 * @param formatAsHtml Set to true to include HTML <br> tags for better formatting
	 * @return Returns a string representing all of the location attributes with HTML <br> tags
	 */
	public String toString(boolean formatAsHtml)
	{
		String str = this.toString();
		
		if (formatAsHtml)
			str = str.replace("\n", "<br>\n");
		
		return str;
	}
	
	//this function takes in lat/long from user and constructs a html string for a location card
	public String toCard(double latitude, double longitude){
		int dist = (int) Distance.getDistance(latitude,longitude,this.latitude,this.longitude);
		String html = "<article><figure>" +
				"<h1 class='text-auto-size'>" + name + "</h1><br/>" +
				"<h2>Distance: " + String.valueOf(dist) + "KM</h2>" +
				"</article></figure>";
		
		return html;
	}
	
	//shows the unlocked card with a description 
	public String toUnlockedCard(){
		String html = "<article><figure>" +
				"<h1 class='text-auto-size'>" + name + "</h1><br/>" +
				"<p class='text-auto-size'>" + description + "</p>" +
				"</article></figure>";
		
		return html;
	}
	
	



}

