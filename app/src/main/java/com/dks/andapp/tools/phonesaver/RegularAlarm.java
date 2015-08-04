package com.dks.andapp.tools.phonesaver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.util.Log;

public class RegularAlarm extends BroadcastReceiver {
	
	private static final String PREFS_NAME = "battBoostSettings";
	private static final String TAG = "RegularAlarm";
	private SharedPreferences settings;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//Log.i(TAG, "Inside Receiver before starting the service");
		Intent locIntent = new Intent(context, ActivityService.class);
		context.startService(locIntent);

	}
	
	public void SetAlarm(Context context) {
		//Log.i(TAG, "Setting Alarm");
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		AlarmManager alarmmanager = (AlarmManager)context.getSystemService("alarm");
		Intent intent = new Intent(context, RegularAlarm.class);
		PendingIntent pendingintent = PendingIntent.getBroadcast(context, 0, intent, 0);
		alarmmanager.setInexactRepeating(0, System.currentTimeMillis(), 
				60000 * settings.getInt("TIMER_FREQ", 15), pendingintent);
		//Log.i(TAG, "Exiting setAlarm");
	}
	
	public void CancelAlarm(Context context) {
		//Log.i(TAG, "Entering CancelAlarm");
		Intent locIntent = new Intent(context, ActivityService.class);
		context.stopService(locIntent);
		PendingIntent pendingintent = PendingIntent.getBroadcast(context, 0, 
				new Intent(context,RegularAlarm.class), 0);
		((AlarmManager)context.getSystemService("alarm")).cancel(pendingintent);
		//Log.i(TAG, "Exiting CancelAlarm");
	}
}
