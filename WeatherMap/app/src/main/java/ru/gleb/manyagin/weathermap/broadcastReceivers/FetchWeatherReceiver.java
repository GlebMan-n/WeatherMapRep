package ru.gleb.manyagin.weathermap.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ru.gleb.manyagin.weathermap.R;
import ru.gleb.manyagin.weathermap.another_tasks.FetchWeatherData;
import ru.gleb.manyagin.weathermap.domain.WeatherData;
import ru.gleb.manyagin.weathermap.notifications.NotifiClass;
import ru.gleb.manyagin.weathermap.provider.WeatherProviderContract;
import ru.gleb.manyagin.weathermap.utilities.UtilitiesClass;
/**
 * Created by gleb.manyagin on 08.05.2015.
 */
public class FetchWeatherReceiver extends BroadcastReceiver {
    private final static int msecInMin = 60000;
    private final static int msecInHour = 3600000;
    private final static int msecInDay = 86400000;

    public FetchWeatherReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        Long lTimeExecuteFetchWeather = new Long(preference.getLong(context.getString(R.string.pref_last_data_update_key),  new Long(context.getString(R.string.pref_last_data_update_default))));

        String strTimeNotifyKey =  preference.getString(context.getString(R.string.pref_time_to_notifi_key), context.getString(R.string.pref_time_to_notifi_default));
        long timeToNotifi =    new Long(strTimeNotifyKey);
        long currentTime = System.currentTimeMillis();

        long timeElepsedFetchWeather = (currentTime - lTimeExecuteFetchWeather) / msecInMin;
        int iInterval =  new Integer(preference.getString(context.getString(R.string.pref_forecast_interval_key), context.getString(R.string.pref_forecast_interval_default)));


        if (timeElepsedFetchWeather > iInterval) {

            FetchWeatherData task;
            task = new FetchWeatherData(context);
            task.execute();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeToNotifi);
        Date dt = new Date();
        if(calendar.getTime().getHours() == dt.getHours() && calendar.getTime().getMinutes() == dt.getMinutes()) {
        String strAdvice = context.getString(R.string.advice);
        String str = WeatherProviderContract.PlaceInfo.PLACE_NAME + " = \"" + context.getString(R.string.current_position_map_text) + "\"";
        ArrayList<WeatherData> data = UtilitiesClass.fetchDataFromBd(context.getContentResolver(), str);
        if (data.isEmpty())
            return;
        WeatherData today = null;
        Calendar _calendar = Calendar.getInstance();
        _calendar.setTimeInMillis(System.currentTimeMillis());
        Date dateDtToday = _calendar.getTime();
        for (int i = 0; i < data.size(); i++) {
            _calendar.setTimeInMillis(data.get(i).getDt() * 1000L);
            Date dateDt = _calendar.getTime();
            if (dateDtToday.getDay() == dateDt.getDay() && dateDtToday.getMonth() == dateDt.getMonth() && dateDtToday.getYear() == dateDt.getYear()) {
                today = data.get(i);
                break;
            }
        }

        switch (UtilitiesClass.getWeatherClass(today.getWeather().getWetherId())) {

            case 2:
                strAdvice = context.getString(R.string.advice_thundershtorm);
                break;
            case 3:
                strAdvice = context.getString(R.string.advice_drizzle);
                break;
            case 5:
                strAdvice = context.getString(R.string.advice_rain);
                break;
            case 6:
                strAdvice = context.getString(R.string.advice_snow);
                break;
            case 7:
                strAdvice = context.getString(R.string.advice_atmosphere);
                break;
            case 8:
                if(today.getWeather().getWetherId() == 800)
                    strAdvice = context.getString(R.string.advice_clear_sky);
                else
                    strAdvice = context.getString(R.string.advice_clouds);
                break;
            case 9:
                break;
            default:
                break;
        }
        NotifiClass notifi = new NotifiClass(context, this.getClass(), "Карта погоды", "Информация", strAdvice, 102);
        }
    }
}
