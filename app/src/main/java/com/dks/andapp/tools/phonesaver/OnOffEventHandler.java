package com.dks.andapp.tools.phonesaver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//import android.os.Handler;
import android.os.IBinder;
//import android.util.Log;
import android.widget.Toast;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;

public class OnOffEventHandler extends Service {
	
	private static final String PREFS_NAME = "battBoostSettings";
	private static final String TAG = "OnOffEventHandler";
	private static final String GLOBAL_DATA = "globalData";
	
	private SharedPreferences globalData;
	private IntentFilter filter;
    private BroadcastReceiver mReceiver;
    private SharedPreferences settings;
    private Editor settingEditor;
    //private InterstitialAd interstitialAds;
    //private AdRequest adRequest;
    
    public void onCreate() {
    	super.onCreate();
    	filter = new IntentFilter("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.USER_PRESENT");
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }
    
    public void onDestroy() {
    	//Log.i(TAG, "onDestroy");
    	try{
    		unregisterReceiver(mReceiver);
    		return;
    	}
    	catch (IllegalArgumentException illegalargumentexception) {
    		Toast.makeText(this, "already unregistered event handler", Toast.LENGTH_LONG).show();
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void onStart(Intent intent, int i) {
    	//Log.i(TAG, "onStart");
    	UtilityWorkMethods utilityworkmethods = new UtilityWorkMethods(this);
    	settings = getSharedPreferences(PREFS_NAME, 0);
    	globalData = getSharedPreferences(GLOBAL_DATA, 0);
    	Editor glbDataEditor = globalData.edit();
    	boolean isScreenOn = settings.getBoolean("SCREEN_STATE", true);
    	//Log.i("OnOffEventHandler", "Screen state is: "+isScreenOn);
    	if (!isScreenOn) {
    		utilityworkmethods.enableDisableWifi(false);
    		utilityworkmethods.enableDisableDataComm(false);
    		//Log.i(TAG, "wasScreenOn: OFF ");
    	}
    	else {
    		utilityworkmethods.enableDisableWifi(settings.getBoolean("ORIG_WIFI_STATE", true));
    		utilityworkmethods.enableDisableDataComm(settings.getBoolean("ORIG_MOBILE_DATA_STATE", true));
    		glbDataEditor.putInt("NUM_OF_TIME", globalData.getInt("NUM_OF_TIME", 0)+1);
    		if (globalData.getInt("NUM_OF_TIME", 0)>=50)
    			utilityworkmethods.displayInterstitialAd();
    		/*interstitialAds = new InterstitialAd(this);
    		interstitialAds.setAdUnitId("ca-app-pub-5761877978588731/2535707201");
    		interstitialAds.setAdListener(new AdListener(){
    			@Override
    			public void onAdLoaded() {
    				//Log.i(TAG,"onAdLoaded");
    			}
    			@Override
    			public void onAdClosed() {
    				//Log.i(TAG,"onAdClosed");
    			}
    		});
    		//interstitialAds.setAdUnitId(this.getResources().getString(R.string.admob_id));
    		//AdRequest.Builder AdRequestBuilder = new AdRequest.Builder();
    		//AdRequestBuilder.addTestDevice("46C8F144B39291FFBBC43BAE83C1BC92");
    		adRequest = new AdRequest.Builder().build();
    		(new Handler()).postDelayed(new Runnable() {
    			@Override
    			public void run() {
    				interstitialAds.loadAd(adRequest);
    				displayInterstitial();
    				//interstitialAds.loadAd(adRequest);
    			}
    		}, 1000 * 10);*/
    		//Log.i(TAG, "wasScreenOn: ON");
    	}
    	glbDataEditor.commit();
    	
    }
    
    /*public void displayInterstitial() {
    	if (interstitialAds.isLoaded()) {
    		interstitialAds.show();
    	}
    	else {
    		//Log.i(TAG, "Interstitial Ad could not be loaded");
    	}
    }*/

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
