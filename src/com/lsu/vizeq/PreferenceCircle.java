package com.lsu.vizeq;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

	public class PreferenceCircle extends View
	{

		public PreferenceCircle(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		String text;
		int x;
		int y;
		int radius;
		Color color;
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
			return super.onTouchEvent(event);
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			super.onDraw(canvas);
			paint.setColor(Color.MAGENTA);
			float top = this.getTop();
			float left = this.getLeft();
			float right = this.getRight();
			float bottom = this.getBottom();
			canvas.drawCircle(x, y, radius, paint);
			paint.setTextSize(50);
		    paint.setColor(Color.BLUE);
		    canvas.drawText(text, x, y, paint);
		}

		public PreferenceCircle(Context context, int x, int y, int radius, String text)
		{
			super(context);
			super.setX(x);
			super.setY(y);
			this.text = text;
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


