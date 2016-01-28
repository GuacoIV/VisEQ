package com.lsu.vizeq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RoleActivity extends Activity
{
	public static MyApplication myapp;
	ActionBar actionBar;
	
	@Override
	protected void onStart(){
		super.onStart();		
		actionBar = getActionBar();
		
		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		int posi = memory.getInt("colorPos", -1);
		if (posi > 0) VizEQ.numRand = posi;		
		switch (VizEQ.numRand)
		{
			case 1:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));
				break;
			case 2:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));				
				break;
			case 3:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));
				break;
			case 4:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));				
				break;
			case 5:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
				break;		
		}
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_role);
		
		Typeface font = Typeface.createFromAsset(getAssets(), "Mohave.otf");
		Typeface orFont = Typeface.createFromAsset(getAssets(), "Mission Gothic Bold.otf");
		TextView DJText = (TextView) findViewById(R.id.DJText);
		TextView notADJText = (TextView) findViewById(R.id.NotADJText);
		TextView orText = (TextView) findViewById(R.id.Or);
		DJText.setTypeface(font);
		notADJText.setTypeface(font);
		orText.setTypeface(orFont);
		DJText.setTextSize(40); //40 pt 153, 153, 153
		DJText.setTextColor(Color.rgb(153, 153, 153));
		notADJText.setTextSize(40); //40 pt
		notADJText.setTextColor(Color.rgb(153, 153, 153));
		orText.setTextSize(27);//27 gothic bold //51, 51, 51 //diameter is 140
		orText.setTextColor(Color.rgb(51, 51, 51));
		
		final float densityMultiplier = this.getResources().getDisplayMetrics().density;
		ImageView picButton = (ImageView) findViewById(R.id.DJ);
		ImageView picButton2 = (ImageView) findViewById(R.id.NotADJ);
		//Scale the text to fit
		for (int j = 40; j > 9; j-=2)
		{
			//Measure
			float scaledPx = j * densityMultiplier;
			int bigTextSize = (int) DJText.getPaint().measureText((String) DJText.getText());
			int littleTextSize = (int) orText.getPaint().measureText((String) orText.getText());
			picButton.measure(0, 0);
			
			//Check
			if (this.getResources().getDisplayMetrics().heightPixels - (bigTextSize*2 + littleTextSize + picButton.getMeasuredHeight()*2) < 0) 
				DJText.setTextSize(scaledPx);	
			else break;
			
			//Fix
			DJText.setTextSize(scaledPx);
			notADJText.setTextSize(scaledPx);
			orText.setTextSize((j - 10) * densityMultiplier);
			picButton.requestLayout();	
			picButton2.requestLayout();
			picButton.setAdjustViewBounds(true);
			picButton2.setAdjustViewBounds(true);
			picButton.setMaxHeight(picButton.getMeasuredHeight() - 10);
			picButton.setMaxWidth(picButton.getMeasuredWidth() - 10);
			picButton2.setMaxHeight(picButton.getMeasuredHeight() - 10);
			picButton2.setMaxWidth(picButton.getMeasuredWidth() - 10);
		}
		actionBar = getActionBar();
		int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
	    TextView yourTextView = (TextView) findViewById(titleId);
	    yourTextView.setTextColor(Color.WHITE);
	    Typeface titleFont = Typeface.createFromAsset(getAssets(), "Mohave-SemiBold.otf");
	    yourTextView.setTypeface(titleFont);
	    yourTextView.setTextSize(22);

		myapp = (MyApplication) this.getApplicationContext();
		
		final String usesTunnel = SystemProperties.get("tunnel.decode");
		
		//Note: when using native analysis, if a debugger is attached, it usually falls behind
		if (usesTunnel.compareTo("true") == 0 || !MyApplication.foundSound)
		{
			MyApplication.nativeAnalysis = true;
			LibSpotifyWrapper.nativeAnalysis = true;
		}			
		
		findViewById(R.id.DJ).setOnTouchListener(new View.OnTouchListener()
		{
			
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				Point touchPoint = new Point((int)event.getX(), (int)event.getY());
				Point center = new Point(v.getWidth()/2, v.getHeight()/2);
				ImageView DJButton = (ImageView) v;
				boolean isInCircle = isInCircle(touchPoint, center, v.getWidth()/2);
				if (event.getAction() == MotionEvent.ACTION_DOWN && isInCircle) DJButton.setImageResource(R.drawable.hostbuttonover_325x325);
				if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)
				{
					DJButton.setImageResource(R.drawable.hostbutton_325x325);
					if (event.getAction() == MotionEvent.ACTION_UP && isInCircle)
					{
						v.playSoundEffect(SoundEffectConstants.CLICK);
						Intent nextIntent = new Intent(RoleActivity.this, LoginActivity.class);
						startActivity(nextIntent);
					}
				}
				return true;
			}
		});
		
		findViewById(R.id.NotADJ).setOnTouchListener(new View.OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				Point touchPoint = new Point((int)event.getX(), (int)event.getY());
				Point center = new Point(v.getWidth()/2, v.getHeight()/2);
				ImageView DJButton = (ImageView) v;
				boolean isInCircle = isInCircle(touchPoint, center, v.getWidth()/2);
				if (event.getAction() == MotionEvent.ACTION_DOWN && isInCircle) DJButton.setImageResource(R.drawable.joinbuttonover_325x325);
				if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP)
				{
					DJButton.setImageResource(R.drawable.joinbutton_325x325);
					if (event.getAction() == MotionEvent.ACTION_UP && isInCircle)
					{
						v.playSoundEffect(SoundEffectConstants.CLICK);
						Intent nextIntent;
						if(myapp.joined)
						{
							nextIntent = new Intent(RoleActivity.this, SoundVisualizationActivity.class);
						}
						else
						{
							nextIntent = new Intent(RoleActivity.this, SearchPartyActivity.class);
						}
						startActivity(nextIntent);	
					}
				}
				return true;
			}
		});		
	}
	
	public void NotCompletelySupportedNotification()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Warning: Not all VizEQ features are currently supported by your device:\nNo sound visualization for host.").setCancelable(false)
		.setPositiveButton("ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	public boolean isInCircle(Point tp, Point c, int radius)
	{
		int diffX = Math.abs(tp.x - c.x);
		int diffY = Math.abs(tp.y - c.y);
		double distFromCenter = Math.sqrt(diffX * diffX + diffY * diffY);
		if (distFromCenter > radius) return false;
		else return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.role, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent nextIntent  = new Intent(RoleActivity.this, ProfileActivity.class);
			startActivity(nextIntent);
			break;
		case R.id.about:
			Intent nextIntent2  = new Intent(RoleActivity.this, AboutActivity.class);
			startActivity(nextIntent2);
			break;
		default:
			super.onOptionsItemSelected(item);
		}
		return true;
	}	
}
