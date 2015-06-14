package com.lsu.vizeq;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TrackRow extends TableRow
{

	public String mTrack;
	public String mAlbum;
	public String mArtist;
	public String mUri;
	public String mThumbnail;
	public String mRequester;
	public int originalColor;
	public static int color1 = Color.rgb(200, 200, 200);
	public static int color2 = Color.WHITE;
	public LinearLayout trackRowLayout;
	public TextView trackView;
	public TextView artistView;
	public boolean moved = false;
	public boolean animationStarted = false;
	private Context context;
	
	public TrackRow(Context context, String track, String album, String artist, String uri) {
		super(context);
		mTrack = track;
		mAlbum = album;
		mArtist = artist;
		mUri = uri;
		
		trackView = new TextView(context);
		artistView = new TextView(context);
		trackRowLayout = new LinearLayout(context);
		this.context = context;
		this.setPadding(4, 4, 0, 4);
		init();
	}
	
	private void init()
	{
		Typeface font = Typeface.createFromAsset(context.getAssets(), "Mission Gothic Regular.otf");
		trackView.setTypeface(font);
		artistView.setTypeface(font);
		trackRowLayout.setOrientation(LinearLayout.VERTICAL);
		trackView.setText(mTrack);
		artistView.setText(mArtist);
		trackView.setTextSize(20);
		trackView.setTextColor(Color.BLACK);
		artistView.setTextColor(Color.DKGRAY);
		trackRowLayout.addView(trackView);
		trackRowLayout.addView(artistView);
		this.addView(trackRowLayout);
	}
	
	public TrackRow(Context context)
	{
		super(context);
	}

	public String getSpotifyUri() {
		return mUri;
	}

	public String getTrackInfo() {
		return mAlbum + " - " + mArtist;
	}

	public CharSequence getTrackName() {
		return mTrack;
	}
	
	public Track getTrack()
	{
		Track tempTrack = new Track(mTrack, mAlbum, mArtist, mUri, mThumbnail, 0);
		return tempTrack;
	}


}
