package com.lsu.vizeq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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
		
		final Visualizer visualizer = new Visualizer(0);
		if (visualizer.getEnabled()) {
			visualizer.setEnabled(false);
		}
		visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
		Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {

			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate)
			{
				for (int i = 0; i < fft.length; i++)
				{
					if (fft[i] != 0)
						MyApplication.foundSound = true;
				}
			}

			@Override
			public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate)
			{
				for (int i = 0; i < waveform.length; i++)
				{
					if (waveform[i] != -128)
						MyApplication.foundSound = true;
				}
			}
			
		};
		visualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate()/4, true, true);
		visualizer.setEnabled(true);
		
		Thread splashThread = new Thread()
		{
			 public void run() { 
				 try
				 {
					MediaPlayer mediaPlayer = MediaPlayer.create(VizEQ.this, R.raw.vizeqintro);
									
					//AudioManager audio = (AudioManager) VizEQ.this.getSystemService(Context.AUDIO_SERVICE);
					//if (audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
						mediaPlayer.start();
					sleep(mediaPlayer.getDuration()); //length of track
					mediaPlayer.release();
					visualizer.release();
				 }
				 catch (Exception e)
				 {

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
