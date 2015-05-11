package ru.gleb.manyagin.weathermap.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.gleb.manyagin.weathermap.R;
import ru.gleb.manyagin.weathermap.domain.WeatherData;
import ru.gleb.manyagin.weathermap.notifications.NotifiClass;
import ru.gleb.manyagin.weathermap.provider.WeatherProviderContract;
import ru.gleb.manyagin.weathermap.utilities.UtilitiesClass;

/**
 * Created by gleb.manyagin on 08.05.2015.
 */
public class DateBroadcastReceiver extends BroadcastReceiver {
    public DateBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
