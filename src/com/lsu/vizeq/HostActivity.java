package com.lsu.vizeq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HostActivity extends BackableActivity
{
	public LocationManager locationManager;
	ActionBar actionBar;
	String myName, zipcode, externalIp;
	MyApplication myapp;
	Location currLocation = null;	
	
	@Override
	protected void onStart(){
		super.onStart();
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));

		RelativeLayout rl = (RelativeLayout) findViewById(R.id.hostBackground);
		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		int posi = memory.getInt("colorPos", -1);
		if (posi > 0) VizEQ.numRand = posi;		
		switch (VizEQ.numRand)
		{
			case 1:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));
				rl.setBackground(getResources().getDrawable(R.drawable.red));
				//r1.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue));
				break;
			case 2:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));
				rl.setBackground(getResources().getDrawable(R.drawable.green));
				break;
			case 3:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));
				rl.setBackground(getResources().getDrawable(R.drawable.blue));
				break;
			case 4:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));
				rl.setBackground(getResources().getDrawable(R.drawable.purple));
				break;
			case 5:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
				rl.setBackground(getResources().getDrawable(R.drawable.orange));
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
		
		Typeface font = Typeface.createFromAsset(getAssets(), "Mission Gothic Bold.otf");
		TextView nameThisSession = (TextView)findViewById(R.id.textView1);
		nameThisSession.setTypeface(font);
		
		Button letsParty = (Button) findViewById(R.id.OK);
		letsParty.setTypeface(font);
		letsParty.setAlpha(1f);
		
		letsParty.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					v.setAlpha(0.7f);
				else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
					v.setAlpha(1f);
				return false;
			}
			
		});		
		
		myapp = (MyApplication) this.getApplicationContext();
				
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		
		letsParty.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				//String name = getName();
				
				if(myapp.zipcode == null || myapp.zipcode.equals("00000"))
		    		myapp.zipcode = getZipcode();
		    	if(myapp.zipcode.equals("00000"))
		    	{
		    		noLocationNotification();
		    	}
		    	else
		    	{
		    		new ContactServerTask().execute(myapp.zipcode);
		    	}
				//myapp.zipcode = getZipcode();//"70820";
				//String ip = "0.0.0.0";
				
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
			Intent nextIntent  = new Intent(HostActivity.this, HostProfileActivity.class);
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(findViewById(R.id.hostBackground).getWindowToken(), 0);
			startActivity(nextIntent);
			break;
		case R.id.about:
			Intent nextIntent2  = new Intent(HostActivity.this, AboutActivity.class);
			InputMethodManager imm1 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm1.hideSoftInputFromWindow(findViewById(R.id.hostBackground).getWindowToken(), 0);
			startActivity(nextIntent2);
			break;
		default:
			super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	public String getZipcode()
	{
		String zipcode = "00000";
		if(currLocation == null)
		{
			String locationProvider = LocationManager.NETWORK_PROVIDER;
			currLocation = locationManager.getLastKnownLocation(locationProvider);
		}

		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(currLocation.getLatitude(), currLocation.getLongitude(), 1);
			zipcode = addresses.get(0).getPostalCode();
		} catch (IOException e) {
			e.printStackTrace();
//			Log.d("zipcode", "Failed to get zipcode");
		} catch (NullPointerException e){
			e.printStackTrace();
//			Log.d("zipcode", "Failed to get zipcode");
		}
//		Log.d("zipcode", zipcode);
		locationManager.removeUpdates(locationListener);
		return zipcode;
	}
	
	public LocationListener locationListener = new LocationListener()
	{
		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
//			Log.d("location listener", "location changed");
			currLocation = arg0;
			
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
	
	public String getPublicIp()
	{
		String ip = "0.0.0.0";
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://whatismyip.akamai.com/");
		HttpResponse response;
		try {
			response = client.execute(request);
			String html = "";
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder str = new StringBuilder();
			String line = null;
			line = reader.readLine();
//			Log.d("external ip", line);
			ip = line;
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return ip;
	}
	
	public String getPrivateIp()
	{
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
//		Log.d("Private ip", "Obtained: "+ip);
		return ip;
	}
	
	public void setName(String name)
	{
		myapp.myName = name;
	}
	
	public void setIp(String ip)
	{
		myapp.myIp = ip;
	}
	
	
	public void setZipcode(String zipcode)
	{
		myapp.zipcode = zipcode;
	}
	
	public void changeNameNotification()
	{
//		Log.d("Contact Server", "Name already in use");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("This party name is already in use. Please choose another.").setCancelable(false)
		.setPositiveButton("ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{

			}
		});
		AlertDialog alert = builder.create();
		alert.show();	
	}
	
	public void connectionErrorNotification()
	{
//		Log.d("Contact Server", "Error connecting");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Error connecting to server").setCancelable(false)
		.setPositiveButton("ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void noLocationNotification()
	{
//		Log.d("Contact Server", "Couldn't find your location.");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout alertLayout = new LinearLayout(this);
		TextView message = new TextView(this);
		message.setText("Couldn't find your location. Please manually enter your zipcode: ");
		final EditText zipin = new EditText(this);
		
		alertLayout.setOrientation(1);
		alertLayout.addView(message, params);
		alertLayout.addView(zipin, params);
		builder.setView(alertLayout).setCancelable(true)
		.setPositiveButton("ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				String zipcode = zipin.getText().toString();
				myapp.zipcode = zipcode;
			}
		})
		.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() 
		{	
			@Override
			public void onClick(DialogInterface arg0, int arg1) 
			{
				
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void moveToMenu()
	{
		MyApplication myapp = (MyApplication) this.getApplicationContext();
		//myapp.myName = getName();
		//myapp.zipcode = getZipcode();
		//myapp.myIp = getExternalIp();
		myapp.hosting = true;
		Intent nextIntent = new Intent(HostActivity.this, HostMenuActivity.class);
		startActivity(nextIntent);
	}
	
	private class ContactServerTask extends AsyncTask<String, Void, Integer>
	{
		String partyName, zipcode, ip;
		@Override
		//params[0] = party name
		//params[1] = zipcode
		//params[2] = ip
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			Integer result = 2;
			
			partyName = getName();
			zipcode = params[0];
			ip = getPrivateIp();
			
			Jedis jedis = null;
			try
			{
				jedis = myapp.jedisPool.getResource();
				jedis.auth(Redis.auth);


				long reply = jedis.setnx(zipcode + ":" + partyName, ip);
				if(reply == 0)
					result = 1;
				else
				{
					result = 0;
					jedis.expire(zipcode + ":" + partyName, 500);
					jedis.sadd(zipcode, partyName);
				}
			}
			catch (JedisConnectionException e)
			{
				if(jedis != null)
				{
					myapp.jedisPool.returnBrokenResource(jedis);
					jedis = null;
				}
			}
			finally
			{
				if(jedis != null)
					myapp.jedisPool.returnResource(jedis);
			}
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
				setName(partyName);
				setIp(ip);
				//setZipcode(zipcode);
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
			else if(result == 3)
			{
				noLocationNotification();
			}
		}
		
	}
}
