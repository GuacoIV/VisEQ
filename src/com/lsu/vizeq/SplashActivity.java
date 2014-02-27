package com.lsu.vizeq;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class SplashActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		
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
						 Intent nextIntent = new Intent(SplashActivity.this, LoginActivity.class);
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

}
