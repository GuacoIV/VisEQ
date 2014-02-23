package com.lsu.vizeq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

public class SearchActivity extends Activity
{
	LinearLayout searchLayout;

	AsyncHttpClient searchClient = new AsyncHttpClient();
	WebService web = new WebService("");
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		searchLayout = (LinearLayout) findViewById(R.id.SearchLayout);
		final EditText searchText = (EditText) findViewById(R.id.SearchField);
		final OnTouchListener rowTap;
		
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
				if (arg1.getAction() == MotionEvent.ACTION_UP)
				{					
					TrackRow row = (TrackRow)arg0;
					row.setBackgroundColor(row.originalColor);
					return true;
				}
				if (arg1.getAction() == MotionEvent.ACTION_CANCEL)
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
								//Log.d("Search", trackName + ": " + trackArtist);
								TrackRow tableRowToAdd = new TrackRow(SearchActivity.this);
								TextView textViewToAdd = new TextView(SearchActivity.this);
								TextView textTwoViewToAdd = new TextView(SearchActivity.this);
								tableRowToAdd.mTrack = trackName;
								tableRowToAdd.mArtist = trackArtist;
								tableRowToAdd.mUri = uri;
								if (i % 2 == 0) 
								{
									tableRowToAdd.setBackgroundColor(Color.rgb(200, 200, 200));
									tableRowToAdd.originalColor = Color.rgb(200, 200, 200);
								}
								else
								{
									tableRowToAdd.setBackgroundColor(Color.WHITE);
									tableRowToAdd.originalColor = Color.WHITE;
								}
								textViewToAdd.setText(trackName);
								textTwoViewToAdd.setText(trackArtist);
								textViewToAdd.setTextSize(20);
								textTwoViewToAdd.setTextColor(Color.DKGRAY);
								LinearLayout linearLayoutToAdd = new LinearLayout(SearchActivity.this);
								linearLayoutToAdd.setOrientation(LinearLayout.VERTICAL);
								linearLayoutToAdd.addView(textViewToAdd);
								linearLayoutToAdd.addView(textTwoViewToAdd);
								tableRowToAdd.setOnTouchListener(rowTap);
								tableRowToAdd.addView(linearLayoutToAdd);
								searchLayout.addView(tableRowToAdd);
								
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

}
