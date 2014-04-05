package com.lsu.vizeq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import com.lsu.vizeq.R.color;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class PreferenceVisualizationActivity extends Activity
{
	ArrayList<Track> requests = new ArrayList<Track>();
	ArrayList<Artist> requestedArtists = new ArrayList<Artist>();
	static final float PERCENT_WHITESPACE = 0.65f;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference_visualization);
		int circleRadius = 100;
		//PreferenceCircle pc = new PreferenceCircle(this, circleRadius, circleRadius, circleRadius, "hi");
		LinearLayout circleScreen = (LinearLayout) this.findViewById(R.id.CircleScreen);
		//circleScreen.addView(pc, circleRadius*2, circleRadius*2); 
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
		switch (VizEQ.numRand)
		{
			case 0:;
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));
				break;
			case 1:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));
				break;
			case 2:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));
				break;
			case 3:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));
				break;
			case 4:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
				break;
		}
		
		//Make some fake request data
		//3 requests (for 1 artist) by 2 people
		requests.add(new Track("Track A", "", "Artist A", "", "Person A"));
		requests.add(new Track("Track B", "", "Artist A", "", "Person A"));
		requests.add(new Track("Track A", "", "Artist A", "", "Person B"));
		
		//4 requests by 1 person
		requests.add(new Track("Track C", "", "Artist B", "", "Person C"));
		requests.add(new Track("Track D", "", "Artist B", "", "Person C"));
		requests.add(new Track("Track E", "", "Artist B", "", "Person C"));
		requests.add(new Track("Track F", "", "Artist B", "", "Person C"));
		
		//11 requests by 9 people
		requests.add(new Track("Track G", "", "Artist C", "", "Person A"));
		requests.add(new Track("Track G", "", "Artist C", "", "Person B"));
		//requests.add(new Track("Track H", "", "Artist C", "", "Person C"));
		//requests.add(new Track("Track I", "", "Artist C", "", "Person D"));
		//requests.add(new Track("Track J", "", "Artist C", "", "Person E"));
		//requests.add(new Track("Track K", "", "Artist C", "", "Person F"));
		//requests.add(new Track("Track L", "", "Artist C", "", "Person G"));
		//requests.add(new Track("Track L", "", "Artist C", "", "Person H"));
		//requests.add(new Track("Track M", "", "Artist C", "", "Person I"));
		//requests.add(new Track("Track N", "", "Artist C", "", "Person I"));
		//requests.add(new Track("Track O", "", "Artist C", "", "Person I"));
		
		//5 requests by 5 people
		requests.add(new Track("Track P", "", "Artist D", "", "Person A"));
		requests.add(new Track("Track Q", "", "Artist D", "", "Person B"));
		requests.add(new Track("Track Q", "", "Artist D", "", "Person C"));
		requests.add(new Track("Track P", "", "Artist D", "", "Person D"));
		requests.add(new Track("Track Q", "", "Artist D", "", "Person E"));
		
		//7 requests by 6 people
		requests.add(new Track("Track R", "", "Artist E", "", "Person I"));
		requests.add(new Track("Track R", "", "Artist E", "", "Person J"));
		requests.add(new Track("Track S", "", "Artist E", "", "Person K"));
		requests.add(new Track("Track T", "", "Artist E", "", "Person L"));
		requests.add(new Track("Track T", "", "Artist E", "", "Person M"));
		requests.add(new Track("Track U", "", "Artist E", "", "Person M"));
		requests.add(new Track("Track T", "", "Artist E", "", "Person N"));
		
		//1 by 1
		requests.add(new Track("Track V", "", "Artist F", "", "Person O"));
		
		Arrays.sort(requests.toArray(), new Comparator<Object>()
		{
			@Override
			public int compare(Object lhs, Object rhs)
			{
				int comparison = ((Track)(rhs)).mArtist.compareTo(((Track)(lhs)).mArtist);
				if (comparison > 0) return 1;
				else if (comparison < 0) return -1;
				return 0;
			}
		});
		
		//Get an artist weight
		String lastArtist = "";
		String lastPerson = "";
		String lastTrack = "";
		int artistCount = 0;
		int personCount = 0;
		int trackCount = 0;
		if (requests.size() > 0) 
		{
			lastArtist = requests.get(0).mArtist;
			lastPerson = requests.get(0).mRequester;
			lastTrack = requests.get(0).mTrack;
			artistCount++;
			personCount++;
			trackCount++;
		}
		int totalWeights = 0;
		Artist artist = new Artist();
		artist.requestTrack(requests.get(0));
		for (int i = 1; i < requests.size(); i++)
		{
			if (lastArtist.compareTo(requests.get(i).mArtist)==0)
			{
				artistCount++;
				lastArtist = requests.get(i).mArtist;
				artist.requestTrack(requests.get(i));
				if (requests.get(i).mRequester.compareTo(lastPerson)!=0)
				{
					personCount++;
					lastPerson = requests.get(i).mRequester;
				}				
				
				if (i==requests.size()-1)
				{
					artist.saveNameAndStats(requests.get(i-1).mArtist, artistCount, personCount);
					this.requestedArtists.add(artist);
					
					totalWeights += artistCount * personCount;
					//lastPerson = requests.get(i).mRequester;
					//artistCount = 1;
					//personCount = 1;
				}
			}
			else
			{
				//Save artist and person count
				artist.saveNameAndStats(requests.get(i-1).mArtist, artistCount, personCount);
				this.requestedArtists.add(artist);
				
				artist = new Artist();
				artist.requestTrack(requests.get(i));

				totalWeights += artistCount * personCount;
				lastArtist = requests.get(i).mArtist;
				lastPerson = requests.get(i).mRequester;
				artistCount = 1;
				personCount = 1;
				
				//For the final one, if it's different
				if (i==requests.size()-1)
				{
					artist.saveNameAndStats(requests.get(i).mArtist, artistCount, personCount);
					this.requestedArtists.add(artist);
					//this.requestedArtists.add(new Artist(requests.get(i).mArtist, requests.get(i), artistCount, personCount));
				}
			}
		}
		
		//Convert to percentage
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;
		int AA = 0;
		int pixelRadius = 0; 
		int numCirclesToDraw = 0;
		PreferenceCircle circles[] = new PreferenceCircle[15];
		for (int i = 0; i < requestedArtists.size(); i++)
		{
			Artist tempArtist = requestedArtists.get(i);
			tempArtist.mPercentage = ((float)tempArtist.mArtistWeight)/totalWeights;
			requestedArtists.set(i, tempArtist);
			AA = (int) ((width*height) - ((PERCENT_WHITESPACE)*(width*height))); //Area available
			pixelRadius = (int) Math.sqrt((AA * tempArtist.mPercentage)/Math.PI);
			
			circles[i] = new PreferenceCircle(this, pixelRadius, pixelRadius, pixelRadius, tempArtist.mArtist);
			numCirclesToDraw++;
			//circleScreen.addView(myCanvas, width, height);
		}
		
		MyCanvas myCanvas = new MyCanvas(this, circles, numCirclesToDraw);
		circleScreen.addView(myCanvas, width, height);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preference_visualization, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent nextIntent  = new Intent(PreferenceVisualizationActivity.this, ProfileActivity.class);
			startActivity(nextIntent);
		}
		return true;
	}
}
