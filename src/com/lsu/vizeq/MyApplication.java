package com.lsu.vizeq;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
	public Map<String, InetAddress> connectedUsers = new HashMap<String,InetAddress>();
	public ArrayList<Track> queue = new ArrayList<Track>();
	public ArrayList<Track> requests = new ArrayList<Track>();
	public InetAddress hostAddress;
	public String myName = "";
	public String zipcode = null;
	public String myIp;
	public boolean joined = false;
	public boolean hosting = false;
	public Jedis jedis;
	
	
	@Override
	public void onTerminate()
	{
		Log.d("Jedis", "Disconnecting Jedis");
		jedis.disconnect();
		super.onTerminate();
	}
	
	

}
