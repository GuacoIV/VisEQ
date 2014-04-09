package com.lsu.vizeq;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
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
		if (extras != null) 
		{
		    String value = extras.getString("whichArtist");
		    PreferenceCircle thisCircle = PreferenceVisualizationActivity.circles[Integer.parseInt(value)];
		    artist = thisCircle.artist;
		    // Show the Up button in the action bar.
		    setupActionBar();
		    TextView info = ((TextView) findViewById(R.id.requestInfo));
		    info.setText(artist.mNumTrackRequests + " track requests by " + artist.mNumPeopleRequestingArtist + " different people.");
		    LinearLayout list = (LinearLayout) findViewById(R.id.trackRequests);
		    //list.setBackgroundColor(thisCircle.color);
		    
		    OnTouchListener rowTap = new OnTouchListener()
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
					}
					return true;
				}
			};
			int startColor = thisCircle.color;
			int redStart = Color.red(startColor);
			int greenStart = Color.green(startColor);
			int blueStart = Color.blue(startColor);
			
			int redEnd = Color.red(startColor);
			int addRed = (redEnd - redStart)/15;
			
			int greenEnd = Color.green(startColor);
			int addGreen = (greenEnd - greenStart)/15;
			
			int blueEnd = Color.blue(startColor);
			int addBlue = (blueEnd - blueStart)/15;
		    for (int i = 0; i < artist.mTrackRequests.size(); i++)
		    {
		    	TrackRow tableRowToAdd = new TrackRow(this);
				TextView textViewToAdd = new TextView(this);
				TextView textTwoViewToAdd = new TextView(this);
		    	tableRowToAdd.setBackgroundColor(Color.argb(255, redStart, greenStart, blueStart));
				tableRowToAdd.originalColor = (Color.argb(255, redStart, greenStart, blueStart));
				if (redStart + addRed < 255 && i < 16) redStart += addRed;
				if (greenStart + addGreen < 255 && i < 16) greenStart += addGreen;
				if (blueStart + addBlue < 255 && i < 16) blueStart += addBlue;
				textViewToAdd.setText(artist.mTrackRequests.get(i).mTrack);
				textTwoViewToAdd.setText(artist.mTrackRequests.get(i).mArtist);
				textViewToAdd.setTextSize(20);
				textTwoViewToAdd.setTextColor(Color.DKGRAY);
				LinearLayout linearLayoutToAdd = new LinearLayout(this);
				linearLayoutToAdd.setOrientation(LinearLayout.VERTICAL);
				linearLayoutToAdd.addView(textViewToAdd);
				linearLayoutToAdd.addView(textTwoViewToAdd);
				tableRowToAdd.setOnTouchListener(rowTap);
				tableRowToAdd.addView(linearLayoutToAdd);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 2, 0, 2);
				list.addView(tableRowToAdd, params);
		    }
	    
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
		case R.id.about:
			Intent nextIntent2  = new Intent(RequestDetailsActivity.this, AboutActivity.class);
			startActivity(nextIntent2);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
