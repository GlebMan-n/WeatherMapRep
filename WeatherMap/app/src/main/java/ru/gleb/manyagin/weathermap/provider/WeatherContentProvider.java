package ru.gleb.manyagin.weathermap.provider;


import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/*
 * Created by Wild on 20.04.2015.
 */
public class WeatherContentProvider extends ContentProvider {


    //region Fields
    private static UriMatcher matcher;
    private static final int WEATHER_MATCH = 1;
    private static final int WEATHER_ITEM_MATCH = 2;
    private static final int PLACE_MATCH = 3;
    private static final int PLACE_ITEM_MATCH = 4;

    static
    {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(WeatherProviderContract.AUTHORITY, WeatherProviderContract.WeatherInfo.WEATHER_INFO, WEATHER_MATCH);
        matcher.addURI(WeatherProviderContract.AUTHORITY, WeatherProviderContract.WeatherInfo.WEATHER_INFO + "/#", WEATHER_ITEM_MATCH);
        matcher.addURI(WeatherProviderContract.AUTHORITY, WeatherProviderContract.PlaceInfo.PLACE_INFO, PLACE_MATCH);
        matcher.addURI(WeatherProviderContract.AUTHORITY, WeatherProviderContract.PlaceInfo.PLACE_INFO + "/#", PLACE_ITEM_MATCH);
    }

    SQLiteOpenHelper dbHelper;
    //endregion

    //region Keys
    //endregion

    //region Constructors
    //endregion

