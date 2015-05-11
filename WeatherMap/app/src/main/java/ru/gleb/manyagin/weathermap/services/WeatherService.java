package ru.gleb.manyagin.weathermap.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import ru.gleb.manyagin.weathermap.R;
import ru.gleb.manyagin.weathermap.broadcastReceivers.DateBroadcastReceiver;
import ru.gleb.manyagin.weathermap.broadcastReceivers.FetchWeatherReceiver;
import ru.gleb.manyagin.weathermap.broadcastReceivers.TimeBroadcastReceiver;

public class WeatherService extends Service {

    private TimeBroadcastReceiver mTimeBroadCastReceiver = new TimeBroadcastReceiver();
    private DateBroadcastReceiver mDateBroadcastReceiver = new DateBroadcastReceiver();
    private FetchWeatherReceiver mFetchWeatherDataReceiver = new FetchWeatherReceiver();


    final String LOG_TAG = "myLogs";

    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOG_TAG, "onStartCommand");
        Toast.makeText(getApplicationContext(), "Сервис запущен", Toast.LENGTH_LONG).show();
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceivers();
        Log.v(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "onBind");
        return null;
    }

    void someTask() {
        registerBroadcastReceiver();
    }
    public void registerBroadcastReceiver() {

        try {


            this.registerReceiver(mTimeBroadCastReceiver, new IntentFilter(
                    "android.intent.action.TIME_TICK"));

            this.registerReceiver(mDateBroadcastReceiver, new IntentFilter(
                    "android.intent.action.DATE_CHANGED"));

            this.registerReceiver(mFetchWeatherDataReceiver, new IntentFilter(
                    getString(R.string.action_fetch_weather_data)));
        }
        catch (Exception e)
        {
            Log.v(LOG_TAG, e.getMessage());
        }


    }

    public void unregisterBroadcastReceivers() {
        this.unregisterReceiver(mTimeBroadCastReceiver);
        this.unregisterReceiver(mDateBroadcastReceiver);
        this.unregisterReceiver(mFetchWeatherDataReceiver);
    }
}
