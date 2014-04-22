package com.lsu.vizeq;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class HostSoundVisualizationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_sound_visualization);
		
		VisualizerView vizView = (VisualizerView)findViewById(R.id.visualizer);
		
		vizView.init(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host_sound_visualization, menu);
		return true;
	}

}
