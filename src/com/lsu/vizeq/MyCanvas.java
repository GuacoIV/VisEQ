package com.lsu.vizeq;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

	public class MyCanvas extends View
	{
		Random r = new Random();
		public MyCanvas(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		public MyCanvas(Context context, PreferenceCircle[] circles)
		{
			super(context);
			this.circlesToDraw = new PreferenceCircle[15];
			for (int i = 0; i < 15; i++)
				this.circlesToDraw[i] = circles[i];
		}

		public MyCanvas(Context context, AttributeSet attrs)
		{
			super(context, attrs);
			// TODO Auto-generated constructor stub
		}

		public MyCanvas(Context context, AttributeSet attrs, int defStyle)
		{
			super(context, attrs, defStyle);
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
		
		Paint paint = new Paint();
		PreferenceCircle circlesToDraw[];

		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			/*animate().setDuration(600);			
			animate().scaleX(9);
			animate().scaleY(9);
			animate().translationY(height/2 - y);
			animate().translationX(width/2 - x);*/
			return super.onTouchEvent(event);
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			super.onDraw(canvas);


			for (int i = 0; i < 6; i++)
			{
				//Get a random point on the screen
				pickColor();
				circlesToDraw[i].x = r.nextInt((int) width);
				circlesToDraw[i].y = r.nextInt((int) height);
				canvas.drawCircle(circlesToDraw[i].x, circlesToDraw[i].y, circlesToDraw[i].radius, paint);
				
			}
			
			paint.setTextSize(50);
		    paint.setColor(Color.BLUE);
		    paint.setTextAlign(Align.CENTER);
		    //canvas.drawText(text, x, y, paint);
		}
		public void pickColor()
		{
			switch (r.nextInt(5))
			{
				case 0:
					paint.setColor(getResources().getColor(R.color.Red));
					break;
				case 1:
					paint.setColor(getResources().getColor(R.color.Green));
					break;
				case 2:
					paint.setColor(getResources().getColor(R.color.Blue));
					break;
				case 3:
					paint.setColor(getResources().getColor(R.color.Purple));
					break;
				case 4:
					paint.setColor(getResources().getColor(R.color.Orange));
					break;
			}
		}


	}


