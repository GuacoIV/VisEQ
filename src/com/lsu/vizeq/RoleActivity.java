package com.lsu.vizeq;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class RoleActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_role);
		findViewById(R.id.DJ).setOnClickListener(new View.OnClickListener() 
		{
				@Override
				public void onClick(View v)
				{
					Intent nextIntent = new Intent(RoleActivity.this, HostActivity.class);
					startActivity(nextIntent);					
				}

		});
		findViewById(R.id.NotADJ).setOnClickListener(new View.OnClickListener() 
		{
				@Override
				public void onClick(View v)
				{
					Intent nextIntent = new Intent(RoleActivity.this, SoundVisualizationActivity.class);
					startActivity(nextIntent);					
				}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.role, menu);
		return true;
	}

}
