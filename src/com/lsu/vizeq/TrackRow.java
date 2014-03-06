package com.lsu.vizeq;

import android.content.Context;
import android.graphics.Color;
import android.widget.TableRow;

public class TrackRow extends TableRow
{

	public String mTrack;
	public String mAlbum;
	public String mArtist;
	public String mUri;
	public int originalColor;
	public String mThumbnail;
	public static int color1 = Color.rgb(200, 200, 200);
	public static int color2 = Color.WHITE;
	
	public TrackRow(Context context, String track, String album, String artist, String uri) {
		super(context);
		mTrack = track;
		mAlbum = album;
		mArtist = artist;
		mUri = uri;
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
		Track tempTrack = new Track(mTrack, mAlbum, mArtist, mUri);
		return tempTrack;
	}

}
