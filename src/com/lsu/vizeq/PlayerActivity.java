/*
 Copyright (c) 2012, Spotify AB
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * Neither the name of Spotify AB nor the names of its contributors may 
 be used to endorse or promote products derived from this software 
 without specific prior written permission.
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL SPOTIFY AB BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * The player view and playlist logic put together.
 * 
 * The logic should be put in the service-layer but currently its here
 */
package com.lsu.vizeq;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lsu.vizeq.ServiceBinder.ServiceBinderDelegate;
import com.lsu.vizeq.SpotifyService.PlayerUpdateDelegate;
import com.lsu.vizeq.util.TunnelPlayerWorkaround;

public class PlayerActivity extends Activity {
	boolean isPlaying = false;
	boolean AudioFocus = false;
	String LOGTAG = "Audio focus";
	
	private NotificationManager mNotificationManager;
	private static int NOTIFICATION = 29213;
	
	AudioManager am;
	AudioManager.OnAudioFocusChangeListener afChangeListener;
		
	private ServiceBinder mBinder;
	private WebService mWebservice;
	private boolean mIsStarred;

	// Disable the ui until a track has been loaded
	private boolean mIsTrackLoaded;
	private ArrayList<Track> mTracks = new ArrayList<Track>();

	private MediaPlayer mSilentPlayer;
	
	private String mAlbumUri; //KEEP IT.  Ignore warning.
	public static int mIndex = 0;
	MyApplication myapp;
	public static Camera cam;
	private static MyApplication MyApp;
	static RelativeLayout playerBackground;
	static int flash = 0;
	ActionBar actionBar;

	
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

	private final PlayerUpdateDelegate playerPositionDelegate = new PlayerUpdateDelegate() {

		@Override
		public void onPlayerPositionChanged(float pos) {

			SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
			seekBar.setProgress((int) (pos * seekBar.getMax()));

		}

		@Override
		public void onEndOfTrack() {
			playNext();
		}

		@Override
		public void onPlayerPause() {
			ImageView image = (ImageView) findViewById(R.id.player_play_pause_image);
			image.setBackgroundResource(R.drawable.playbutton_140x140);
		}

		@Override
		public void onPlayerPlay() {
			ImageView image = (ImageView) findViewById(R.id.player_play_pause_image);
			image.setBackgroundResource(R.drawable.pausebutton_140x140);
			VizEQ.nowPlaying = mTracks.get(mIndex).mTrack + " - " + mTracks.get(mIndex).mArtist;
			SendTrackInfo();
		}

		@Override
		public void onTrackStarred() {
			ImageView view = (ImageView) findViewById(R.id.star_image);
			view.setBackgroundResource(R.drawable.star_100x100);
			mIsStarred = true;
	}

		@Override
		public void onTrackUnStarred() {
			ImageView view = (ImageView) findViewById(R.id.star_image);
			view.setBackgroundResource(R.drawable.unstar_100x100);
			mIsStarred = false;
		}
	};

	public void star() {
		if (mTracks.size() == 0 || !mIsTrackLoaded)
			return;
		if (mIsStarred) {
			mBinder.getService().unStar();
		} else {
			mBinder.getService().star();
		}
	}
	
	public boolean isStarred()
	{
		mIsStarred = mBinder.getService().isStarred();
		return mIsStarred;
	}

