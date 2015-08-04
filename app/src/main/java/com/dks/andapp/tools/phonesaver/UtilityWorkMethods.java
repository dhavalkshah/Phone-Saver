package com.dks.andapp.tools.phonesaver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;
//import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class UtilityWorkMethods {
	private static final String TAG = "UtilityWorkMethods";
	
	Context locContext;
	private PowerManager pm;
	private InterstitialAd interstitialAds;
	private AdRequest adRequest;
	
	private class CachePackageDataObserver extends IPackageDataObserver.Stub {
		public void onRemoveCompleted(String packageName, boolean succeeded) {
			
		}
	}
	public UtilityWorkMethods(Context context) {
		locContext = context;
		pm = (PowerManager)context.getSystemService("power");
	}
	
	public boolean checkScreen() {
		return pm.isScreenOn();
	}
	
	public void clearCache(){
		CachePackageDataObserver mClearCacheObserver=new CachePackageDataObserver();
		PackageManager mPM=locContext.getPackageManager();
		long CACHE_APP = Long.MAX_VALUE;
		
		@SuppressWarnings("rawtypes")
	    final Class[] classes= { Long.TYPE, IPackageDataObserver.class };
		
		Long localLong=Long.valueOf(CACHE_APP);
		
		try {
	      Method localMethod= mPM.getClass().getMethod("freeStorageAndNotify", classes);
	      try {
	    	  localMethod.invoke(mPM, localLong, mClearCacheObserver);
	      }
	      catch(Exception e) {
	    	  e.printStackTrace();
	      }
	    }
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void enableDisableSync(boolean sync) {
		ContentResolver.setMasterSyncAutomatically(sync); 
	}
	
	public void enableDisableDataComm(boolean flag) {
		try
        {
            //Log.i(TAG, "Entering enableDisableDataComm with enabled as: "+ flag);
            ConnectivityManager connectivitymanager = (ConnectivityManager)locContext.getSystemService("connectivity");
            Class conManClass = Class.forName(connectivitymanager.getClass().getName());
            conManClass.getDeclaredField("mService").setAccessible(true);
            Field field = conManClass.getDeclaredField("mService");
            field.setAccessible(true);
            Object obj = field.get(connectivitymanager);
            Class class2 = Class.forName(obj.getClass().getName());
            Class aclass[] = new Class[1];
            aclass[0] = Boolean.TYPE;
            Method method = class2.getDeclaredMethod("setMobileDataEnabled", aclass);
            method.setAccessible(true);
            Object aobj[] = new Object[1];
            aobj[0] = Boolean.valueOf(flag);
            method.invoke(obj, aobj);
        }
        catch (Exception exception)
        {
            //Log.i(TAG, "Exiting enableDisableDataComm with errors");
            exception.printStackTrace();
        }
        //Log.i(TAG, "Exiting enableDisableDataComm");
	}
	
	public void enableDisableWifi(boolean flag)
    {
        //Log.i("UtilityWorkMethods", "enableDataComm");
        ((WifiManager)locContext.getSystemService("wifi")).setWifiEnabled(flag);
    }
	
	public boolean isMobDataEnabled(){
    	boolean flag;
        try
        {
            ConnectivityManager connectivitymanager = (ConnectivityManager)locContext.getSystemService("connectivity");
            Method method = Class.forName(connectivitymanager.getClass().getName()).getDeclaredMethod("getMobileDataEnabled", new Class[0]);
            method.setAccessible(true);
            flag = ((Boolean)method.invoke(connectivitymanager, new Object[0])).booleanValue();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            return false;
        }
        return flag;
    }
	
	public boolean isWifiEnabled() {
    	return ((WifiManager)locContext.getSystemService("wifi")).isWifiEnabled();
    }
	
	public void displayInterstitialAd() {
		//Log.i(TAG, "Entering displayInterstitialAd");		
		interstitialAds = new InterstitialAd(locContext);
		interstitialAds.setAdUnitId(locContext.getResources().getString(R.string.admob_interstitial_id));
		adRequest = new AdRequest.Builder().build();
							//.addTestDevice("46C8F144B39291FFBBC43BAE83C1BC92")
		interstitialAds.loadAd(adRequest);
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
		
		(new Handler()).postDelayed(new Runnable() {
			@Override
			public void run() {
				displayInterstitial();
				//interstitialAds.loadAd(adRequest);
			}
		}, 1000 * 5);					
		//Log.i(TAG, "Exiting displayInterstitialAd");
	}
	
	private void displayInterstitial() {
    	if (interstitialAds.isLoaded()) {
    		interstitialAds.show();
    	}
    	else {
    		//Log.i(TAG, "Interstitial Ad could not be loaded");
    	}
    }
}
