package com.lsu.vizeq;

import java.util.ArrayList;

public class Artist
{
	public String mArtist;
	public String mUri;
	public ArrayList<Track> mTrackRequests = new ArrayList<Track>();
	public int mNumTrackRequests;
	public int mNumPeopleRequestingArtist;
	public int mArtistWeight;
	public float mPercentage;

	public Artist(String artist, Track trackRequests, int numTrackRequests, int personCount)
	{
		mArtist = artist;
		mTrackRequests.add(trackRequests);
		mNumTrackRequests = numTrackRequests;
		mNumPeopleRequestingArtist = personCount;
		mArtistWeight = numTrackRequests * personCount;
	}
	
	public void requestTrack(Track trackRequests)
	{
		mTrackRequests.add(trackRequests);
	}
	
	public Artist(String artist, int numTrackRequests, int personCount)
	{
		mArtist = artist;
		mNumTrackRequests = numTrackRequests;
		mNumPeopleRequestingArtist = personCount;
		mArtistWeight = numTrackRequests * personCount;
	}

}
