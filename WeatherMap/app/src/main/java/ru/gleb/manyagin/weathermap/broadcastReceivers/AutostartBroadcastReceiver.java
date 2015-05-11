package ru.gleb.manyagin.weathermap.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.gleb.manyagin.weathermap.services.WeatherService;
/**
 * Created by gleb.manyagin on 08.05.2015.
 */
public class AutostartBroadcastReceiver extends BroadcastReceiver {
    public AutostartBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent srvIntent = new Intent(context, WeatherService.class);
        context.startService(srvIntent);
    }
}
