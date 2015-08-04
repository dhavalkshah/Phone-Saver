package com.dks.andapp.tools.phonesaver;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class ActivityService extends Service {
	
	private static final String PREFS_NAME = "battBoostSettings";
	private static final String GLOBAL_DATA = "globalData";
	private static final String TAG = "ActivityService";
	
	private UtilityWorkMethods utilityWM;
    private SharedPreferences settings;
	private SharedPreferences globalData;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int onStartCommand(Intent intent, int i, int j) {
		Log.i(TAG,"Entering onStartCommand");
		
		settings = getSharedPreferences(PREFS_NAME, 0);
		globalData = getSharedPreferences(GLOBAL_DATA, 0);
    	Editor glbDataEditor = globalData.edit();
		try {
			utilityWM = new UtilityWorkMethods(this);
		}
		catch (Exception exception) {
			exception.printStackTrace();
			return -1;
		}
		if (!utilityWM.checkScreen()) {
			utilityWM.enableDisableWifi(settings.getBoolean("WIFI_SWITCH", true));
            utilityWM.enableDisableDataComm(settings.getBoolean("MOBILE_DATA_SWITCH", true));
            utilityWM.enableDisableSync(true);
            glbDataEditor.putInt("NUM_OF_CYCLE", globalData.getInt("NUM_OF_CYCLE", 0)+1);
            glbDataEditor.commit();
            (new Handler()).postDelayed(new Runnable() {
            	@Override
            	public void run() {
            		if (!utilityWM.checkScreen()) {
	            		utilityWM.enableDisableWifi(false);
	            		utilityWM.enableDisableDataComm(false);
            		}
            		if (settings.getBoolean("CACHE_SWITCH", true)) {
            			utilityWM.clearCache();
            		}
            		utilityWM.enableDisableSync(false);
            	}
            }, 1000 * 60 * 3);
		}
		Log.i(TAG,"Exiting onStartCommand");
		return Service.START_STICKY;
	}

}
