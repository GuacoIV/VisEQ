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
		int numCirclesToDraw;
		public MyCanvas(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		public MyCanvas(Context context, PreferenceCircle[] circles, int num)
		{
			super(context);
			this.circlesToDraw = new PreferenceCircle[15];
			this.numCirclesToDraw = num; 
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

			paint.setTextSize(45);
			int safety = 0;
			for (int i = 0; i < numCirclesToDraw; i++)
			{
				safety = 0;
				//Get a random point on the screen
				pickColor();

				boolean success = false;
				while (success == false  && safety < 200)
				{
					circlesToDraw[i].x = r.nextInt((int) width);
					circlesToDraw[i].y = r.nextInt((int) height);
					success = isNotColliding(i);
					safety++;
				}
				canvas.drawCircle(circlesToDraw[i].x, circlesToDraw[i].y, circlesToDraw[i].radius, paint);
				paint.setColor(Color.BLUE);
				int halfOfText =  0;
				//if (circlesToDraw[i].text != null) halfOfText = (int) (paint.measureText(circlesToDraw[i].text)/2);
				canvas.drawText(circlesToDraw[i].text, circlesToDraw[i].x-halfOfText, circlesToDraw[i].y, paint);
			}
		}
		
		public boolean isNotColliding(int whichCircle)
		{
			boolean soFarSoGood = true;
			int diffX = 0;
			int diffY = 0;
			for (int j = 0; j < whichCircle; j++)
			{
				//are the distances between circle i and circle j centers > circle[i].radius + circle[j].radius?
				diffX = Math.abs(circlesToDraw[whichCircle].x - circlesToDraw[j].x);
				diffY = Math.abs(circlesToDraw[whichCircle].y - circlesToDraw[j].y);
				double distFromEachOther = Math.sqrt(diffX * diffX + diffY * diffY);
				if (distFromEachOther < circlesToDraw[whichCircle].radius + circlesToDraw[j].radius) 
				{
					soFarSoGood = false;
					return soFarSoGood;
				}
			}
				int quadrant = 0;
				if (circlesToDraw[whichCircle].x >= width/2 && circlesToDraw[whichCircle].y <= height/2) quadrant = 1;
				else if (circlesToDraw[whichCircle].x <= width/2 && circlesToDraw[whichCircle].y <= height/2) quadrant = 2;
				else if (circlesToDraw[whichCircle].x <= width/2 && circlesToDraw[whichCircle].y >= height/2) quadrant = 3;
				else quadrant = 4;
					
				//Edge detection
				switch (quadrant)
				{
					case 1:
						diffX = (int) Math.abs(circlesToDraw[whichCircle].x - width);
						diffY = (int) Math.abs(circlesToDraw[whichCircle].y);
						if (diffX < circlesToDraw[whichCircle].radius || diffY < circlesToDraw[whichCircle].radius)
							return false;
						break;
					case 2:
						diffX = (int) Math.abs(circlesToDraw[whichCircle].x);
						diffY = (int) Math.abs(circlesToDraw[whichCircle].y);
						if (diffX < circlesToDraw[whichCircle].radius || diffY < circlesToDraw[whichCircle].radius)
							return false;
						break;
					case 3:
						diffX = (int) Math.abs(circlesToDraw[whichCircle].x);
						diffY = (int) Math.abs(circlesToDraw[whichCircle].y - height);
						if (diffX < circlesToDraw[whichCircle].radius || diffY < circlesToDraw[whichCircle].radius)
							return false;
						break;
					case 4:
						diffX = (int) Math.abs(circlesToDraw[whichCircle].x - width);
						diffY = (int) Math.abs(circlesToDraw[whichCircle].y - height);
						if (diffX < circlesToDraw[whichCircle].radius || diffY < circlesToDraw[whichCircle].radius)
							return false;
						break;
				}
			
			return soFarSoGood;
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


