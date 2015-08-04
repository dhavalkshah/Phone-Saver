package com.dks.andapp.tools.phonesaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.util.Log;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ScreenReceiver extends BroadcastReceiver {
	
	private static final String TAG = "ScreenReceiver";
	private static final String PREFS_NAME = "battBoostSettings";
	private boolean screenState;
	private SharedPreferences settings;
    private Editor settingEditor;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//Log.i(TAG, "onReceive");
		if (!intent.getAction().equals("android.intent.action.SCREEN_OFF")){
			screenState = true;
		}
		else {
			screenState = false;			
		}
		Intent intent1 = new Intent(context, OnOffEventHandler.class);
		settings = context.getSharedPreferences(PREFS_NAME, 0);
		settingEditor = settings.edit();
		settingEditor.putBoolean("SCREEN_STATE", screenState);
		settingEditor.commit();
		context.startService(intent1);
	}
}
