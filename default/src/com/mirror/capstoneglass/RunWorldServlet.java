package com.mirror.capstoneglass;

import java.io.IOException;
import java.util.Date;
import com.google.appengine.api.ThreadManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.google.api.services.mirror.model.Location;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.appengine.api.datastore.*;
import com.google.glassware.AuthUtil;
import com.tour.capstoneglass.*;

public class RunWorldServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException 
	{
		//EXAMPLE: https://capstoneglassapi.appspot.com/runworld?world_id=XXXXXXXX		
				
		String world_id = req.getParameter("world_id");
				
				if(!world_id.isEmpty() && world_id != null)
				{
					String msg = "You have started the " + world_id + " Tour!<br />\n";
					World w = new World(world_id);
					
					if (w!=null)
					{
						Mirror mirror = getMirror(req);
						Timeline timeline = mirror.timeline();
						new TourBuilder("capstoneglass2014@gmail.com",w,req, timeline);
						
						TourUpdater update  = new TourUpdater("capstoneglass2014@gmail.com", w, timeline);
						Thread thread = ThreadManager.createBackgroundThread(update);  
						thread.start();
						
					}
					
					
									
					//print out results
					resp.setContentType("text/html; charset=utf-8");
					resp.getWriter().println(
							"<html><head>" +
							"<meta http-equv=\"refresh\"content=\"3;url=/index.html\">" +
							"</head>" +
							"<body>" + msg + "</body></html>");
					
				}
				else
				{
					//print out results
					resp.setContentType("text/html; charset=utf-8");
					resp.getWriter().println(
							"<html><head>" +
							"<meta http-equv=\"refresh\"content=\"3;url=/index.html\">" +
							"</head>" +
							"<body>The url parameter 'world_id' is Empty. Please Try again.<br></body></html>");
				
				}
	}
	
	//allows access to the Mirror API
	public Mirror getMirror(HttpServletRequest req) throws IOException{
		//get credential of client
		Credential credential = AuthUtil.getCredential(req);
		
		//build access to Mirror API
		return new Mirror.Builder(new UrlFetchTransport(), new JacksonFactory(), credential)
		.setApplicationName("Hello Glass!").build();
		
	}
	
}
