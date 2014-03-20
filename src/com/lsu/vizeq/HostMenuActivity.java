package com.lsu.vizeq;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

import com.lsu.vizeq.R.color;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class HostMenuActivity extends Activity
{
	MyApplication myapp;
	
	public String getIpString()
	{
		WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    	DhcpInfo dhcp = wifi.getDhcpInfo();
    	ByteBuffer b = ByteBuffer.allocate(4);
    	b.putInt(dhcp.ipAddress);
    	InetAddress myAddress;
    	String ipString = "fail";
    	try {
			 myAddress = InetAddress.getByAddress(b.array());
			 ipString = myAddress.getHostAddress();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ipString;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_menu);
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
		myapp = (MyApplication) this.getApplicationContext();
		
		new Thread( new Runnable()
		{
			public void run()
			{
				
				try
				{
					DatagramSocket listenSocket = new DatagramSocket(7770);
					DatagramSocket sendSocket = new DatagramSocket();
					while(true)
					{
						//listen for search
						Log.d("listen thread","listening");
						byte[] receiveData = new byte[1024];
						DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);
						listenSocket.receive(receivedPacket);
						Log.d("listen thread", "packet received");
						
						InetAddress ip = receivedPacket.getAddress();
						int port = receivedPacket.getPort();
						
						String data = new String(receivedPacket.getData());
						if (data.substring(0, 6).equals("search"))
						{
							Log.d("listen thread", "search received from "+ip.toString()+" "+ip.getHostAddress());
							//send back information
							String information = "found ";
							information += (myapp.myName);
							Log.d("listen thread", "sending back"+information);
							
							//make a packet
							byte[] sendData = new byte[1024];
							sendData = information.getBytes();
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, 7770);
							sendSocket.send(sendPacket);
						}
						
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
		
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
		try {
			myapp.connectedUsers.put(nameField.getText().toString(), InetAddress.getByName(ipField.getText().toString()));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent nextIntent  = new Intent(HostMenuActivity.this, SettingsActivity.class);
			startActivity(nextIntent);
		}
		return true;
	}
}
