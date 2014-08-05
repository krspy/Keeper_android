package com.yhsg.safeguard;

import static com.yhsg.safeguard.gcmInfo.SENDER_ID;
import static com.yhsg.safeguard.gcmInfo.displayMessage;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	private static final String TAG = "GCM_Inten";
	
	public GCMIntentService(){
		super(SENDER_ID);
	}

	@Override
	protected void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
        String message = getString(R.string.gcm_message);
        displayMessage(context, message);        
        // notifies user
        generateNotification(context, message);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId +"//end");
        displayMessage(context, getString(R.string.gcm_registered));        
    }

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));        
    }
	
	private static void generateNotification(Context context, String message) {
        int icon = R.drawable.motion_detect;
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(icon)        		
        	.setContentTitle("SafeGuard")
        	.setContentText("움직임이 감지되었습니다!")
        	.setAutoCancel(true)
			.setVibrate(new long[]{1000,2000});        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);    
        builder.setContentIntent(intent);
        notificationManager.notify(1,builder.build());
    }

}
