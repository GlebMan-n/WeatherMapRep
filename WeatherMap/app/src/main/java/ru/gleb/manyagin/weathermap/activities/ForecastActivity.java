package ru.gleb.manyagin.weathermap.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ru.gleb.manyagin.weathermap.R;
import ru.gleb.manyagin.weathermap.domain.WeatherData;
import ru.gleb.manyagin.weathermap.provider.WeatherProviderContract;
import ru.gleb.manyagin.weathermap.utilities.UtilitiesClass;
public class ForecastActivity extends ActionBarActivity {

    Integer iCityId = 0;
    WeatherData weatherData = null;
    private TextView cityNameTextView;
    private TextView tempTextView;
    private TextView iconTextView;
    private Typeface weatherFont;
    ListView myListView;

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        Intent intent = getIntent();
        iCityId = intent.getIntExtra("place_id", 0); //if it's a string you stored.
        ArrayList<WeatherData> arrayList = new ArrayList<WeatherData>();
        try {
            arrayList = UtilitiesClass.fetchDataFromBd(getContentResolver(), WeatherProviderContract.PlaceInfo.PLACE_ID + " = " + iCityId.toString());
        }
        catch(Exception e)
        {

            Log.e(ForecastActivity.class.toString(), "Ошибка получения данных о знаке из БД");
        }
        if(arrayList.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Нет данных по этому знаку", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        weatherData = arrayList.get(0);
        weatherFont = Typeface.createFromAsset(this.getAssets(), "fonts/webfont.ttf");
        adapter = new ArrayAdapter<String>(this,
                R.layout.list_item_layout,
                listItems);
        myListView = (ListView) findViewById(R.id.weatherListData);
        myListView.setAdapter(adapter);
        cityNameTextView = (TextView) findViewById(R.id.cityText);
        tempTextView = (TextView) findViewById(R.id.temperature);
        iconTextView = (TextView) findViewById(R.id.iconTextView);

        iconTextView.setTypeface(weatherFont);
        cityNameTextView.setText(weatherData.getCity().getName());
        iconTextView.setText(UtilitiesClass.getIconChar(weatherData.getWeather().getIcon()));
        String degrCel = getString(R.string.caption_weather_forecast_degrees) + getString(R.string.caption_weather_forecast_c);

        tempTextView.setText(new Integer(weatherData.getWeather().getTemp().intValue()).toString() + degrCel);

        String caption = getString(R.string.caption_weather_forecast_date) + ": ";
        SimpleDateFormat df = new SimpleDateFormat("HH:mm, dd.MM.yyyy");
        String date = df.format(weatherData.getDt()*(long) 1000);
        listItems.add(caption + date);

        caption = getString(R.string.caption_weather_forecast_main) + ": ";
        listItems.add(caption + weatherData.getWeather().getMain());

        caption = getString(R.string.caption_weather_forecast_description) + ": ";
        listItems.add(caption + weatherData.getWeather().getDescription());

        caption = getString(R.string.caption_weather_forecast_temp_min) + ": ";
        listItems.add(caption + new Integer(weatherData.getWeather().getTempMin().intValue()).toString() + degrCel);

        caption = getString(R.string.caption_weather_forecast_temp_max) + ": ";
        listItems.add(caption + new Integer(weatherData.getWeather().getTempMax().intValue()).toString() + degrCel);

        caption = getString(R.string.caption_weather_forecast_pressure) + ": ";
        listItems.add(caption + new Integer(weatherData.getWeather().getPressure().intValue()).toString() +" " + getString(R.string.caption_weather_forecast_mm_rt_st));

        caption = getString(R.string.caption_weather_forecast_humidity) + ": ";
        listItems.add(caption + new Integer(weatherData.getWeather().getHumidity().intValue()).toString() + "%");

        caption = getString(R.string.caption_weather_forecast_wind_speed) + ": ";
        listItems.add(caption + new Integer(weatherData.getWeather().getWndSpeed().intValue()).toString() + " " + getString(R.string.caption_weather_forecast_speed_descr));

        caption = getString(R.string.caption_weather_forecast_wind_deg) + ": ";
        listItems.add(caption + new Integer(weatherData.getWeather().getWndDeg().intValue()).toString() + getString(R.string.caption_weather_forecast_degrees));

        caption = getString(R.string.caption_weather_forecast_lat) + ": ";
        listItems.add(caption + weatherData.getCity().getLat().toString());
        caption = getString(R.string.caption_weather_forecast_lon) + ": ";
        listItems.add(caption + weatherData.getCity().getLon().toString());

    }
}
