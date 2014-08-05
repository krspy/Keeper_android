package com.yhsg.safeguard;

import android.content.Context;
import android.content.Intent;

public class gcmInfo {
	static final String SENDER_ID = "564134033008"; //My Sender id (PID)
	//static final String SENDER_ID = "526444376080";	//Authorized by google in place to use google's echo server
	
	static final String TAG = "GCM_info";
	
	static final String DISPLAY_MESSAGE_ACTION =
            "com.yhsg.safeguard.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
