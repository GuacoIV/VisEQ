package com.lsu.vizeq;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.lsu.vizeq.R.color;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
	MyApplication myapp;
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
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));

		myapp = (MyApplication) this.getApplicationContext();
		
		findViewById(R.id.DJ).setOnTouchListener(new View.OnTouchListener()
		{
			
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				Point touchPoint = new Point((int)event.getX(), (int)event.getY());
				Point center = new Point(v.getWidth()/2, v.getHeight()/2);
				ImageView DJButton = (ImageView) v;
				boolean isInCircle = isInCircle(touchPoint, center, v.getWidth()/2);
				if (event.getAction() == MotionEvent.ACTION_DOWN && isInCircle) DJButton.setImageResource(R.drawable.hostbuttonover_325x325);
				if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)
				{
					DJButton.setImageResource(R.drawable.hostbutton_325x325);
					if (event.getAction() == MotionEvent.ACTION_UP && isInCircle)
					{
						Intent nextIntent = new Intent(RoleActivity.this, HostActivity.class);
						startActivity(nextIntent);
					}
				}
				return true;
			}
		});
		
		findViewById(R.id.NotADJ).setOnTouchListener(new View.OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				Point touchPoint = new Point((int)event.getX(), (int)event.getY());
				Point center = new Point(v.getWidth()/2, v.getHeight()/2);
				ImageView DJButton = (ImageView) v;
				boolean isInCircle = isInCircle(touchPoint, center, v.getWidth()/2);
				if (event.getAction() == MotionEvent.ACTION_DOWN && isInCircle) DJButton.setImageResource(R.drawable.joinbuttonover_325x325);
				if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)
				{
					DJButton.setImageResource(R.drawable.joinbutton_325x325);
					if (event.getAction() == MotionEvent.ACTION_UP && isInCircle)
					{
						Intent nextIntent;
						if(myapp.joined)
						{
							nextIntent = new Intent(RoleActivity.this, SoundVisualizationActivity.class);
						}
						else
						{
							nextIntent = new Intent(RoleActivity.this, SearchPartyActivity.class);
						}
						startActivity(nextIntent);	
					}
				}
				return true;
			}
		});		
	}
	
	public boolean isInCircle(Point tp, Point c, int radius)
	{
		int diffX = Math.abs(tp.x - c.x);
		int diffY = Math.abs(tp.y - c.y);
		double distFromCenter = Math.sqrt(diffX * diffX + diffY * diffY);
		if (distFromCenter > radius) return false;
		else return true;
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
