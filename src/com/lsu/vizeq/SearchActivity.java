package com.lsu.vizeq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

public class SearchActivity extends Activity
{
	AsyncHttpClient searchClient = new AsyncHttpClient();
	WebService web = new WebService("");
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		final LinearLayout searchLayout = (LinearLayout) findViewById(R.id.SearchLayout);
		final EditText searchText = (EditText) findViewById(R.id.SearchField);
		
		
		
		findViewById(R.id.SearchOK).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
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
								String uri = tracks.getJSONObject(i).getString("href");
								Log.d("Search", trackName + ": " + uri);
								TableRow tableRowToAdd = new TableRow(SearchActivity.this);
								TextView textViewToAdd = new TextView(SearchActivity.this);
								textViewToAdd.setText(trackName);
								tableRowToAdd.addView(textViewToAdd);
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
