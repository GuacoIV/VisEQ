package com.lsu.vizeq;


import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableRow;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;



public class ProfileActivity extends Activity implements OnItemSelectedListener{
	
	public String mColor;
	Spinner spinner;
	LinearLayout customSearchLayout;
	OnClickListener submitListener;
	ActionBar actionBar;
	public MyApplication myapp;
	
	AsyncHttpClient searchClient = new AsyncHttpClient();
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using parent.getItemAtPosition(pos)
		spinner.setSelection(pos);
		SharedPreferences memory = getSharedPreferences("VizEQ", MODE_PRIVATE);
		mColor = (String) parent.getItemAtPosition(pos);
		SharedPreferences.Editor saver = memory.edit();
		saver.putString("color", mColor);
		saver.putInt("colorPos", pos);
		saver.commit();
				
		actionBar = getActionBar();
		int posi = memory.getInt("colorPos", -1);
		if (posi != -1) 
		{
			VizEQ.numRand = posi;	
			if (VizEQ.numRand == 0){
				Random r = new Random();
				VizEQ.numRand = r.nextInt(5) + 1;
			}			
			
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
		}
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    	actionBar = getActionBar();
    	SharedPreferences memory = getSharedPreferences("VizEQ", MODE_PRIVATE);
		int posi = memory.getInt("colorPos", -1);
		spinner.setSelection(posi);
		if (posi > 0) 
		{
			VizEQ.numRand = posi;	
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
		}
    }    
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{				
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);		
		actionBar = getActionBar();
		
		myapp = (MyApplication) this.getApplicationContext();
		
		EditText et = (EditText) this.findViewById(R.id.ProfileUsername);
		if(myapp.myName.equals("")) et.setText("Enter username");
		else et.setText(myapp.myName);
		
		refreshQueue();
		
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
		
		customSearchLayout = (LinearLayout) findViewById(R.id.customSearchLayout);
		final EditText searchText = (EditText) findViewById(R.id.CustomSearchField);
		final OnTouchListener rowTap;

		TabHost tabhost = (TabHost) findViewById(android.R.id.tabhost);
	    tabhost.setup();
	    TabSpec ts = tabhost.newTabSpec("tab01"); 
	    ts.setContent(R.id.tab01);
	    ts.setIndicator("Search");
	    tabhost.addTab(ts);
	
	    ts = tabhost.newTabSpec("tab02"); 
	    ts.setContent(R.id.tab02);
	    ts.setIndicator("Playlist"); 
	    tabhost.addTab(ts);
	    
	    ts = tabhost.newTabSpec("tab03");
	    ts.setContent(R.id.tab03);
	    ts.setIndicator("Profile");
	    tabhost.addTab(ts);
	    final LinearLayout customListTab = (LinearLayout) findViewById(R.id.tab02);
	    Button submit = (Button)findViewById(R.id.SubmitCustomList);
	    submit.setOnClickListener(submitListener);
	    //Animation an = new Animation();	   
	    
	    // Color Spinner
	    spinner = (Spinner) findViewById(R.id.colorspinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.color_spinner, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	
		spinner.setOnItemSelectedListener(this);
		spinner.setAdapter(adapter);
		if (posi == -1) posi = 0;
		spinner.setSelection(posi);
		
		rowTap = new OnTouchListener()
		{
	
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1)
			{
				if (arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					TableRow row = (TableRow)arg0;
					row.setBackgroundColor(Color.BLUE);
					return true;
				}
				else if (arg1.getAction() == MotionEvent.ACTION_UP)
				{					
					final TrackRow row = (TrackRow)arg0;
					row.setBackgroundColor(row.originalColor);
					AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
			        builder.setMessage(R.string.QueueTopOrBottom)
			               .setPositiveButton("Top", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   myapp.queue.add(0, row.getTrack());
			                	   refreshQueue();
			                	   customSearchLayout.removeView(row);
			                	   /*TrackRow customListRow = row;
			                	   customListRow.setOnTouchListener(null);
			                	   customSearchLayout.removeView(customListRow);
			                	   
			                	   customListTab.addView(customListRow, 0);
								if (customListTab.getChildCount() > 1)
								{
			                		    if (((TrackRow)(customListTab.getChildAt(1))).originalColor == TrackRow.color1)
			                		    {
			                		    	customListRow.setBackgroundColor(TrackRow.color2);
			                		    	customListRow.originalColor = TrackRow.color2;
			                		    }
			                		    else 
			                		    {
			                		    	customListRow.setBackgroundColor(TrackRow.color1);
			                		    	customListRow.originalColor = TrackRow.color1;
			                		    }
								}
								else
								{
									customListRow.setBackgroundColor(TrackRow.color1);
			                	    customListRow.originalColor = TrackRow.color1;
								}*/
			                	   
			                   }
			               })
			               .setNegativeButton("Bottom", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
			                	   myapp.queue.add(row.getTrack());
			                	   refreshQueue();
			                	   customSearchLayout.removeView(row);
			                	   /*TrackRow customListRow = row;
			                	   customListRow.setOnTouchListener(null);
			                	   customSearchLayout.removeView(customListRow);
			                	   customListTab.addView(customListRow);
			                	   if (customListTab.getChildCount() > 0)
									{
				                		    if (((TrackRow)(customListTab.getChildAt(customListTab.getChildCount() - 1))).originalColor == TrackRow.color1)
				                		    	customListRow.setBackgroundColor(TrackRow.color2);
				                		    else 
				                		    {
				                		    	customListRow.setBackgroundColor(TrackRow.color1);
				                		    	customListRow.originalColor = TrackRow.color1;
				                		    }
									}
									else
									{
										customListRow.setBackgroundColor(TrackRow.color1);
				                	    customListRow.originalColor = TrackRow.color1;
									}*/
			                	   
			                   }
			               });
			        //builder.create();
			        builder.show();
					return true;
				}
				else if (arg1.getAction() == MotionEvent.ACTION_CANCEL)
				{
					TrackRow row = (TrackRow)arg0;
					row.setBackgroundColor(row.originalColor);
					return true;
				}
				return false;
			}
		
		};
		
		
		findViewById(R.id.SearchOK).setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View arg0)
			{
				// TODO Auto-generated method stub
				customSearchLayout.removeAllViews();
				String strSearch = searchText.getText().toString();
				strSearch = strSearch.replace(' ', '+');
				searchText.clearFocus();
				searchClient.get("http://ws.spotify.com/search/1/track.json?q=" + strSearch, new JsonHttpResponseHandler() {
	
					public void onSuccess(JSONObject response) {
						try {
							JSONArray tracks = response.getJSONArray("tracks");
							//JSONObject track = tracks.getJSONObject(0);
							for (int i = 0; i < tracks.length(); i++)
							{
								String trackName = tracks.getJSONObject(i).getString("name");
								String trackArtist = tracks.getJSONObject(i).getJSONArray("artists").getJSONObject(0).getString("name");
								String uri = tracks.getJSONObject(i).getString("href");
								String trackAlbum = tracks.getJSONObject(i).getJSONObject("album").getString("name");
								//Log.d("Search", trackName + ": " + trackArtist);
								TrackRow tableRowToAdd = new TrackRow(ProfileActivity.this);
								TextView textViewToAdd = new TextView(ProfileActivity.this);
								TextView textTwoViewToAdd = new TextView(ProfileActivity.this);
								tableRowToAdd.mTrack = trackName;
								tableRowToAdd.mArtist = trackArtist;
								tableRowToAdd.mAlbum = trackAlbum;
								tableRowToAdd.mUri = uri;
								if (i % 2 == 0) 
								{
									tableRowToAdd.setBackgroundColor(TrackRow.color1);
									tableRowToAdd.originalColor = TrackRow.color1;
								}
								else
								{
									tableRowToAdd.setBackgroundColor(TrackRow.color2);
									tableRowToAdd.originalColor = TrackRow.color2;
								}
								textViewToAdd.setText(trackName);
								textTwoViewToAdd.setText(trackArtist);
								textViewToAdd.setTextSize(20);
								textTwoViewToAdd.setTextColor(Color.DKGRAY);
								LinearLayout linearLayoutToAdd = new LinearLayout(ProfileActivity.this);
								linearLayoutToAdd.setOrientation(LinearLayout.VERTICAL);
								linearLayoutToAdd.addView(textViewToAdd);
								linearLayoutToAdd.addView(textTwoViewToAdd);
								tableRowToAdd.setOnTouchListener(rowTap);
								tableRowToAdd.addView(linearLayoutToAdd);
								customSearchLayout.addView(tableRowToAdd);
							}
							
							//mAlbumUri = album.getString("spotify");
							//mImageUri = album.getString("image");
							// Now get track details from the webapi
							//LSU Team, it looks like .get(http://ws.spotify.com/search/1/track?q=kaizers+orchestra) is the way to do a search
							//mSpotifyWebClient.get("http://ws.spotify.com/lookup/1/.json?uri=" + album.getString("spotify") + "&extras=track", SpotifyWebResponseHandler);
	
						} 
						catch (JSONException e) {
							throw new RuntimeException("Could not parse the results");
						}
					}
				});
			}
		});
		findViewById(R.id.SubmitCustomList).setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(!isNetworkAvailable())
				{
					noNetworkNotification();
				}
				else if(myapp.hostAddress == null)
				{
					noHostNotification();
				}
				else
				{
					Log.d("yo", "yo");
					sendRequest();
					Log.d("lol","lol");
				}
			}

		});
	}
	
	public void updateName(View view)
	{
		EditText et = (EditText) findViewById(R.id.ProfileUsername);
		myapp.myName = et.getText().toString();
	}
	
	public void noNetworkNotification()
	{
		//Log.d("Contact Server", "Name already in use");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("No network connection").setCancelable(false)
		.setPositiveButton("ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{

			}
		});
		AlertDialog alert = builder.create();
		alert.show();	
	}
	
	public void noHostNotification()
	{
		//Log.d("Contact Server", "Name already in use");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You haven't joined a party yet. :(").setCancelable(false)
		.setPositiveButton("ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{

			}
		});
		AlertDialog alert = builder.create();
		alert.show();	
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	

	public void sendRequest()
	{
		new Thread(new Runnable()
		{
				@Override
				public void run() 
				{
					// TODO Auto-generated method stub
					DatagramSocket sendSocket;
					try 
					{
							sendSocket = new DatagramSocket();
							//make a packet containing all elements with newlines between each
							for (int j = 0; j < myapp.queue.size(); j++)
							{
								Log.d("SendRequestTask", "Sending: "+myapp.queue.get(j).mTrack);
								byte[] requestHeader = "request\n".getBytes();
								byte[] backslashN = "\n".getBytes();
								byte[] albumBytes = myapp.queue.get(j).mAlbum.getBytes();
								byte[] artistBytes = myapp.queue.get(j).mArtist.getBytes();
								byte[] requesterBytes = myapp.myName.getBytes(); //myapp.queue.get(j).mRequester.getBytes(); //NO
								byte[] trackBytes= myapp.queue.get(j).mTrack.getBytes();
								byte[] uriBytes = myapp.queue.get(j).mUri.getBytes();
								ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
								outputStream.write(requestHeader);
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
								byte[] sendData = outputStream.toByteArray();
								InetAddress ip = RoleActivity.myapp.hostAddress;
								DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, 7770);
								sendSocket.send(sendPacket);
							}
							sendSocket.close();
							myapp.queue.clear();
					} 
					catch (Exception e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					runOnUiThread(new Runnable()
					{

						@Override
						public void run() {
							// TODO Auto-generated method stub
							refreshQueue();
						}
						
					});
				}
				
		}).start();
		
		
	}
	
	public void refreshQueue()
	{
		LinearLayout queueTab = (LinearLayout) findViewById(R.id.profile_queue);
		queueTab.removeAllViews();	//remove everything that's there
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		Log.d("refreshQueue", "refreshing");
		/*---color stuff---*/
		//Calculate start and end colors
		int startColor = 0;
		int endColor = 0;
		switch (VizEQ.numRand)
		{
			case 1:
				startColor = getResources().getColor(R.color.Red); //203, 32, 38
				endColor = Color.rgb(203+50, 32+90, 38+90);
				break;
			case 2:
				startColor = getResources().getColor(R.color.Green);//100, 153, 64
				endColor = Color.rgb(100+90, 153+90, 64+90);
				break;
			case 3:
				startColor = getResources().getColor(R.color.Blue); //0, 153, 204
				endColor = Color.rgb(0+90, 153+90, 204+50);
				break;
			case 4:
				startColor = getResources().getColor(R.color.Purple); //155, 105, 172
				endColor = Color.rgb(155+70, 105+70, 172+70);
				break;
			case 5:
				startColor = getResources().getColor(R.color.Orange); //245, 146, 30
				endColor = Color.rgb(245, 146+90, 30+90);
				break;
		}
		
		int redStart = Color.red(startColor);
		int redEnd = Color.red(endColor);
		int addRed = (redEnd - redStart)/15;
		
		int greenStart = Color.green(startColor);
		int greenEnd = Color.green(endColor);
		int addGreen = (greenEnd - greenStart)/15;
		
		int blueStart = Color.blue(startColor);
		int blueEnd = Color.blue(endColor);
		int addBlue = (blueEnd - blueStart)/15;
		
		/*---queue stuff---*/
		
		for(int i=0; i<myapp.queue.size(); i++)
		{
			TrackRow queueRow = new TrackRow(this.getBaseContext(), myapp.queue.get(i).mTrack, myapp.queue.get(i).mAlbum, myapp.queue.get(i).mArtist, myapp.queue.get(i).mUri);
			queueRow.setOnTouchListener(null);
			int r,g,b;
			
			if (i>15) 
			{
				r = redEnd;
				g = greenEnd;
				b = blueEnd;
			}
			else
			{
				r = redStart + addRed * i;
				g = greenStart + addGreen * i;
				b = blueStart + addBlue * i;
			}
			queueRow.setBackgroundColor(Color.argb(255, r, g, b));
			params.setMargins(0, 2, 0, 2);
			queueTab.addView(queueRow, params);
			Log.d("refresh queue", "adding row to tab");
		}
	}

}
