package com.lsu.vizeq;

public class SearchResults
{
	public boolean mConnectionError;
	public boolean mJsonError;
	public TrackRow[] mResults;
	
	public SearchResults(boolean connectionError, boolean jsonError, TrackRow[] results)
	{
		mConnectionError = connectionError;
		mJsonError = jsonError;
		mResults = results;
	}
	
	public SearchResults()
	{
		
	}
}