    //region Getter
    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)) {

            case WEATHER_MATCH: return "vnd.android.cursor.dir/vnd." +
                    WeatherProviderContract.AUTHORITY + "." + WeatherProviderContract.WeatherInfo.WEATHER_INFO;
            case WEATHER_ITEM_MATCH: return "vnd.android.cursor.item/vnd." +
                    WeatherProviderContract.AUTHORITY + "." + WeatherProviderContract.WeatherInfo.WEATHER_INFO;

            case PLACE_MATCH: return "vnd.android.cursor.dir/vnd." +
                    WeatherProviderContract.AUTHORITY + "." + WeatherProviderContract.PlaceInfo.PLACE_INFO;
            case PLACE_ITEM_MATCH: return "vnd.android.cursor.item/vnd." +
                    WeatherProviderContract.AUTHORITY + "." + WeatherProviderContract.PlaceInfo.PLACE_INFO;

            default: throw new IllegalArgumentException("Unknown uri " + uri);

        }
    }

    private ContentResolver getResolver() {
        return getContext().getContentResolver();
    }
    //endregion

    //region Setter
    //endregion

    //region Methods
    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = selection;
        Cursor c;
        int uriType = matcher.match(uri);
        switch (uriType) {

            case WEATHER_ITEM_MATCH: where = addIdCondition(uri.getLastPathSegment(), selection);

            case WEATHER_MATCH:
                c = db.query(WeatherProviderContract.WeatherInfo.WEATHER_TABLE,
                        projection, where, selectionArgs, null, null, sortOrder);
                break;

            case PLACE_ITEM_MATCH: where = addIdCondition(uri.getLastPathSegment(), selection);

            case PLACE_MATCH:
                c = db.query(WeatherProviderContract.PlaceInfo.PLACE_TABLE,
                        projection, where, selectionArgs, null, null, sortOrder);
                break;

            default: throw new IllegalArgumentException("Unknown uri " + uri);
        }
        c.setNotificationUri(getResolver(), uri);
        return c;
    }

    private String addIdCondition(String id, String selection) {
        String s = String.format("%s = %s", BaseColumns._ID, id);
        if (!TextUtils.isEmpty(selection))
            s = String.format("%s and (%s)", s, selection);
        return s;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db;
        db = dbHelper.getWritableDatabase();
        Long id;
        int iTmp = matcher.match(uri);
        switch (iTmp)
        {
            case WEATHER_MATCH:
                id = db.insert(WeatherProviderContract.WeatherInfo.WEATHER_TABLE, null, values);
                break;
            case PLACE_MATCH:
                id = db.insert(WeatherProviderContract.PlaceInfo.PLACE_TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        getResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = selection;
        int count;
        switch (matcher.match(uri)) {
            case WEATHER_ITEM_MATCH: where = addIdCondition(uri.getLastPathSegment(), where);
            case WEATHER_MATCH:
                count = db.delete(WeatherProviderContract.WeatherInfo.WEATHER_TABLE, where, selectionArgs);
                break;
            case PLACE_ITEM_MATCH: where = addIdCondition(uri.getLastPathSegment(), where);
            case PLACE_MATCH:
                count = db.delete(WeatherProviderContract.PlaceInfo.PLACE_TABLE, where, selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unknown uri " + uri);
        }
        if (count > 0)
            getResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = selection;
        int count;
        switch (matcher.match(uri)) {
            case WEATHER_ITEM_MATCH: where = addIdCondition(uri.getLastPathSegment(), where);
            case WEATHER_MATCH:
                count = db.update(WeatherProviderContract.WeatherInfo.WEATHER_TABLE, values, where, selectionArgs);
                break;
            case PLACE_ITEM_MATCH: where = addIdCondition(uri.getLastPathSegment(), where);
            case PLACE_MATCH:
                count = db.update(WeatherProviderContract.PlaceInfo.PLACE_TABLE, values, where, selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unknown uri " + uri);
        }
        if (count > 0)
            getResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {

        if(matcher.match(uri) != WEATHER_MATCH && matcher.match(uri) != PLACE_MATCH)
            throw new IllegalArgumentException("Unknown uri " + uri);

        int count = 0;
        long id;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        switch (matcher.match(uri))
        {
            case WEATHER_MATCH:
                try {
                    for (ContentValues cv: values) {
                        id = db.insert(WeatherProviderContract.WeatherInfo.WEATHER_TABLE, null, cv);
                        if (id <= 0)
                            throw new SQLException("Failed to insert row into " + uri);
                    }
                    db.setTransactionSuccessful();
                    count = values.length;
                    getResolver().notifyChange(uri, null);
                }
                finally {
                    db.endTransaction();
                }
                break;
            case PLACE_MATCH:
                try {
                    for (ContentValues cv: values) {
                        id = db.insert(WeatherProviderContract.PlaceInfo.PLACE_TABLE, null, cv);
                        if (id <= 0)
                            throw new SQLException("Failed to insert row into " + uri);
                    }
                    db.setTransactionSuccessful();
                    count = values.length;
                    getResolver().notifyChange(uri, null);
                }
                finally {
                    db.endTransaction();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        return count;
    }
    //endregion

    //region Classes

    private class DBHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "weather_database.db";
        private static final int DB_VERSION = 5;

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION, null);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            String sql = String.format("create table %s" //PLACE_TABLE
                            + "(%s integer primary key autoincrement," //PLACE_ID
                            + " %s text," //PLACE_NAME
                            + " %s real," //PLACE_LAT
                            + " %s real," //PLACE_LON
                            + " %s text," //PLACE_COUNTRY
                            + " %s integer)" //PLACE_MIN_ZOOM_LEVEL
                    ,
                    WeatherProviderContract.PlaceInfo.PLACE_TABLE,
                    WeatherProviderContract.PlaceInfo.PLACE_ID,
                    WeatherProviderContract.PlaceInfo.PLACE_NAME,
                    WeatherProviderContract.PlaceInfo.PLACE_LAT,
                    WeatherProviderContract.PlaceInfo.PLACE_LON,
                    WeatherProviderContract.PlaceInfo.PLACE_COUNTRY,
                    WeatherProviderContract.PlaceInfo.PLACE_MIN_ZOOM_LEVEL);
            db.execSQL(sql);

            sql = String.format("create table %s"               //WEATHER_TABLE
                            + "(%s integer primary key autoincrement,"  //WEATHER_PLACE_ID
                            + " %s text,"                               //WEATHER_DATE
                            + " %s real,"                               //WEATHER_TEMP
                            + " %s real,"                               //WEATHER_TEMP_MIN
                            + " %s real,"                               //WEATHER_TEMP_MAX
                            + " %s integer,"                            //WEATHER_HUMIDITY
                            + " %s real,"                               //WEATHER_PRESSURE
                            + " %s real,"                               //WEATHER_SEA_LVL
                            + " %s real,"                               //WEATHER_GRND_LVL
                            + " %s real,"                               //WEATHER_WND_SPEED
                            + " %s real,"                               //WEATHER_WND_DEG
                            + " %s real,"                               //WEATHER_WND_GUST
                            + " %s integer,"                            //WEATHER_ID
                            + " %s text,"                               //WEATHER_MAIN
                            + " %s text,"                               //WEATHER_DESC
                            + " %s text)"                               //WEATHER_ICON
                    ,
                    WeatherProviderContract.WeatherInfo.WEATHER_TABLE,
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
                    WeatherProviderContract.WeatherInfo.WEATHER_ICON);
            db.execSQL(sql);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String sql = String.format("drop table if exists %s", WeatherProviderContract.PlaceInfo.PLACE_TABLE);
            db.execSQL(sql);

            sql = String.format("drop table if exists %s", WeatherProviderContract.WeatherInfo.WEATHER_TABLE);
            db.execSQL(sql);

            onCreate(db);
        }
    }

    //endregion











}
