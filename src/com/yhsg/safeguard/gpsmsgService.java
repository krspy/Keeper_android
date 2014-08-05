package com.yhsg.safeguard;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

// NETWORK_PROVIDER ==> 오차범위 50m
// NETWORK_PROVIDER_WIFI ==>
// GPS_PROVIDER ==>
public class gpsmsgService extends Service {
	//private static final String IP="220.85.90.231";
	//private static final int PORT=6609;
	private String send_msg;
	private String recv_msg;
	boolean outOfrange;		//base 50M.  errorrange 1: 16m   2: 50m  3: 10m
	
	private static final String TAG="serviceLog";
	NotificationManager nM;
	
	double baseLat;
	double baseLng;
	String baseNIC;
	String baseRID;
	int baseFlag;	//	1:gps	2:network	3:network_wifi	4:err	
	
	SharedPreferences sInfo;
	SharedPreferences.Editor ed;	
	
	LocationManager locManager;
	Location loc;
	
	double service_lat;
	double service_lng;
	float dist[] = new float[2];		//distant
	int service_flag;	// 1:gps 2:netwrok 3:wifi	default = 2;	
						// standard val is considered wifi val  
	
	boolean getdistance;
	
	private Handler toastHandler;
	
	Thread nThread;
	Thread sThread;
	boolean run;
	
	String toastmsg;
	int cnt,cnt2;
	
	@Override
	public void onCreate(){
		super.onCreate();		
		Log.d("serviceLog", "onCreate()");		
		
		sInfo = getSharedPreferences("Information",MODE_PRIVATE);
		baseNIC = sInfo.getString("NIC","");
		baseRID = sInfo.getString("RegId","");
		baseFlag = sInfo.getInt("Flag", 4);
		baseLat = Double.parseDouble(sInfo.getString("Slat", ""));
		baseLng = Double.parseDouble(sInfo.getString("Slng", ""));
		Log.d("serviceLog", "getShared preference");
		cnt=0;cnt2=0;
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("serviceLog", "onStart()");
	    super.onStartCommand(intent, flags, startId);	    	    
	    
	    toastHandler = new Handler();
	    outOfrange=false;    
	    Log.d("serviceLog", "SharedPreference val is...");
	    Log.d("serviceLog", baseNIC);
	    Log.d("serviceLog", ""+baseFlag);
	    Log.d("serviceLog", ""+baseLat);
	    Log.d("serviceLog", ""+baseLng);
	    Log.d("ServiceLog", baseRID);
	    
	    service_flag = 2;
	    
	    if(baseFlag == 4)
	    	stopSelf();
	    
	    sThread = new ServerThread();
	    sThread.start();
	    backgroundGetLoc();
	    
	    return START_STICKY;
	    }//end of onStart()
	

	@Override
	public void onDestroy() { 
		super.onDestroy();
	    Log.d(TAG, "onDestroy()");
	    locManager.removeUpdates(Llistener);
	    sThread.interrupt();
	    run = false;
	    outOfrange = false;
	    sThread=null;
	    }

//////////////////////location thread job//////////////////////////////////
	private void backgroundGetLoc(){
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		chkstatus();
	
		if(service_flag == 1){
			loc = locManager.getLastKnownLocation(locManager.GPS_PROVIDER);
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Llistener); //0min , 0m
			updateLocation(loc);	
			if(service_lat==0.0&&service_lng==0.0){
				loc = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER);
				locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Llistener); //0sec , 0m
				service_flag = 2;
				updateLocation(loc);
				}
			}
		else{
			loc = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER);
			locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Llistener); //0sec , 0m
			chkstatus();
			updateLocation(loc);
		}
	}
////////////////////////location thread job//////////////////////////////////

