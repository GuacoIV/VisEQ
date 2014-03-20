package com.lsu.vizeq;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchPartyActivity extends Activity {
	
	MyApplication myapp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		myapp = (MyApplication) this.getApplicationContext();
		super.onCreate(savedInstanceState);
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
		RelativeLayout myRelativeLayout = (RelativeLayout) findViewById(R.id.searchpartylayout);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		Button b = new Button(this);
		b.setText("Hi");
		b.setLayoutParams(params);
		myRelativeLayout.addView(b);
		
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
					receiveSocket.receive(receivePacket);
					receiveString = new String(receivePacket.getData());
					if(receiveString.substring(0, 5).equals("found"))
					{
						found = true;
						partyName = receiveString.substring(6, receiveString.length());
						partyIp = receivePacket.getAddress().getHostAddress();
						myapp.connectedUsers.put(partyName, InetAddress.getByName(partyIp));
					}
				}
				Log.d("search thread", "received "+partyName+" "+partyIp);
				result = "Found parties:";
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			TextView resultText = (TextView) findViewById(R.id.resultText);
			resultText.setText(result);
			refreshPartyList();
			receiveSocket.close();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
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
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
