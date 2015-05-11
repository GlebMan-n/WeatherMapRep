package ru.gleb.manyagin.weathermap.utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

import ru.gleb.manyagin.weathermap.R;
import ru.gleb.manyagin.weathermap.domain.City;
import ru.gleb.manyagin.weathermap.domain.Weather;
import ru.gleb.manyagin.weathermap.domain.WeatherData;
import ru.gleb.manyagin.weathermap.provider.WeatherProviderContract;
import ru.gleb.manyagin.weathermap.tokens.WeatherToken;

/**
 * Created by user on 01.05.2015.
 */
public class UtilitiesClass {
    //region Fields
    //endregion

    //region Keys
    //endregion

    //region Constructors
    //endregion

    //region Getter
    //endregion

    //region Setter
    //endregion

    //region Methods
    public static String getTokenText(String icon) {
        String result;

        if(icon.equals("01d"))
        {
            result = "ясно";
        }
        else if(icon.equals("02d"))
        {
            result = "облачно с прояснениями";
        }
        else if(icon.equals("03d"))
        {
            result = "облачно";
        }
        else if(icon.equals("04d"))
        {
            result = "сильная облачность";
        }
        else if(icon.equals("09d"))
        {
            result = "сильный дождь";
        }
        else if(icon.equals("10d"))
        {
            result = "дождь";
        }
        else if(icon.equals("11d"))
        {
            result = "гроза";
        }
        else if(icon.equals("13d"))
        {
            result = "снег";
        }
        else if(icon.equals("50d"))
        {
            result = "туман";
        }
        else if(icon.equals("01n"))
        {
            result = "ясно";
        }
        else if(icon.equals("02n"))
        {
            result = "облачно с прояснениями";
        }
        else if(icon.equals("03n"))
        {
            result = "облачно";
        }
        else if(icon.equals("04n"))
        {
            result = "сильная облачность";
        }
        else if(icon.equals("09n"))
        {
            result = "сильный дождь";
        }
        else if(icon.equals("10n"))
        {
            result = "дождь";
        }
        else if(icon.equals("11n"))
        {
            result = "гроза";
        }
        else if(icon.equals("13n"))
        {
            result = "снег";
        }
        else if(icon.equals("50n"))
        {
            result = "туман";
        }
        else
        {
            result = "непонятно";
        }

        return result;

    }

