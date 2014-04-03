package com.lsu.vizeq;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
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
		
		
		switch (VizEQ.numRand)
		{
			case 0:;
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));
				break;
			case 1:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));
				break;
			case 2:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));
				break;
			case 3:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));
				break;
			case 4:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
				break;
		}
		
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
						
						String message = PacketParser.getHeader(receivedPacket);
						if (message.equals("search"))
						{
							Log.d("listen thread", "search received from "+ip.toString()+" "+ip.getHostAddress());
							//send back information
							String information = "found\n"+myapp.myName;
							Log.d("listen thread", "sending back "+information+ " to "+ip.getHostAddress());
							
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
		
		new ListenForJoinRequestTask().execute();
		
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
			String ip = ((InetAddress) pairs.getValue()).getHostAddress();
			nameString += (name + "\n");
			ipString += (ip + "\n");
		}
		//iterate through usersConnected
		
		nameList.setText(nameString);
		ipList.setText(ipString);
	}
	
	private class ListenForJoinRequestTask extends AsyncTask <Void, Void, Void>
	{
		DatagramSocket listenSocket;
		DatagramSocket sendSocket;
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				listenSocket = new DatagramSocket(7771);
				sendSocket = new DatagramSocket();
				while(true)
				{
					byte listenData[] = new byte[1024]; 
					DatagramPacket listenPacket = new DatagramPacket(listenData, listenData.length);
					listenSocket.receive(listenPacket);
					String message = PacketParser.getHeader(listenPacket);
					if(message.equals("join"))
					{
						String clientName = PacketParser.getArgs(listenPacket)[0];
						InetAddress clientIp = listenPacket.getAddress();
						myapp.connectedUsers.put(clientName, clientIp);
						Log.d("join listener", "added "+clientName+" "+clientIp.getHostName());
						byte sendData[] = new byte[1024];
						String sendString = "accept";
						sendData = sendString.getBytes();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIp, 7771);
						sendSocket.send(sendPacket);
						Log.d("accept thread", "accept sent");
						publishProgress();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			listenSocket.close();
			sendSocket.close();
		}

		@Override
		protected void onCancelled(Void result) {
			// TODO Auto-generated method stub
			sendSocket.close();
			listenSocket.close();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			refreshLists();
		}
		
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
			Intent nextIntent  = new Intent(HostMenuActivity.this, ProfileActivity.class);
			startActivity(nextIntent);
		}
		return true;
	}
}
