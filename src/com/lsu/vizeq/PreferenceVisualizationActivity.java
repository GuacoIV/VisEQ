package com.lsu.vizeq;

import com.lsu.vizeq.R.color;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

public class PreferenceVisualizationActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference_visualization);
		
		int circleRadius = 100;
		PreferenceCircle pc = new PreferenceCircle(this, circleRadius, circleRadius, circleRadius, "hi");
		RelativeLayout circleScreen = (RelativeLayout) this.findViewById(R.id.CircleScreen);
		circleScreen.addView(pc, circleRadius*2, circleRadius*2); 
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preference_visualization, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent nextIntent  = new Intent(PreferenceVisualizationActivity.this, SettingsActivity.class);
			startActivity(nextIntent);
		}
		return true;
	}
}
