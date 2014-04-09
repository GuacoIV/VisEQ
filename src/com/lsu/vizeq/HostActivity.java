package com.lsu.vizeq;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import redis.clients.jedis.Jedis;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class HostActivity extends Activity
{

	public LocationManager locationManager;
	ActionBar actionBar;
	
	@Override
	protected void onStart(){
		super.onStart();
		actionBar = getActionBar();
		
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
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));		
		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		
		findViewById(R.id.OK).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				String name = getName();
				String zipcode = "70820";
				String ip = "0.0.0.0";
				new ContactServerTask().execute(name, zipcode, ip);
			}
			
		});
	}
	
	public String getName()
	{
		EditText et = (EditText) findViewById(R.id.editText1);
		return et.getText().toString();
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
	
	public String getZipcode()
	{
		String zipcode = "00000";
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
		
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1);
			zipcode = addresses.get(0).getPostalCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("zipcode", zipcode);
		return zipcode;
	}
	
	public LocationListener locationListener = new LocationListener()
	{

		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public String getIp()
	{
		String ip = "0.0.0.0";
		
		
		
		return ip;
	}
	
	public void changeNameNotification()
	{
		
	}
	
	public void connectionErrorNotification()
	{
		
	}
	
	public void moveToMenu()
	{
		MyApplication myapp = (MyApplication) this.getApplicationContext();
		myapp.myName = getName();
		myapp.zipcode = getZipcode();
		myapp.myIp = getIp();
		myapp.hosting = true;
		Intent nextIntent = new Intent(HostActivity.this, HostMenuActivity.class);
		startActivity(nextIntent);
	}
	
	private class ContactServerTask extends AsyncTask<String, Void, Integer>
	{

		@Override
		//params[0] = party name
		//params[1] = zipcode
		//params[2] = ip
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			Integer result = 2;
			Jedis jedis = new Jedis(Redis.host, Redis.port);
			jedis.auth(Redis.auth);
			String partyName = params[0];
			String zipcode = params[1];
			String ip = params[2];
			
			long reply = jedis.setnx(zipcode + ":" + partyName, ip);
			if(reply == 0)
				result = 1;
			else
			{
				result = 0;
				jedis.expire(zipcode + ":" + partyName, 500);
				jedis.sadd(zipcode, partyName);
			}
			jedis.close();
			return result;
		}

		@Override
		//0 = good to go
		//1 = need different name
		//2 = couldn't connect to server
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			if(result == 0)
			{
				moveToMenu();
			}
			else if(result == 1)
			{
				changeNameNotification();
			}
			else if(result == 2)
			{
				connectionErrorNotification();
			}
		}
		
	}
}
