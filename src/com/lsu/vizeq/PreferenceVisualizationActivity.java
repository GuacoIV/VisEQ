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

public class PreferenceVisualizationActivity extends Activity {

	public MyApplication myapp;
	public Visualizer viz;
	private String currentSort;
	private ActionBar actionBar;
	
	@Override
	protected void onStart(){
		super.onStart();
		actionBar = getActionBar();

		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		int posi = memory.getInt("colorPos", -1);
		if (posi != -1) VizEQ.numRand = posi;		
		switch (VizEQ.numRand)
		{
			case 0:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
				break;
			case 1:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));				
				break;
			case 2:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));
				break;
			case 3:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));				
				break;
			case 4:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Grey85)));
				break;
			case 5:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
				break;
			case 6:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));
				break;			
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myapp = (MyApplication) this.getApplicationContext();
		viz = new Visualizer(myapp.requests);
		setContentView(R.layout.activity_preference_visualization);
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
		new Thread(new Runnable()
		{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				final List<Circle> circles;
				viz.init();
				viz.sortByArtist();
				currentSort = "artist";
				Log.d("viz", "packing circles");
				viz.packCircles2();
				circles = viz.getCircles();
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
					final List<Circle> circles;
					viz.init();
					viz.sortByArtist();
					currentSort = "artist";
					Log.d("viz", "packing circles");
					viz.packCircles2();
					circles = viz.getCircles();
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
					// TODO Auto-generated method stub
					final List<Circle> circles;
					viz.init();
					viz.sortByAlbum();
					currentSort = "album";
					Log.d("viz", "packing circles");
					viz.packCircles2();
					circles = viz.getCircles();
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
					final List<Circle> circles;
					viz.init();
					viz.sortByTrack();
					currentSort = "track";
					Log.d("viz", "packing circles");
					viz.packCircles2();
					circles = viz.getCircles();
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
			}).start();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preference_visualization, menu);
		return true;
	}
	
	public void viewCircles(List<Circle> circles)
	{
		RelativeLayout vizlayout = (RelativeLayout) findViewById(R.id.vizlayout);
		vizlayout.removeAllViews();
		List<PreferenceCircle> pCircles = new ArrayList<PreferenceCircle>();
		for(int i=0; i<circles.size(); i++)
		{
			Circle currCircle = circles.get(i);
			Log.d("viewCircles", "currCircle.getRadius = " + currCircle.getRadius());
			Log.d("viewCircles", "currCircle.getRadius * 300 = " + (currCircle.getRadius()*225));
			PreferenceCircle pCircle = new PreferenceCircle(this, (int) (currCircle.getX()*225), (int) (currCircle.getY()*225), (int) (currCircle.getRadius()*225), currCircle.getName(), currCircle.getTrackList());
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
