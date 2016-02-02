package com.lsu.vizeq;

import android.os.Bundle;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.widget.TextView;

public class AboutActivity extends BackableActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		TextView txtAbout = (TextView)findViewById(R.id.txtAbout);
		CharSequence text = txtAbout.getText();
		String versionNumber = "";
		try
		{
			versionNumber = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		txtAbout.setText("Version: " + versionNumber + text);
		
		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		ActionBar actionBar = getActionBar();
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

}
