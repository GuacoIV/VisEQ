package com.lsu.vizeq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.graphics.Color;
import android.util.Log;

//pure data with no view components.
//takes care of all the logic for creating/packing circles
//includes ways to visualize according to artist, album, track, and genre

//keeps track of Circles instead of PreferenceCircles - the difference being that the latter is a view and the former is not

public class PreferenceVisualizer {
	
	private ArrayList<Track> requests;
	private Map<String, Integer> artists;
	private Map<String, Integer> albums;
	private Map<String, Integer> tracks;
	private Map<String, Integer> genres;
	private List<String> circleNames;		//max size is 10
	private final int MAX_CIRCLES = 20;
	private double MAX_RADIUS;
	private int res_x, res_y;
	private double ar_x, ar_y;		//aspect ratio: <float>:1
	private String sortType;
	private List<PVCircle> circles;
	
	
	public PreferenceVisualizer(ArrayList<Track> requests, int res_x, int res_y)
	{
		this.requests = requests;
		this.res_x = res_y;
		this.res_y = res_y-200;
		this.ar_x = (double) res_x / this.res_y;
		this.ar_y = 1.0;
		this.MAX_RADIUS = ar_x / 6.0;
		artists = new HashMap<String, Integer>();
		albums = new HashMap<String, Integer>();
		tracks = new HashMap<String, Integer>();
		genres = new HashMap<String, Integer>();
		circleNames = new ArrayList<String>();
		circles = new ArrayList<PVCircle>();
	}
	
	public void countStats()
	{
		for(int i=0; i<requests.size(); i++)
		{
			String album = requests.get(i).mAlbum;
			String artist = requests.get(i).mArtist;
			String track = requests.get(i).mTrack;
			
			if(albums.get(album) == null) albums.put(album, 1);
			else
			{
				Integer count = albums.get(album) + 1;
				albums.put(album, count);
			}
			
			if(artists.get(artist) == null) artists.put(artist, 1);
			else
			{
				Integer count = artists.get(artist) + 1;
				artists.put(artist, count);
			}
			
			if(tracks.get(track) == null) tracks.put(track, 1);
			else
			{
				Integer count = tracks.get(track) + 1;
				tracks.put(track, count);
			}
		}
	}
	
	public void init()
	{
		countStats();
	}
	
	private void logCircleNames(Map<String,Integer> countMap)
	{
		String n0 = circleNames.get(0);
		String n1 = circleNames.get(1);
		String n2 = circleNames.get(2);
		String n3 = circleNames.get(3);
		String n4 = circleNames.get(4);
		String n5 = circleNames.get(5);
		String n6 = circleNames.get(6);
		String n7 = circleNames.get(7);
		String n8 = circleNames.get(8);
		String n9 = circleNames.get(9);
		
//		Log.d("sorted", n0 + ", " + n1 + ", " + n2 + ", " + n3 + ", " + n4 + ", " + n5 + ", " + n6 + ", " + n7 + ", " + n8 + ", " + n9);
//		Log.d("sorted", countMap.get(n0) + ", " + countMap.get(n1) + ", " + countMap.get(n2) + ", " + countMap.get(n3) + ", " + countMap.get(n4) + ", " + countMap.get(n5) + ", " + countMap.get(n6) + ", " + countMap.get(n7) + ", " + countMap.get(n8) + ", " + countMap.get(n9));
	}
	
	private void useMap(Map<String, Integer> countMap)
	{
		if(circleNames != null) circleNames.clear();
		if(circles != null) circles.clear();
		Iterator< Entry<String, Integer> > it = countMap.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, Integer> curr_entry = it.next();
//			Log.d("useMap", curr_entry.getKey());
			circleNames.add(curr_entry.getKey());
		}
		

		//good ole insertion sort
		String key;
		for(int i=1; i<circleNames.size(); i++)
		{
			key = circleNames.get(i);
			int j;
			for(j=i-1; j>=0; j--)
			{
				String compkey = circleNames.get(j);
				if(countMap.get(compkey) > countMap.get(key))
					break;
				else
					circleNames.set(j+1, circleNames.get(j));
			}
			circleNames.set(j+1, key);

		}
			
