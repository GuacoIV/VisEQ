package com.lsu.vizeq;

import java.net.DatagramPacket;

import android.util.Log;

public class PacketParser {
	
	public static String getHeader(DatagramPacket packet)
	{
		String header = "";
		
		String data = new String(packet.getData());
		int nlindx = data.indexOf('\n');
		if(nlindx < 0)
			nlindx = data.indexOf(0);
		header = data.substring(0,nlindx);
		
		return header;
	}
	
	public static String[] getArgs(DatagramPacket packet)
	{
		String[] args;
		String data = new String(packet.getData());
		int numArgs = 0;
//		Log.d("packetparser", data);
		
		for(int i=0; i<data.length(); i++)
		{
			if(data.charAt(i)=='\n')
				numArgs++;
		}
		args = new String[numArgs];
		
		int nlindx = data.indexOf('\n');
		boolean end = false;
		
		int count = 0;
		
		while(!end)
		{
			String arg;
			
			data = data.substring(nlindx+1);
			nlindx = data.indexOf('\n');
			if(nlindx < 0)
			{
				nlindx = data.indexOf(0);
				end = true;
			}
			arg = data.substring(0, nlindx);
			args[count++] = arg;
		}
		return args;
	}

}
