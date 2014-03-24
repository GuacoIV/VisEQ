package com.lsu.vizeq;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.lsu.vizeq.R.color;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableRow;
import android.widget.TextView;

public class SearchActivity extends Activity
{
	LinearLayout searchLayout;
	public static ArrayList<Track> queue;

	AsyncHttpClient searchClient = new AsyncHttpClient();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
		searchLayout = (LinearLayout) findViewById(R.id.SearchLayout);
		final EditText searchText = (EditText) findViewById(R.id.SearchField);
		final OnTouchListener rowTap;
		queue = new ArrayList<Track>();
		TabHost tabhost = (TabHost) findViewById(android.R.id.tabhost);
	    tabhost.setup();
	    
	    TabSpec ts = tabhost.newTabSpec("tag1"); 
	    ts.setContent(R.id.tab1);
	    ts.setIndicator("Search");
	    tabhost.addTab(ts);
	    ts = tabhost.newTabSpec("tag2"); 
	    ts.setContent(R.id.tab2);
	    ts.setIndicator("Queue");  
	    tabhost.addTab(ts);
	    ts= tabhost.newTabSpec("tag3");
	    ts.setContent(R.id.tab3);
	    ts.setIndicator("Third Tab");
	    tabhost.addTab(ts);
	    //for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++)
		//{
			//tabhost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.LightGreen));
		//}
	    final LinearLayout queueTab = (LinearLayout) findViewById(R.id.tab2);
		
