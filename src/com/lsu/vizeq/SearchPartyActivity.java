package com.lsu.vizeq;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchPartyActivity extends Activity {
	
	MyApplication myapp;
	SearchPartyActivity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		myapp = (MyApplication) this.getApplicationContext();
		super.onCreate(savedInstanceState);
		thisActivity = this;
		setContentView(R.layout.activity_search_party);
		
		// Show the Up button in the action bar.
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
		setupActionBar();
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
    	Log.d("username", username);
    	
    	if(!username.isEmpty())
    	{
    		nameEntered = true;
    		myapp.myName = username;
    	}
    			
    	return nameEntered;
    }
	
	public void searchForParties(View view)
	{	
		new ListPartiesTask().execute();
		new Thread(new Runnable()
		{

			@Override
			public void run() {
				DatagramSocket sendSocket;
				try {
					sendSocket = new DatagramSocket();
					//send search signal
					byte[] sendData = new byte[1024];
					String searchString = "search";
					sendData = searchString.getBytes();
					DatagramPacket searchPacket = new DatagramPacket(sendData, sendData.length, getBroadcastAddress(), 7770);
					//send it a bunch of times
					for(int i=0; i<100; i++)
					{
						sendSocket.send(searchPacket);
						Thread.sleep(10L);
					}
					Log.d("search party", "search sent");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void refreshPartyList()
	{
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		LinearLayout nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
		LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
		
		Iterator<Map.Entry<String,InetAddress>> it = myapp.connectedUsers.entrySet().iterator();
		while(it.hasNext())
		{
			final Map.Entry<String,InetAddress> pairs = (Map.Entry<String,InetAddress>) it.next();
			
			//name of party
			TextView tv = new TextView(this);
			tv.setText((String)pairs.getKey());
			tv.setWidth(200);
			tv.setHeight(60);
			tv.setTextSize(20.f);
			tv.setLayoutParams(params);
			
			//join button
			Button b = new Button(this);
			b.setText("Join");
			b.setWidth(75);
			b.setHeight(60);
			b.setLayoutParams(params);
			b.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View arg0) {
					new JoinTask().execute((InetAddress) pairs.getValue());
				}
			});
			
			//add them
			nameLayout.addView(tv);
			buttonLayout.addView(b);
		}
		
	}
	
	private class ListPartiesTask extends AsyncTask<Void, String, String>
	{
		DatagramSocket receiveSocket;
		@Override
		protected String doInBackground(Void... arg0) {
			//listen for incoming party info
			String result = "Unable to find parties. Make sure you're connected to Wifi and try again.";
			try {
				receiveSocket = new DatagramSocket(7770);
				receiveSocket.setSoTimeout(2000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
				String partyName = "";
				String partyIp = "";
				
				//listen for response
				boolean found = false;
				//base on time elapsed to receive more parties (or no parties) - later
				while (!found)
				{
					byte[] receiveData = new byte[1024];
					String receiveString;
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					try
					{
						receiveSocket.receive(receivePacket);
						receiveString = new String(receivePacket.getData());
						if(receiveString.substring(0, 5).equals("found"))
						{
							found = true;
							partyName = receiveString.substring(6, receiveString.length());
							partyIp = receivePacket.getAddress().getHostAddress();
							myapp.connectedUsers.put(partyName, InetAddress.getByName(partyIp));
							result = "Found parties:";
							publishProgress();
						}
					}
					catch(Exception e)
					{
						if(e.getClass().equals(SocketTimeoutException.class))
						{
							found = true;
						}
						else e.printStackTrace();
					}
				}

				

			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			TextView resultText = (TextView) findViewById(R.id.resultText);
			resultText.setText(result);

			receiveSocket.close();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			refreshPartyList();
		}
		
	}
	
	public class JoinTask extends AsyncTask<InetAddress, Void, String>
	{

		@Override
		protected String doInBackground(InetAddress... arg0) {
			
				//check if not entered username
				if(!checkIfNameEntered()) return "Must enter name first";
			
				DatagramSocket sendSocket;
				DatagramSocket listenSocket;
				// TODO Auto-generated method stub
				//send join request
				Log.d("join party", "Clicked");
				try {
					sendSocket = new DatagramSocket();
					listenSocket = new DatagramSocket(7771);
					//send join request
					byte[] sendData = new byte[1024];
					byte[] receiveData = new byte[1024];
					String searchString = "join "+myapp.myName;
					sendData = searchString.getBytes();
					InetAddress ipaddress = arg0[0];
					
					Log.d("join party", "Sending to " + ipaddress.getHostName());
					DatagramPacket searchPacket = new DatagramPacket(sendData, sendData.length, ipaddress, 7771);
					sendSocket.send(searchPacket);
					Log.d("join party", "join sent to "+ipaddress.getHostName());
					//now wait for response
					boolean joined = false;
					while(!joined)
					{
						Log.d("listen for join", "listening");
						DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);
						listenSocket.receive(receivePacket);
						String message = new String(receivePacket.getData());
						Log.d("listen for join", message);
						if(message.substring(0, 6).equals("accept"))
						{
							Log.d("listen for join", "we are joined");
							myapp.joined = true;
							myapp.hostAddress = receivePacket.getAddress();
							joined = true;
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
			//move to dummy content
			final String s = result;
			AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
			builder.setMessage(result).setCancelable(false)
			.setPositiveButton("ok", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					if(!s.equals("Must enter name first"))
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
		}
		return true;
	}

}
