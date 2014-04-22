package com.lsu.vizeq;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

	public class MyCanvas extends View
	{
		Random r = new Random();
		int numCirclesToDraw;
		boolean drawText = true;
		int onlyCircle = 0;
		Context appContext;
		
		String text;
		int x;
		int y;
		int radius;
		Color color;
		final float scale = getResources().getDisplayMetrics().density;
		final float width = getResources().getDisplayMetrics().widthPixels;
		final float height = getResources().getDisplayMetrics().heightPixels - 70;
		
		Paint paint = new Paint();
		List<PreferenceCircle> circlesToDraw;
		
		public MyCanvas(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		public MyCanvas(Context context, List<PreferenceCircle> circles, int num)
		{
			super(context);
			appContext = context;
			this.circlesToDraw = circles;
			this.numCirclesToDraw = num; 
			//init() //my version doesn't need this

		}
		
		public void init()
		{
			
			//looks like we're sorting the circles by their radius?
			for (int i = 0; i < numCirclesToDraw; i++) {
			    for (int x = 1; x < numCirclesToDraw - i; x++) {
			        if (circlesToDraw.get(x - 1).radius < circlesToDraw.get(x).radius) {
			            PreferenceCircle temp = circlesToDraw.get(x - 1);
			            circlesToDraw.set(x-1, circlesToDraw.get(x));
			            circlesToDraw.set(x, temp);
			        }
			    }
			}
			
			int safety = 0;
			
			//for all the circles
			for (int i = 0; i < numCirclesToDraw; i++)
			{
				safety = 0;
	
				//get a random color
				pickColor();
				circlesToDraw.get(i).color = paint.getColor();

				boolean success = false;
				
				//Get a random point on the screen
				//try a max of 200 times
				while (success == false  && safety < 200)
				{
					circlesToDraw.get(i).x = r.nextInt((int) width);
					circlesToDraw.get(i).y = r.nextInt((int) height);
					success = isNotColliding(i);
					safety++;
				}
			}
		}
		
		
		public boolean isInCircle(Point tp, Point c, int radius)
		{
			boolean result = false;
			int diffX = Math.abs(tp.x - c.x);
			int diffY = Math.abs(tp.y - c.y);
			double distFromCenter = Math.sqrt(diffX * diffX + diffY * diffY);
			if ((int)distFromCenter > radius) result = false;
			else result = true;
			return result;
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
	
		

		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			if (event.getAction()==MotionEvent.ACTION_DOWN)
			{
				int thisOne = -1;
				for (int i = 0; i < numCirclesToDraw; i++)
				{
					Point touch = new Point();
					touch.x = (int) event.getX();
					touch.y = (int) event.getY();
					Point center = new Point();
					center.x = circlesToDraw.get(i).x;
					center.y = circlesToDraw.get(i).y;
					if (isInCircle(touch, center, circlesToDraw.get(i).radius))
					{
						Log.d("Circles", "Touched circle " + i);
						thisOne = i;
					}
				}
				if (thisOne != -1)
				{
					final int thisOneForSure = thisOne;
					onlyCircle = thisOne;
					Thread expandCircle = new Thread(new Runnable()
					{
						public void run()
						{
							drawText = false;
							while (circlesToDraw.get(thisOneForSure).radius < 1000)
							{
								circlesToDraw.get(thisOneForSure).radius += 15;
								postInvalidate();

								try
								{
									Thread.sleep(20);
								} 
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
							}
							//synchronized(this)
							//{
							//	this.notify();
							//}
						}
					});
					expandCircle.start();
					//synchronized (expandCircle)
					//{
						//try
						//{
						//	expandCircle.wait();

						//} catch (InterruptedException e)
						//{
						//	e.printStackTrace();
						//}
					//}
					Thread startDetails = new Thread(new Runnable()
					{
						public void run()
						{
							try
							{
								Thread.sleep(700);
							} catch (InterruptedException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//PVA is sorted by Artist name.  My Canvas is sorted by size.  Necessary, so, switch it right here.
							/*for (int i = 0; i < numCirclesToDraw; i++)
							{
								PreferenceVisualizationActivity.circles[i] = circlesToDraw.get(i);
							}*/
							PreferenceVisualizationActivity.getDetails(appContext, circlesToDraw.get(thisOneForSure));
						}
					});
					startDetails.start();
				}
			}
			return super.onTouchEvent(event);
		}

		@Override
		protected void onDraw(Canvas canvas)
		{
			super.onDraw(canvas);
			Log.d("onDraw", "Starting Drawing");

			paint.setTextSize(45);
			for (int i = 0; i < numCirclesToDraw; i++)
			{
				Log.d("onDraw", "Drawing circle " + circlesToDraw.get(i).name);
				paint.setColor(circlesToDraw.get(i).color);
				if (drawText == false)
				{
					if (i == onlyCircle) canvas.drawCircle(circlesToDraw.get(i).x, circlesToDraw.get(i).y, circlesToDraw.get(i).radius, paint);
				}
				else 
					canvas.drawCircle(circlesToDraw.get(i).x, circlesToDraw.get(i).y, circlesToDraw.get(i).radius, paint);
				paint.setColor(Color.BLUE);
				int halfOfText =  0;
				//int fittedTextSize = 45;
				for (int j = 45; j > 5; j-=2)
				{
					if (paint.measureText(circlesToDraw.get(i).text) > circlesToDraw.get(i).radius * 2) paint.setTextSize(j);
					else break;
				}
				//paint.setTextSize(fittedTextSize);
				if (circlesToDraw.get(i).text != null) halfOfText = (int) (paint.measureText(circlesToDraw.get(i).text)/2);
				if (drawText) canvas.drawText(circlesToDraw.get(i).text, circlesToDraw.get(i).x-halfOfText, circlesToDraw.get(i).y - paint.ascent()/3, paint);
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
				diffX = Math.abs(circlesToDraw.get(whichCircle).x - circlesToDraw.get(j).x);
				diffY = Math.abs(circlesToDraw.get(whichCircle).y - circlesToDraw.get(j).y);
				double distFromEachOther = Math.sqrt(diffX * diffX + diffY * diffY);
			if (distFromEachOther < circlesToDraw.get(whichCircle).radius + circlesToDraw.get(j).radius) 
				{
					soFarSoGood = false;
					return soFarSoGood;
				}
			}
				int quadrant = 0;
				if (circlesToDraw.get(whichCircle).x >= width/2 && circlesToDraw.get(whichCircle).y <= height/2) quadrant = 1;
				else if (circlesToDraw.get(whichCircle).x <= width/2 && circlesToDraw.get(whichCircle).y <= height/2) quadrant = 2;
				else if (circlesToDraw.get(whichCircle).x <= width/2 && circlesToDraw.get(whichCircle).y >= height/2) quadrant = 3;
				else quadrant = 4;
					
				//Edge detection
				switch (quadrant)
				{
					case 1:
						diffX = (int) Math.abs(circlesToDraw.get(whichCircle).x - width);
						diffY = (int) Math.abs(circlesToDraw.get(whichCircle).y);
						if (diffX < circlesToDraw.get(whichCircle).radius || diffY < circlesToDraw.get(whichCircle).radius)
							return false;
						break;
					case 2:
						diffX = (int) Math.abs(circlesToDraw.get(whichCircle).x);
						diffY = (int) Math.abs(circlesToDraw.get(whichCircle).y);
						if (diffX < circlesToDraw.get(whichCircle).radius || diffY < circlesToDraw.get(whichCircle).radius)
							return false;
						break;
					case 3:
						diffX = (int) Math.abs(circlesToDraw.get(whichCircle).x);
						diffY = (int) Math.abs(circlesToDraw.get(whichCircle).y - height);
						if (diffX < circlesToDraw.get(whichCircle).radius || diffY < circlesToDraw.get(whichCircle).radius)
							return false;
						break;
					case 4:
						diffX = (int) Math.abs(circlesToDraw.get(whichCircle).x - width);
						diffY = (int) Math.abs(circlesToDraw.get(whichCircle).y - height);
						if (diffX < circlesToDraw.get(whichCircle).radius || diffY < circlesToDraw.get(whichCircle).radius)
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


