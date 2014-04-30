package com.lsu.vizeq;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

	public class PreferenceCircle extends View
	{
		Random r = new Random();
		public PreferenceCircle(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		public List<Track> tracks;
		String text;
		int x;
		int y;
		int radius;
		int color;
		String name;
		final float scale = getResources().getDisplayMetrics().density;
		final float width = getResources().getDisplayMetrics().widthPixels;
		final float height = getResources().getDisplayMetrics().heightPixels;
		//Code for working with bitmaps and density pixels
		//int twentyFiveDP = (int) (25 * scale + 0.5f);
		// Bitmap sub8 = BitmapFactory.decodeResource(getResources(), R.drawable.eighth);
		//Bitmap sub4 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.quarter), twentyFiveDP, twentyFiveDP, true);
		Paint paint = new Paint();

		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			animate().setDuration(600);			
			animate().scaleX(9);
			animate().scaleY(9);
			animate().translationY(height/2 - y);
			animate().translationX(width/2 - x);
			//((View) this.getParent()).animate().setDuration(600).scaleX(9);
			//((View)this.getParent()).animate().setDuration(600).scaleY(5);
			return super.onTouchEvent(event);
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			//Color paintColor = new Color();
			super.onDraw(canvas);
			switch (r.nextInt(5) + 1)
			{
				case 1:
					paint.setColor(getResources().getColor(R.color.Red));
					break;
				case 2:
					paint.setColor(getResources().getColor(R.color.Green));
					break;
				case 3:
					paint.setColor(getResources().getColor(R.color.Blue));
					break;
				case 4:
					paint.setColor(getResources().getColor(R.color.Purple));
					break;
				case 5:
					paint.setColor(getResources().getColor(R.color.Orange));
					break;
			}
			
			//paint.setColor(Color.MAGENTA);
			canvas.drawCircle(x, y, radius, paint);
			paint.setTextSize(50);
		    paint.setColor(Color.BLUE);
		    paint.setTextAlign(Align.CENTER);
		    canvas.drawText(text, x, y, paint);
		}

		public PreferenceCircle(Context context, int x, int y, int radius, String a, List<Track> tracks)
		{
			super(context);
			this.tracks = tracks;
			//super.setX(x);
			//super.setY(y);
			this.name = a;
			this.text = a;
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.setPadding(0, 0, 0, 0);
			this.setTag(text);
		}

		public PreferenceCircle(Context context, AttributeSet attrs)
		{
			super(context, attrs);
			// TODO Auto-generated constructor stub
		}

		public PreferenceCircle(Context context, AttributeSet attrs, int defStyle)
		{
			super(context, attrs, defStyle);
			// TODO Auto-generated constructor stub
		}

	}


