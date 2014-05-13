package com.lsu.vizeq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import android.graphics.Color;
import android.util.Log;

//pure data with no view components.
//takes care of all the logic for creating/packing circles
//includes ways to visualize according to artist, album, track, and genre

//keeps track of Circles instead of PreferenceCircles - the difference being that the latter is a view and the former is not

public class PreferenceVisualizer {
	
	private ArrayList<Track> requests;
	private final int MAX_CIRCLES = 20;
	private double MAX_RADIUS;
	private int res_x, res_y;
	//private double ar_x, ar_y;		//aspect ratio: <float>:1
	private String sortType;
	private List<PVCircle> circles;
	int totalWeight;
	
	
	public PreferenceVisualizer(ArrayList<Track> requests, int res_x, int res_y)
	{
		this.requests = requests;
		this.res_x = res_x;
		this.res_y = res_y-200;
		this.MAX_RADIUS = (res_x > res_y) ? res_y : res_x;
		circles = new ArrayList<PVCircle>();
	}
	
	
	private void sortByWeight()
	{
		//sort all circles by their weight
		for(int i=1; i<circles.size(); i++)
		{
			PVCircle temp = circles.get(i);
			int thisWeight = circles.get(i).getWeight();
			int j;
			for(j=i-1; j>=0; j--)
			{
				int compWeight = circles.get(j).getWeight();
				if(thisWeight < compWeight)
					break;
				else
					circles.set(j+1, circles.get(j));
			}
			circles.set(j+1, temp);
		}
		
	}
	
	private void initCircles_()
	{
		circles.clear();
		for(int i=0; i<requests.size(); i++)
		{
			//Log.d("initCircles", "processing request " + i);
			//later
			String artistName = requests.get(i).mArtist;
			String trackName = requests.get(i).mTrack;
			String albumName = requests.get(i).mAlbum;
			String uri = requests.get(i).mUri;
			String requester = requests.get(i).mRequester;
			
			Track track = requests.get(i);
			
			boolean exists = false;
			//add track to appropriate circle
			for(int j=0; j<circles.size(); j++)
			{
				if(sortType.equals("artist") && circles.get(j).getName().equals(artistName))
				{
					exists = true;
					circles.get(j).getTrackList().add(track);
				}
				else if(sortType.equals("album") && circles.get(j).getName().equals(albumName))
				{
					exists = true;
					circles.get(j).getTrackList().add(track);
				}
				else if(sortType.equals("track") && circles.get(j).getName().equals(trackName))
				{
					exists = true;
					circles.get(j).getTrackList().add(track);
				}
			}
			
			//if circle didn't exist, then make a new one
			if(!exists)
			{
				PVCircle circle = new PVCircle();
				if(sortType.equals("artist"))
				{
					circle.setName(artistName);
					circle.getTrackList().add(track);
				}
				else if(sortType.equals("album"))
				{
					circle.setName(albumName);
					circle.getTrackList().add(track);
				}
				else if(sortType.equals("track"))
				{
					circle.setName(trackName);
					circle.getTrackList().add(track);
				}
				circles.add(circle);
			}
		}
		
		//after all tracks allocated, get weights and set colors!
		totalWeight = 0;
		Random rng = new Random();
		for(int i=0; i<circles.size(); i++)
		{
			//get individual weight
			//count num unique requesters
			Set<String> uniqueRequesters = new HashSet<String>();
			for(int j=0; j<circles.get(i).getTrackList().size(); j++)
			{
				uniqueRequesters.add(circles.get(i).getTrackList().get(j).mRequester);
			}
			//weight = unique requesters * unique tracks
			int weight = uniqueRequesters.size() * circles.get(i).getTrackList().size();
			totalWeight += weight;
			circles.get(i).setWeight(weight);
			circles.get(i).setScale(res_y);
			//randomly set color
			switch(rng.nextInt(5))
			{
			case 0: circles.get(i).setColor(Color.rgb(203, 32, 38)); break;
			case 1: circles.get(i).setColor(Color.rgb(100, 153, 64)); break;
			case 2: circles.get(i).setColor(Color.rgb(0, 153, 204)); break;
			case 3: circles.get(i).setColor(Color.rgb(155, 105, 172)); break;
			case 4: circles.get(i).setColor(Color.rgb(245, 146, 30)); break;
			}
		}
		
		//set our radii
		for(int i=0; i<circles.size(); i++)
		{
			double percentage = (double) circles.get(i).getWeight() / (double) totalWeight;
			int areaAvailable = (int) ((0.3) * (res_x * res_y));
			double radius = (int) Math.sqrt((areaAvailable * percentage)/Math.PI);
			circles.get(i).setRadius(radius);
		}
		
		//sort our circles by weight
		sortByWeight();
	}
	
	private boolean checkCollision(PVCircle circle, int maxIndx, int indx)
	{
		boolean collision = false;
		if((circle.getX() - circle.getRadius()) < 0 || (circle.getX() + circle.getRadius()) > res_x)
			collision = true;
		else if((circle.getY() - circle.getRadius()) < 0 || (circle.getY() + circle.getRadius()) > res_y)
			collision = true;
		else
		{
			//check against each circle
			for(int i=0; i<maxIndx; i++)
			{
				PVCircle nextCircle = circles.get(i);
				if(i == indx) continue;
				double distance = getDistance(circle, nextCircle);
				if(distance < (nextCircle.getRadius() + circle.getRadius()))
				{
					collision = true;
					break;
				}
			}
		}
		return collision;
	}
	
	private double getDistance(PVCircle c1, PVCircle c2)
	{
		double distance = Math.sqrt(Math.pow(c1.getX() - c2.getX(), 2.0) + Math.pow(c1.getY() - c2.getY(), 2.0));
		return distance;
	}
	
	
	
	private void positionCircles_()
	{
		//use iterator to be able to remove
		Iterator<PVCircle> it = circles.iterator();
		int indx = 0;
		Random rng = new Random();
		while (it.hasNext())
		{	
			PVCircle currCircle = it.next();
			//currCircle.setRadius(MAX_RADIUS * (double) currCircle.getWeight() / totalWeight);
			boolean collision = true;
			int loopCount = 0;
			while(collision && loopCount < 100)
			{
				currCircle.setPosition(res_x * rng.nextDouble(), res_y * rng.nextDouble());
				collision = checkCollision(currCircle, indx, indx);
				loopCount++;
			}
			if(loopCount == 100)
			{
				//Log.d("position", "Circle too big to fit");
				it.remove();
			}
			else indx++;
		}
	}
	
	public void sortByArtist()
	{
		sortType = "artist";
		initCircles_();
		positionCircles_();
	}
	
	public void sortByTrack()
	{
		sortType = "track";
		initCircles_();
		positionCircles_();
	}
	
	public void sortByAlbum()
	{
		sortType = "album";
		initCircles_();
		positionCircles_();
	}
	
	public void sortByGenre()
	{
		sortType = "genre";
		initCircles_();
		positionCircles_();
	}
	
	public List<PVCircle> getCircles()
	{
		return circles;
	}
	
}
