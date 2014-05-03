package com.lsu.vizeq;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PreferenceVisualizationActivity extends Activity {

	public MyApplication myapp;
	public PreferenceVisualizer viz;
	private String currentSort = "none";
	private ActionBar actionBar;
	
	@Override
	protected void onStart(){
		super.onStart();
		actionBar = getActionBar();

		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		int posi = memory.getInt("colorPos", -1);
		if (posi > 0) VizEQ.numRand = posi;		
		switch (VizEQ.numRand)
		{
			case 1:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));
				break;
			case 2:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));				
				break;
			case 3:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));
				break;
			case 4:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));				
				break;
			case 5:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
				break;			
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myapp = (MyApplication) this.getApplicationContext();
		
		setContentView(R.layout.activity_preference_visualization);
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
		
		RelativeLayout vizlayout = (RelativeLayout) findViewById(R.id.vizlayout);
		
		int res_x = this.getResources().getDisplayMetrics().widthPixels;
		int res_y = this.getResources().getDisplayMetrics().heightPixels;
		viz = new PreferenceVisualizer(myapp.requests, res_x, res_y);
		new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				viz.init();
				viz.sortByArtist();
				currentSort = "artist";
				Log.d("viz", "packing circles...");
				//viz.packCircles2();
				Log.d("viz","circles packed");
				final List<PVCircle> circles = viz.getCircles();
				
				if (circles.isEmpty()==false)
				{
					Log.d("viz", "circles obtained");
				
					runOnUiThread(new Runnable()
					{
	
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.d("viz", "running on ui thread - viewCircles");
							viewCircles(circles);
							
						}
						
					});
				}
				else 
				{
					runOnUiThread(new Runnable()
					{
						@Override
						public void run() {
							Toast.makeText(PreferenceVisualizationActivity.this, "There are no requests from the crowd", Toast.LENGTH_LONG).show();
						}
					});
				
				}
			}
			
		}).start();
		
	}
	
	public void sortByArtist(View view)
	{
		if(!currentSort.equals("artist"))
		{
			new Thread(new Runnable()
			{
				public void run()
				{
					// TODO Auto-generated method stub
					viz.init();
					viz.sortByArtist();
					currentSort = "artist";
					Log.d("viz", "packing circles...");
					//viz.packCircles2();
					Log.d("viz","circles packed");
					final List<PVCircle> circles = viz.getCircles();
					
					if (circles.isEmpty()==false)
					{
						
						Log.d("viz", "circles obtained");
						runOnUiThread(new Runnable()
						{
	
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Log.d("viz", "running on ui thread - viewCircles");
								viewCircles(circles);
							}
							
						});
					}
					else 
					{
						runOnUiThread(new Runnable()
						{
							@Override
							public void run() {
								Toast.makeText(PreferenceVisualizationActivity.this, "There are no requests from the crowd", Toast.LENGTH_LONG).show();
							}
						});
					
					}
				}
			}).start();
		}
	}
	
	public void sortByAlbum(View view)
	{
		if(!currentSort.equals("album"))
		{
			new Thread(new Runnable()
			{
				public void run()
				{
					viz.init();
					viz.sortByAlbum();
					currentSort = "album";
					Log.d("viz", "packing circles...");
					//viz.packCircles2();
					Log.d("viz","circles packed");
					final List<PVCircle> circles = viz.getCircles();
					
					if (circles.isEmpty()==false)
					{
						Log.d("viz", "circles obtained");
						runOnUiThread(new Runnable()
						{
	
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Log.d("viz", "running on ui thread - viewCircles");
								viewCircles(circles);
							}
							
						});
					}
					else 
					{
						runOnUiThread(new Runnable()
						{
							@Override
							public void run() {
								Toast.makeText(PreferenceVisualizationActivity.this, "There are no requests from the crowd", Toast.LENGTH_LONG).show();
							}
						});
					
					}
				}
			}).start();
		}
	}
	
	public void sortByTrack(View view)
	{
		if(!currentSort.equals("track"))
		{
			new Thread(new Runnable()
			{
				public void run()
				{
					// TODO Auto-generated method stub
					viz.init();
					viz.sortByTrack();
					currentSort = "track";
					//viz.packCircles2();
					Log.d("viz","circles packed");
					final List<PVCircle> circles = viz.getCircles();
					if (viz.getCircles().isEmpty()==false)
					{
						Log.d("viz", "circles obtained");
						runOnUiThread(new Runnable()
						{
	
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Log.d("viz", "running on ui thread - viewCircles");
								viewCircles(circles);
							}
							
						});
					}
					else 
					{
						runOnUiThread(new Runnable()
						{
							@Override
							public void run() {
								Toast.makeText(PreferenceVisualizationActivity.this, "There are no requests from the crowd", Toast.LENGTH_LONG).show();
							}
						});
					
					}
				}
			}).start();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preference_visualization, menu);
		return true;
	}
	
	public void viewCircles(List<PVCircle> circles)
	{
		RelativeLayout vizlayout = (RelativeLayout) findViewById(R.id.vizlayout);
		vizlayout.removeAllViews();
		List<PreferenceCircle> pCircles = new ArrayList<PreferenceCircle>();
		for(int i=0; i<circles.size(); i++)
		{
			PVCircle currCircle = circles.get(i);
			Log.d("viewCircles", "currCircle.getRadius = " + currCircle.getRadius());
			Log.d("viewCircles", "currCircle.getRadius * 300 = " + (currCircle.getRadius()*225));
			PreferenceCircle pCircle = new PreferenceCircle(this, (int) (currCircle.getX()*currCircle.getScale()), (int) (currCircle.getY()*currCircle.getScale()), (int) (currCircle.getRadius()*currCircle.getScale()), currCircle.getName(), currCircle.getTrackList(), currCircle.getRequesters());
			Log.d("viewCircles", "pCircle.getRadius = " + pCircle.radius);
			pCircle.color = currCircle.getColor();
			pCircles.add(pCircle);
		}
		MyCanvas myCanvas = new MyCanvas(this, pCircles, pCircles.size());
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;
		vizlayout.addView(myCanvas, width, height);
	}
	
	public static void getDetails(Context context, PreferenceCircle pCircle)
	{
		Intent i = new Intent(context, RequestDetailsActivity.class);
		i.putParcelableArrayListExtra("tracks", (ArrayList<Track>) pCircle.tracks);
		i.putExtra("requestName", pCircle.name);
		i.putExtra("color", pCircle.color);
		i.putExtra("numRequesters", pCircle.requesters.size());
		context.startActivity(i);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent nextIntent  = new Intent(PreferenceVisualizationActivity.this, ProfileActivity.class);
			startActivity(nextIntent);
			break;
		case R.id.about:
			Intent nextIntent2  = new Intent(PreferenceVisualizationActivity.this, AboutActivity.class);
			startActivity(nextIntent2);
			break;
		}
		return true;
	}

}
