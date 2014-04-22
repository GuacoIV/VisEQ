package com.lsu.vizeq;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
class Circle {
	
}

public class VisualizerView extends View {
	
	private byte[] fromBytes = new byte[50000];
	private byte[] toBytes = new byte[50000];
	private Rect mRect = new Rect();
	private Visualizer mVisualizer;
	private Paint mPaint;
	
	private float[] pastEnergies;
	
	private ObjectAnimator mAnimator;
	
	private float toSize;
	private float size;
	private float normalizingFactor;
	
	private Interpolator mInterpolator = new AccelerateInterpolator();
	
	private int captureRate;
	
	private final float MIN_CIRCLE_FRACTION = .05f;
	private final float MAX_CIRCLE_FRACTION = 1f;
	private final float DIFF_CIRCLE_FRACTION = MAX_CIRCLE_FRACTION - MIN_CIRCLE_FRACTION;
	
	public VisualizerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSize(float s) {
		size = s;
		if (size < 0) size = 0;
		if (size > 1) size = 1;
		invalidate();
	}
	
	private int actualSize() {
		normalizingFactor = getWidth()/2;
		float t = (MIN_CIRCLE_FRACTION + DIFF_CIRCLE_FRACTION*size)*normalizingFactor;
		Log.d(String.valueOf(t), "ttt");
		return (int)(t);
	}
	
	public void init(int audioSessionId) {
		mVisualizer = new Visualizer(audioSessionId);
		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
		
		captureRate = Visualizer.getMaxCaptureRate()/4;
		
		Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
			
			@Override
			public void onWaveFormDataCapture(Visualizer arg0, byte[] arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFftDataCapture(Visualizer arg0, byte[] arg1, int arg2) {
				//System.arraycopy(arg1, 0, fromBytes, 0, arg1.length);
				int numSamples = arg1.length/4;
				
				double maxSize = numSamples*16;
				toSize = 0;
				for (int i = 0; i < numSamples; i++) {
					toSize += Math.abs(arg1[i]);
				}
				toSize /= maxSize;
				
				float avg = 0;
				for (int i = 0; i < pastEnergies.length; i++) {
					avg += pastEnergies[i];
				}
				avg /= pastEnergies.length;
				
				float c = 1.2f;
				
				if (toSize <= c*avg) {
					toSize = 0;
				}
				
				mAnimator.setValues(PropertyValuesHolder.ofFloat("size", size, toSize));
				mAnimator.start();
			}
		};
		
		mVisualizer.setDataCaptureListener(captureListener, captureRate, false, true);
		mVisualizer.setEnabled(true);
		
		mPaint = new Paint();
		
		mAnimator = new ObjectAnimator();
		mAnimator.setTarget(this);
		mAnimator.setInterpolator(mInterpolator);
		mAnimator.setDuration(captureRate/10);
		
		Log.d(String.valueOf(captureRate), "captureRate");
		
		pastEnergies = new float[captureRate];
		
		mPaint.setColor(VizEQ.colorScheme);
		
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int w = getWidth();
		int h = getHeight();
		
		canvas.drawCircle(w/2, h/2, actualSize(), mPaint);
		
	}
	
	 
}