    public static Bitmap getBitmap(Context _context, String icon) {
        Bitmap result;

        if (icon.equals("01d")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_01d);
        } else if (icon.equals("02d")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_02d);
        } else if (icon.equals("03d")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_03d);
        } else if (icon.equals("04d")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_04d);
        } else if (icon.equals("09d")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_09d);
        } else if (icon.equals("10d")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_10d);
        } else if (icon.equals("11d")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_11d);
        } else if (icon.equals("13d")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_13d);
        } else if (icon.equals("50d")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_50d);
        } else if (icon.equals("01n")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_01n);
        } else if (icon.equals("02n")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_02n);
        } else if (icon.equals("03n")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_03n);
        } else if (icon.equals("04n")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_04n);
        } else if (icon.equals("09n")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_09n);
        } else if (icon.equals("10n")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_10n);
        } else if (icon.equals("11n")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_11n);
        } else if (icon.equals("13n")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_13n);
        } else if (icon.equals("50n")) {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_50n);
        } else {
            result = BitmapFactory.decodeResource(_context.getResources(), R.drawable.weather_01d);
        }

        //увеличиваю масштаб иконки на коэффекциэнт
        // weatherData.bitmap_icon = tmpBitmap;// Bitmap.createScaledBitmap(tmpBitmap, tmpBitmap.getWidth()*2,

        return result;
    }
    public static void drawTokenOnMap(Context _context, MapView _mapView, WeatherData data) {
        try {
            Weather weather = data.getWeather();
            City city = data.getCity();

            if(weather == null || city == null || city.getId() < 100)
                return;
            Bitmap bitmap = getBitmap(_context, weather.getIcon());
            GeoPoint location = new GeoPoint(city.getLat(), city.getLon());
            Double temp = data.getWeather().getTemp();
            String strTemp = "0";
            if (temp > 0)
                strTemp = "+" + new Integer(temp.intValue()).toString();// + "°C";
            else
                strTemp = "-" + new Integer(temp.intValue()).toString();// + "°C";
            String tokenText = getTokenText(weather.getIcon()) + " " + strTemp;

            //WeatherToken token = new WeatherToken(_context, location, bitmap, 15, tokenText, data.getZoomLevel());
            WeatherToken token = new WeatherToken(_context, city, weather, bitmap, tokenText, data.getZoomLevel());
            _mapView.getOverlays().add(token);
        }
        catch(Exception e)
        {
            Log.e("drawTokenOnMap", e.toString());
        }

    }


    public static ArrayList<WeatherData> fetchDataFromBd(ContentResolver resolver, String where) {
        String[] projection1 = new String[]{
                WeatherProviderContract.PlaceInfo.PLACE_ID,
                WeatherProviderContract.PlaceInfo.PLACE_NAME,
                WeatherProviderContract.PlaceInfo.PLACE_LAT,
                WeatherProviderContract.PlaceInfo.PLACE_LON,
                WeatherProviderContract.PlaceInfo.PLACE_COUNTRY,
                WeatherProviderContract.PlaceInfo.PLACE_MIN_ZOOM_LEVEL};

        String[] projection2 = new String[]{
                WeatherProviderContract.WeatherInfo.WEATHER_PLACE_ID,
                WeatherProviderContract.WeatherInfo.WEATHER_DATE,
                WeatherProviderContract.WeatherInfo.WEATHER_TEMP,
                WeatherProviderContract.WeatherInfo.WEATHER_TEMP_MIN,
                WeatherProviderContract.WeatherInfo.WEATHER_TEMP_MAX,
                WeatherProviderContract.WeatherInfo.WEATHER_HUMIDITY,
                WeatherProviderContract.WeatherInfo.WEATHER_PRESSURE,
                WeatherProviderContract.WeatherInfo.WEATHER_SEA_LVL,
                WeatherProviderContract.WeatherInfo.WEATHER_GRND_LVL,
                WeatherProviderContract.WeatherInfo.WEATHER_WND_SPEED,
                WeatherProviderContract.WeatherInfo.WEATHER_WND_DEG,
                WeatherProviderContract.WeatherInfo.WEATHER_WND_GUST,
                WeatherProviderContract.WeatherInfo.WEATHER_ID,
                WeatherProviderContract.WeatherInfo.WEATHER_MAIN,
                WeatherProviderContract.WeatherInfo.WEATHER_DESC,
                WeatherProviderContract.WeatherInfo.WEATHER_ICON};

        Cursor cityCursor = resolver.query(
                WeatherProviderContract.PlaceInfo.CONTENT_URI,
                projection1,
                where,
                null,
                null);

        Cursor weatherCursor = resolver.query(
                WeatherProviderContract.WeatherInfo.CONTENT_URI,
                projection2,
                null,
                null,
                null);

        ArrayList<WeatherData> result = new ArrayList<WeatherData>();

        if (cityCursor != null && cityCursor.moveToFirst()) {
            while (!cityCursor.isAfterLast()) {
                WeatherData data = new WeatherData();
                data.setZoomLevel(cityCursor.getInt(cityCursor.getColumnIndex(WeatherProviderContract.PlaceInfo.PLACE_MIN_ZOOM_LEVEL)));
                data.getCity().setCountry(cityCursor.getString(cityCursor.getColumnIndex(WeatherProviderContract.PlaceInfo.PLACE_COUNTRY)));
                data.getCity().setId(cityCursor.getInt(cityCursor.getColumnIndex(WeatherProviderContract.PlaceInfo.PLACE_ID)));
                data.getCity().setLon(cityCursor.getDouble(cityCursor.getColumnIndex(WeatherProviderContract.PlaceInfo.PLACE_LON)));
                data.getCity().setLat(cityCursor.getDouble(cityCursor.getColumnIndex(WeatherProviderContract.PlaceInfo.PLACE_LAT)));
                data.getCity().setName(cityCursor.getString(cityCursor.getColumnIndex(WeatherProviderContract.PlaceInfo.PLACE_NAME)));


                if (weatherCursor != null && weatherCursor.moveToFirst()) {

                    while (!weatherCursor.isAfterLast()) {
                        int iPlaceIndex = weatherCursor.getInt(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_PLACE_ID));
                        if (iPlaceIndex == data.getCity().getId()) {
                            Weather weather = new Weather();
                            weather.setPlaceId(iPlaceIndex);
                            weather.setTemp(weatherCursor.getDouble(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_TEMP)));
                            weather.setHumidity(weatherCursor.getInt(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_HUMIDITY)));
                            weather.setTempMin(weatherCursor.getDouble(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_TEMP_MIN)));
                            weather.setTempMax(weatherCursor.getDouble(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_TEMP_MAX)));
                            weather.setPressure(weatherCursor.getDouble(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_PRESSURE)));
                            weather.setPressureSeaLvl(weatherCursor.getDouble(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_SEA_LVL)));
                            weather.setPressureGrndLvl(weatherCursor.getDouble(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_GRND_LVL)));
                            weather.setWndSpeed(weatherCursor.getDouble(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_WND_SPEED)));
                            weather.setWndDeg(weatherCursor.getDouble(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_WND_DEG)));
                            weather.setWndGust(weatherCursor.getDouble(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_WND_GUST)));
                            weather.setWeatherId(weatherCursor.getInt(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_ID)));
                            weather.setMain(weatherCursor.getString(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_MAIN)));
                            weather.setDescription(weatherCursor.getString(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_DESC)));
                            weather.setIcon(weatherCursor.getString(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_ICON)));
                            data.setDt(weatherCursor.getInt(weatherCursor.getColumnIndex(WeatherProviderContract.WeatherInfo.WEATHER_DATE)));
                            data.setWeather(weather);
                            break;
                        }
                        weatherCursor.moveToNext();
                    }
                    //костыль
                    result.add(data);
                    cityCursor.moveToNext();
                }
            }
        }

        cityCursor.close();
        weatherCursor.close();
        return result;
    }

    public static ArrayList<WeatherData> fetchDataFromBd(ContentResolver resolver)
    {
        return fetchDataFromBd(resolver,null);
    }

    public static String urlGenerator(GeoPoint topLeft, GeoPoint bottomRight, Integer zoomLvl) {

        String result;
        String bbox = new Double(topLeft.getLongitude()).toString();
        bbox += ",";
        bbox += new Double(topLeft.getLatitude()).toString();
        bbox += ",";
        bbox += new Double(bottomRight.getLongitude()).toString();
        bbox += ",";
        bbox += new Double(bottomRight.getLatitude()).toString();
        bbox += ",";
        bbox += zoomLvl;
        String cluster = "yes";
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("box")
                .appendPath("city")
                .appendQueryParameter("bbox", bbox)
                .appendQueryParameter("cluster", cluster);
        result = builder.build().toString();
        return result;
    }

    public static String urlGenerator(GeoPoint topLeft, Integer numDays) {

        String result;
        Uri.Builder builder = new Uri.Builder();
        builder .scheme("http")
                .authority("www.openweathermap.org")
                .appendPath("data")
                .appendPath("2.5")
                .appendPath("forecast")
                .appendPath("daily")
                .appendQueryParameter("lat", new Double(topLeft.getLatitude()).toString())
                .appendQueryParameter("lon", new Double(topLeft.getLongitude()).toString())
                .appendQueryParameter("cnt", numDays.toString())
                .appendQueryParameter("mode", "json");
        result = builder.build().toString();
        return result;
    }

    public static void swapIfNeed(Point var1, Point var2)
    {
        int iTmp;
        if(var1.x > var2.x)
        {
            iTmp = var1.x;
            var1.x = var2.x;
            var2.x = iTmp;
        }

        if(var1.y > var2.y)
        {
            iTmp = var1.y;
            var1.y = var2.y;
            var2.y = iTmp;
        }
    }

    public static float applyDimension(int unit, float value,
                                       DisplayMetrics metrics)
    {
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f/72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f/25.4f);
        }
        return 0;
    }

    public static float calcPixelRasst(Point point1, Point point2,Context context)
    {
        float result;

        double i =  Math.pow(point2.x - point1.x, 2);
        double j =  Math.pow(point2.y - point1.y, 2);
        double z = Math.sqrt(i + j);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        windowManager.getDefaultDisplay().getMetrics(metrics);

        result = applyDimension(TypedValue.COMPLEX_UNIT_MM,new Double(z).floatValue(),metrics);
        return result;

    }

    public static WeatherData weatherDataById(Integer cityId)
    {
        WeatherData result = new WeatherData();
        return result;
    }

    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static Boolean isLatValid(double lat)
    {
        Boolean result = false;
        if(lat >= -180 && lat <= 180)
            result = true;
        return result;
    }

    public static Boolean isLonValid(double lon)
    {
        Boolean result = false;
        if(lon >= -90 && lon <= 90)
            result = true;
        return result;
    }
    public static String getIconChar(String icon)
    {
        char c;
        if (icon.equals("01d"))
            c = 0xf00d;
        else if (icon.equals("02d"))
            c = 0xf002;
        else if (icon.equals("03d"))
            c = 0xf041;
        else if (icon.equals("04d"))
            c = 0xf013;
        else if (icon.equals("09d"))
            c = 0xf019;
        else if (icon.equals("10d"))
            c = 0xf008;
        else if (icon.equals("11d"))
            c = 0xf005;
        else if (icon.equals("13d"))
            c = 0xf00a;
        else if (icon.equals("50d"))
            c = 0xf014;
        else if (icon.equals("01n"))
            c = 0xf02e;
        else if (icon.equals("02n"))
            c = 0xf031;
        else if (icon.equals("03n"))
            c = 0xf041;
        else if (icon.equals("04n"))
            c = 0xf013;
        else if (icon.equals("09n"))
            c = 0xf019;
        else if (icon.equals("10n"))
            c = 0xf036;
        else if (icon.equals("11n"))
            c = 0xf033;
        else if (icon.equals("13n"))
            c = 0xf038;
        else if (icon.equals("50n"))
            c = 0xf014;
        else
            return "?";
        return String.valueOf(c);
    }

    public static int getWeatherClass(int weatherId){
        if(weatherId > 0 && weatherId < 1000)
            return ( (int) weatherId / 100);
        else
            return -1;
    }
}
