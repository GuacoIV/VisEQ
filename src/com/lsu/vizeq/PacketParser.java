package com.lsu.vizeq;

import java.net.DatagramPacket;

public class PacketParser {
	
	public static String getHeader(DatagramPacket packet)
	{
		String header = "";
		
		String data = new String(packet.getData());
		int nlindx = data.indexOf('\n');
		header = data.substring(0,nlindx);
		
		return header;
	}
	
	public static String[] getArgs(DatagramPacket packet)
	{
		String[] args;
		String data = new String(packet.getData());
		int numArgs = 0;
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
				arg = data;
				end = true;
			}
			else
			{
				arg = data.substring(0, nlindx);
			}
			args[count++] = arg;
		}
		return args;
	}

}
