package com.lsu.vizeq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class VizEQ extends Activity
{
	static int numRand;
	
	static int colorScheme;
	
	public static String nowPlaying = "nothing playing!";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
//		Log.d("Flow", "onCreate VizEQ");
		//Makes volume buttons control music stream even when nothing playing
		setVolumeControlStream(AudioManager.STREAM_MUSIC); 
		Random r = new Random();		
		
		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		int posi = memory.getInt("colorPos", -1);
		
		// SET COLORS FROM PREFERENCE HERE 
		// Code is really jank, sorry guys! Basically posi gets the savedPreference value of the index of the color from the string-array color_spinner in colors.xml and uses the old numRand method to assigning those colors
		// BUG#1 Changing colors only effects screens during onCreate (which is only called when the screen is pulled up again)
		if (posi != -1) numRand = posi;
		if (posi <= 0) numRand = r.nextInt(5) + 1;

		switch (numRand)
		{
			case 1:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));
				colorScheme = getResources().getColor(R.color.Red);
				break;
			case 2:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));		
				colorScheme = getResources().getColor(R.color.Green);
				break;
			case 3:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));
				colorScheme = getResources().getColor(R.color.Blue);
				break;
			case 4:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));	
				colorScheme = getResources().getColor(R.color.Purple);
				break;
			case 5:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
				colorScheme = getResources().getColor(R.color.Orange);
				break;			
		}						
		
		//load anything if necessary
		
		Thread splashThread = new Thread()
		{
			 public void run() { 
					 try
					 {
						MediaPlayer mediaPlayer = MediaPlayer.create(VizEQ.this, R.raw.vizeqintro);
						AudioManager audio = (AudioManager) VizEQ.this.getSystemService(VizEQ.this.AUDIO_SERVICE);
						if (audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) mediaPlayer.start(); // no need to call prepare(); create() does that for you
						sleep(mediaPlayer.getDuration()); //length of track
						mediaPlayer.release();
					 }
					 catch (Exception e)
					 {
//						 Log.d("Activity", "Login activity not started.");
					 }
					 finally
					 {
						 
						 finish();
						 Intent nextIntent = new Intent(VizEQ.this, RoleActivity.class);
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
			Intent nextIntent  = new Intent(VizEQ.this, ProfileActivity.class);
			startActivity(nextIntent);
			break;
		case R.id.about:
			Intent nextIntent2 = new Intent(VizEQ.this, AboutActivity.class);
			startActivity(nextIntent2);
			break;
		}
		return true;
	}
}
