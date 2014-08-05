package com.yhsg.safeguard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends Activity{
	//////////////Using shared preference/////////////////////////
	String MAC;
	double ilat;	
	double ilng;
	SharedPreferences sInfo;
	SharedPreferences.Editor ed;	
	//////////////Using shared preference////////////////////////	
	
	WifiManager wifi;
	WifiInfo info;
	int WifiCnt;
	
	String locationProvider;
	LocationManager locManager;
	Location loc;
	
	boolean mapcheck;
	
	String send_msg;
	String recv_msg;
	
	private static final String TAG="Register_Log";
	static int flag;	//	1:gps	2:network	3:network_wifi	4:err
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.register);
	    Log.v(TAG,"Register.class start");	    
	    
	    TextView mac = (TextView)findViewById(R.id.Rmac);
	    TextView gstat = (TextView)findViewById(R.id.Rgstat);
	    
	    //↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓Wifi stuff↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓//////
	    //Log.v(TAG,"ready to Wifi stuff");
	    wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
	    info=wifi.getConnectionInfo();
	    MAC=info.getMacAddress();
	    //Log.v(TAG,"finish initialization wifi stuff");
	    if((!wifi.isWifiEnabled())&&(MAC==null)){			//check wifi on/off 
	    	//Log.v(TAG,"MAC check start");
	    	alertCheckWifi();
	    	//Log.v(TAG,"MAC finish");
	    	}	
	    /////////↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑Wifi stuff↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑/////////////
	    
	    
	    ////↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓GPS stuff↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓/////
	    locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);	//get location manager obj
	    //Log.v(TAG,"finish initialization gps stuff");
	    
	    if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){		//GPS on/off check
	    	gstat.setText("OFF");
	    	alertCheckGPS();
	    	//Log.v(TAG,"GPS check");
	    	}
	    if(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
	    	gstat.setText("ON");
	    }
	    /////////↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑GPS stuff↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑//////////////////////
	   
	    //final RelativeLayout rl = (RelativeLayout)findViewById(R.id.mainlayout);	    
	    
	    ((Button)findViewById(R.id.Rconfirm)).setOnClickListener(blistener);
	    ((Button)findViewById(R.id.Rrenew)).setOnClickListener(blistener);
	    ((Button)findViewById(R.id.Rloc)).setOnClickListener(blistener);
	    ((Button)findViewById(R.id.Rmap)).setOnClickListener(blistener);
	    ((Button)findViewById(R.id.Killservice)).setOnClickListener(blistener);
	    
	    mac.setText(MAC);	   
	    
	}	//End of Create	
	
	
	/////↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓GPS stuff↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓/////////////
	private final LocationListener Llistener = new LocationListener(){	
		@Override		
			public void onLocationChanged(Location location) {
				updateLocation(location);					
			}
			@Override
			public void onProviderDisabled(String provider) {}

			@Override
			public void onProviderEnabled(String provider) {}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}	    	
	    };
	    
	private void updateLocation(Location location){
		Log.v(TAG,"Call updateLocation succeed");
		TextView Tlat = (TextView)findViewById(R.id.Rlat);
	    TextView Tlng = (TextView)findViewById(R.id.Rlng);
	    TextView addr = (TextView)findViewById(R.id.addr);
	    
	    if(location == null){
	    	mapcheck=false;
	    	Log.v(TAG,"location=null");
	    	Tlat.setText("location null");
	    	Tlng.setText("location null");
	    	//Log.v(TAG,"request location");
	    	flag=4;
	    	}	    	
	    
	    else if(location != null){
	    	mapcheck=true;
	    	Log.v(TAG,"location != null");
	    	double lat=location.getLatitude();
	    	double lng=location.getLongitude();
	    	
	    	ilat=lat;
	    	ilng=lng;
	    	
	    	addr.setText("lat="+ilat+", lng="+ilng);
	    	Tlat.setText(""+ilat);
	    	Tlng.setText(""+ilng);
	    	}	
	    }	
	//////////↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑GPS stuff↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑////////////
	
	//////////↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓Give connection status to service↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓////////
	private void chkstatus() { // check network status
		//flag val ---> 1:gps	2:network	3:network_wifi	4:err
		ConnectivityManager conMan = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		//NetworkInfo status_mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo status_wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if(status_wifi.isConnected())
			flag=3;
		else{
			flag=2;
			Toast.makeText(getApplicationContext(),"오차를 줄이려면 wifi를 키세요",Toast.LENGTH_SHORT).show();
		}
	}
	//////////↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑Give connection status to service↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑////////
	
	/////////↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓Button↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓/////////////////
	Button.OnClickListener blistener=new Button.OnClickListener(){
		public void onClick(View v){
			Intent service = new Intent(Register.this, gpsmsgService.class);		
			
			switch(v.getId()){
			case R.id.Rconfirm:
				sInfo=getSharedPreferences("Information",MODE_PRIVATE);
				if(MAC==null){
					Toast.makeText(getApplicationContext(),"Wifi를 한번 키세요",Toast.LENGTH_SHORT).show();
					break;
				}else if(ilat == 0.0 || ilng == 0.0){
					Toast.makeText(getApplicationContext(),"GPS 수신 정보가 없습니다",Toast.LENGTH_SHORT).show();
					break;
				}				
				if(MAC !=null && ilat!=0.0 && ilng!=0.0){										
					if(wifi.isWifiEnabled())
						chkstatus();										
					//[AND_R]MAC_regid
					send_msg ="[AND_R]"+MAC+"_"+sInfo.getString("RegId","");
					recv_msg="";
					
					Thread nThread = new Thread(new Runnable(){
						@Override
						public void run() {
							UDP_svr udp = new UDP_svr(send_msg);
							recv_msg = udp.run();
							Log.d(TAG,"R_UDP"+recv_msg);
						}						
					});					
					nThread.start();
					try {
						nThread.join(1500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.d(TAG,"thread end");
					Log.d(TAG,"R_UDP"+recv_msg);
					if(recv_msg.equals("[SVR]EXIST")&&sInfo.getString("Rflag","").equals("true")){
						Toast.makeText(getApplicationContext(),"이미 등록된 유저\n백그라운드 실행",Toast.LENGTH_SHORT).show();
						startService(service);
					}
					else if(recv_msg.equals("[SVR]REGISTER")){
						Log.v(TAG,"Saving SharedPreferences starts");
						ed = sInfo.edit();
						ed.putString("NIC", MAC);
						ed.putString("Slat", Double.toString(ilat));
						ed.putString("Slng", Double.toString(ilng));
						ed.putInt("Flag", flag);
						ed.putString("Rflag","true");
						ed.commit();
						Log.v(TAG,"Saving SharedPreferences finish");
						Toast.makeText(getApplicationContext(),"등록 완료\n백그라운드 실행",Toast.LENGTH_SHORT).show();						
						startService(service);
					}
					else if(recv_msg.equals("")&&sInfo.getString("Rflag","").equals("true")){
						Toast.makeText(getApplicationContext(),"서버 다운\n백그라운드 실행",Toast.LENGTH_SHORT).show();
						startService(service);
					}
					else
						Toast.makeText(getApplicationContext(),"err: background does not support",Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.Rrenew:		//refresh MAC , latitude, longitude val
				TextView mac = (TextView)findViewById(R.id.Rmac);
				wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
			    info=wifi.getConnectionInfo();
			    MAC=info.getMacAddress();
				mac.setText(MAC);
				//updateLocation(loc);
				
				if((wifi.isWifiEnabled()) && (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))){
					loc = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER);
					locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Llistener); //0sec , 0m
					//Log.v(TAG,"Network_request");	
					//chkstatus();
					updateLocation(loc);
				}
				else if(!wifi.isWifiEnabled()){
					loc = locManager.getLastKnownLocation(locManager.GPS_PROVIDER);
					locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Llistener); //0sec , 0m					
					updateLocation(loc);
					if(ilat==0.0 && ilng==0.0){
						loc = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER);
						locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Llistener); //0sec , 0m					
						updateLocation(loc);
					}
				}
				TextView gstat = (TextView)findViewById(R.id.Rgstat);
				if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
					gstat.setText("OFF");
				else
					gstat.setText("ON");
				break;
			case R.id.Rloc:			//Using spinner stuff
				Log.v(TAG,"loc button click");
				
				//if(!wifi.isWifiEnabled())
					//alertCheckWifi();
				if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
					alertCheckGPS();
				
				if((wifi.isWifiEnabled()) && (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))){
					loc = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER);
					locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Llistener); //0sec , 0m
					//Log.v(TAG,"Network_request");	
					chkstatus();
					updateLocation(loc);
				}
				else if(!wifi.isWifiEnabled()){
					loc = locManager.getLastKnownLocation(locManager.GPS_PROVIDER);
					locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Llistener); //0sec , 0m					
					updateLocation(loc);
					if(ilat==0.0 && ilng==0.0){
						loc = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER);
						locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Llistener); //0sec , 0m					
						updateLocation(loc);
					}
				}
				/*String provider = (String)spinLocation.getSelectedItem();
				
					if(provider.equalsIgnoreCase(locManager.GPS_PROVIDER)){
						if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
							alertCheckGPS();
						
						loc = locManager.getLastKnownLocation(locManager.GPS_PROVIDER);
						locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Llistener); //1min , 5m
						Log.v(TAG,"GPS_request");	   
						flag=1;
						updateLocation(loc);
					}
					else if(provider.equalsIgnoreCase(locManager.NETWORK_PROVIDER)){
						loc = locManager.getLastKnownLocation(locManager.NETWORK_PROVIDER);
						locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Llistener); //0sec , 0m
						Log.v(TAG,"Network_request");	
						chkstatus();
						updateLocation(loc);
					}*/
					else
						loc = null;
					break;
			case R.id.Rmap:
				if(mapcheck){
					Intent intent1 = new Intent(Register.this, Gmap.class);
					Bundle blat=new Bundle();
					Bundle blng=new Bundle();
					
					blat.putDouble("Plat", ilat);
					blng.putDouble("Plng", ilng);
					Log.v(TAG,Double.toString(ilat));
					Log.v(TAG,Double.toString(ilng));
					intent1.putExtras(blat);
					intent1.putExtras(blng);
	        		startActivity(intent1);
				}else
					Toast.makeText(getApplicationContext(),"위치정보가 없습니다.",Toast.LENGTH_SHORT).show();
				break;
			case R.id.Killservice:
				stopService(service);
				Toast.makeText(getApplicationContext(),"백그라운드 종료",Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	};
	/////////////↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑Button↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑//////////////
	
	/////////↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓WIfi, GPS chek↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓/////////////////
	private void alertCheckWifi() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Wi-Fi가 꺼져있습니다. 활성화 하시겠습니까?")
        .setCancelable(false)
        .setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveConfigWiFi();
                    }
            })
        .setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
            });
		AlertDialog alert = builder.create();
		alert.show();		
	}	
	
	private void alertCheckGPS() {		//GPS on/off check
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS가 꺼져있습니다. 활성화 하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveConfigGPS();
                            }
                    })
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                    });                    
        AlertDialog alert = builder.create();
        alert.show();
	}
	
	private void moveConfigWiFi(){
		Intent wifiopt = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
		startActivity(wifiopt);
	}
	private void moveConfigGPS(){
		Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
	}
	//////////////////Wifi, GPS stuff//////////////////	
	
}	//End of appl