	public void togglePlay() {
		if (mTracks.size() == 0)
			return;

		Track track = mTracks.get(mIndex);
		if (isPlaying)
			isPlaying = false;
		else
			isPlaying = true;
		int result = am.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

		if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED)
			isPlaying = false;
		else if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
			isPlaying = true;
			mBinder.getService().togglePlay(track.getSpotifyUri(), playerPositionDelegate);
		}
	}

	public void playNext() {
		if (mTracks.size() == 0)
			return;

		mIndex++;
		if (mIndex >= mTracks.size())
			mIndex = 0;
		mBinder.getService().playNext(mTracks.get(mIndex).getSpotifyUri(), playerPositionDelegate);
		updateTrackState();
		Thread nextCoverThread = new Thread(new Runnable()
		{
			public void run()
			{
				try {
					URL url = new URL(mTracks.get(mIndex).mThumbnail);
					final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							((ImageView) findViewById(R.id.cover_image)).setImageBitmap(bmp);
							
						}
						
					});
				} catch (MalformedURLException e) {
					throw new RuntimeException("Cannot load cover image", e);
				} catch (IOException e) {
					throw new RuntimeException("Cannot load cover image", e);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		nextCoverThread.start();
	}

	public void playPrev() {

		if (mTracks.size() == 0)
			return;

		Log.i("", "Play previous song");
		mIndex--;
		if (mIndex < 0)
			mIndex = mTracks.size() - 1;
			mBinder.getService().playNext(mTracks.get(mIndex).getSpotifyUri(), playerPositionDelegate);
		updateTrackState();
		Thread prevCoverThread = new Thread(new Runnable()
		{
			public void run()
			{
				try {
					URL url = new URL(mTracks.get(mIndex).mThumbnail);
					final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							((ImageView) findViewById(R.id.cover_image)).setImageBitmap(bmp);
							
						}
						
					});
				} catch (MalformedURLException e) {
					throw new RuntimeException("Cannot load cover image", e);
				} catch (IOException e) {
					throw new RuntimeException("Cannot load cover image", e);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		prevCoverThread.start();
	}

	public void updateTrackState() {
		ImageView view = (ImageView) findViewById(R.id.star_image);
		view.setBackgroundResource(R.drawable.unstar_100x100);
		if (mTracks.size() > 0)
		{
			((TextView) findViewById(R.id.track_info)).setText(mTracks.get(mIndex).getTrackInfo());
			((TextView) findViewById(R.id.track_name)).setText(mTracks.get(mIndex).getTrackName());
		}
	}

	protected void onNewIntent(Intent intent) {

		int keycode = intent.getIntExtra("keycode", -1);
		//if (keycode == -1)
			//throw new RuntimeException("Could not identify the keycode");

		if (keycode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keycode == KeyEvent.KEYCODE_HEADSETHOOK
				|| keycode == KeyEvent.KEYCODE_MEDIA_PLAY || keycode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
			togglePlay();
		} else if (keycode == KeyEvent.KEYCODE_MEDIA_NEXT) {
			playNext();
		} else if (keycode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
			playPrev();
		}
	};

	@Override
	protected void onResume() {
		// Register media buttons
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

		// Start listening for button presses
		am.registerMediaButtonEventReceiver(new ComponentName(getPackageName(), RemoteControlReceiver.class.getName()));

	    initTunnelPlayerWorkaround();
		
		//Refresh the queue
		checkTheQueue();
		super.onResume();
	}

	private float[] prevEnergies = new float[VisualizerView.NUM_BANDS];
	private boolean[] isOnLastFrame = new boolean[VisualizerView.NUM_BANDS];
	
	public static void SendBeat(final String[] datas) {
		final MyApplication myapp = MyApp;
		new Thread(new Runnable()
		{

			@Override
			public void run() 
			{

					try
					{
						byte[] sendData = new byte[200];
						DatagramSocket sendSocket = new DatagramSocket();
						String data = "freq_circle";
						for (int i = 0; i < datas.length; i++) {
							data += "\n" + datas[i];
						}
						data += "\n" + " junk ";
						HostSoundVisualizationActivity.data = datas;
						HostSoundVisualizationActivity.dirty = true;

						sendData = data.getBytes();
						Iterator it = MyApp.connectedUsers.entrySet().iterator();
						while (it.hasNext())
						{
							Log.d("Send circl data", "hey");
							Map.Entry pairs= (Map.Entry) it.next();
							InetAddress IPAddress = (InetAddress) pairs.getValue();
							String test = "name: " + pairs.getKey() + " ip: " + pairs.getValue();
							Log.d("UDP",test);
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7770);
							sendSocket.send(sendPacket);
						}
						sendSocket.close();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			
		}).start();

	}
	
	public void SendTrackInfo() {
		final MyApplication myapp = MyApp;
		new Thread(new Runnable()
		{

			@Override
			public void run() 
			{

					try
					{
						byte[] sendData = new byte[200];
						DatagramSocket sendSocket = new DatagramSocket();
						String data = "track_info\n" + VizEQ.nowPlaying;
						data += "\n" + " junk ";

						sendData = data.getBytes();
						Iterator it = MyApp.connectedUsers.entrySet().iterator();
						while (it.hasNext())
						{
							Log.d("Send circl data", "hey");
							Map.Entry pairs= (Map.Entry) it.next();
							InetAddress IPAddress = (InetAddress) pairs.getValue();
							String test = "name: " + pairs.getKey() + " ip: " + pairs.getValue();
							Log.d("UDP",test);
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7770);
							sendSocket.send(sendPacket);
						}
						sendSocket.close();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			
		}).start();

	}
	
	Visualizer mVisualizer;
	int captureRate;
	
	private void showNotification() {
		//Notification notification = new Notification(R.drawable.player_next_album, "VizEQ is playing...", System.currentTimeMillis());
		//Intent notificationIntent = new Intent(this, PlayerActivity.class);
		//PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		//notification.setLatestEventInfo(this, "VizEQ", "Party!", pendingIntent);
		//startForeground(NOTIFICATION, notification);
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.launch)
		        .setContentTitle("VizEQ")
		        .setContentText("Now Playing " + mTracks.get(mIndex).getTrackName() + " by " + mTracks.get(mIndex).mArtist);
		Intent resultIntent = new Intent(this, PlayerActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(PlayerActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
		int mId = 1;
			mNotificationManager.notify(mId, mBuilder.build());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.LightGreen)));	
		
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

		if (LoginActivity.logCheck == false) {
			Toast.makeText(PlayerActivity.this, "You must log in to Spotify Premium first!", Toast.LENGTH_LONG).show();
			LoginActivity.backToPlayer = true;
			Intent nextIntent = new Intent(PlayerActivity.this, LoginActivity.class);
			startActivity(nextIntent);
		}
		
		
		
		mVisualizer = new Visualizer(0);
		if (mVisualizer.getEnabled()) {
			mVisualizer.setEnabled(false);
		}
		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);

		captureRate = Visualizer.getMaxCaptureRate()/4;
		
		for (int i = 0; i < isOnLastFrame.length; i++) {
			isOnLastFrame[i] = true;
		}
		
		Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {

			@Override
			public void onWaveFormDataCapture(Visualizer arg0, byte[] arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			private float threshold = 1.1f;
			
			@Override
			public void onFftDataCapture(Visualizer arg0, byte[] arg1, int arg2) {
				int bandWidth = arg1.length/VisualizerView.NUM_BANDS;
				boolean needToSend = false;
				String[] sendValues = new String[VisualizerView.NUM_BANDS];
				for (int i = 0; i < sendValues.length; i++) {
					sendValues[i] = "none";
				}
				for (int j = 0; j < VisualizerView.NUM_BANDS; j++) {
					float thisEnergy = 0;
					for (int i = bandWidth*j; i < bandWidth*(j+1); i++) {
						thisEnergy += Math.abs(arg1[i]);
					}
					thisEnergy /= bandWidth;
					
					boolean isOnThisFrame = false;

					if (thisEnergy > threshold*prevEnergies[j]) {
						isOnThisFrame = true;
					}
					if (isOnLastFrame[j] ^ isOnThisFrame) {
						needToSend = true;
						if (isOnThisFrame) {
							sendValues[j] = "on";
						}
						else {
							sendValues[j] = "off";
						}
					}
					
					if (isOnThisFrame) {
						isOnLastFrame[j] = true;
					}
					else {
						isOnLastFrame[j] = false;
					}
					prevEnergies[j] = thisEnergy;
				}
				
				if (needToSend) {
					SendBeat(sendValues);
				}
			}
		};
		
		mVisualizer.setDataCaptureListener(captureListener, captureRate, false, true);
		mVisualizer.setEnabled(true);	
		
		//Makes volume buttons control music stream even when nothing playing
		setVolumeControlStream(AudioManager.STREAM_MUSIC); 
		myapp = (MyApplication) this.getApplicationContext();
		MyApp = myapp;
		playerBackground = (RelativeLayout) findViewById(R.id.PlayerLayout);
		am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		OnAudioFocusChangeListener mAudioFocusListener = new OnAudioFocusChangeListener() {
			public void onAudioFocusChange(int focusChange) {
			    switch (focusChange) {
			        case AudioManager.AUDIOFOCUS_LOSS:
			            Log.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_LOSS");
			            AudioFocus = false;
			            if (isPlaying) togglePlay();
			            break;

			        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
			            Log.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
			            break;

			        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			        	AudioFocus = false;
			        	if (isPlaying) togglePlay();
			            Log.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_LOSS_TRANSIENT");
			            break;
			        case AudioManager.AUDIOFOCUS_GAIN:
			            Log.v(LOGTAG, "AudioFocus: received AUDIOFOCUS_GAIN");
			            AudioFocus = true;
			            break;
			        default:
			            Log.e(LOGTAG, "Unknown audio focus change code " + focusChange);
			    }
			}
		};
//		//light sending stuff
//		new Thread( new Runnable()
//		{
//			public void run()
//			{
//				try {
//					
//					DatagramSocket sendSocket = new DatagramSocket();
//					int count = 0;
//					while(true)
//					{
//						byte[] sendData = new byte[7];
//						String data = "#FF0000";
//						if (count%2==0) 
//						{
//							data = "#000000";
//							try
//							{
//						        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) 
//						        {
//						            cam = Camera.open();
//						            Parameters p = cam.getParameters();
//						            p.setFlashMode(Parameters.FLASH_MODE_TORCH);
//						            cam.setParameters(p);
//						            cam.startPreview();
//						        }
//							}
//							catch (Exception e) {
//						        e.printStackTrace();
//						        Log.d("Flashlight", "Exception flashLightOn");
//						    }
//						}
//						else
//						{
//							try 
//							{
//						        if (getPackageManager().hasSystemFeature(
//						                PackageManager.FEATURE_CAMERA_FLASH)) {
//						            cam.stopPreview();
//						            cam.release();
//						            cam = null;
//						        }
//						    } 
//							catch (Exception e) 
//							{
//						        e.printStackTrace();
//						        Log.d("Flashlight", "Exception flashLightOff");
//						    }
//						}
//						sendData = data.getBytes();
//						Iterator it = myapp.connectedUsers.entrySet().iterator();
//						while (it.hasNext())
//						{
//							Map.Entry pairs= (Map.Entry) it.next();
//							InetAddress IPAddress = InetAddress.getByName((String) pairs.getValue());
//							String test = "name: " + pairs.getKey() + " ip: " + pairs.getValue();
//							Log.d("UDP",test);
//							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 7770);
//							sendSocket.send(sendPacket);
//							//Log.d("UDP","Sent! "+ (String) pairs.getValue());
//						}
//						Log.d("UDP","Sent!");
//						Thread.sleep(500);
//						count++;
//						if(count == 2000) break;
//					}
//					sendSocket.close();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}	
//			}
//		}).start();
		//end light sending stuff
		
		new Thread( new Runnable()
		{
			public void run()
			{
				while (true)
				{
					flashOwnScreen();
					try
					{
						Thread.sleep(140);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}).start();
		
		SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setMax(300);
		mBinder = new ServiceBinder(this);
		mBinder.bindService(new ServiceBinderDelegate() {

			@Override
			public void onIsBound() {

			}
		});
		Log.e("", "Your login id is " + Installation.id(this));
		mWebservice = new WebService(Installation.id(this));
		checkTheQueue();
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		showNotification();
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if (mIsTrackLoaded)
					mBinder.getService().seek((float) seekBar.getProgress() / seekBar.getMax());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			}
		});

		

		findViewById(R.id.player_prev).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				playPrev();
			}
		});

		findViewById(R.id.player_next).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				playNext();
			}
		});

		
		findViewById(R.id.player_play_pause).setOnClickListener(

		new OnClickListener() {

			@Override
			public void onClick(View v) {
				togglePlay();
			}
		});

		findViewById(R.id.player_star).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				star();

			}
		});

		/*findViewById(R.id.player_next_album).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mTracks.size() == 0 || mAlbumUri == null)
					return;

				AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);

				builder.setMessage("Are you sure you want to skip to the next Album?").setTitle("Alert");
				builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						mWebservice.loadNextAlbum(Installation.id(PlayerActivity.this), mAlbumUri);
					}
				});
				builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});

				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});*/
		
		//LibSpotifyWrapper.BeginPolling();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_player, menu);
		return true;
	}
	
	@Override
	public void finish() {
		mBinder.getService().destroy();
		// Cancel the persistent notification.
		mNotificationManager.cancel(NOTIFICATION);
		super.finish();
	}

	private void initTunnelPlayerWorkaround() {
		 // Read "tunnel.decode" system property to determine
		 // the workaround is needed
		 if (TunnelPlayerWorkaround.isTunnelDecodeEnabled(this)) {
		      mSilentPlayer = TunnelPlayerWorkaround.createSilentMediaPlayer(this);
		 }
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Process.killProcess(Process.myPid());
			mBinder.getService().destroy();
			break;
		case R.id.about:
			Intent nextIntent2  = new Intent(PlayerActivity.this, AboutActivity.class);
			startActivity(nextIntent2);
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void flashOwnScreen()
	{
		if (flash==1) 
		{
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					playerBackground.setBackgroundColor(Color.BLUE);					
				}
				
			});
			
			flash = 0;
		}
		else
		{
			runOnUiThread(new Runnable()
			{

				@Override
				public void run()
				{
					playerBackground.setBackgroundColor(Color.BLACK);					
				}
				
			});			
		}
	}
	public void checkTheQueue()
	{
		mWebservice.loadAlbum(new WebService.TracksLoadedDelegate() {
			public void onTracksLoaded(ArrayList<Track> tracks, String albumUri, final String imageUri) {
				mTracks = tracks;
				mAlbumUri = albumUri;
				// Set the data of the first track
				//mIndex = 0;
				updateTrackState();
					Thread coverThread = new Thread(new Runnable()
					{
						public void run()
						{
							try {
								URL url = new URL(imageUri);
								final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
								runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										((ImageView) findViewById(R.id.cover_image)).setImageBitmap(bmp);
										
									}
									
								});
								//return bmp;
							} catch (MalformedURLException e) {
								throw new RuntimeException("Cannot load cover image", e);
							} catch (IOException e) {
								throw new RuntimeException("Cannot load cover image", e);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					});
					coverThread.start();
				// track might not be loaded yet but assume it is
				mIsTrackLoaded = true;
			}
		}, myapp);
	}

}
