package com.yhsg.safeguard;

import static com.yhsg.safeguard.gcmInfo.DISPLAY_MESSAGE_ACTION;
import static com.yhsg.safeguard.gcmInfo.EXTRA_MESSAGE;
import static com.yhsg.safeguard.gcmInfo.SENDER_ID;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
//GCM stuff//


public class MainActivity extends Activity {	
	Intent intent1,intent2;	
	SharedPreferences sInfo;
	SharedPreferences.Editor ed;
/////////////GCM_STUFF////////////
	String regid;
////////////GCM_STUFF/////////////
	private static final String TAG2="GCM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
////////////////////////////////GCM Stuff/////////////////////////////////////
        checkNotNull(SENDER_ID, "SENDER_ID");
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        registerReceiver(mHandleMessageReceiver,
        		new IntentFilter(DISPLAY_MESSAGE_ACTION));
        regid=GCMRegistrar.getRegistrationId(this);
        Log.i(TAG2,"SENDER_ID:"+SENDER_ID);
        Log.i(TAG2,"regid:"+regid);

        if (regid.equals("")) {
        	// Automatically registers application on startup.
        	GCMRegistrar.register(this, SENDER_ID);
        } else {
        	// regId?? 
        	Log.i(TAG2, "regid:" + regid + "//end");
        	sInfo=getSharedPreferences("Information",MODE_PRIVATE);
        	ed = sInfo.edit();
			ed.putString("RegId",regid);
			ed.commit();
			Toast.makeText(getApplicationContext(),"Get regId complete",Toast.LENGTH_SHORT).show();
        }
////////////////////////////////GCM Stuff/////////////////////////////////////
        
        Button register=(Button)findViewById(R.id.Mregister);
        Button test=(Button)findViewById(R.id.Mtest); 
        Button stop=(Button)findViewById(R.id.Mstop);
        
        register.setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v) {
        		intent1 = new Intent(MainActivity.this, Register.class);
        		startActivity(intent1);
        	}        	
        });
        
        test.setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v){
        		intent2 = new Intent(MainActivity.this, Test.class);
        		startActivity(intent2);
        	}
        });
        stop.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
				//System.exit(0);
				android.os.Process.killProcess(android.os.Process.myPid());
			}
        	
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }
    ///////////////GCM_STUFF////////////////
    private void checkNotNull(Object reference, String name) {
    	if (reference == null) {
    		Log.v(TAG2,"GCM_reference null");
    		throw new NullPointerException(
    				getString(R.string.error_config, name));
    	}
    }

    private final BroadcastReceiver mHandleMessageReceiver =
    		new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
    		//mDisplay.append(newMessage + "\n");
    	}
    };
    ///////////////GCM_STUFF////////////////
}
