package com.tumblr.apps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class RefreshService extends Service {	
	private Intent refreshBroadcastIntent;
	
	private AlarmManager alarmManager;
	
	private PendingIntent timerIntent;
	
	//TODO
	private SharedPreferences preferences = null;
	public static final String REFRESHFEEDS_ACTION = "com.tumblr.apps.refresh";
	
	@Override
	public IBinder onBind(Intent intent) {
		onRebind(intent);
		return null;
	}
	
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return true;  // we want to use rebind
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		refreshBroadcastIntent = new Intent(REFRESHFEEDS_ACTION).putExtra("scheduled", true);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		restartTimer(true);
	}

	private void restartTimer(boolean created) {
		if (timerIntent == null) {
			timerIntent = PendingIntent.getBroadcast(this, 0, refreshBroadcastIntent, 0);
		} else {
			alarmManager.cancel(timerIntent);
		}
		
		int time = 3600000;
		
		long initialRefreshTime = SystemClock.elapsedRealtime() + 10000;
		
		if (created) {
			int lastRefresh = 0;
			if (lastRefresh > 0) {
				// this indicates a service restart by the system
				initialRefreshTime = Math.max(SystemClock.elapsedRealtime() + 10000, lastRefresh+time);
			}
		}
		
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, initialRefreshTime, time, timerIntent);
	}

	@Override
	public void onDestroy() {
		if (timerIntent != null) {
			alarmManager.cancel(timerIntent);
		}
		super.onDestroy();
	}
}