package com.lsu.vizeq;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class HostSoundVisualizationActivity extends Activity {
	
	private VisualizerView vizView;

	public static boolean dirty = false;
	public static boolean flash = false;
	public static String[] data = new String[VisualizerView.NUM_BANDS];
	
	AsyncTask<Void,String,String> my_task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dirty = false;
		data = new String[VisualizerView.NUM_BANDS];
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_host_sound_visualization);
		
		my_task = new AsyncTask<Void,String,String>() {
			int flashTime = 0;
			@Override
			protected String doInBackground(Void... params) {
				while (!isCancelled()) {
					try {
						Thread.sleep(20);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (dirty) {
						vizView.SetCircleStates(data);
						dirty = false;
					}
					if (flash)
					{
						vizView.flash = true;
						flash = false;
					}
					//if (flashTime == 1)
						//vizView.flash = false;
					
					//flashTime = ++flashTime % 2;
				}
				return "hostsoundvisualization asynctask canceled";
			}
		};
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		    my_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
		else
		    my_task.execute((Void[])null);
		
		
		vizView = (VisualizerView)findViewById(R.id.visualizer);
		
		vizView.init(this);
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host_sound_visualization, menu);
		return true;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		my_task.cancel(true);
	}

	
	
}
