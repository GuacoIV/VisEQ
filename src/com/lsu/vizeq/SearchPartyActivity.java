package com.lsu.vizeq;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

enum alertType { NO_ERROR, LOCATION_ERROR, SERVER_ERROR, NO_PARTIES_ERROR, SAME_NAME_ERROR };

public class SearchPartyActivity extends BackableActivity {
	
	public LocationManager locationManager;
	MyApplication myapp;
	SearchPartyActivity thisActivity;
	ActionBar actionBar;
	Location currLocation = null;
	
	@Override
	protected void onStart(){
		super.onStart();
		setupActionBar();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		myapp = (MyApplication) this.getApplicationContext();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_party);
		
		EditText et = (EditText) findViewById(R.id.username_box);
		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		String userName = memory.getString("username", "");
		if(userName.equals("")) et.setHint("Enter username");
		else et.setText(userName);
		thisActivity = this;
		
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		
		// Show the Up button in the action bar.
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
		setupActionBar();
		
		Typeface font = Typeface.createFromAsset(getAssets(), "Mission Gothic Regular.otf");
		Button searchParties = (Button) findViewById(R.id.button1);
		searchParties.setTypeface(font);
		
		searchParties.setOnTouchListener(new OnTouchListener()
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
			
	}
	
    public InetAddress getBroadcastAddress() throws IOException
    {
    	WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    	DhcpInfo dhcp = wifi.getDhcpInfo();
    	
    	int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
    	byte[] quads = new byte[4];
    	for(int k = 0; k < 4; k++)
    	{
    		quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
    	}
    	return InetAddress.getByAddress(quads);
    }
    
    public boolean checkIfNameEntered()
    {
    	boolean nameEntered = false;
    	
    	EditText et = (EditText) findViewById(R.id.username_box);
    	
    	String username = et.getText().toString();
//    	Log.d("username", username);
    	
    	if(!username.isEmpty())
    	{
    		nameEntered = true;
    		myapp.myName = username;
    	}
    			
    	return nameEntered;
    }
    
    public void searchForPartiesServer(View view)
    {
    	searchServer();
    
    }
	
