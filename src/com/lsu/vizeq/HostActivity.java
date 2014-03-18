package com.lsu.vizeq;

import android.app.Activity;
import android.content.Intent;
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
			Intent nextIntent  = new Intent(HostActivity.this, SettingsActivity.class);
			startActivity(nextIntent);
		}
		return true;
	}
}
