package com.lsu.vizeq;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RoleActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_role);
		Typeface font = Typeface.createFromAsset(getAssets(), "Mohave.otf");
		Typeface orFont = Typeface.createFromAsset(getAssets(), "Mission Gothic Bold.otf");
		TextView DJText = (TextView) findViewById(R.id.DJText);
		TextView notADJText = (TextView) findViewById(R.id.NotADJText);
		TextView orText = (TextView) findViewById(R.id.Or);
		DJText.setTypeface(font);
		notADJText.setTypeface(font);
		orText.setTypeface(orFont);
		DJText.setTextSize(40); //40 pt 153, 153, 153
		DJText.setTextColor(Color.rgb(153, 153, 153));
		notADJText.setTextSize(40); //40 pt
		notADJText.setTextColor(Color.rgb(153, 153, 153));
		orText.setTextSize(27);//27 gothic bold //51, 51, 51 //diameter is 140
		orText.setTextColor(Color.rgb(51, 51, 51));
		findViewById(R.id.DJ).setOnClickListener(new View.OnClickListener() 
		{
				@Override
				public void onClick(View v)
				{
					Intent nextIntent = new Intent(RoleActivity.this, HostActivity.class);
					startActivity(nextIntent);					
				}

		});
		findViewById(R.id.NotADJ).setOnClickListener(new View.OnClickListener() 
		{
				@Override
				public void onClick(View v)
				{
					Intent nextIntent = new Intent(RoleActivity.this, SoundVisualizationActivity.class);
					startActivity(nextIntent);					
				}

		});
		findViewById(R.id.DJ).setOnTouchListener(new View.OnTouchListener()
		{
			
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				ImageView DJButton = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) DJButton.setImageResource(R.drawable.hostbuttonover_325x325);
				else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) DJButton.setImageResource(R.drawable.hostbutton_325x325);
				return false;
			}
		});
		
		findViewById(R.id.NotADJ).setOnTouchListener(new View.OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				float x = event.getX();
				float y = event.getY(); 
				Log.d("Coordinates", ""+x);
				Log.d("Coordinates", ""+y);
				ImageView DJButton = (ImageView) v;
				if (event.getAction() == MotionEvent.ACTION_DOWN) DJButton.setImageResource(R.drawable.joinbuttonover_325x325);
				else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) DJButton.setImageResource(R.drawable.joinbutton_325x325);
				return false;
			}
		});		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.role, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent nextIntent  = new Intent(RoleActivity.this, SettingsActivity.class);
			startActivity(nextIntent);
		}
		return true;
	}	
}
