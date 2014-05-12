package com.lsu.vizeq;

import java.util.Timer;
import java.util.TimerTask;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera.Parameters;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;



public class VisualizerView extends View {
	private class FrequencyCircle {
		private float prevEnergy;
		private float energy;
		private Paint mPaint;
		private ObjectAnimator mAnimator;
		
		private float size;
		private float onSize;
		private float offSize;
		private float threshold = 1.1f;

		
		public FrequencyCircle(Paint p, float on, float off) {
			mPaint = p;
			onSize = on;
			offSize = off;
			mAnimator = new ObjectAnimator();
			mAnimator.setTarget(this);
			mAnimator.setInterpolator(mInterpolator);
			mAnimator.setDuration(captureRate/10);
		}
		
		public void setSize(float s) {
			size = s;
			invalidate();
		}
		
		public void SetEnergy(boolean isOn) {
			if (isOn) {
				mAnimator.setValues(PropertyValuesHolder.ofFloat("size", size, onSize));
			}
			else {
				mAnimator.setValues(PropertyValuesHolder.ofFloat("size", size, offSize));
			}
			mAnimator.start();
		}
		
		public void render(Canvas canvas) {
			canvas.drawCircle(getWidth()/2, getHeight()/2, size*getWidth(), mPaint);
		}
	}
	
	public static final int NUM_BANDS = 4;
	
	
	private boolean tapToFlash = false;
	private Activity mActivity;
	private Visualizer mVisualizer;
	private Interpolator mInterpolator = new DecelerateInterpolator();
	private int captureRate;
	private FrequencyCircle[] circles = new FrequencyCircle[NUM_BANDS];
	
	public boolean flash = false;
	Context context;
	public static Camera cam;
	public VisualizerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public void init(Activity host, boolean isHost) {

		captureRate = Visualizer.getMaxCaptureRate()/4;

		tapToFlash = isHost;
		
		this.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (tapToFlash) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						HostSoundVisualizationActivity.flash = true;
						PlayerActivity.SendBeat(HostSoundVisualizationActivity.data, "yes");
					}
				}
				return false;
			}
		});
		
		mActivity = host;
		flashThread.start();
		for (int i = 0; i < circles.length; i++) {
			Paint p = new Paint();
			float[] hsv = new float[3];
			Color.colorToHSV(VizEQ.colorScheme, hsv);
			hsv[2] = 1.0f - (0.8f - i * 0.6f) * (1.0f - hsv[2]);
			int lightenedColor = Color.HSVToColor(hsv);
			p.setColor(lightenedColor);
			p.setAlpha(255/4);
			circles[circles.length - 1 - i] = new FrequencyCircle(p, (.5f - i * .05f), .1f);
		}
	}

	public void uninit() {
		flashThread.interrupt();
		flashThread = null;
	}
	
	public void SetCircleStates(final String[] states) {
		mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	for (int i = 0; i < circles.length; i++) {
//        			Log.d("setting energy", states[i] + " " + i);
            		if (states[i].equals("off")) {
        				circles[i].SetEnergy(false);
        			}
        			else if (states[i].equals("on")) {
        				circles[i].SetEnergy(true);
        			}
        		}
            }
            
        });
		
//		Log.d("setting energy", " ");
	}
	Parameters p;
	Thread flashThread = new Thread(new Runnable()
	{
		public void run()
		{
			if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) && MyApplication.doFlash) 
	        {
				try
				{
		            cam = Camera.open();
		            p = cam.getParameters();
		            p.setFlashMode(Parameters.FLASH_MODE_TORCH);
		            cam.setParameters(p);
				}
				catch( Exception e)
				{
					
				}
	            
	        }
			while (true)
			{
				try
				{
					Thread.sleep(30);
				} catch (InterruptedException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (flash)
				{
					try
					{
				        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) && MyApplication.doFlash) 
				        {
				            cam.startPreview();
				            p.setFlashMode(Parameters.FLASH_MODE_TORCH);
				            cam.setParameters(p);
				        }
					}
					catch (Exception e) {
				        e.printStackTrace();
//				        Log.d("Flashlight", "Exception flashLightOn");
				    }
					//Time it
					Timer persistFlash = new Timer();
					persistFlash.schedule(new TimerTask(){
			
						@Override
						public void run()
						{
							flash = false;
							try 
							{
						        if (context.getPackageManager().hasSystemFeature(
						                PackageManager.FEATURE_CAMERA_FLASH)) {
						            cam.stopPreview();
						            p.setFlashMode(Parameters.FLASH_MODE_OFF);
						            cam.setParameters(p);
						           // cam = null;
						        }
						    } 
							catch (Exception e) 
							{
						        e.printStackTrace();
//						        Log.d("Flashlight", "Exception flashLightOff");
						    }
						}	
					}, 40);
				}
			}
		}
	});
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (flash == false || MyApplication.doBackground == false) this.setBackgroundColor(Color.BLACK);
		else 
		{
			this.setBackgroundColor(Color.WHITE);
		}
		for (int i = 0; i < circles.length; i++) {
			if (circles[i] != null) {
				circles[i].render(canvas);
			}
		}
	}
}
