package com.yhsg.safeguard;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import android.util.Log;

public class UDP_svr {
	private static final String IP="220.85.90.231";
	private static final int PORT=6609;
	private String send_msg;
	private String recv_msg;
	private static final String TAG = "UDP";
	
	public UDP_svr(String msg){
		this.send_msg=msg;
		Log.d(TAG,"UDP_svr() msg: "+send_msg);
	}
	
	public String run(){
		try {
			recv_msg="";
			Log.d(TAG,"run() start, "+send_msg);
			InetAddress server = InetAddress.getByName(IP);
			byte[] buff = new byte[512];
			buff = send_msg.getBytes();
			
			DatagramPacket packet = new DatagramPacket(buff,buff.length,server,PORT);
			Log.d(TAG,"make packet complete");
			DatagramSocket socket = new DatagramSocket();
			Log.d(TAG,"socket initialize");
			socket.send(packet);
			Arrays.fill(buff,(byte) 0);			//memset of buff(byte[])
			Log.d(TAG,"send complete");
			socket.receive(packet);
					
			recv_msg=new String(packet.getData()).trim();		//trim() --> cut the empty field of buff(byte[])
			Log.d(TAG,"receive complete: "+recv_msg);
			socket.close();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"Socket err");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"unkown host err");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"send pack err");
			e.printStackTrace();
		}
		return recv_msg;
	}

}