	public void refreshPartyList(ArrayList<String> partyNames)
	{
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(10, 2, 10, 2);
		LinearLayout nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
		LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
		
		nameLayout.removeAllViews();
		buttonLayout.removeAllViews();
		for(int i=0; i<partyNames.size(); i++)
		{
			final String name = partyNames.get(i);
			
			//name of party
			TextView tv = new TextView(this);
			tv.setText(name);
			tv.setHeight(100);
			tv.setTextSize(20.f);
			params.gravity = Gravity.CENTER;
			tv.setLayoutParams(params);
			
			//join button
			Button b = new Button(this);
			b.setText("Join");
			b.setWidth(75);
			b.setHeight(100);
			b.setLayoutParams(params);
			b.setBackgroundColor(Color.WHITE);
			b.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0) {
//					Log.d("Join", "Joining party");
					new JoinTaskServer().execute(myapp.zipcode + ":" + name);
					//new JoinTask().execute((InetAddress) pairs.getValue());
				}
			});
			
			b.setOnTouchListener(new OnTouchListener()
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
			
			//add them
			nameLayout.addView(tv);
			buttonLayout.addView(b);
		}
	}
	
	
	public void noPartiesNotification()
	{
//		Log.d("Contact Server", "No parties found");
		AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
		builder.setMessage("No parties found").setCancelable(false)
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
//		Log.d("Contact Server", "Error Connecting");
		AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
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
//		Log.d("Contact Server", "no location");
		AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		final LinearLayout alertLayout = new LinearLayout(this);
		TextView message = new TextView(this);
		message.setText("Couldn't find your location. Please manually enter your zipcode: ");
		final EditText zipin = new EditText(this);
		
		alertLayout.setOrientation(1);
		alertLayout.addView(message, params);
		alertLayout.addView(zipin, params);
		builder.setView(alertLayout).setCancelable(true)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				String zipcode = zipin.getText().toString();
				myapp.zipcode = zipcode;
				searchForPartiesServer(alertLayout);
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
	
	
	private void searchServer()
	{
    	if(myapp.zipcode == null || myapp.zipcode.compareTo("00000")==0)
    		myapp.zipcode = getZipcode();
    	if(myapp.zipcode.equals("00000"))
    	{
    		noLocationNotification();
    		return;
    	}
    	
    	final String zipcode = myapp.zipcode;
    	
    	Thread contactServerThread = new Thread(new Runnable()
    	{
			@Override
			public void run() {
				
				alertType alert = alertType.SERVER_ERROR;
				final ArrayList<String> partyNames = new ArrayList<String>();
				Jedis jedis = null;
				try
				{
					jedis = myapp.jedisPool.getResource();
					jedis.auth(Redis.auth);
					Set<String> names = jedis.smembers(zipcode);
					
					//Check if they all have valid ips
					Iterator<String> it = names.iterator();
					
					while(it.hasNext())
					{
						String name = it.next();
						boolean check = jedis.exists(zipcode + ":" + name);
						if(!check)
						{
							jedis.srem(zipcode, name);
							it.remove();
						}
						else partyNames.add(name);
					}
					
					if (partyNames.size() <= 0) alert = alertType.NO_PARTIES_ERROR;
					else alert = alertType.NO_ERROR;
					if (zipcode.equals("00000")) alert = alertType.LOCATION_ERROR;
				}
				catch (JedisConnectionException e)
				{
					e.printStackTrace();
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
				
				final alertType finalAlert = alert;
				runOnUiThread(new Runnable()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
						refreshPartyList(partyNames);
						showAlert(finalAlert);
					}
					
				});	
			}
    	});
    	contactServerThread.start();
	}
	
	private void showAlert(alertType type)
	{
		switch(type)
		{
		case SERVER_ERROR: connectionErrorNotification(); break;
		case LOCATION_ERROR: noLocationNotification(); break;
		case NO_PARTIES_ERROR: noPartiesNotification(); break;
		}
	}
	
	private class ContactServerTask extends AsyncTask<String, Integer, ArrayList<String>>
	{

		@Override
		//params[0] = party name
		//params[1] = zipcode
		protected ArrayList<String> doInBackground(String... params) {
//			Log.d("ContactServerTask", "Trying to contact server");
			String partyName = params[0];
			String zipcode = params[1];
			Log.d("zipcode: ", zipcode);
			Integer result = 2;
			ArrayList<String> partyNames = new ArrayList<String>();
			Jedis jedis = myapp.jedisPool.getResource();
			try
			{
				jedis.auth(Redis.auth);
				Set<String> names = jedis.smembers(zipcode);
				
				//Check if they all have valid ips
				Iterator<String> it = names.iterator();
				
				while(it.hasNext())
				{
					String name = it.next();
					boolean check = jedis.exists(zipcode + ":" + name);
					if(!check)
					{
						jedis.srem(zipcode, name);
						it.remove();
					}
					else partyNames.add(name);
				}
				
				if (partyNames.size() <= 0) result = 1;
				else result = 0;
				if (zipcode.equals("00000")) result = 3;
				publishProgress(result);
			}
			catch (JedisConnectionException e)
			{
				e.printStackTrace();
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
			return partyNames;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
//			Log.d("ContactServerTask", "Finished");
			refreshPartyList(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			if(values[0] == 0)
			{
				//good to go
			}
			else if(values[0] == 1)
			{
				noPartiesNotification();
			}
			else if(values[0] == 2)
			{
				connectionErrorNotification();
			}
			else if(values[0] == 3)
			{
				noLocationNotification();
			}
			
		}
		
	}
	
	private void joinParty()
	{
		
	}
	
	public class JoinTaskServer extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... params) {
			//check if not entered username
			if(!checkIfNameEntered()) return "Must enter name first";
			String ip = "0.0.0.0";
			Jedis jedis = myapp.jedisPool.getResource();
			try
			{
				jedis.auth(Redis.auth);
				ip = jedis.get(params[0]);
			}
			catch (JedisConnectionException e)
			{
				e.printStackTrace();
				if(jedis != null)
				{
					myapp.jedisPool.returnBrokenResource(jedis);
					jedis = null;
				}
				return "Error connecting to server";
			}
			finally
			{
				if(jedis != null)
					myapp.jedisPool.returnResource(jedis);
			}
		
			DatagramSocket sendSocket;
			DatagramSocket listenSocket;
			// TODO Auto-generated method stub
			//send join request
//			Log.d("join party", "Clicked");
			try {
				sendSocket = new DatagramSocket();
				listenSocket = new DatagramSocket(7771);
				//send join request
				byte[] sendData = new byte[1024];
				byte[] receiveData = new byte[1024];
				String searchString = "join\n"+myapp.myName;
				sendData = searchString.getBytes();
				
				
//				Log.d("join thru server", "host ip: "+ip);
				
				InetAddress ipaddress = InetAddress.getByName(ip);
				
//				Log.d("join party", "Sending to " + ipaddress.getHostName());
				DatagramPacket searchPacket = new DatagramPacket(sendData, sendData.length, ipaddress, 7771);
				sendSocket.send(searchPacket);
//				Log.d("join party", "join sent to "+ipaddress.getHostName());
				//now wait for response
				boolean joined = false;
				while(!joined)
				{
//					Log.d("listen for join", "listening");
					DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
					listenSocket.receive(receivePacket);
					String message = PacketParser.getHeader(receivePacket);
//					Log.d("listen for join", message);
					if(message.equals("accept"))
					{
//						Log.d("listen for join", "we are joined");
						myapp.joined = true;
						myapp.hostAddress = receivePacket.getAddress();
						joined = true;
						VizEQ.nowPlaying = PacketParser.getArgs(receivePacket)[0];
					}
				}
				return "Joined!";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return "Failed!";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			final String s = result;
			AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
			builder.setMessage(result).setCancelable(false)
			.setPositiveButton("ok", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					if(s.equals("Joined!"))
					{	
						Intent nextIntent = new Intent(SearchPartyActivity.this, SoundVisualizationActivity.class);
						startActivity(nextIntent);
					}
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		
	}
	

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_party, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent nextIntent  = new Intent(SearchPartyActivity.this, ProfileActivity.class);
			startActivity(nextIntent);
			break;
		case R.id.about:
			Intent nextIntent2  = new Intent(SearchPartyActivity.this, AboutActivity.class);
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
			//String locationProvider = LocationManager.GPS_PROVIDER;
			//currLocation = locationManager.getLastKnownLocation(locationProvider);
			zipcode = "00000";
			return zipcode;
		}

		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(currLocation.getLatitude(), currLocation.getLongitude(), 1);
			zipcode = addresses.get(0).getPostalCode();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Log.d("zipcode", zipcode);
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
	
}
