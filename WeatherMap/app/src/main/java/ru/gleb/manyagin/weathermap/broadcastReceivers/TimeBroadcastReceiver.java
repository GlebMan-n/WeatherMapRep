package ru.gleb.manyagin.weathermap.broadcastReceivers;

/**
 * Created by gleb.manyagin on 08.05.2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.gleb.manyagin.weathermap.R;


public class TimeBroadcastReceiver extends BroadcastReceiver {
    public TimeBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent _intent = new Intent();
        _intent.setAction(context.getString(R.string.action_fetch_weather_data));
        context.sendBroadcast(_intent);
    }
}