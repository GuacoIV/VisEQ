package com.lsu.vizeq;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class HostActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
		
		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		int posi = memory.getInt("colorPos", -1);
		if (posi != -1) VizEQ.numRand = posi;		
		switch (VizEQ.numRand)
		{
			case 0:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
				break;
			case 1:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));				
				break;
			case 2:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));
				break;
			case 3:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));				
				break;
			case 4:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Grey85)));
				break;
			case 5:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
				break;
			case 6:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));
				break;			
		}
		
		findViewById(R.id.OK).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setName();
				Intent nextIntent = new Intent(HostActivity.this, HostMenuActivity.class);
				startActivity(nextIntent);	
			}
			
		});
	}
	
	public void setName()
	{
		MyApplication myapp = (MyApplication) this.getApplicationContext();
		EditText et = (EditText) findViewById(R.id.editText1);
		myapp.myName = et.getText().toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent nextIntent  = new Intent(HostActivity.this, ProfileActivity.class);
			startActivity(nextIntent);
		}
		return true;
	}
}
