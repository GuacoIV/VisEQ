package com.lsu.vizeq;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class VizEQ extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
		//Makes volume buttons control music stream even when nothing playing
		setVolumeControlStream(AudioManager.STREAM_MUSIC); 
		
		//load anything if necessary
		
		Thread splashThread = new Thread()
		{
			 public void run() { 
					 try
					 {
						sleep(2000); //2 seconds
			        	startActivity(new Intent("com.lsu.viseq.LOGIN"));
					 }
					 catch (Exception e)
					 {
						 Log.d("Activity", "Login activity not started.");
					 }
					 finally
					 {
						 finish();
						 Intent nextIntent = new Intent(VizEQ.this, LoginActivity.class);
						startActivity(nextIntent);	
					 }
		        }
		};
		splashThread.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu., menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent nextIntent  = new Intent(VizEQ.this, SettingsActivity.class);
			startActivity(nextIntent);
		}
		return true;
	}
}