///////////////////////Range watcher///////////////////////
	private void watcher(boolean check){ //Service_Flag -->  1:gps 2:netwrok 3:wifi  errorrange 1: 10m   2: 30m  3: 10m
		if(check){		
			if(service_flag == 1){
				if(dist[0] > 17){
					outOfrange = true;
					Log.v(TAG,"Watcher_dist(1) is"+dist[0]);
					cnt++;cnt2=0;
				}else{
					outOfrange = false;
					cnt=0;cnt2++;
				}
			}else if(service_flag == 2){
				if(dist[0] > 51){
					outOfrange = true;
					Log.v(TAG,"Watcher_dist(2) is"+dist[0]);
					cnt++;cnt2=0;
				}else{
					outOfrange = false;
					cnt=0;cnt2++;
				}
			}else if(service_flag == 3){
				if(dist[0] > 0){	// original val = 11
					outOfrange = true;
					Log.v(TAG,"Watcher_dist(3) is"+dist[0]);
					cnt++;cnt2=0;
				}else{
					outOfrange = false;
					cnt=0;cnt2++;
				}
			}else{
				;//do nothing
			}
		}
	}
///////////////////////Range Wathcer///////////////////////

////////////////////////toast stuff//////////////////////////////////
	private class ToastRunnable implements Runnable{
		String tText;
		public ToastRunnable(String text){
		tText = text;
		}
		@Override
		public void run(){
			Toast.makeText(getApplicationContext(), tText, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void toastMSG(String msg){
		toastHandler.post(new ToastRunnable(msg));
	}
////////////////////////toast stuff//////////////////////////////////
	
///////////////////////Server thread///////////////////////
	private class ServerThread extends Thread{
		@Override
		public void run(){
			run=true;
			Log.d(TAG,"outOfrange: "+outOfrange);
			while(run){
				watcher(getdistance);
				if(outOfrange && cnt<5){	//location thread , udp thread synchronization issue
					send_msg="[AND_O]"+baseNIC+"_"+baseRID;	//OUT
					UDP_svr udp = new UDP_svr(send_msg);
					recv_msg = udp.run();
					Log.d(TAG,"Thread!!!");
					if(recv_msg.equals("[SVR]OK"))
						cnt++;
					else
						cnt = 0;
					try { Thread.sleep(5000); } catch (InterruptedException e) { }
				}	
				else if( !outOfrange && cnt2<5){
					Log.d(TAG,"Thread!!!");
					send_msg="[AND_I]"+baseNIC+"_"+baseRID;	//IN
					UDP_svr udp = new UDP_svr(send_msg);
					recv_msg = udp.run();
					if(recv_msg.equals("[SVR]OK"))
						cnt2++;
					else
						cnt2=0;
					try { Thread.sleep(5000); } catch (InterruptedException e) { }
				}
			}
		}
	}
///////////////////////Server thread///////////////////////


////////////////////////////////////////Location stuff////////////////////////////////////////
	private final LocationListener Llistener = new LocationListener(){	
			@Override		
			public void onLocationChanged(Location location) {
				updateLocation(location);					
			}
			@Override
			public void onProviderDisabled(String provider) {
				Log.v(TAG,"Llistener disable");
			}
			@Override
			public void onProviderEnabled(String provider) {
				Log.v(TAG,"Llistener enable");
			}
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.v(TAG,"Llistener change status");
			}	    	
		};
	    
		private void updateLocation(Location location){	    
			if(location == null){
				service_flag=4;
				getdistance=false;
			}
			else if(location != null){
				chkstatus();
				service_lat=location.getLatitude();
				service_lng=location.getLongitude();
				Location.distanceBetween(baseLat, baseLng, service_lat, service_lng, dist);
				getdistance=true;
				
				Log.v(TAG,"service_lat:"+service_lat);
				Log.v(TAG,"service_lng:"+service_lng);
				Log.v(TAG,"dist[0](m):"+dist[0]);
				toastmsg="위도:"+service_lat+" 경도:"+service_lng+"\ndist:"+dist[0]+"\nflag:"+service_flag;
				toastMSG(toastmsg);
			}	
		}
////////////////////////////////////////Location stuff////////////////////////////////////////
	
///////////////////////////////check network status///////////////////////////////
		private void chkstatus() {
		//flag val ---> 1:gps	2:network	3:network_wifi	4:err
			locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			ConnectivityManager conMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo status_mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo status_wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			
			if(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
				service_flag=1;
			if(status_wifi.isConnected())
				service_flag=3;
			if(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && status_wifi.isConnected())
				service_flag = 3;
			else
				service_flag=2;
		}
/////////////////////////////check network status///////////////////////////////
	

}	// end of service

