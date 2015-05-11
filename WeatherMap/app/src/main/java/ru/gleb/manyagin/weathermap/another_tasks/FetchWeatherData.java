package ru.gleb.manyagin.weathermap.another_tasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ru.gleb.manyagin.weathermap.R;
import ru.gleb.manyagin.weathermap.domain.City;
import ru.gleb.manyagin.weathermap.domain.Weather;
import ru.gleb.manyagin.weathermap.domain.WeatherData;
import ru.gleb.manyagin.weathermap.notifications.NotifiClass;
import ru.gleb.manyagin.weathermap.provider.WeatherProviderContract;
import ru.gleb.manyagin.weathermap.utilities.UtilitiesClass;

/**
 * Created by user on 01.05.2015.
 */

public class FetchWeatherData extends AsyncTask<Void, Void, ArrayList<WeatherData>> {
    private final ContentResolver resolver;
    private Integer zoomLvl;
    private final String LOG_TAG = "***FetchWeatherData***";
    Context context;
    private GeoPoint geoSpecLeftTop = null;
    private GeoPoint geoSpecRightBottom = null;
    private GeoPoint geoGenLeftTop = null;
    private GeoPoint geoGenRightBottom = null;
    Boolean bShowToasts = true;
    private GeoPoint geoCurrentPosition = null;
    Integer numDaysForForecast = 0;
    public FetchWeatherData(Context context)
    {
        this.resolver = context.getContentResolver();
        this.context = context;
        zoomLvl = 0;
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        bShowToasts = new Boolean(preference.getBoolean(context.getString(R.string.pref_show_debug_key), new Boolean(context.getString(R.string.pref_show_debug_default))));
        Double latFirst = new Double(preference.getFloat(context.getString(R.string.general_left_top_lat_key), new Float(context.getString(R.string.general_left_top_lat_default))));
        Double lonFirst = new Double(preference.getFloat(context.getString(R.string.general_left_top_lon_key), new Float(context.getString(R.string.general_left_top_lon_default))));
        Double latSecond = new Double(preference.getFloat(context.getString(R.string.general_right_bottom_lat_key), new Float(context.getString(R.string.general_right_bottom_lat_default))));
        Double lonSecond = new Double(preference.getFloat(context.getString(R.string.general_right_bottom_lon_key), new Float(context.getString(R.string.general_right_bottom_lon_default))));
        geoGenLeftTop = new GeoPoint(latFirst,lonFirst);
        geoGenRightBottom = new GeoPoint(latSecond,lonSecond);
        latFirst = new Double(preference.getFloat(context.getString(R.string.special_left_top_lat_key), new Float(context.getString(R.string.special_left_top_lat_default))));
        lonFirst = new Double(preference.getFloat(context.getString(R.string.special_left_top_lon_key), new Float(context.getString(R.string.special_left_top_lon_default))));
        latSecond = new Double(preference.getFloat(context.getString(R.string.special_right_bottom_lat_key), new Float(context.getString(R.string.special_right_bottom_lat_default))));
        lonSecond = new Double(preference.getFloat(context.getString(R.string.special_right_bottom_lon_key), new Float(context.getString(R.string.special_right_bottom_lon_default))));
        geoSpecLeftTop = new GeoPoint(latFirst,lonFirst);
        geoSpecRightBottom = new GeoPoint(latSecond,lonSecond);
        Double lat = new Double(preference.getFloat(context.getString(R.string.pref_current_latitude_key), new Float(context.getString(R.string.pref_current_latitude_default))));
        Double lon = new Double(preference.getFloat(context.getString(R.string.pref_current_longitude_key), new Float(context.getString(R.string.pref_current_longitude_default))));
        numDaysForForecast = new Integer(preference.getString(context.getString(R.string.pref_forecast_days_key), context.getString(R.string.pref_forecast_days_default)));
        geoCurrentPosition = new GeoPoint(lat,lon);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(bShowToasts)
         Toast.makeText(context, "Начинаю загрузку данных, может занять около минуты", Toast.LENGTH_LONG).show();
    }

