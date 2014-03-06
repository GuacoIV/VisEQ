package com.lsu.vizeq;

import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class HostMenuActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_menu);
		findViewById(R.id.NowPlaying).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent nextIntent = new Intent(HostMenuActivity.this, PlayerActivity.class);
				startActivity(nextIntent);		
			}
			
		});
		findViewById(R.id.Scope).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				Intent nextIntent = new Intent(HostMenuActivity.this, PreferenceVisualizationActivity.class);
				startActivity(nextIntent);	
			}
			
		});
		findViewById(R.id.MusicQueue).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent nextIntent = new Intent(HostMenuActivity.this, MusicQueueActivity.class);
				startActivity(nextIntent);	
			}
			
		});
		findViewById(R.id.Search).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent nextIntent = new Intent(HostMenuActivity.this, SearchActivity.class);
				startActivity(nextIntent);	
			}
			
		});
	}
	
	public void addUserToList(View view)
	{
		EditText nameField = (EditText) this.findViewById(R.id.name_field);
		EditText ipField = (EditText) this.findViewById(R.id.ip_field);
		MyApplication myapp = (MyApplication) this.getApplicationContext();
		myapp.connectedUsers.put(nameField.getText().toString(), ipField.getText().toString());
		nameField.setText("");
		ipField.setText("");
		refreshLists();
	}
	
	private void refreshLists()
	{
		TextView nameList = (TextView) this.findViewById(R.id.name_list);
		TextView ipList = (TextView) this.findViewById(R.id.ip_list);
		String nameString = "";
		String ipString = "";
		MyApplication myapp = (MyApplication) this.getApplicationContext();
		Iterator it = myapp.connectedUsers.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pairs= (Map.Entry) it.next();
			String name = (String) pairs.getKey();
			String ip = (String) pairs.getValue();
			nameString += (name + "\n");
			ipString += (ip + "\n");
		}
		//iterate through usersConnected
		
		nameList.setText(nameString);
		ipList.setText(ipString);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.host_menu, menu);
		return true;
	}

}
