package com.lsu.vizeq;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

public class MyApplication extends Application {
	public Map<String, String> connectedUsers = new HashMap<String,String>();

}
