package com.lsu.vizeq;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

public class MyApplication extends Application {
	public Map<InetAddress, String> connectedUsers = new HashMap<InetAddress, String>();
	public ArrayList<Track> queue = new ArrayList<Track>();
	public ArrayList<Track> requests = new ArrayList<Track>();
	public InetAddress hostAddress;
	public String myName = "";
	public String zipcode = null;
	public String myIp;
	public boolean joined;
	public boolean hosting;
	public JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), Redis.host, Redis.port);
	String brand = Build.BRAND; // for getting BrandName
	String model = Build.MODEL; // for getting Model of the device
	public static boolean doFlash;
	public static boolean doBackground;
	public static boolean tapToFlash;
	public static boolean nativeAnalysis;
	public static boolean foundSound;
	@Override
	public void onCreate()
	{
		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		doFlash = memory.getBoolean("cameraFlash", true);
		doBackground = memory.getBoolean("backgroundFlash", true);
		//super.onCreate();
		/*
		 * Workaround attempt for Nexus 7.  See what the brand and model strings actually are in debug mode and adjust accordingly.
		 * Then, it would solo that stream for the whole application, but I think our sound driver probably won't push the music out through there.
		 * Give it a try though.
		*/
		
	}
	
	@Override
	public void onTerminate()
	{
//		Log.d("Jedis", "Disconnecting Jedis");
		jedisPool.destroy();
		super.onTerminate();
	}
	
	

}
