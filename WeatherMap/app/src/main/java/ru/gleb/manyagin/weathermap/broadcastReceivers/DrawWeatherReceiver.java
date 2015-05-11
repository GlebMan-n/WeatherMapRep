package ru.gleb.manyagin.weathermap.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.osmdroid.views.MapView;
import ru.gleb.manyagin.weathermap.another_tasks.DrawWeatherData;
/**
 * Created by gleb.manyagin on 08.05.2015.
 */
public class DrawWeatherReceiver extends BroadcastReceiver {
    private MapView mapView;
    Boolean isDrawing = false;
    public DrawWeatherReceiver() {
        this.mapView = null;
    }

    public DrawWeatherReceiver(MapView mapView) {
        this.mapView = mapView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        DrawWeatherData task = null;
        task = new DrawWeatherData(context, mapView);
        task.execute();
    }

}

