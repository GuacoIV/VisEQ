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

package com.lsu.vizeq;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.os.ParcelableCompat;

class Track implements Parcelable
{
	public String mTrack;
	public String mAlbum;
	public String mArtist;
	public String mUri;
	public String mThumbnail;
	public String mRequester;
	
	public Track(String track, String album, String artist,	String uri, String thumbnail, int dummy) {
		mTrack = track;
		mAlbum = album;
		mArtist = artist;
		mUri = uri;
		mThumbnail = thumbnail;
	}
	
	public Track(Parcel source)
	{
		mTrack = source.readString();
		mAlbum = source.readString();
		mArtist = source.readString();
		mUri = source.readString();
		mThumbnail = source.readString();
		mRequester = source.readString();
	}
	
	public Track(String track, String album, String artist,	String uri, String requester) {
		mTrack = track;
		mAlbum = album;
		mArtist = artist;
		mUri = uri;
		mRequester = requester;
	}
	
	public Track(String track, String album, String artist, String uri)
	{
		mTrack = track;
		mAlbum = album;
		mArtist = artist;
		mUri = uri;
	}
	
	public Track()
	{
		
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
	
	public String getAlbumInfo()
	{
		return mAlbum;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mTrack);
		dest.writeString(mAlbum);
		dest.writeString(mArtist);
		dest.writeString(mUri);
		dest.writeString(mThumbnail);
		dest.writeString(mRequester);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
		{
			public Track createFromParcel(Parcel in)
			{
				return new Track(in);
			}
			
			public Track[] newArray(int size)
			{
				return new Track[size];
			}
		};
	
}