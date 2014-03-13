package com.lsu.vizeq;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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
		PreferenceCircle pc = new PreferenceCircle(this, 200, 200, 100, "hi");
		RelativeLayout circleScreen = (RelativeLayout) this.findViewById(R.id.CircleScreen);
		circleScreen.addView(pc);
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
