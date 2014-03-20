package com.lsu.vizeq;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;

public class MyApplication extends Application {
	public Map<String, InetAddress> connectedUsers = new HashMap<String,InetAddress>();
	public String myName;
	public boolean joined = false;

}
