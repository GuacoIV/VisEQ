package com.lsu.vizeq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.lsu.vizeq.R.color;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

public class PreferenceVisualizationActivity extends Activity
{
	ArrayList<Track> requests = new ArrayList<Track>();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference_visualization);
		
		int circleRadius = 100;
		PreferenceCircle pc = new PreferenceCircle(this, circleRadius, circleRadius, circleRadius, "hi");
		RelativeLayout circleScreen = (RelativeLayout) this.findViewById(R.id.CircleScreen);
		circleScreen.addView(pc, circleRadius*2, circleRadius*2); 
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
		
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
		requests.add(new Track("Track H", "", "Artist C", "", "Person C"));
		requests.add(new Track("Track I", "", "Artist C", "", "Person D"));
		requests.add(new Track("Track J", "", "Artist C", "", "Person E"));
		requests.add(new Track("Track K", "", "Artist C", "", "Person F"));
		requests.add(new Track("Track L", "", "Artist C", "", "Person G"));
		requests.add(new Track("Track L", "", "Artist C", "", "Person H"));
		requests.add(new Track("Track M", "", "Artist C", "", "Person I"));
		requests.add(new Track("Track N", "", "Artist C", "", "Person I"));
		requests.add(new Track("Track O", "", "Artist C", "", "Person I"));
		
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
		int trackDefaultCount = 5;
		for (int i = 1; i < requests.size(); i++)
		{
			if (lastArtist.compareTo(requests.get(i).mArtist)==0)
			{
				artistCount++;
				if (requests.get(i).mRequester.compareTo(lastPerson)!=0)
					personCount++;
				
				//Trying to do too much at once.  Tracks are not sorted inside the list so this is wrong.
				//if (requests.get(i).mTrack.compareTo(lastTrack)==0)
					//trackCount++;
			}
			else
			{
				//Save artist and person count...somewhere...
				
				//Run through list of track requests for that artist
				//Dummy for now: (5)
				int artistWeight = trackDefaultCount * personCount;
				
			}
		}
		
		
		
		//Convert to percentage
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
			Intent nextIntent  = new Intent(PreferenceVisualizationActivity.this, SettingsActivity.class);
			startActivity(nextIntent);
		}
		return true;
	}
}
