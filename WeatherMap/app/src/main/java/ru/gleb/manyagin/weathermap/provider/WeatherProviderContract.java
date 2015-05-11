package ru.gleb.manyagin.weathermap.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Wild on 20.04.2015.
 */
public class WeatherProviderContract {

    //region Fields
    public static final String AUTHORITY = "ru.gleb.manyagin.weathermap.provider.WeatherProvider";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
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
    //endregion

    //region Classes

    public static class PlaceInfo implements BaseColumns {

        public static final String PLACE_INFO = "place_info";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PLACE_INFO);

        public static final String PLACE_TABLE = "place_table";
        public static final String PLACE_ID = "_id";
        public static final String PLACE_NAME = "place_name";
        public static final String PLACE_LAT = "place_lat";
        public static final String PLACE_LON = "place_lon";
        public static final String PLACE_COUNTRY = "place_country";
        public static final String PLACE_MIN_ZOOM_LEVEL = "place_min_zoom_lvl";
    }

    public static class WeatherInfo implements BaseColumns {

        public static final String WEATHER_INFO = "weather_info";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, WEATHER_INFO);

        public static final String WEATHER_TABLE = "weather_table";
        public static final String WEATHER_PLACE_ID =   "_id";
        public static final String WEATHER_DATE =       "weather_date";
        public static final String WEATHER_TEMP =       "weather_temp";
        public static final String WEATHER_TEMP_MIN =   "weather_temp_min";
        public static final String WEATHER_TEMP_MAX =   "weather_temp_max";
        public static final String WEATHER_HUMIDITY =   "weather_humadity";
        public static final String WEATHER_PRESSURE =   "weather_pressure";
        public static final String WEATHER_SEA_LVL =    "weather_sea_lvl";
        public static final String WEATHER_GRND_LVL =   "weather_grnd_lvl";
        public static final String WEATHER_WND_SPEED =  "weather_wnd_speed";
        public static final String WEATHER_WND_DEG =    "weather_wnd_deg";
        public static final String WEATHER_WND_GUST =   "weather_wnd_gust";
        public static final String WEATHER_ID =         "weather_id";
        public static final String WEATHER_MAIN =       "weather_main";
        public static final String WEATHER_DESC =       "weather_desc";
        public static final String WEATHER_ICON =       "weather_icon";
        private WeatherInfo() {}
    }
    //endregion




}
