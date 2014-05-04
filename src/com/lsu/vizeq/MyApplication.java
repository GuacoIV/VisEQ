package com.lsu.vizeq;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
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
	public JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), Redis.host, Redis.port);
	String brand = Build.BRAND; // for getting BrandName
	String model = Build.MODEL; // for getting Model of the device
	public static boolean doFlash;
	public static boolean doBackground;
	@Override
	public void onCreate()
	{
		SharedPreferences memory = getSharedPreferences("VizEQ",MODE_PRIVATE);
		doFlash = memory.getBoolean("cameraFlash", true);
		doBackground = memory.getBoolean("backgroundFlash", true);
		//super.onCreate();
	}
	
	@Override
	public void onTerminate()
	{
//		Log.d("Jedis", "Disconnecting Jedis");
		jedisPool.destroy();
		super.onTerminate();
	}
	
	

}
