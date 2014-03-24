package com.lsu.vizeq;


import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.os.Process;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableRow;
import android.widget.TextView;



public class ProfileActivity extends Activity {
	
	LinearLayout customSearchLayout;
	public static ArrayList<Track> customList;
	
	/*public class colorAdapter extends BaseAdapter implements SpinnerAdapter{
		//@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent){

		    View v = convertView;

		    if (v == null) {
		        Activity mContext;
				LayoutInflater vi = (LayoutInflater) mContext
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        v = vi.inflate(R.layout.customSearchLayout, null);
		    }
		    TextView tv=(TextView) v.findViewById(R.id.colorspinner);
		    tv.setText(R.array.color_spinner.getItem(position));
		    switch (position) {
		    	case 0:
		    		//set tv's color here...
		    		break;
		    	case 1:
		    		//set tv's color here...
		    		break;
		    	default:
		    		//set default color or whatever...
		    }       
	        return v;
	    }

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return null;
		}
	}
*/	
	
	AsyncHttpClient searchClient = new AsyncHttpClient();
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		customSearchLayout = (LinearLayout) findViewById(R.id.customSearchLayout);
		final EditText searchText = (EditText) findViewById(R.id.CustomSearchField);
		final OnTouchListener rowTap;
		customList = new ArrayList<Track>();
		TabHost tabhost = (TabHost) findViewById(android.R.id.tabhost);
	    tabhost.setup();
	    TabSpec ts = tabhost.newTabSpec("tab01"); 
	    ts.setContent(R.id.tab01);
	    ts.setIndicator("Search");
	    tabhost.addTab(ts);
	
	    ts = tabhost.newTabSpec("tab02"); 
	    ts.setContent(R.id.tab02);
	    ts.setIndicator("Playlist");  
	    tabhost.addTab(ts);
	    
	    ts = tabhost.newTabSpec("tab03");
	    ts.setContent(R.id.tab03);
	    ts.setIndicator("Profile");
	    tabhost.addTab(ts);
	    final LinearLayout customListTab = (LinearLayout) findViewById(R.id.tab02);
	    //Animation an = new Animation();
	    
	    /*// Color Spinner
	    Spinner spinner = (Spinner) findViewById(R.id.colorspinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.color_spinner, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		spinner.setAdapter(adapter);*/	    	    	    
		
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
					AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
			        builder.setMessage(R.string.QueueTopOrBottom)
			               .setPositiveButton("Top", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   customList.add(0, row.getTrack());
			                	   TrackRow customListRow = row;
			                	   customListRow.setOnTouchListener(null);
			                	   customSearchLayout.removeView(customListRow);
			                	   
			                	   customListTab.addView(customListRow, 0);
								if (customListTab.getChildCount() > 1)
								{
			                		    if (((TrackRow)(customListTab.getChildAt(1))).originalColor == TrackRow.color1)
			                		    {
			                		    	customListRow.setBackgroundColor(TrackRow.color2);
			                		    	customListRow.originalColor = TrackRow.color2;
			                		    }
			                		    else 
			                		    {
			                		    	customListRow.setBackgroundColor(TrackRow.color1);
			                		    	customListRow.originalColor = TrackRow.color1;
			                		    }
								}
								else
								{
									customListRow.setBackgroundColor(TrackRow.color1);
			                	    customListRow.originalColor = TrackRow.color1;
								}
			                	   
			                   }
			               })
			               .setNegativeButton("Bottom", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   customList.add(row.getTrack());
			                	   TrackRow customListRow = row;
			                	   customListRow.setOnTouchListener(null);
			                	   customSearchLayout.removeView(customListRow);
			                	   customListTab.addView(customListRow);
			                	   if (customListTab.getChildCount() > 0)
									{
				                		    if (((TrackRow)(customListTab.getChildAt(customListTab.getChildCount() - 1))).originalColor == TrackRow.color1)
				                		    	customListRow.setBackgroundColor(TrackRow.color2);
				                		    else 
				                		    {
				                		    	customListRow.setBackgroundColor(TrackRow.color1);
				                		    	customListRow.originalColor = TrackRow.color1;
				                		    }
									}
									else
									{
										customListRow.setBackgroundColor(TrackRow.color1);
				                	    customListRow.originalColor = TrackRow.color1;
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
				customSearchLayout.removeAllViews();
				String strSearch = searchText.getText().toString();
				strSearch = strSearch.replace(' ', '+');
				searchText.clearFocus();
				searchClient.get("http://ws.spotify.com/search/1/track.json?q=" + strSearch, new JsonHttpResponseHandler() {
	
					public void onSuccess(JSONObject response) {
						try {
							JSONArray tracks = response.getJSONArray("tracks");
							//JSONObject track = tracks.getJSONObject(0);
							for (int i = 0; i < tracks.length(); i++)
							{
								String trackName = tracks.getJSONObject(i).getString("name");
								String trackArtist = tracks.getJSONObject(i).getJSONArray("artists").getJSONObject(0).getString("name");
								String uri = tracks.getJSONObject(i).getString("href");
								String trackAlbum = tracks.getJSONObject(i).getJSONObject("album").getString("name");
								//Log.d("Search", trackName + ": " + trackArtist);
								TrackRow tableRowToAdd = new TrackRow(ProfileActivity.this);
								TextView textViewToAdd = new TextView(ProfileActivity.this);
								TextView textTwoViewToAdd = new TextView(ProfileActivity.this);
								tableRowToAdd.mTrack = trackName;
								tableRowToAdd.mArtist = trackArtist;
								tableRowToAdd.mAlbum = trackAlbum;
								tableRowToAdd.mUri = uri;
								if (i % 2 == 0) 
								{
									tableRowToAdd.setBackgroundColor(TrackRow.color1);
									tableRowToAdd.originalColor = TrackRow.color1;
								}
								else
								{
									tableRowToAdd.setBackgroundColor(TrackRow.color2);
									tableRowToAdd.originalColor = TrackRow.color2;
								}
								textViewToAdd.setText(trackName);
								textTwoViewToAdd.setText(trackArtist);
								textViewToAdd.setTextSize(20);
								textTwoViewToAdd.setTextColor(Color.DKGRAY);
								LinearLayout linearLayoutToAdd = new LinearLayout(ProfileActivity.this);
								linearLayoutToAdd.setOrientation(LinearLayout.VERTICAL);
								linearLayoutToAdd.addView(textViewToAdd);
								linearLayoutToAdd.addView(textTwoViewToAdd);
								tableRowToAdd.setOnTouchListener(rowTap);
								tableRowToAdd.addView(linearLayoutToAdd);
								customSearchLayout.addView(tableRowToAdd);
								
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
}
