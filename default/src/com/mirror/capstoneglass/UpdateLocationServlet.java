package com.mirror.capstoneglass;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.appengine.api.datastore.*;
import com.google.glassware.AuthUtil;
import com.tour.capstoneglass.Location;


public class UpdateLocationServlet extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException 
	{
		double latitude = Double.parseDouble(req.getParameter("lat"));
		double longitude = Double.parseDouble(req.getParameter("long"));
		String email = req.getParameter("email");
		
		SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("America/Phoenix"));
		/* Timestamp of the program */
		Date date = new Date();
		Long unix2 = date.getTime();
		String time_now = sdf.format(unix2);
		
		DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
		Entity w = new Entity("Location Update", email);
		
		
		w.setProperty("Latitude", latitude);
		w.setProperty("Longitude", longitude);
		w.setProperty("LocTimestamp", time_now);
		
		dss.put(w);
		
		
		resp.setContentType("text/html; charset=utf-8");
		resp.getWriter().println(
				"<html>\n" +
				"<head><meta http-equv=\"refresh\"content=\"3;url=/index.html\"></head>\n" +
				"<body>\n" +
				"\t<h1>Location Update</h1><br />\n" +
				"\tLatitude:" + latitude + "<br />\n" +
				"\tLatitude:" + longitude + "</body></html>");
		
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException 
	{
		double latitude = Double.parseDouble(req.getParameter("lat"));
		double longitude = Double.parseDouble(req.getParameter("long"));
		String email = req.getParameter("email");
		
		SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("America/Phoenix"));
		/* Timestamp of the program */
		Date date = new Date();
		Long unix2 = date.getTime();
		String time_now = sdf.format(unix2);
		
		DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
		Entity w = new Entity("Location Update", email);
		w.setProperty("Latitude", latitude);
		w.setProperty("Longitude", longitude);
		w.setProperty("LocTimestamp", time_now);
		
		dss.put(w);
		
		
		resp.setContentType("text/html; charset=utf-8");
		resp.getWriter().println(
				"<html>\n" +
				"<head><meta http-equv=\"refresh\"content=\"3;url=/index.html\"></head>\n" +
				"<body>\n" +
				"\t<h1>Location Update</h1><br />\n" +
				"\tLatitude:" + latitude + "<br />\n" +
				"\tLatitude:" + longitude + "</body></html>");
	}
}
