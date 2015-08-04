package com.dks.andapp.tools.phonesaver;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity 
implements android.widget.AdapterView.OnItemSelectedListener{
	
	private static final String TAG = "MainActivity";
	private static final String PREFS_NAME = "battBoostSettings";
	private static final String GLOBAL_DATA = "globalData";
	
	private Context locContext;
	private SharedPreferences settings;
	private SharedPreferences globalData;
	private Spinner spinner;
	private RegularAlarm alarm;
	private Editor settingEditor;
	private UtilityWorkMethods utilityWM;
	
	private void enableDisableContent(boolean enabled){
		ImageView imgWifi = (ImageView)findViewById(R.id.ic_wifi);
        Switch switchWifi = (Switch)findViewById(R.id.wifi_switch);
        ImageView imgMobData = (ImageView)findViewById(R.id.ic_mobile_data);
        Switch switchMobData = (Switch)findViewById(R.id.mobile_data_switch);
        ImageView imgCacheCleaner = (ImageView)findViewById(R.id.ic_cache_cleaner);
        Switch switchCacheCleaner = (Switch)findViewById(R.id.cache_cleaner_switch);
        Spinner selectorMins = (Spinner)findViewById(R.id.minsSpinner);
        imgWifi.setEnabled(enabled);
        switchWifi.setEnabled(enabled);
        imgMobData.setEnabled(enabled);
        switchMobData.setEnabled(enabled);
        imgCacheCleaner.setEnabled(enabled);
        switchCacheCleaner.setEnabled(enabled);
        selectorMins.setEnabled(enabled);
	}
	
	private void getDefaults() {
		
	}
	
	private void initialise() {
		settings = getSharedPreferences(PREFS_NAME, 0);
		settingEditor = settings.edit();
		if (!isMyServiceRunning(OnOffEventHandler.class)) {
			settingEditor.clear();
			settingEditor.commit();
		}
		spinner = (Spinner)findViewById(R.id.minsSpinner);
        alarm = new RegularAlarm();
        locContext = getApplicationContext();
        utilityWM = new UtilityWorkMethods(locContext);
        Switch switchWifi = (Switch)findViewById(R.id.wifi_switch);
        Switch switchMobData = (Switch)findViewById(R.id.mobile_data_switch);
        Switch switchCacheCleaner = (Switch)findViewById(R.id.cache_cleaner_switch);
        switchWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	@Override
        	public void onCheckedChanged(CompoundButton compoundbutton, boolean isChecked){
                onWifiSwitchClick(compoundbutton);
            }
        });
        switchMobData.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	@Override
            public void onCheckedChanged(CompoundButton compoundbutton, boolean isChecked){
                onMobileDataSwitchClick(compoundbutton);
            }
        });
        switchCacheCleaner.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	@Override
        	public void onCheckedChanged(CompoundButton compoundbutton, boolean isChecked){
                onCacheCleanerSwitchClick(compoundbutton);
            }
        });
        spinner.setOnItemSelectedListener(this);
	}
	
	public void onButtonClick(View view) {
		//Log.i(TAG, "Entering onButtonClick");
        boolean isServiceOnFlg = settings.getBoolean("IS_SERVICE_ON", false);
        settingEditor = settings.edit();
        Button startStopBtn = (Button)findViewById(R.id.startStopButton);
        if (isServiceOnFlg)
        {
            stopService(view);
            settingEditor.putBoolean("IS_SERVICE_ON", false);
            startStopBtn.setText(R.string.btn_start);
            startStopBtn.setBackgroundResource(R.drawable.start_button_bg);
            enableDisableContent(true);
        } else
        {
            startService(view);
            settingEditor.putBoolean("IS_SERVICE_ON", true);
            startStopBtn.setText(R.string.btn_stop_service);
            startStopBtn.setBackgroundResource(R.drawable.stop_button_bg);
            enableDisableContent(false);
        }
        settingEditor.commit();
        //Log.i(TAG, "Exiting onButtonClick");
	}
	
	public void startService(View view) {
		//Log.i(TAG, "Entering startService");
        settingEditor = settings.edit();
        settingEditor.putBoolean("ORIG_WIFI_STATE", utilityWM.isWifiEnabled());
        settingEditor.putBoolean("ORIG_MOBILE_DATA_STATE", utilityWM.isMobDataEnabled());
        settingEditor.putInt("TIMER_FREQ", Integer.parseInt((String)spinner.getSelectedItem()));
        settingEditor.commit();
        Intent intent;
        if (alarm != null)
        {
            alarm.SetAlarm(locContext);
        } else
        {
            Toast.makeText(locContext, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
        intent = new Intent(this, OnOffEventHandler.class);
        intent.putExtra("screen_state", true);
        startService(intent);
        
        addNotification();
        //Log.i(TAG, "Exiting startService");
	}
	private void addNotification() {
		//Log.i(TAG, "Entering addNotification");
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
													.setSmallIcon(R.drawable.ic_launcher)
													.setContentTitle("Phone Saver")
													.setContentText("Phone Saver is still running")
													.setOngoing(true);
		Intent notificationIntent = new Intent(this,MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pIntent);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify("PhoneSaverNotification", 100,builder.build());
		//Log.i(TAG, "Entering addNotification");
	}
	
	private void removeNotification() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel("PhoneSaverNotification", 100);
	}
	
	public void stopService(View view) {
		//Log.i(TAG, "Entering stopService");
        Context context = getApplicationContext();
        if (alarm != null)
        {
            alarm.CancelAlarm(context);
        } else
        {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
        context.stopService(new Intent(context, OnOffEventHandler.class));
        removeNotification();
        settingEditor.clear();
		settingEditor.commit();
		
		globalData = getSharedPreferences(GLOBAL_DATA, 0);
		if ((globalData.getInt("NUM_OF_TIME", 0) > 100) 
				&& (globalData.getInt("NUM_OF_CYCLE", 0) > 1000))  {
	        AppRating apprater = new AppRating(MainActivity.this);
	        apprater.showRateDialog();
	        Editor glbDataEditor = globalData.edit();
	        glbDataEditor.clear();
	        glbDataEditor.commit();
		}
        
        //Log.i(TAG, "Exiting stopService");
	}
	
	public void onWifiSwitchClick(View view) {
		//Log.i(TAG, "Entering onWifiSwitchClick");
        Switch wifiSwitch = (Switch)findViewById(R.id.wifi_switch);
        ImageView wifiImg = (ImageView)findViewById(R.id.ic_wifi);
        TextView wifiText = (TextView)findViewById(R.id.wifi_text);
        settingEditor = settings.edit();
        if (wifiSwitch.isChecked())
        {
            settingEditor.putBoolean("WIFI_SWITCH", true);
            wifiImg.setImageResource(R.drawable.wifi_on);
            wifiText.setTextAppearance(this, R.style.enabledText);
        } else
        {
            settingEditor.putBoolean("WIFI_SWITCH", false);
            wifiImg.setImageResource(R.drawable.wifi_off);
            wifiText.setTextAppearance(this, R.style.disabledText);
        }
        settingEditor.commit();
        //Log.i(TAG, "Exiting onWifiSwitchClick");
	}
	
	public void onMobDataImgClick(View view) {
		//Log.i(TAG, "Entering onMobDataImgClick");
        Switch mobSwitch = (Switch)findViewById(R.id.mobile_data_switch);
        ImageView mobImg = (ImageView)findViewById(R.id.ic_mobile_data);
        settingEditor = settings.edit();
        TextView mobText = (TextView)findViewById(R.id.mobile_data_text);
        if (!mobSwitch.isChecked())
        {
            mobImg.setImageResource(R.drawable.mobile_data_on);
            mobSwitch.setChecked(true);
            mobText.setTextAppearance(this, R.style.enabledText);
            settingEditor.putBoolean("MOBILE_DATA_SWITCH", true);
        } else
        {
            mobImg.setImageResource(R.drawable.mobile_data_off);
            mobText.setTextAppearance(this, R.style.disabledText);
            mobSwitch.setChecked(false);
            settingEditor.putBoolean("MOBILE_DATA_SWITCH", false);
        }
        settingEditor.commit();
        //Log.i(TAG, "Exiting onMobDataImgClick");
	}
	
	public void onCacheCleanerImgClick(View view) {
		//Log.i(TAG, "Entering onCacheCleanerImgClick");
        Switch cacheSwitch = (Switch)findViewById(R.id.cache_cleaner_switch);
        ImageView cacheImg = (ImageView)findViewById(R.id.ic_cache_cleaner);
        TextView cacheText = (TextView)findViewById(R.id.cache_cleaner_text);
        settingEditor = settings.edit();
        if (!cacheSwitch.isChecked()){
            cacheImg.setImageResource(R.drawable.cache_cleaner_on);
            cacheText.setTextAppearance(this, R.style.enabledText);
            cacheSwitch.setChecked(true);
            settingEditor.putBoolean("CACHE_SWITCH", true);
        } 
        else {
            cacheImg.setImageResource(R.drawable.cache_cleaner_off);
            cacheText.setTextAppearance(this, R.style.disabledText);
            cacheSwitch.setChecked(false);
            settingEditor.putBoolean("CACHE_SWITCH", false);
        }
        settingEditor.commit();
        //Log.i(TAG, "Exiting onCacheCleanerImgClick");
	}
	
	public void onCacheCleanerSwitchClick(View view) {
		//Log.i(TAG, "Entering onCacheCleanerSwitchClick");
        Switch cacheSwitch = (Switch)findViewById(R.id.cache_cleaner_switch);
        ImageView cacheImg = (ImageView)findViewById(R.id.ic_cache_cleaner);
        TextView cacheText = (TextView)findViewById(R.id.cache_cleaner_text);
        settingEditor = settings.edit();
        if (cacheSwitch.isChecked()){
            cacheImg.setImageResource(R.drawable.cache_cleaner_on);
            cacheText.setTextAppearance(this, R.style.enabledText);
            settingEditor.putBoolean("CACHE_SWITCH", true);
        } 
        else{
            cacheImg.setImageResource(R.drawable.cache_cleaner_off);
            cacheText.setTextAppearance(this, R.style.disabledText);
            settingEditor.putBoolean("CACHE_SWITCH", false);
        }
        settingEditor.commit();
        //Log.i(TAG, "Exiting onCacheCleanerSwitchClick");
	}
	
	public void onMobileDataSwitchClick(View view) {
		//Log.i(TAG, "Entering onMobileDataSwitchClick");
        Switch mobSwitch = (Switch)findViewById(R.id.mobile_data_switch);
        ImageView mobImg = (ImageView)findViewById(R.id.ic_mobile_data);
        TextView mobText = (TextView)findViewById(R.id.mobile_data_text);
        settingEditor = settings.edit();
        if (mobSwitch.isChecked())
        {
            mobImg.setImageResource(R.drawable.mobile_data_on);
            settingEditor.putBoolean("MOBILE_DATA_SWITCH", true);
            mobText.setTextAppearance(this, R.style.enabledText);
        } else
        {
            settingEditor.putBoolean("MOBILE_DATA_SWITCH", false);
            mobImg.setImageResource(R.drawable.mobile_data_off);
            mobText.setTextAppearance(this, R.style.disabledText);
        }
        settingEditor.commit();
        //Log.i(TAG, "Exiting onMobileDataSwitchClick");
	}
	
	public void onWifiImgClick(View view) {
		//Log.i(TAG, "Entering onWifiImgClick");
        Switch wifiSwitch = (Switch)findViewById(R.id.wifi_switch);
        ImageView wifiImg = (ImageView)findViewById(R.id.ic_wifi);
        settingEditor = settings.edit();
        TextView wifiText = (TextView)findViewById(R.id.wifi_text);
        if (!wifiSwitch.isChecked())
        {
            wifiImg.setImageResource(R.drawable.wifi_on);
            wifiSwitch.setChecked(true);
            wifiText.setTextAppearance(this, R.style.enabledText);
            settingEditor.putBoolean("WIFI_SWITCH", true);
        } 
        else{
            wifiImg.setImageResource(R.drawable.wifi_off);
            wifiSwitch.setChecked(false);
            wifiText.setTextAppearance(this, R.style.disabledText);
            settingEditor.putBoolean("WIFI_SWITCH", false);
        }
        settingEditor.commit();
        //Log.i(TAG, "Exiting onWifiImgClick");
	}
	
	public void onInstCacheCleanerImgClick(View view) {
        //Log.i(TAG, "Entering onInstMobDataImgClick");
        utilityWM.clearCache();
        Toast.makeText(this, "Cache Cleared", Toast.LENGTH_SHORT).show();
        //Log.i(TAG, "Exiting onInstMobDataImgClick");
    }
	
	public void onInstMobDataImgClick(View view) {
        //Log.i(TAG, "Entering onInstMobDataImgClick");
        ImageView instMobImg = (ImageView)findViewById(R.id.ic_inst_mobile_data);
        //Log.i(TAG, (new StringBuilder(String.valueOf(utilityWM.isMobDataEnabled()))).toString());
        if (utilityWM.isMobDataEnabled()){
            UtilityWorkMethods utilityworkmethods1 = utilityWM;
            boolean isModEnabledFlg = utilityWM.isMobDataEnabled();
            boolean enableDisableFlg = false;
            if (!isModEnabledFlg)
            {
                enableDisableFlg = true;
            }
            utilityworkmethods1.enableDisableDataComm(enableDisableFlg);
            instMobImg.setImageResource(R.drawable.mobile_data_off);
        } 
        else{
            UtilityWorkMethods utilityworkmethods = utilityWM;
            boolean isModEnabledFlg = utilityWM.isMobDataEnabled();
            boolean enableDisableFlg = false;
            if (!isModEnabledFlg)
            {
                enableDisableFlg = true;
            }
            utilityworkmethods.enableDisableDataComm(enableDisableFlg);
            instMobImg.setImageResource(R.drawable.mobile_data_on);
        }
        //Log.i(TAG, "Exiting onInstMobDataImgClick");
    }
	
	public void onInstWifiImgClick(View view)
    {
        //Log.i(TAG, "Entering onInstWifiImgClick");
        ImageView wifiInstImg = (ImageView)findViewById(R.id.ic_instant_wifi);
        //Log.i(TAG, (new StringBuilder(String.valueOf(utilityWM.isWifiEnabled()))).toString());
        if (utilityWM.isWifiEnabled())
        {
            UtilityWorkMethods utilityworkmethods1 = utilityWM;
            boolean isWifiEnabledFlg = utilityWM.isWifiEnabled();
            boolean enableDisableFlg = false;
            if (!isWifiEnabledFlg)
            {
                enableDisableFlg = true;
            }
            utilityworkmethods1.enableDisableWifi(enableDisableFlg);
            wifiInstImg.setImageResource(R.drawable.wifi_off);
        } 
        else{
            UtilityWorkMethods utilityworkmethods = utilityWM;
            boolean isWifiEnabledFlg = utilityWM.isWifiEnabled();
            boolean enableDisableFlg = false;
            if (!isWifiEnabledFlg)
            {
                enableDisableFlg = true;
            }
            utilityworkmethods.enableDisableWifi(enableDisableFlg);
            wifiInstImg.setImageResource(R.drawable.wifi_on);
        }
        //Log.i(TAG, "Exiting onInstWifiImgClick");
    }
	
	public void onItemSelected(AdapterView adapterview, View view, int i, long l)
	{
		//Log.i(TAG, "Entering onItemSelected with mins as:"+i);
		Spinner minsSpinner = (Spinner)findViewById(R.id.minsSpinner);
		minsSpinner.setSelection(i);
        Toast.makeText(this, (String)minsSpinner.getSelectedItem(), Toast.LENGTH_SHORT).show();
//        settingEditor = settings.edit();
//        settingEditor.putInt("TIMER_FREQ", Integer.parseInt((String)minsSpinner.getSelectedItem()));
//        settingEditor.commit();
        //Log.i(TAG, "Exiting onItemSelected");
	}
	
    public void onNothingSelected(AdapterView adapterview)
    {
    }
    
    private void populateSpinner(int mins){
    	//Log.i(TAG, "Entering populateSpinner with mins:"+mins);
    	List<String> list = new ArrayList<String>();
    	for (int i=1;i<=60;i++){
    		list.add(i+"");
    	}
    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
    			R.layout.my_spinner, list);
    	dataAdapter.setDropDownViewResource(R.layout.my_spinner);
    	spinner.setAdapter(dataAdapter);
    	spinner.setSelection(dataAdapter.getPosition(mins+""));
//    	Log.i(TAG, "Exiting populateSpinner");
    }
    
    private void setDefaults() {
    	settingEditor = settings.edit();
//    	Log.i(TAG, "Getting value of timer freq: "+ settings.getInt("TIMER_FREQ", 15));
        settingEditor.putInt("TIMER_FREQ", settings.getInt("TIMER_FREQ", 15));
        settingEditor.commit();
        populateSpinner(settings.getInt("TIMER_FREQ", 15));
        if (settings.getBoolean("IS_SERVICE_ON", false)) {
        	boolean wifiSwitchFlg = settings.getBoolean("WIFI_SWITCH", false);
            boolean mobSwitchFlg = settings.getBoolean("MOBILE_DATA_SWITCH", false);
            boolean cacheSwitchFlg = settings.getBoolean("CACHE_SWITCH", false);
            ImageView wifiImg = (ImageView)findViewById(R.id.ic_wifi);
            Switch wifiSwitch = (Switch)findViewById(R.id.wifi_switch);
            ImageView mobImg = (ImageView)findViewById(R.id.ic_mobile_data);
            Switch mobSwitch = (Switch)findViewById(R.id.mobile_data_switch);
            ImageView cacheImg = (ImageView)findViewById(R.id.ic_cache_cleaner);
            Switch cacheSwitch = (Switch)findViewById(R.id.cache_cleaner_switch);
            Button startStopBtn = (Button)findViewById(R.id.startStopButton);
            startStopBtn.setText(R.string.btn_stop_service);
            startStopBtn.setBackgroundResource(R.drawable.stop_button_bg);
            if (wifiSwitchFlg)
            {
                wifiImg.setImageResource(R.drawable.wifi_on);
                wifiSwitch.setChecked(true);
            } else
            {
                wifiImg.setImageResource(R.drawable.wifi_off);
                wifiSwitch.setChecked(false);
            }
            if (mobSwitchFlg)
            {
                mobImg.setImageResource(R.drawable.mobile_data_on);
                mobSwitch.setChecked(true);
            } else
            {
                mobImg.setImageResource(R.drawable.mobile_data_off);
                mobSwitch.setChecked(false);
            }
            if (cacheSwitchFlg)
            {
                cacheImg.setImageResource(R.drawable.cache_cleaner_on);
                cacheSwitch.setChecked(true);
            } else
            {
                cacheImg.setImageResource(R.drawable.cache_cleaner_off);
                cacheSwitch.setChecked(false);
            }
            enableDisableContent(false);
        }
        else {
        	ImageView wifiImg = (ImageView)findViewById(R.id.ic_wifi);
            Switch wifiSwitch = (Switch)findViewById(R.id.wifi_switch);
            ImageView mobImg = (ImageView)findViewById(R.id.ic_mobile_data);
            Switch mobSwitch = (Switch)findViewById(R.id.mobile_data_switch);
            ImageView cacheImg = (ImageView)findViewById(R.id.ic_cache_cleaner);
            Switch cacheSwitch = (Switch)findViewById(R.id.cache_cleaner_switch);
            Button startStopBtn = (Button)findViewById(R.id.startStopButton);
            TextView textview = (TextView)findViewById(R.id.wifi_text);
            TextView textview1 = (TextView)findViewById(R.id.mobile_data_text);
            TextView textview2 = (TextView)findViewById(R.id.cache_cleaner_text);
            textview.setTextAppearance(this, R.style.disabledText);
            textview1.setTextAppearance(this, R.style.disabledText);
            textview2.setTextAppearance(this, R.style.disabledText);
            startStopBtn.setText(R.string.btn_start);
            startStopBtn.setBackgroundResource(R.drawable.start_button_bg);
            wifiImg.setImageResource(R.drawable.wifi_off);
            wifiSwitch.setChecked(false);
            mobImg.setImageResource(R.drawable.mobile_data_off);
            mobSwitch.setChecked(false);
            cacheImg.setImageResource(R.drawable.cache_cleaner_off);
            cacheSwitch.setChecked(false);
            enableDisableContent(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();
        getDefaults();
        setDefaults();
        AdView adview = (AdView)findViewById(R.id.ad);
        com.google.android.gms.ads.AdRequest adrequest = (new com.google.android.gms.ads.AdRequest.Builder()).build();
        //Log.i(TAG, "Before load of Ad");
        adview.loadAd(adrequest);
        utilityWM.displayInterstitialAd();
        //Log.i(TAG, "After load of Ad");
    }
    
    @Override
	protected void onStop() {
    	//Log.i(TAG, "Entering onStop");
    	super.onDestroy();
    	utilityWM.displayInterstitialAd();
    	settings = getSharedPreferences(PREFS_NAME, 0);
		settingEditor = settings.edit();
		if (!isMyServiceRunning(OnOffEventHandler.class)) {
			settingEditor.clear();
			settingEditor.commit();
		}
    	//Log.i(TAG, "Exiting onStop");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//    	Log.i(TAG, "Entering onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
//        Log.i(TAG, "Exiting onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
