package com.lsu.viseq;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class SplashScreen extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
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
					 }
		        }
		};
		splashThread.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
