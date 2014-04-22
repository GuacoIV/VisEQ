package com.lsu.vizeq;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

public class VisualizerView extends View {

	private byte[] mFFTBytes = new byte[50000];
	private Rect mRect = new Rect();
	private Visualizer mVisualizer;
	private Paint mPaint;
	
	private int minCircleRadius = 10;
	
	public VisualizerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init(int audioSessionId) {
		mVisualizer = new Visualizer(audioSessionId);
		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
		
		Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
			
			@Override
			public void onWaveFormDataCapture(Visualizer arg0, byte[] arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFftDataCapture(Visualizer arg0, byte[] arg1, int arg2) {
				System.arraycopy(arg1, 0, mFFTBytes, 0, arg1.length);
				invalidate();
			}
		};
		
		mVisualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate(), false, true);
		mVisualizer.setEnabled(true);
		
		mPaint = new Paint();
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int w = getWidth();
		int h = getHeight();
		
		mRect.set(0, 0, w, h);
		
		float size = 0;
		float prev_size = 0;
		
		for (int i = 0; i < mFFTBytes.length; i++) {
			size = mFFTBytes[i]*8;
			if (size < prev_size - 20 || size > prev_size + 20) {
				canvas.drawCircle(w/2, h/2, size < minCircleRadius ? minCircleRadius : size, mPaint);
			}
			prev_size = size;
		}
		
	}
}
