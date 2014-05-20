package com.lsu.vizeq;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RequestDetailsActivity extends BackableActivity
{
	public LinearLayout list;
	public MyApplication myapp;
	private String requestName;
	private List<Track> tracks;
	private int color;
	private int numRequesters;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_details);
		myapp = (MyApplication) this.getApplicationContext();

		Bundle extras = getIntent().getExtras();
		if (extras != null) 
		{
			tracks = extras.getParcelableArrayList("tracks");
			requestName = extras.getString("requestName");
			color = extras.getInt("color");
		}
		//artist = thisCircle.name;
		// Show the Up button in the action bar.
		setupActionBar();
		sortTracks();
		refreshList();
	}
	
	//sort tracks by most unique requests
	private void sortTracks()
	{
		
	}

	
	//create/refresh the list
	
	private void refreshList()
	{
		TextView info = ((TextView) findViewById(R.id.requestInfo));
		//get info on number of tracks and requesters
		Set<String> uniqueRequesters = new HashSet<String>();
		for(int i=0; i<tracks.size(); i++)
		{
			for(int j=0; j<tracks.get(i).requesters.size(); j++)
			{
				uniqueRequesters.add(tracks.get(i).requesters.get(j));
			}
		}
		
		int numTracks = tracks.size();
		int numRequesters = uniqueRequesters.size();
		String trackString = (numTracks == 1) ? " track request by " : " track requests by ";
		String requesterString = (numRequesters == 1) ? " person." : " people.";
		
		info.setText(numTracks + trackString + numRequesters + requesterString);
		list = (LinearLayout) findViewById(R.id.trackRequests);
		
		list.removeAllViews();
		//list.setBackgroundColor(thisCircle.color);

		OnTouchListener rowTap = new OnTouchListener()
		{
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1)
			{
				if (arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					TrackRow row = (TrackRow)arg0;
					row.setBackgroundColor(Color.WHITE);
					addToQueue(row);

					return true;
				}
				else if (arg1.getAction() == MotionEvent.ACTION_UP)
				{		
				}
				return true;
			}
		};
		int startColor = color;
		int redStart = Color.red(startColor);
		int greenStart = Color.green(startColor);
		int blueStart = Color.blue(startColor);

		int redEnd = Color.red(startColor);
		int addRed = (redEnd - redStart)/15;

		int greenEnd = Color.green(startColor);
		int addGreen = (greenEnd - greenStart)/15;

		int blueEnd = Color.blue(startColor);
		int addBlue = (blueEnd - blueStart)/15;
		for (int i = 0; i < tracks.size(); i++)
		{
			String trackName = tracks.get(i).mTrack;
			String trackArtist = tracks.get(i).mArtist;
			String trackAlbum = tracks.get(i).mAlbum;
			String trackUri = tracks.get(i).mUri;

			if (redStart + addRed < 255 && i < 16) redStart += addRed;
			if (greenStart + addGreen < 255 && i < 16) greenStart += addGreen;
			if (blueStart + addBlue < 255 && i < 16) blueStart += addBlue;

			TrackRow trackRowToAdd = new TrackRow(this, trackName, trackAlbum, trackArtist, trackUri);

			trackRowToAdd.setBackgroundColor(Color.argb(255, redStart, greenStart, blueStart));
			trackRowToAdd.originalColor = (Color.argb(255, redStart, greenStart, blueStart));


			//textViewToAdd.setText(artist.mTrackRequests.get(i).mTrack);
			//textTwoViewToAdd.setText(artist.mTrackRequests.get(i).mArtist);
			//textViewToAdd.setTextSize(20);
			//textTwoViewToAdd.setTextColor(Color.DKGRAY);
			trackRowToAdd.setOnTouchListener(rowTap);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 2, 0, 2);
			list.addView(trackRowToAdd, params);
		}
	}
	
	
	

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar()
	{
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Requests for " + requestName);
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));	
		
		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		int posi = memory.getInt("colorPos", -1);
		if (posi > 0) VizEQ.numRand = posi;		
		switch (VizEQ.numRand)
		{
			case 1:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));
				break;
			case 2:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));				
				break;
			case 3:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));
				break;
			case 4:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));				
				break;
			case 5:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
				break;			
		}
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.request_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.about:
			Intent nextIntent2  = new Intent(RequestDetailsActivity.this, AboutActivity.class);
			startActivity(nextIntent2);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void addToQueue(final TrackRow row)
	{
		final Track track = row.getTrack();
//		Log.d("addToQueue", "track name = " + track.mTrack);
        //get the track cover
        final Thread getTrackCoverThread = new Thread(new Runnable()
		{
			public void run()
			{
        	    URI url;
				//Get the album art
				try
				{									
					url = new URI("https://embed.spotify.com/oembed/?url=" + track.mUri);
					HttpClient httpClient = new DefaultHttpClient();
					HttpResponse response2 = httpClient.execute(new HttpGet(url));
					HttpEntity entity = response2.getEntity();
					String s = EntityUtils.toString(entity, "UTF-8");
//					Log.d("Get Album Art", "String s = " + s);
					int numThumb = s.indexOf("thumbnail_url");
					String thumbnail = s.substring(numThumb + 16);
					thumbnail = thumbnail.substring(0, thumbnail.indexOf("\""));
					thumbnail = thumbnail.replace("\\", "");
					track.mThumbnail = thumbnail;
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		});
		
		AlertDialog.Builder builder = new AlertDialog.Builder(RequestDetailsActivity.this);
        builder.setMessage(R.string.QueueTopOrBottom)
        	.setPositiveButton("Top", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
//					Log.d("addToQueue", "Adding " + track.mTrack + " to top");	
					if (PlayerActivity.mIndex > 0) 
             	    {
             		   myapp.queue.add(PlayerActivity.mIndex + 1, track);
             	    }
             	    else myapp.queue.add(PlayerActivity.mIndex, track);
					removeRequest(track);
					refreshList();
					getTrackCoverThread.start();
				}
        	})
        	.setNegativeButton("Bottom", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
//					Log.d("addToQueue","Adding " + track.mTrack + " to bottom");
					myapp.queue.add(track);
					removeRequest(track);
					refreshList();
					getTrackCoverThread.start();
				}
        	});
        
        builder.show();
        	
	}
	
	private void removeRequest(Track track)
	{
		//remove from myapp request list
		for(int i=0; i<myapp.requests.size(); i++)
		{
			if(myapp.requests.get(i).mUri.equals(track.mUri))
			{
				myapp.requests.remove(i);
				break;
			}
		}
		
		//remove from our list
		for(int i=0; i<tracks.size(); i++)
		{
			if(tracks.get(i).mUri.equals(track.mUri))
			{
				tracks.remove(i);
				break;
			}
		}
	}

}
