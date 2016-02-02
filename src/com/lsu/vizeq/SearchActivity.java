package com.lsu.vizeq;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends BackableActivity implements UseSearchResults
{
	LinearLayout searchLayout;
	MyApplication myapp;

	ActionBar actionBar;
	LinearLayout queueTab;
	ProgressBar spinner;
	int colorForPlaylists;
	OnTouchListener swipeListener;
	OnTouchListener rowTap;
	public void refreshQueue()
	{
		queueTab = (LinearLayout) findViewById(R.id.host_queue);
		queueTab.removeAllViews();	//remove everything that's there
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		/*---color stuff---*/
		//Calculate start and end colors
		int startColor = 0;
		int endColor = 0;
		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		int posi = memory.getInt("colorPos", -1);
		if (posi > 0) VizEQ.numRand = posi;	
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
		
		for(int i=PlayerActivity.mIndex; i<myapp.queue.size(); i++)
		{
			TrackRow queueRow = new TrackRow(this.getBaseContext(), myapp.queue.get(i).mTrack, myapp.queue.get(i).mAlbum, myapp.queue.get(i).mArtist, myapp.queue.get(i).mUri, myapp.queue.get(i).mThumbnail);
			queueRow.setOnTouchListener(swipeListener);
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
//			Log.d("refresh queue", "adding row to tab");

		}
	}
	
	public void NoConnectionNotification()
	{
//		Log.d("Contact Server", "Error connecting");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Unable to search - no network connection").setCancelable(false)
		.setPositiveButton("ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
//		Log.d("Flow", "onCreate SearchActivity");
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));	
		spinner = (ProgressBar)findViewById(R.id.spinner);
		
		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		int posi = memory.getInt("colorPos", -1);
		if (posi > 0) VizEQ.numRand = posi;		
		switch (VizEQ.numRand)
		{
			case 1:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Red)));
				colorForPlaylists = getResources().getColor(R.color.Red);
				break;
			case 2:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Green)));	
				colorForPlaylists = getResources().getColor(R.color.Green);
				break;
			case 3:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Blue)));
				colorForPlaylists = getResources().getColor(R.color.Blue);
				break;
			case 4:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Purple)));
				colorForPlaylists = getResources().getColor(R.color.Purple);
				break;
			case 5:
				actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Orange)));
				colorForPlaylists = getResources().getColor(R.color.Orange);
				break;		
		}
		
		myapp = (MyApplication) this.getApplicationContext();
		
		searchLayout = (LinearLayout) findViewById(R.id.SearchLayout);
		final EditText searchText = (EditText) findViewById(R.id.SearchField);
		final OnTouchListener playlistTap;
		TabHost tabhost = (TabHost) findViewById(android.R.id.tabhost);
	    tabhost.setup();
	    
	    TabSpec ts = tabhost.newTabSpec("tag1"); 
	    ts.setContent(R.id.tab1);
	    ts.setIndicator("Search");
	    tabhost.addTab(ts);
	    ts = tabhost.newTabSpec("tag2"); 
	    ts.setContent(R.id.tab2);
	    ts.setIndicator("Queue");  
	    tabhost.addTab(ts);
	    ts = tabhost.newTabSpec("tag3");
	    ts.setContent(R.id.tab3);
	    ts.setIndicator("Playlists");
	    tabhost.addTab(ts);
	    //ts= tabhost.newTabSpec("tag3");
	    //ts.setContent(R.id.tab3);
	    //ts.setIndicator("Third Tab");
	    //tabhost.addTab(ts);
	    //for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++)
		//{
			//tabhost.getTabWidget().getChildAt(i).setBackgroundColor(getResources().getColor(R.color.LightGreen));
		//}
	    refreshQueue();
	    
	    swipeListener = new OnTouchListener()
	    {
	    	View lastTouched;

			Point screenSize = new Point();
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1)
			{
				TrackRow row = (TrackRow)arg0;
				if (arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					lastTouched = arg0;
					row.moved = false;
					return true;
				}
				else if (arg1.getAction() == MotionEvent.ACTION_MOVE)
				{
					row.moved = true;
					if (arg1.getHistorySize() > 1)
					{
						if (!row.animationStarted)
							lastTouched.setPadding((int)arg1.getHistoricalX(0, arg1.getHistorySize() - 1), lastTouched.getPaddingTop(), lastTouched.getPaddingRight(), lastTouched.getPaddingBottom());
						
						Log.d("gesture", "swiping " + arg1.getHistoricalX(0, arg1.getHistorySize() - 1));
					}
					
				}
			
				else if (arg1.getAction() == MotionEvent.ACTION_UP)
				{	
					if (!row.moved)
					{
						return true;
					}
					else
					{
						//Determine if it dragged far enough
						for (int i = 0; i < myapp.queue.size(); i++)
							Log.d("before queue", ""+myapp.queue.get(i).mTrack);
						
						getWindowManager().getDefaultDisplay().getSize(screenSize);
						if (lastTouched.getPaddingLeft() > screenSize.x / 4)
						{
							row.animationStarted = true;
							slideToRight(arg0);
							return false;
						}
						else if (!row.animationStarted)
							lastTouched.setPadding(0, lastTouched.getPaddingTop(), lastTouched.getPaddingRight(), lastTouched.getPaddingBottom());
						
						Log.d("gesture", "animating");
					}
					row.moved = false;
				}
				else if (arg1.getAction() == MotionEvent.ACTION_CANCEL)
				{
					if (!row.animationStarted)
					{
						row.setBackgroundColor(row.originalColor);
						lastTouched.setPadding(0, lastTouched.getPaddingTop(), lastTouched.getPaddingRight(), lastTouched.getPaddingBottom());
						row.moved = false;
						return true;
					}
				}
				return false;
			}
	    	
	    };
		rowTap = new OnTouchListener()
		{
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1)
			{
				
				if (arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					TableRow row = (TableRow)arg0;
					row.setBackgroundColor(Color.WHITE);
					return true;
				}
			
				else if (arg1.getAction() == MotionEvent.ACTION_UP)
				{	
					final TrackRow row = (TrackRow)arg0;
					row.setBackgroundColor(row.originalColor);
					AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
			        builder.setMessage(R.string.QueueTopOrBottom)
			               .setPositiveButton("Top", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
									Track tempTrack = row.getTrack();
									
									if (PlayerActivity.mIndex > 0) 
										myapp.queue.add(PlayerActivity.mIndex + 1, tempTrack);
									else if (myapp.queue.size() == 0)
										myapp.queue.add(PlayerActivity.mIndex, tempTrack);
									else if (myapp.queue.size() > 0 && PlayerActivity.mIndex >= 0)
										myapp.queue.add(PlayerActivity.mIndex + 1, tempTrack);
		                	   
									searchLayout.removeView(row);
									refreshQueue();
			                   }
			               })
			               .setNegativeButton("Bottom", new DialogInterface.OnClickListener() {
			                   public void onClick(DialogInterface dialog, int id) {
									Track tempTrack = row.getTrack();											
									myapp.queue.add(tempTrack);
									searchLayout.removeView(row);
									refreshQueue();
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
					row.setTranslationX(0);
					return true;
				}
				return false;
			}
		};
				
		
		findViewById(R.id.SearchOK).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0)
			{
				//Close keyboard
				InputMethodManager imm1 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm1.hideSoftInputFromWindow(searchLayout.getWindowToken(), 0);
				searchLayout.removeAllViews();
				searchText.clearFocus();				
				spinner.setVisibility(View.VISIBLE);
				String strSearch;
				try
				{
					strSearch = URLEncoder.encode(searchText.getText().toString(), "UTF-8");
				} catch (UnsupportedEncodingException e1)
				{
					strSearch = searchText.getText().toString().replace(' ', '+');
					e1.printStackTrace();
				}
				
				final String search = strSearch;
				final SearchProvider searchProvider = new SearchProvider(SearchActivity.this);

				
				searchProvider.SearchForTrack(search, SearchActivity.this);
			}		
		});
				
		playlistTap = new OnTouchListener()
		{

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1)
			{
				if (arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					TrackRow row = (TrackRow)arg0;
					row.setBackgroundColor(Color.WHITE);
					return true;
				}
				else if (arg1.getAction() == MotionEvent.ACTION_UP)
				{
					TrackRow row = (TrackRow)arg0;
					row.setBackgroundColor(row.originalColor);
					
					//If clear whole queue:
					queueTab.removeAllViews();
					myapp.queue.clear();
					PlayerActivity.mIndex = 0;
					//See how many to add
					SharedPreferences memory = getSharedPreferences("VizEQ", MODE_PRIVATE);
					int playlistIndex = ((ViewGroup) findViewById(R.id.SavedPlaylists)).indexOfChild(row);
					String name = memory.getString("playlist" + playlistIndex, "DNE");
					if (name.equals("DNE") == false)
					{
						int playlistLength = memory.getInt("playlist" + playlistIndex + "Length", 0);
						//String list = memory.getString("playlist" + playlistIndex + "Tracks", null);
						for (int i = 0; i < playlistLength; i++)
						{
							String track = memory.getString("playlist" + playlistIndex + "Track" + i, "");
							String album = memory.getString("playlist" + playlistIndex + "Album" + i, "");
							String artist = memory.getString("playlist" + playlistIndex + "Artist" + i, "");
							String uri = memory.getString("playlist" + playlistIndex + "Uri" + i, "");
							String thumbnail = memory.getString("playlist" + playlistIndex + "Thumbnail" + i, "");
							Track trackToAdd = new Track(track, album, artist, uri);
							trackToAdd.mThumbnail = thumbnail;
							myapp.queue.add(trackToAdd);
						}
						refreshQueue();
						Toast.makeText(SearchActivity.this, "" + name + " has been loaded as the queue", Toast.LENGTH_SHORT).show();
					}
					else
						return false;
					return true;
				}
				return false;
			}
		};
		
		findViewById(R.id.SaveAsPlaylist).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				//Scheme: save strings playlist0, playlist1, playlist2... for every one that exists
				//then playlist0Length
				//then playlist0Track0, playlist0Album0, playlist0Artist0, playlist0Uri0, playlist0Thumbnail
				
				AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				LinearLayout alertLayout = new LinearLayout(SearchActivity.this);
				TextView message = new TextView(SearchActivity.this);
				message.setText("Name this playlist: ");
				final EditText nameIn = new EditText(SearchActivity.this);
				
				alertLayout.setOrientation(1);
				alertLayout.addView(message, params);
				alertLayout.addView(nameIn, params);
				builder.setView(alertLayout).setCancelable(true)
				.setPositiveButton("ok", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						String name = nameIn.getText().toString();
						SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
						SharedPreferences.Editor saver = memory.edit();
						int numPlaylists = memory.getInt("numPlaylists", -1);
						saver.putString("playlist"+ (++numPlaylists), name);
						saver.putInt("numPlaylists", numPlaylists);
						saver.putInt("playlist" + numPlaylists + "Length", queueTab.getChildCount());
						ArrayList<TrackRow> playlist = new ArrayList<TrackRow>();
						for (int i = 0; i < queueTab.getChildCount(); i++)
						{
							playlist.add(((TrackRow) queueTab.getChildAt(i)));
							saver.putString("playlist" + numPlaylists + "Track" + i, playlist.get(i).mTrack);
							saver.putString("playlist" + numPlaylists + "Album" + i, playlist.get(i).mAlbum);
							saver.putString("playlist" + numPlaylists + "Artist" + i, playlist.get(i).mArtist);
							saver.putString("playlist" + numPlaylists + "Uri" + i, playlist.get(i).mUri);
							saver.putString("playlist" + numPlaylists + "Thumbnail" + i, myapp.queue.get(i+PlayerActivity.mIndex).mThumbnail);
						}
						saver.commit();
						TrackRow tableRowToAdd = new TrackRow(SearchActivity.this);
						TextView textViewToAdd = new TextView(SearchActivity.this);
						tableRowToAdd.setBackgroundColor(colorForPlaylists);
						tableRowToAdd.originalColor = colorForPlaylists;
						textViewToAdd.setText(name);
						textViewToAdd.setTextSize(25);
						tableRowToAdd.setOnTouchListener(playlistTap);
						tableRowToAdd.addView(textViewToAdd);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						tableRowToAdd.setPadding(0, 5, 0, 5);
						params.setMargins(0, 2, 0, 2);
						((ViewGroup) findViewById(R.id.SavedPlaylists)).addView(tableRowToAdd, params);
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
		});
		//refresh Playlist list
	    int numPlaylists = memory.getInt("numPlaylists", -1);
	    for (int i = 0; i <= numPlaylists; i++)
	    {
	    	TrackRow tableRowToAdd = new TrackRow(SearchActivity.this);
			TextView textViewToAdd = new TextView(SearchActivity.this);
			tableRowToAdd.setBackgroundColor(colorForPlaylists);
			tableRowToAdd.originalColor = colorForPlaylists;
			textViewToAdd.setText(memory.getString("playlist" + i, ""));
			textViewToAdd.setTextSize(25);
			tableRowToAdd.setOnTouchListener(playlistTap);
			tableRowToAdd.addView(textViewToAdd);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			tableRowToAdd.setPadding(0, 5, 0, 5);
			params.setMargins(0, 2, 0, 2);
			((ViewGroup) findViewById(R.id.SavedPlaylists)).addView(tableRowToAdd, params);
	    }
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent nextIntent  = new Intent(SearchActivity.this, HostProfileActivity.class);
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchLayout.getWindowToken(), 0);
			startActivity(nextIntent);
			break;
		case R.id.about:
			Intent nextIntent2  = new Intent(SearchActivity.this, AboutActivity.class);
			InputMethodManager imm1 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm1.hideSoftInputFromWindow(searchLayout.getWindowToken(), 0);
			startActivity(nextIntent2);
			break;
		default:
			super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	public void slideToRight(final View view){
		TranslateAnimation animate = new TranslateAnimation(0,view.getWidth(),0,0);
		animate.setDuration(700);
		animate.setFillAfter(true);
		view.startAnimation(animate);
		animate.setAnimationListener(new AnimationListener()
		{

			@Override
			public void onAnimationEnd(Animation animation)
			{
				int index = queueTab.indexOfChild((TrackRow) view);
				if (index >= 0 && index < queueTab.getChildCount())
				{
					if (myapp.queue.get(index).mUri.equals(((TrackRow) view).getSpotifyUri()))
					{
						myapp.queue.remove(index);
						((LinearLayout)view.getParent()).removeView(view);
						for (int i = 0; i < myapp.queue.size(); i++)
							Log.d("after queue", ""+myapp.queue.get(i).mTrack);
					}
				}
			}

			@Override
			public void onAnimationStart(Animation animation)
			{
				
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
								
			}
			
		});		
	}
	
	public void useResults(SearchResults searchResults)
	{
		spinner.setVisibility(Spinner.GONE);
		
		if (searchResults.mConnectionError) {
			NoConnectionNotification();
			return;
		}
		if (searchResults.mJsonError) {
			return;
		}
		if (searchResults.mResults.length == 0) 
		{
			final TextView noResults = new TextView(SearchActivity.this);
			noResults.setText("There are no results.");
			searchLayout.addView(noResults);
			return;
		}
		
		//Calculate start and end colors
		int startColor = 0;
		int endColor = 0;
		SharedPreferences memory = getSharedPreferences("VizEQ", Context.MODE_PRIVATE);
		int posi = memory.getInt("colorPos", -1);
		if (posi > 0) VizEQ.numRand = posi;	
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
		
		for (int i = 0; i < searchResults.mResults.length; i++) {
			TrackRow tableRowToAdd = searchResults.mResults[i];
			tableRowToAdd.setBackgroundColor(Color.argb(255, redStart, greenStart, blueStart));
			tableRowToAdd.originalColor = (Color.argb(255, redStart, greenStart, blueStart));
			tableRowToAdd.setOnTouchListener(rowTap);
			//tableRowToAdd.setOnDragListener(swipeListener);

			if (redStart + addRed < 255 && i < 16) redStart += addRed;
			if (greenStart + addGreen < 255 && i < 16) greenStart += addGreen;
			if (blueStart + addBlue < 255 && i < 16) blueStart += addBlue;
			final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 2, 0, 2);

			searchLayout.addView(tableRowToAdd, params);
		}
	}
	/**
	 * Shows the progress UI and hides the login form.
	 */
	/*@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}*/
}
