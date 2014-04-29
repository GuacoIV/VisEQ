package com.lsu.vizeq;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class HostMenuActivity extends Activity
{
	MyApplication myapp;
	ActionBar actionBar;
	
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
	
	public void serverHeartbeat()
	{
		final MyApplication myapp = (MyApplication) this.getApplicationContext();
		new Thread(new Runnable()
		{

			@Override
			public void run() 
			{
				while(myapp.hosting)
				{
					long test = 0;
					while(test != 1)
					{
						Jedis jedis = myapp.jedisPool.getResource();
						try
						{
							jedis.auth(Redis.auth);
							Log.d("heartbeat", "sending heartbeat");
							//jedis.set(myapp.zipcode + ":" + myapp.myName, myapp.myIp);
							test = jedis.expire(myapp.zipcode + ":" + myapp.myName, 5);
							while(test != 1)
							{
								jedis.set(myapp.zipcode + ":" + myapp.myName, myapp.myIp);
								test = jedis.expire(myapp.zipcode + ":" + myapp.myName, 5);
							}
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
					}
					try {
						Thread.sleep(3000L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}).start();
	}
	
	
	public void userHeartbeat()
	{
		new Thread(new Runnable()
			{
				@Override
				public void run() {
					DatagramSocket listenSocket, sendSocket;
	
					try {
						listenSocket = new DatagramSocket(7772);
						sendSocket = new DatagramSocket();
					
						while(myapp.hosting)
						{
							Iterator< Entry<String, InetAddress> > it = myapp.connectedUsers.entrySet().iterator();
							byte [] ping = new byte[1024];
							byte [] ack = new byte[1024];
							ping = "ping".getBytes();
							
							while(it.hasNext())
							{
								Entry<String, InetAddress> currEntry = it.next();
								InetAddress currIp = currEntry.getValue();
								final String guestName = currEntry.getKey();
								DatagramPacket pingPacket = new DatagramPacket(ping, ping.length, currIp, 7772);
								DatagramPacket ackPacket = new DatagramPacket(ack, ack.length);
								try
								{
									sendSocket.send(pingPacket);
									listenSocket.setSoTimeout(5000);
									listenSocket.receive(ackPacket);
								}
								catch(InterruptedIOException e)
								{
									it.remove();
									//remove user from party
									runOnUiThread(new Runnable()
									{

										@Override
										public void run() {
											// TODO Auto-generated method stub
											Log.d("Removing Guest ", guestName);
											refreshLists();
										}
										
									});
									
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
							}
							Thread.sleep(3000L);
						}
						sendSocket.close();
						listenSocket.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}).start();		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_menu);
		Log.d("Flow", "onCreate HostMenu");
		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));
		Log.d("Flow", "onStart HostMenu");
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
		SeekBar freqSlider = new SeekBar(this);
		RelativeLayout.LayoutParams params =  new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(3,3,3,3);
		freqSlider.setLayoutParams(params);
		freqSlider.setMax(7);
		freqSlider.setProgress(0);
		((ViewGroup) findViewById(R.id.HostMenuView)).addView(freqSlider);
		
		final Dialog dialog = new Dialog(HostMenuActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.activity_host_sound_visualization);
		final Window window = dialog.getWindow();
		window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		
		freqSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                PlayerActivity.BAND_TO_FLASH = progress - 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {;
                
               
               // dialog.show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            	//dialog.dismiss();
            	//dialog.hide();
            }

        });
		
		myapp = (MyApplication) this.getApplicationContext();		
		
		serverHeartbeat();
		//userHeartbeat();
		
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
						if (message.equals("request"))
						{
							/*Protocol
							 * 			outputStream.write(requestHeader);
										outputStream.write(albumBytes);
										outputStream.write(backslashN);
										outputStream.write(artistBytes);
										outputStream.write(backslashN);
										outputStream.write(requesterBytes);
										outputStream.write(backslashN);
										outputStream.write(trackBytes);
										outputStream.write(backslashN);
										outputStream.write(uriBytes);
										outputStream.write(backslashN);
							 */
							Track request = new Track();
							request.mAlbum = PacketParser.getArgs(receivedPacket)[0];
							request.mArtist = PacketParser.getArgs(receivedPacket)[1];
							request.mRequester = PacketParser.getArgs(receivedPacket)[2];
							request.mTrack = PacketParser.getArgs(receivedPacket)[3];
							request.mUri = PacketParser.getArgs(receivedPacket)[4];
							myapp.requests.add(request);
							Log.d("listen thread", "Request added!");
							Log.d("listen thread", "request album = " + request.mAlbum);
							Log.d("listen thread", "request artist = " + request.mArtist);
							Log.d("listen thread", "request requester = " + request.mRequester);
							Log.d("listen thread", "request track = " + request.mTrack);
							Log.d("listen thread", "request uri = " + request.mUri);
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
		
		findViewById(R.id.sound_viz).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent nextIntent = new Intent(HostMenuActivity.this, HostSoundVisualizationActivity.class);
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
						String sendString = "accept\n" + VizEQ.nowPlaying + "\njunkk";
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
			break;
		case R.id.about:
			Intent nextIntent2  = new Intent(HostMenuActivity.this, AboutActivity.class);
			startActivity(nextIntent2);
			break;
		}
		return true;
	}
	
}
