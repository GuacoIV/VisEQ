package com.lsu.vizeq;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
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
	
	private Activity mActivity;
	private Visualizer mVisualizer;
	private Interpolator mInterpolator = new DecelerateInterpolator();
	private int captureRate;
	private FrequencyCircle[] circles = new FrequencyCircle[NUM_BANDS];
	
	public boolean flash = false;
	
	public VisualizerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init(Activity host) {

		captureRate = Visualizer.getMaxCaptureRate()/4;

		mActivity = host;
		
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

	public void SetCircleStates(final String[] states) {
		mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	for (int i = 0; i < circles.length; i++) {
        			Log.d("setting energy", states[i] + " " + i);
            		if (states[i].equals("off")) {
        				circles[i].SetEnergy(false);
        			}
        			else if (states[i].equals("on")) {
        				circles[i].SetEnergy(true);
        			}
        		}
            }
            
        });
		
		Log.d("setting energy", " ");
	}
	
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (flash == false) this.setBackgroundColor(Color.BLACK);
		else this.setBackgroundColor(Color.WHITE);
		for (int i = 0; i < circles.length; i++) {
			if (circles[i] != null) {
				circles[i].render(canvas);
			}
		}
	}
}