    @Override
    protected ArrayList<WeatherData> doInBackground(Void... strings) {

        ArrayList<WeatherData> weatherDatas = new ArrayList<WeatherData>();
        ArrayList<WeatherData> weatherDatasCurrent = new ArrayList<WeatherData>();
        String strUrl = null;
        GeoPoint topLeft = null;
        GeoPoint buttomRight = null;

        strUrl = UtilitiesClass.urlGenerator(geoCurrentPosition,numDaysForForecast);
        try {
            weatherDatasCurrent.addAll(arrayFromCloud(strUrl));
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG," exeption - weatherDatas.addAll(arrayFromCloud(strUrl))");
        }

        for(int i = 1; i < 15; i++) {

            zoomLvl = i;
            Double latOfTheTopLeftPoint;
            Double lonOfTheTopLeftPoint;
            Double latOfTheBottomRightPoint;
            Double lonOfTheBottomRight;

            if (i < 6)
            {
                latOfTheTopLeftPoint = geoGenLeftTop.getLatitude();//56.80;//70.0;//geoUpLeft.getLatitude();////
                lonOfTheTopLeftPoint = geoGenLeftTop.getLongitude();//36.23;//30.0;//geoUpLeft.getLongitude();////
                latOfTheBottomRightPoint = geoGenRightBottom.getLatitude();///54.79;//42.0;//geoButtomRight.getLatitude();////
                lonOfTheBottomRight = geoGenRightBottom.getLongitude();//40.063;//160.0;//geoButtomRight.getLongitude();////

            }
            else
            {
                latOfTheTopLeftPoint = geoSpecLeftTop.getLatitude();//56.80;//70.0;//geoUpLeft.getLatitude();////
                lonOfTheTopLeftPoint = geoSpecLeftTop.getLongitude();//36.23;//30.0;//geoUpLeft.getLongitude();////
                latOfTheBottomRightPoint = geoSpecRightBottom.getLatitude();///54.79;//42.0;//geoButtomRight.getLatitude();////
                lonOfTheBottomRight = geoSpecRightBottom.getLongitude();//40.063;//160.0;//geoButtomRight.getLongitude();////

            }
            topLeft = new GeoPoint(latOfTheTopLeftPoint, lonOfTheTopLeftPoint);
            buttomRight = new GeoPoint(latOfTheBottomRightPoint, lonOfTheBottomRight);


            strUrl = UtilitiesClass.urlGenerator(topLeft, buttomRight, i);
            ArrayList<WeatherData> tmpData = new  ArrayList<WeatherData>();

            try {
                tmpData = arrayFromCloud(strUrl);
            }
            catch (Exception e)
            {
                Log.e(LOG_TAG," exeption - tmpData = arrayFromCloud(strUrl);");
            }

            for(int j = 0; j < tmpData.size(); j++) {
                Boolean bPut = true;

                for(int p = 0; p < weatherDatas.size(); p++)
                {
                    int iIdOne;
                    int iIdTwo;

                    iIdOne = tmpData.get(j).getCity().getId();
                    iIdTwo = weatherDatas.get(p).getCity().getId();
                    if(iIdOne == iIdTwo) {
                        bPut = false;
                        break;
                    }
                }

                if(bPut) {
                    tmpData.get(j).setZoomLevel(i);
                    weatherDatas.add(tmpData.get(j));
                }
            }
        }
        try {
            weatherDatas.addAll(weatherDatasCurrent);
            putAllDataToBd(weatherDatas);
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG," exeption - putAllDataToBd(weatherDatas);");
        }
        return weatherDatas;
    }

    private String featchJsonString(String strUrl)
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;
        String result = null;

        try {

            URL url;
            url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return result;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return result;
            }
            forecastJsonStr = buffer.toString();
            JSONObject object = new JSONObject();
            Boolean bOk = UtilitiesClass.isJSONValid(forecastJsonStr);
            if (bOk)
                result = forecastJsonStr;
            if (result == null) return result;

        }
        catch(Exception e) {

        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {}
            }
        }

        return result;
    }

    private ArrayList<WeatherData> arrayFromCloud(String strUrl) {

        ArrayList<WeatherData> result = new ArrayList<WeatherData>();
        try {
            String str = featchJsonString(strUrl);
            if(UtilitiesClass.isJSONValid(str))
                result = getWeatherDataFromJson(str);

            if (result == null || result.size() < 1)
                return null;
            else
                return result;
        }catch (Exception e)
        {
            Log.e(LOG_TAG," exeption - private ArrayList<WeatherData> arrayFromCloud(String strUrl)");
        }

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<WeatherData> result) {
        super.onPostExecute(result);
       if(result != null && result.size() > 0) {

           NotifiClass notifi = new NotifiClass(context, this.getClass(), "Данные о погоде обновлены", "Информация", "Получены обновленные данные о погоде для заданных областей", 101);

           long currentTime = System.currentTimeMillis();
           final SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
           edit.putLong(context.getString(R.string.pref_last_data_update_key), currentTime);
           edit.commit();

           Intent _intent = new Intent();
           _intent.setAction(context.getString(R.string.action_map_redraw_tokens));
           context.sendBroadcast(_intent);
        }
        else if(bShowToasts) {
           NotifiClass notifi = new NotifiClass(context, this.getClass(), "Нет доступа к сети", "Информация", "Для получения данных о погоде необходим доступ в интернет", 101);
       }

        String log = " End of fetch weather data task, result:" + result.toString();
        Log.e(LOG_TAG, log);
    }

    private ArrayList<WeatherData> getWeatherDataFromJson(String forecastJsonStr)
            throws JSONException {
        //region Keys
        final String OWM_LIST = "list";

        final String OWM_SIZE = "cnt";
        final String OWM_COORD = "coord";
        final String OWM_ID = "id";
        final String OWM_NAME = "name";
        final String OWM_LAT = "lat";
        final String OWM_LON = "lon";
        final String OWM_COUNTRY = "country";

        final String OWM_MAIN = "main";
        final String OWM_TEMP = "temp";
        final String OWM_TEMP_MIN = "temp_min";
        final String OWM_TEMP_MAX = "temp_max";
        final String OWM_PRESSURE = "pressure";
        final String OWM_SEA_LEVEL = "sea_level";
        final String OWM_GRND_LEVEL = "grnd_level";
        final String OWM_HUMIDITY = "humidity";

        final String OWM_WEATHER = "weather";
        final String OWM_WEATHER_ID = "id";
        final String OWM_WEATHER_MAIN = "main";
        final String OWM_WEATHER_DESCRIPTION = "description";
        final String OWM_WEATHER_ICON = "icon";
        final String OWM_WND_SPEED= "speed";
        final String OWM_WND_DEG = "deg";
        final String OWM_WND_GUST = "gust";
        final String OWM_WND_CLOUDS = "clouds";
        final String OWM_WND_CLOUDS_ALL = "all";
        final String OWM_DT = "dt";
        final String OWM_WIND = "wind";
        //endregion

        JSONObject forecastJson = null;
        ArrayList<WeatherData> resultArray = new ArrayList<WeatherData>();
        try
        {

            forecastJson = new JSONObject(forecastJsonStr);

            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
            for (int i = 0; i < weatherArray.length(); i++)
            {

                WeatherData weatherData = new WeatherData();
                JSONObject coordObject = null;
                JSONObject mainObject = null;
                JSONObject cloudObject = null;
                JSONObject windObject = null;
                JSONObject weatherObject = null;
                final String OWM_TEMPERATURE = "temp";
                final String OWM_MAX = "max";
                JSONObject currentObject = weatherArray.getJSONObject(i);
                try
                {
                    coordObject = currentObject.getJSONObject(OWM_COORD);
                }
                catch (JSONException e){}

                try
                {
                    weatherObject = currentObject.getJSONArray(OWM_WEATHER).getJSONObject(0);
                }
                catch (JSONException e){}


                try
                {
                    mainObject = currentObject.getJSONObject(OWM_MAIN);
                }
                catch (JSONException e){}

                try
                {
                    cloudObject = currentObject.getJSONObject(OWM_WND_CLOUDS);
                }
                catch (JSONException e){}

                try
                {
                    windObject = currentObject.getJSONObject(OWM_WIND);
                }
                catch (JSONException e){}


                //region GetDataFromJsonObjects
                try
                {
                    weatherData.setDt(currentObject.getInt(OWM_DT));
                }
                catch (JSONException e){}

                //City
                try
                {
                    weatherData.getCity().setId(currentObject.getInt(OWM_ID));
                }
                catch (JSONException e)
                {
                    weatherData.getCity().setId(i+1);
                }

                try
                {
                    weatherData.getCity().setName(currentObject.getString(OWM_NAME));
                }
                catch (JSONException e){
                    weatherData.getCity().setName(context.getString(R.string.current_position_map_text));
                }

                try
                {
                    if(coordObject != null)
                        weatherData.getCity().setLat(coordObject.getDouble(OWM_LAT));
                    else
                        weatherData.getCity().setLat(geoCurrentPosition.getLatitude());
                }
                catch (JSONException e){}

                try
                {
                    if(coordObject != null)
                        weatherData.getCity().setLon(coordObject.getDouble(OWM_LON));
                    else
                        weatherData.getCity().setLon(geoCurrentPosition.getLongitude());
                }
                catch (JSONException e){}


                try
                {
                    String country = currentObject.getString(OWM_COUNTRY);
                    weatherData.getCity().setCountry(country);
                }
                catch (JSONException e)
                {

                }

                weatherData.setZoomLevel(zoomLvl);



                Weather weather = new Weather();

                JSONObject temperatureObject = null;
                try{
                    if(mainObject != null)
                        weather.setTemp(mainObject.getDouble(OWM_TEMP));
                    else {
                        temperatureObject = currentObject.getJSONObject(OWM_TEMPERATURE);
                        weather.setTemp(temperatureObject.getDouble(OWM_MAX) - 274.15  );

                    }
                }
                catch (JSONException e){}



                try{
                    if(mainObject != null)
                        weather.setHumidity(mainObject.getInt(OWM_HUMIDITY));
                    else
                    {
                        weather.setHumidity(currentObject.getInt(OWM_HUMIDITY));
                    }
                }
                catch (JSONException e){}
                try{

                    if(mainObject != null)
                        weather.setTempMin(mainObject.getDouble(OWM_TEMP_MIN));
                    else
                    {
                        weather.setTempMin(temperatureObject.getDouble(OWM_TEMP_MIN));
                    }

                }
                catch (JSONException e){}

                try
                {
                    if(mainObject != null)
                        weather.setTempMax(mainObject.getDouble(OWM_TEMP_MAX));
                    else
                    {
                        weather.setTempMax(temperatureObject.getDouble(OWM_TEMP_MAX));
                    }


                }
                catch (JSONException e){}


                try
                {
                    if(mainObject != null)
                        weather.setPressure(mainObject.getDouble(OWM_PRESSURE));
                    else
                    {
                        weather.setPressure(currentObject.getDouble(OWM_PRESSURE));
                    }
                }
                catch (JSONException e){}

                try
                {
                    if(mainObject != null)
                    weather.setPressureSeaLvl(mainObject.getDouble(OWM_SEA_LEVEL));
                }
                catch (JSONException e){}

                try
                {
                    if(mainObject != null)
                    weather.setPressureGrndLvl(mainObject.getDouble(OWM_GRND_LEVEL));
                }
                catch (JSONException e){}

                try
                {
                    if(windObject != null)
                        weather.setWndSpeed(windObject.getDouble(OWM_WND_SPEED));
                    else
                        weather.setWndSpeed(currentObject.getDouble(OWM_WND_SPEED));
                }
                catch (JSONException e){}


                try
                {
                    if(windObject != null)
                        weather.setWndDeg(windObject.getDouble(OWM_WND_DEG));
                    else
                        weather.setWndDeg(currentObject.getDouble(OWM_WND_DEG));
                }
                catch (JSONException e){}

                try
                {
                    if(windObject != null)
                        weather.setWndGust(windObject.getDouble(OWM_WND_GUST));
                }
                catch (JSONException e){}

                try
                {
                    if(cloudObject != null)
                    weather.setCloudiness(cloudObject.getString(OWM_WND_CLOUDS_ALL));
                }
                catch (JSONException e){}


                try
                {
                    if(weatherObject != null)
                        weather.setWeatherId(weatherObject.getInt(OWM_WEATHER_ID));
                }
                catch (JSONException e){}

                try
                {
                    if(weatherObject != null)
                        weather.setMain(weatherObject.getString(OWM_WEATHER_MAIN));
                }
                catch (JSONException e){}

                try
                {
                    if(weatherObject != null)
                     weather.setDescription(weatherObject.getString(OWM_WEATHER_DESCRIPTION));
                }
                catch (JSONException e){}

                try
                {
                    if(weatherObject != null)
                        weather.setIcon(weatherObject.getString(OWM_WEATHER_ICON));
                }
                catch (JSONException e){}
                //endregion

                weather.setPlaceId(weatherData.getCity().getId());

                weatherData.setWeather(weather);
                resultArray.add(weatherData);
            }
        }
        catch(Exception e)
        {
            return null;
        }
        return resultArray;

    }

    private void putAllDataToBd(ArrayList<WeatherData> data) {

        if(data.size() < 1)
            return;
        resolver.delete(WeatherProviderContract.PlaceInfo.CONTENT_URI, null, null);
        resolver.delete(WeatherProviderContract.WeatherInfo.CONTENT_URI, null, null);
        for(int i = 0; i < data.size(); i++)
        {
            putDataToBd(data.get(i));
        }

    }

    private void putDataToBd(WeatherData data)
    {
        try {
            City city = data.getCity();
            Weather weather = null;
            weather = data.getWeather();

            ContentValues cityValue = new ContentValues();
            cityValue.put(WeatherProviderContract.PlaceInfo.PLACE_ID, city.getId());
            cityValue.put(WeatherProviderContract.PlaceInfo.PLACE_COUNTRY, city.getCountry());
            cityValue.put(WeatherProviderContract.PlaceInfo.PLACE_MIN_ZOOM_LEVEL, data.getZoomLevel());
            cityValue.put(WeatherProviderContract.PlaceInfo.PLACE_LAT, city.getLat());
            cityValue.put(WeatherProviderContract.PlaceInfo.PLACE_LON, city.getLon());
            cityValue.put(WeatherProviderContract.PlaceInfo.PLACE_NAME, city.getName());


            ContentValues weatherValue = new ContentValues();
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_PLACE_ID, city.getId());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_DATE, data.getDt());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_TEMP, weather.getTemp());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_TEMP_MIN, weather.getTempMin());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_TEMP_MAX, weather.getTempMax());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_HUMIDITY, weather.getHumidity());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_PRESSURE, weather.getPressure());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_SEA_LVL, weather.getPressureSeaLvl());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_GRND_LVL, weather.getPressureGrndLvl());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_WND_SPEED, weather.getWndSpeed());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_WND_DEG, weather.getWndDeg());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_WND_GUST, weather.getWndGust());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_ID, weather.getWetherId());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_MAIN, weather.getMain());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_DESC, weather.getDescription());
            weatherValue.put(WeatherProviderContract.WeatherInfo.WEATHER_ICON, weather.getIcon());

            ContentValues[] values = new ContentValues[1];
            values[0] = cityValue;

            resolver.bulkInsert(WeatherProviderContract.PlaceInfo.CONTENT_URI, values);
            values[0] = weatherValue;
            resolver.bulkInsert(WeatherProviderContract.WeatherInfo.CONTENT_URI, values);

        }
        catch (Exception e)
        {
            Log.e("putDataToBd","Не удалось добавить запись в БД");
        }
    }



}