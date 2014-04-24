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
	public static String[] data = new String[VisualizerView.NUM_BANDS];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_host_sound_visualization);
		
		AsyncTask<Void,String,String> my_task = new AsyncTask<Void,String,String>() {

			@Override
			protected String doInBackground(Void... params) {
				Log.d("do in", "background");
				while (!isCancelled()) {
					try {
						Thread.sleep(20);
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.d("polling", "hsv");
					if (dirty) {
						Log.d("dirty", "nigga");
						vizView.SetCircleStates(data);
						dirty = false;
					}
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

	
	
}
