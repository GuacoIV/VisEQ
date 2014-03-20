package com.lsu.vizeq;

import java.util.ArrayList;

public class Artist
{
	public String mArtist;
	public String mUri;
	public ArrayList<Track> mTrackRequests;
	public int mNumTrackRequests;
	public int nNumPeopleRequestingArtist;


	public Artist(String artist, ArrayList<Track> trackRequests, int numTrackRequests)
	{
		mArtist = artist;
		mTrackRequests = trackRequests;
		mNumTrackRequests = numTrackRequests;
	}
}
