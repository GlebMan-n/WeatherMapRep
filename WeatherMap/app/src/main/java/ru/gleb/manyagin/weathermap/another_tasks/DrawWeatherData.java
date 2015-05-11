package ru.gleb.manyagin.weathermap.another_tasks;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;

import ru.gleb.manyagin.weathermap.R;
import ru.gleb.manyagin.weathermap.domain.WeatherData;
import ru.gleb.manyagin.weathermap.provider.WeatherProviderContract;
import ru.gleb.manyagin.weathermap.tokens.WeatherToken;
import ru.gleb.manyagin.weathermap.utilities.UtilitiesClass;


/**
 * Created by user on 01.05.2015.
 */

public class DrawWeatherData extends AsyncTask<Void, Void,  ArrayList<WeatherData>> {

    private final ContentResolver resolver;
    private final MapView mapView;
    Context context;
    private  final String where;
    Boolean bShowToasts = true;
    public DrawWeatherData(Context _context, MapView _mapView)
    {
        context = _context;
        resolver = context.getContentResolver();
        mapView = _mapView;
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        bShowToasts = new Boolean(preference.getBoolean(context.getString(R.string.pref_show_debug_key), new Boolean(context.getString(R.string.pref_show_debug_default))));
        where = WeatherProviderContract.PlaceInfo.PLACE_NAME + " != \"" + context.getString(R.string.current_position_map_text) + "\"";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(bShowToasts)
            Toast.makeText(context, "Начинаем рисовать знаки", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected ArrayList<WeatherData> doInBackground(Void... params) {
        ArrayList<WeatherData> data = new ArrayList<WeatherData>();
        try
        {
            data = UtilitiesClass.fetchDataFromBd(resolver,where);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(ArrayList<WeatherData> result) {
        super.onPostExecute(result);
        try {
            ArrayList<Overlay> arrayList = new ArrayList<Overlay>();
            for (int i = 0; i < mapView.getOverlays().size(); i++) {
                WeatherToken token = null;
                Overlay overlay = mapView.getOverlayManager().get(i);

                if (!overlay.getClass().equals(WeatherToken.class))
                    arrayList.add(overlay);
            }

            mapView.getOverlays().clear();
            mapView.getOverlays().addAll(arrayList);
            mapView.invalidate();
            if (result.size() < 1) {
                if (bShowToasts)
                    Toast.makeText(context, "Нет знаков для отрисовки", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < result.size(); i++) {
                UtilitiesClass.drawTokenOnMap(context, mapView, result.get(i));
            }
            mapView.invalidate();

            if (bShowToasts)
                Toast.makeText(context, "Удалось нарисовать знаки", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){}

    }
}

