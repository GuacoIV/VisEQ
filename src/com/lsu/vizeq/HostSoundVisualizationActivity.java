package com.lsu.vizeq;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class HostSoundVisualizationActivity extends Activity {
	
	private VisualizerView vizView;
	private static int saveSlider = 0;

	public static boolean dirty = false;
	public static boolean flash = false;
	public static String[] data = new String[VisualizerView.NUM_BANDS];
	
	AsyncTask<Void,String,String> my_task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dirty = false;
		data = new String[VisualizerView.NUM_BANDS];
		for (int i = 0; i < data.length; i++) {
			data[i] = "none";
		}
		
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
						if (MyApplication.nativeAnalysis) {
							Thread.sleep(100);
						}
						else {
							Thread.sleep(20);
						}
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (dirty) {
						vizView.SetCircleStates(data);
					}
					if (flash) {
						vizView.flash = true;
					}
					
					if ((flash || dirty) && MyApplication.nativeAnalysis)
					{
						String strFlash = flash ? "yes" : "no";
						PlayerActivity.SendBeat(data, strFlash);
					}
					if (dirty) {
						dirty = false;
					}
					if (flash) {
						flash = false;
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
		
		
		
		vizView.init(this, true);
		
		if (!MyApplication.nativeAnalysis)
		{
			LinearLayout controls = new LinearLayout(this);
			controls.setOrientation(LinearLayout.VERTICAL);
			controls.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			LinearLayout text = new LinearLayout(this);
			text.setOrientation(LinearLayout.HORIZONTAL);
			text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			LinearLayout text2 = new LinearLayout(this);
			text2.setOrientation(LinearLayout.HORIZONTAL);
			text2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
			LinearLayout text3 = new LinearLayout(this);
			text3.setOrientation(LinearLayout.HORIZONTAL);
			text3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
			SeekBar freqSlider = new SeekBar(this);
			RelativeLayout.LayoutParams params =  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(3,3,3,3);
			freqSlider.setLayoutParams(params);
			freqSlider.setMax(PlayerActivity.NUM_FLASH_BANDS - 1);
			freqSlider.setProgress(saveSlider);
			TextView lowText = new TextView(this);
			lowText.setText("Bass drum");
			TextView highText = new TextView(this);
			highText.setText("Hi-hat");
			//lowText.setGravity(Gravity.LEFT);
			//highText.setGravity(Gravity.RIGHT);
			text2.setGravity(Gravity.LEFT);
			text3.setGravity(Gravity.RIGHT);
			lowText.setTextColor(Color.WHITE);
			highText.setTextColor(Color.WHITE);
			Typeface font = Typeface.createFromAsset(getAssets(), "Mission Gothic Light.otf");
			lowText.setTypeface(font);
			highText.setTypeface(font);
			text2.addView(lowText);
			text3.addView(highText);
			controls.addView(freqSlider);
			text.addView(text2);
			text.addView(text3);
			controls.addView(text);
			((ViewGroup) findViewById(R.id.host_viz_daddy)).addView(controls);
			
			freqSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
	            @Override
	            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	                PlayerActivity.BAND_TO_FLASH = progress;
	            }

	            @Override
	            public void onStartTrackingTouch(SeekBar seekBar) {;
	                
	               
	               // dialog.show();
	            }

	            @Override
	            public void onStopTrackingTouch(SeekBar seekBar) {
	                // TODO Auto-generated method stub
	            	saveSlider = PlayerActivity.BAND_TO_FLASH;
	            }

	        });
		}
		
		ToggleButton tog = (ToggleButton)findViewById(R.id.taptoflash);
		tog.setChecked(!MyApplication.tapToFlash);
		tog.setTextOff("Auto");
		tog.setTextOn("Auto");
		tog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					MyApplication.tapToFlash = false;
				}
				else {
					MyApplication.tapToFlash = true;
					Toast.makeText(HostSoundVisualizationActivity.this, "You've got the lights!  Tap anywhere to flash all party lights.", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
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
		if (VisualizerView.cam != null) VisualizerView.cam.release();
		VisualizerView.cam = null;
	}

	
	
}