		rowTap = new OnTouchListener()
		{

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1)
			{
				if (arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					TableRow row = (TableRow)arg0;
					row.setBackgroundColor(Color.BLUE);
					return true;
				}
				else if (arg1.getAction() == MotionEvent.ACTION_UP)
				{					
					final TrackRow row = (TrackRow)arg0;
					row.setBackgroundColor(row.originalColor);
					AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
			        builder.setMessage(R.string.QueueTopOrBottom)
			               .setPositiveButton("Top", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   queue.add(0, row.getTrack());
			                	   TrackRow queueRow = row;
			                	   queueRow.setOnTouchListener(null);
			                	   searchLayout.removeView(queueRow);
			                	   
			                	   queueTab.addView(queueRow, 0);
								if (queueTab.getChildCount() > 1)
								{
			                		    if (((TrackRow)(queueTab.getChildAt(1))).originalColor == TrackRow.color1)
			                		    {
			                		    	queueRow.setBackgroundColor(TrackRow.color2);
			                		    	queueRow.originalColor = TrackRow.color2;
			                		    }
			                		    else 
			                		    {
			                		    	queueRow.setBackgroundColor(TrackRow.color1);
			                		    	queueRow.originalColor = TrackRow.color1;
			                		    }
								}
								else
								{
									queueRow.setBackgroundColor(TrackRow.color1);
			                	    queueRow.originalColor = TrackRow.color1;
								}
			                	   
			                   }
			               })
			               .setNegativeButton("Bottom", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   queue.add(row.getTrack());
			                	   TrackRow queueRow = row;
			                	   queueRow.setOnTouchListener(null);
			                	   searchLayout.removeView(queueRow);
			                	   queueTab.addView(queueRow);
			                	   if (queueTab.getChildCount() > 0)
									{
				                		    if (((TrackRow)(queueTab.getChildAt(queueTab.getChildCount() - 1))).originalColor == TrackRow.color1)
				                		    	queueRow.setBackgroundColor(TrackRow.color2);
				                		    else 
				                		    {
				                		    	queueRow.setBackgroundColor(TrackRow.color1);
				                		    	queueRow.originalColor = TrackRow.color1;
				                		    }
									}
									else
									{
										queueRow.setBackgroundColor(TrackRow.color1);
				                	    queueRow.originalColor = TrackRow.color1;
									}
			                	   
			                   }
			               });
			        //builder.create();
			        builder.show();
					return true;
				}
				else if (arg1.getAction() == MotionEvent.ACTION_CANCEL)
				{
					TrackRow row = (TrackRow)arg0;
					row.setBackgroundColor(row.originalColor);
					return true;
				}
				return false;
			}
		
		};
		
		
		findViewById(R.id.SearchOK).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				searchLayout.removeAllViews();
				String strSearch = searchText.getText().toString();
				strSearch = strSearch.replace(' ', '+');
				searchText.clearFocus();
				searchClient.get("http://ws.spotify.com/search/1/track.json?q=" + strSearch, new JsonHttpResponseHandler() {

					public void onSuccess(JSONObject response) {
						try {
							JSONArray tracks = response.getJSONArray("tracks");
							//JSONObject track = tracks.getJSONObject(0);
							
							//Calculate start and end colors
							int startColor = getResources().getColor(R.color.Green);
							int endColor = getResources().getColor(R.color.LightGreen);
							
							int redStart = Color.red(startColor);
							int redEnd = Color.red(endColor);
							int addRed = (redEnd - redStart)/20;
							
							int greenStart = Color.green(startColor);
							int greenEnd = Color.green(endColor);
							int addGreen = (greenEnd - greenStart)/20;
							
							int blueStart = Color.blue(startColor);
							int blueEnd = Color.blue(endColor);
							int addBlue = (blueEnd - blueStart)/20;
							
							
							for (int i = 0; i < tracks.length(); i++)
							{
								String trackName = tracks.getJSONObject(i).getString("name");
								String trackArtist = tracks.getJSONObject(i).getJSONArray("artists").getJSONObject(0).getString("name");
								String uri = tracks.getJSONObject(i).getString("href");
								String trackAlbum = tracks.getJSONObject(i).getJSONObject("album").getString("name");
								//Log.d("Search", trackName + ": " + trackArtist);
								TrackRow tableRowToAdd = new TrackRow(SearchActivity.this);
								TextView textViewToAdd = new TextView(SearchActivity.this);
								TextView textTwoViewToAdd = new TextView(SearchActivity.this);
								tableRowToAdd.mTrack = trackName;
								tableRowToAdd.mArtist = trackArtist;
								tableRowToAdd.mAlbum = trackAlbum;
								tableRowToAdd.mUri = uri;
								/*if (i % 2 == 0) 
								{
									tableRowToAdd.setBackgroundColor(TrackRow.color1);
									tableRowToAdd.originalColor = TrackRow.color1;
								}
								else
								{
									tableRowToAdd.setBackgroundColor(TrackRow.color2);
									tableRowToAdd.originalColor = TrackRow.color2;
								}*/
								tableRowToAdd.setBackgroundColor(Color.argb(255, redStart, greenStart, blueStart));
								tableRowToAdd.originalColor = (Color.argb(255, redStart, greenStart, blueStart));
								if (redStart + addRed < 255 && i < 20) redStart += addRed;
								if (greenStart + addGreen < 255 && i < 20) greenStart += addGreen;
								if (blueStart + addBlue < 255 && i < 20) blueStart += addBlue;
								textViewToAdd.setText(trackName);
								textTwoViewToAdd.setText(trackArtist);
								textViewToAdd.setTextSize(20);
								textTwoViewToAdd.setTextColor(Color.DKGRAY);
								LinearLayout linearLayoutToAdd = new LinearLayout(SearchActivity.this);
								linearLayoutToAdd.setOrientation(LinearLayout.VERTICAL);
								//linearLayoutToAdd.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING);
								linearLayoutToAdd.addView(textViewToAdd);
								linearLayoutToAdd.addView(textTwoViewToAdd);
								tableRowToAdd.setOnTouchListener(rowTap);
								tableRowToAdd.addView(linearLayoutToAdd);
								LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
								params.setMargins(0, 3, 0, 3);
								searchLayout.addView(tableRowToAdd, params);
							}
							
							//mAlbumUri = album.getString("spotify");
							//mImageUri = album.getString("image");
							// Now get track details from the webapi
							//LSU Team, it looks like .get(http://ws.spotify.com/search/1/track?q=kaizers+orchestra) is the way to do a search
							//mSpotifyWebClient.get("http://ws.spotify.com/lookup/1/.json?uri=" + album.getString("spotify") + "&extras=track", SpotifyWebResponseHandler);

						} 
						catch (JSONException e) {
							throw new RuntimeException("Could not parse the results");
						}
					}
				});
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent nextIntent  = new Intent(SearchActivity.this, SettingsActivity.class);
			startActivity(nextIntent);
		}
		return true;
	}
}