			//trim if too many circles
		if(circleNames.size() > MAX_CIRCLES)
		{
			circleNames = circleNames.subList(0, MAX_CIRCLES);
			logCircleNames(countMap);
		}
		
	}
	
	private boolean checkCollision(PVCircle circle, int maxIndx, int indx)
	{
		boolean collision = false;
		if((circle.getX() - circle.getRadius()) < -0.002 || (circle.getX() + circle.getRadius()) > ar_x + 0.002)
			collision = true;
		else if((circle.getY() - circle.getRadius()) < 0.002 || (circle.getY() + circle.getRadius()) > ar_y + 0.002)
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
	
	
	private PVCircle getClosestNeighbor(PVCircle circle)
	{
		PVCircle nCircle = circles.get(0);
		double closest_distance = -1;
		for(int i=0; i< circles.size(); i++)
		{
			if(circle.equals(circles.get(i))) continue;
			
			double distance = getDistance(circle, circles.get(i));
			if( (closest_distance == -1) || (distance < closest_distance) )
			{
				closest_distance = distance;
				nCircle = circles.get(i);
			}
		}
		
		return nCircle;
	}
	
	
	//method one
	//problem: they tend to form little circle islands
	public void packCircles()
	{
		//run for 10 steps
		double stepSize = 1.0;
		double minStepSize = 0.00001;
		for(int i=0; i<10; i++)
		{
			boolean moved = false;
			do
			{
				moved = false;
				for(int j=0; j<circles.size(); j++)
				{
					//get closest neighbor
					PVCircle closestNeighbor, hypotheticalCircle;
					double hypotheticalDistance, currentDistance;
					closestNeighbor = getClosestNeighbor(circles.get(j));
					currentDistance = getDistance(circles.get(j), closestNeighbor);
					hypotheticalCircle = new PVCircle();
					
					//what if we move north?
					hypotheticalCircle.setPosition(circles.get(j).getX(), circles.get(j).getY() - stepSize);
					hypotheticalCircle.setRadius(circles.get(j).getRadius());
					hypotheticalDistance = getDistance(hypotheticalCircle, closestNeighbor);
					
					if(hypotheticalDistance < currentDistance && !checkCollision(hypotheticalCircle, circles.size(), j))
					{
						//move north
						circles.get(j).setPosition(circles.get(j).getX(), circles.get(j).getY() - stepSize);
						
						//get new next neighbor
						closestNeighbor = getClosestNeighbor(circles.get(j));
						currentDistance = getDistance(circles.get(j), closestNeighbor);
						moved = true;
					}
					
					//what if we move east?
					hypotheticalCircle.setPosition(circles.get(j).getX() + stepSize, circles.get(j).getY());
					hypotheticalCircle.setRadius(circles.get(j).getRadius());
					hypotheticalDistance = getDistance(hypotheticalCircle, closestNeighbor);
					
					if(hypotheticalDistance < currentDistance && !checkCollision(hypotheticalCircle, circles.size(), j))
					{
						//move east
						circles.get(j).setPosition(circles.get(j).getX() + stepSize, circles.get(j).getY());
						
						//get new next neighbor
						closestNeighbor = getClosestNeighbor(circles.get(j));
						currentDistance = getDistance(circles.get(j), closestNeighbor);
						moved = true;
					}
					
					//what if we move south?
					hypotheticalCircle.setPosition(circles.get(j).getX(), circles.get(j).getY() + stepSize);
					hypotheticalCircle.setRadius(circles.get(j).getRadius());
					hypotheticalDistance = getDistance(hypotheticalCircle, closestNeighbor);
					
					if(hypotheticalDistance < currentDistance && !checkCollision(hypotheticalCircle, circles.size(), j))
					{
						//move south
						circles.get(j).setPosition(circles.get(j).getX(), circles.get(j).getY() + stepSize);
						
						//get new next neighbor
						closestNeighbor = getClosestNeighbor(circles.get(j));
						currentDistance = getDistance(circles.get(j), closestNeighbor);
						moved = true;
					}
					
					//what if we move west?
					hypotheticalCircle.setPosition(circles.get(j).getX() - stepSize, circles.get(j).getY());
					hypotheticalCircle.setRadius(circles.get(j).getRadius());
					hypotheticalDistance = getDistance(hypotheticalCircle, closestNeighbor);
					
					if(hypotheticalDistance < currentDistance && !checkCollision(hypotheticalCircle, circles.size(), j))
					{
						//move west
						circles.get(j).setPosition(circles.get(j).getX() - stepSize, circles.get(j).getY());
						
						//get new next neighbor
						closestNeighbor = getClosestNeighbor(circles.get(j));
						currentDistance = getDistance(circles.get(j), closestNeighbor);
						moved = true;
					}
				}
			} while (moved);
			//decrease stepSize
			stepSize /= 2.0;
		}
	}
	
	//method 2
	//problem: circles tend to form a line. still better than method 1
	public void packCircles2()
	{
		//if no circles, then screw it
		if(circles.isEmpty()) return;
		//find center-most circle
		PVCircle mid = new PVCircle();
		mid.setPosition(2.0, 4.0);
		PVCircle centerCircle = circles.get(0);
		double centerDistance = getDistance(mid, centerCircle);
		for(int i=1; i<circles.size(); i++)
		{
			double d = getDistance(mid, circles.get(i));
			if(d < centerDistance)
			{
				centerCircle = circles.get(0);
				centerDistance = d;
			}
		}
		
		//try and move everything closer and closer to center circle
		double stepSize = 1.0;
		for(int i=0; i<10; i++)
		{
			boolean moved;
			do
			{
				moved = false;
				for(int j=0; j<circles.size(); j++)
				{
					if(circles.get(j).equals(centerCircle)) continue;
					
					double dirx = centerCircle.getX() - circles.get(j).getX();
					double diry = centerCircle.getY() - circles.get(j).getY();
					
					//normalize
					double d = getDistance(circles.get(j), centerCircle);
					dirx /= d;
					diry /= d;
					
					//lets see if we can make this move
					PVCircle hypotheticalCircle = new PVCircle();
					hypotheticalCircle.setRadius(circles.get(j).getRadius());
					hypotheticalCircle.setPosition(circles.get(j).getX() + dirx * stepSize, circles.get(j).getY() + diry * stepSize);
					
					if(!checkCollision(hypotheticalCircle, circles.size(), j))
					{
						circles.get(j).setPosition(circles.get(j).getX() + dirx * stepSize, circles.get(j).getY() + diry * stepSize);
						moved = true;
					}
				}
			} while(moved);
			stepSize /= 2.0;
		}
		
	}
	
	
	private void initCircles(Map<String, Integer> countMap)
	{
//		Log.d("initCircles", "entering");
		int maxCount = 0;
		Random rng = new Random();

		//pass #1 to set simple info and get maxCount
//		Log.d("initCircles", "pass 1");
		for(int i=0; i<circleNames.size(); i++)
		{
			int count = countMap.get(circleNames.get(i));
			if (count > maxCount)
				maxCount = count;
			PVCircle circle = new PVCircle();
			circle.setWeight(count);
			circle.setScale(res_y);
			circle.setName(circleNames.get(i));
			//randomly set color
			switch(rng.nextInt(5))
			{
			case 0: circle.setColor(Color.rgb(203, 32, 38)); break;
			case 1: circle.setColor(Color.rgb(100, 153, 64)); break;
			case 2: circle.setColor(Color.rgb(0, 153, 204)); break;
			case 3: circle.setColor(Color.rgb(155, 105, 172)); break;
			case 4: circle.setColor(Color.rgb(245, 146, 30)); break;
			}
			
			circles.add(circle);
		}
		
		//pass #2 to normalize radii and set random position
//		Log.d("initCircles", "pass 2");
		
		//use iterator to be able to remove
		Iterator<PVCircle> it = circles.iterator();
		int indx = 0;
		while (it.hasNext())
		{	
			PVCircle currCircle = it.next();
			currCircle.setRadius(MAX_RADIUS * (double) currCircle.getWeight() / maxCount);
			boolean collision = true;
			int loopCount = 0;
			while(collision && loopCount < 100)
			{
				currCircle.setPosition(ar_x*rng.nextDouble(), ar_y*rng.nextDouble());
				collision = checkCollision(currCircle, indx, indx);
				loopCount++;
			}
			if(loopCount == 100)
			{
				it.remove();
			}
			else indx++;
		}
		
		//final pass to set tracks for each circle
//		Log.d("initCircles", "pass 3");
		for(int i=0; i<requests.size(); i++)
		{
			//later
			String artistName = requests.get(i).mArtist;
			String trackName = requests.get(i).mTrack;
			String albumName = requests.get(i).mAlbum;
			
			Track track = requests.get(i);
			
			for(int j=0; j<circles.size(); j++)
			{
				if(sortType.equals("artist") && circles.get(j).getName().equals(artistName))
				{
					if(!circles.get(j).getTrackList().contains(track))
						circles.get(j).getTrackList().add(track);
				}
				else if(sortType.equals("album") && circles.get(j).getName().equals(albumName))
				{
					if(!circles.get(j).getTrackList().contains(track))
						circles.get(j).getTrackList().add(track);
				}
				else if(sortType.equals("track") && circles.get(j).getName().equals(trackName))
				{
					if(!circles.get(j).getTrackList().contains(track))
						circles.get(j).getTrackList().add(track);
				}
			}
			
		}
	}
	
	public void sortByArtist()
	{
		useMap(artists);
		sortType = "artist";
		initCircles(artists);
	}
	
	public void sortByTrack()
	{
		useMap(tracks);
		sortType = "track";
		initCircles(tracks);
	}
	
	public void sortByAlbum()
	{
		useMap(albums);
		sortType = "album";
		initCircles(albums);
	}
	
	public void sortByGenre()
	{
		useMap(genres);
		sortType = "genre";
		initCircles(genres);
	}
	
	public List<PVCircle> getCircles()
	{
		return circles;
	}
	
}
