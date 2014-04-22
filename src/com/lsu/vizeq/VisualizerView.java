package com.lsu.vizeq;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
		
		public void SetEnergy(float s) {
			boolean isOnLastFrame = false;
			boolean isOnThisFrame = false;
			if (energy > threshold*prevEnergy) {
				isOnLastFrame = true;
			}
			prevEnergy = energy;
			energy = s;
			if (energy > threshold*prevEnergy) {
				isOnThisFrame = true;
			}
			if (isOnLastFrame ^ isOnThisFrame) {
				if (isOnThisFrame) {
					mAnimator.setValues(PropertyValuesHolder.ofFloat("size", size, onSize));
				}
				else {
					mAnimator.setValues(PropertyValuesHolder.ofFloat("size", size, offSize));
				}
				mAnimator.start();
			}
		}
		
		public void render(Canvas canvas) {
			Log.d("color", String.valueOf(mPaint.getColor()));
			canvas.drawCircle(getWidth()/2, getHeight()/2, size*getWidth(), mPaint);
		}
	}
	
	private final int NUM_BANDS = 4;
	
	private Visualizer mVisualizer;
	private Interpolator mInterpolator = new DecelerateInterpolator();
	private int captureRate;
	private FrequencyCircle[] circles = new FrequencyCircle[NUM_BANDS];
	
	public VisualizerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init(int audioSessionId) {
		mVisualizer = new Visualizer(audioSessionId);
		if (mVisualizer.getEnabled()) {
			mVisualizer.setEnabled(false);
		}
		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);

		captureRate = Visualizer.getMaxCaptureRate()/4;

		Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {

			@Override
			public void onWaveFormDataCapture(Visualizer arg0, byte[] arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFftDataCapture(Visualizer arg0, byte[] arg1, int arg2) {
				int bandWidth = arg1.length/NUM_BANDS;
				for (int j = 0; j < NUM_BANDS; j++) {
					float sum = 0;
					for (int i = bandWidth*j; i < bandWidth*(j+1); i++) {
						sum += Math.abs(arg1[i]);
					}
					sum /= bandWidth;
					circles[j].SetEnergy(sum);
				}
			}
		};

		mVisualizer.setDataCaptureListener(captureListener, captureRate, false, true);
		mVisualizer.setEnabled(true);
		
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

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.setBackgroundColor(Color.BLACK);
		for (int i = 0; i < circles.length; i++) {
			circles[i].render(canvas);
		}
	}

}
