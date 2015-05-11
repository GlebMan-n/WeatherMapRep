package ru.gleb.manyagin.weathermap.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import ru.gleb.manyagin.weathermap.R;
import ru.gleb.manyagin.weathermap.domain.WeatherData;
import ru.gleb.manyagin.weathermap.provider.WeatherProviderContract;
import ru.gleb.manyagin.weathermap.utilities.UtilitiesClass;

public class CurrentPositionForecastActivity extends ActionBarActivity {

    ArrayAdapter<String> adapter;
    ListView myListView;
    ArrayList<String> listItems;
    ArrayList<WeatherData> data = new ArrayList<WeatherData>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String str = WeatherProviderContract.PlaceInfo.PLACE_NAME + " = \"" + getString(R.string.current_position_map_text) + "\"";
        data = UtilitiesClass.fetchDataFromBd(getContentResolver(), str);

        if(data.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Требуется обновить базу", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_current_position_forecast);
        listItems = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                R.layout.list_item_forecast,
                listItems);
        myListView = (ListView) findViewById(R.id.listview_forecast);
        myListView.setAdapter(adapter);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String degrCel = getString(R.string.caption_weather_forecast_degrees) + getString(R.string.caption_weather_forecast_c);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Date dateDtToday = calendar.getTime();
        ArrayList<String> strArray = new ArrayList<String>();
        for(int i = 0; i < data.size(); i++)
        {
            calendar.setTimeInMillis(data.get(i).getDt()*1000L);
            Date dateDt = calendar.getTime();
            String strDate = df.format(data.get(i).getDt()*(long) 1000);
            if(dateDtToday.getDate() <= dateDt.getDate()) {
                 String strTmp = strDate + " "
                        + new Integer(data.get(i).getWeather().getTemp().intValue()).toString()
                        + degrCel + " "
                        + data.get(i).getWeather().getDescription();
                strArray.add(strTmp);
            }
        }
        adapter.addAll(strArray);
    }
}
