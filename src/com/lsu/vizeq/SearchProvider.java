package com.lsu.vizeq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

//Search documented at: https://developer.spotify.com/web-api/endpoint-reference/
public class SearchProvider
{
	AsyncHttpClient searchClient;
	Context mContext;
	
	public SearchProvider(Context context)
	{
		searchClient = new AsyncHttpClient();
		mContext = context;
	}
	
	public SearchResults SearchForTrack(String strSearch, final UseSearchResults callback)
	{
		final SearchResults results = new SearchResults();
		JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {

			public void onSuccess(final JSONObject response) {
				try {
					JSONObject tracks = response.getJSONObject("tracks");
					JSONArray items = tracks.getJSONArray("items");
					
					results.mResults = new TrackRow[items.length()];
					for (int i = 0; i < items.length(); i++)
					{
						String trackName = items.getJSONObject(i).getString("name");
						String trackArtist = items.getJSONObject(i).getJSONArray("artists").getJSONObject(0).getString("name");
						final String uri = items.getJSONObject(i).getString("uri");
						String trackAlbum = items.getJSONObject(i).getJSONObject("album").getString("name");
						//Image options: 640px, 300px, 64px
						String thumbnail = items.getJSONObject(i).getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
						results.mResults[i] = new TrackRow(mContext, trackName, trackAlbum, trackArtist, uri, thumbnail);
						
					}
				} 
				catch (JSONException e) {
					e.printStackTrace();					
					results.mJsonError = true;
				}
				finally {
					callback.useResults(results);
				}
			}

			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				e.printStackTrace();
				results.mConnectionError = true;
				callback.useResults(results);
			}
		};
		searchClient.get("https://api.spotify.com/v1/search?q=" + strSearch + "&type=track", handler);
		
		return results;
	}
}
