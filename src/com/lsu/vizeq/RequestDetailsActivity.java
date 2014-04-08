package com.lsu.vizeq;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class RequestDetailsActivity extends Activity
{
	Artist artist;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_details);
			
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    String value = extras.getString("whichArtist");
	    PreferenceCircle thisCircle = PreferenceVisualizationActivity.circles[Integer.parseInt(value)];
	    artist = thisCircle.artist;
	    // Show the Up button in the action bar.
	    setupActionBar();
	    TextView info = ((TextView) findViewById(R.id.requestInfo));
	    info.setText(artist.mNumTrackRequests + " track requests by " + artist.mNumPeopleRequestingArtist + " different people.");
	    ScrollView list = (ScrollView) findViewById(R.id.trackRequests);
	    list.setBackgroundColor(thisCircle.color);
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar()
	{

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("Requests for " + artist.mArtist);

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
		}
		return super.onOptionsItemSelected(item);
	}

}
