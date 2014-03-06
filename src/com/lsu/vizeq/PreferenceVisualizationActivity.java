package com.lsu.vizeq;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.RelativeLayout;

public class PreferenceVisualizationActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference_visualization);
		PreferenceCircle pc = new PreferenceCircle(this, 50, 50, 20, "hi");
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

}
