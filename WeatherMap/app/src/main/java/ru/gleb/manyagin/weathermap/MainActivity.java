package ru.gleb.manyagin.weathermap;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.List;

import ru.gleb.manyagin.weathermap.activities.CurrentPositionForecastActivity;
import ru.gleb.manyagin.weathermap.activities.ForecastActivity;
import ru.gleb.manyagin.weathermap.another_tasks.DrawWeatherData;
import ru.gleb.manyagin.weathermap.another_tasks.FetchWeatherData;
import ru.gleb.manyagin.weathermap.broadcastReceivers.DrawWeatherReceiver;
import ru.gleb.manyagin.weathermap.broadcastReceivers.FetchWeatherReceiver;
import ru.gleb.manyagin.weathermap.broadcastReceivers.TimeBroadcastReceiver;
import ru.gleb.manyagin.weathermap.domain.Weather;
import ru.gleb.manyagin.weathermap.provider.WeatherProviderContract;
import ru.gleb.manyagin.weathermap.services.WeatherService;
import ru.gleb.manyagin.weathermap.settings.DataControlActivity;
import ru.gleb.manyagin.weathermap.settings.SettingsActivity;
import ru.gleb.manyagin.weathermap.tokens.CurrentPositionToken;
import ru.gleb.manyagin.weathermap.tokens.RectangleToken;

public class MainActivity extends ActionBarActivity {

    private MapView mapView;
    private RelativeLayout rl;
    private ResourceProxy mResourceProxy;
    final static int margings = 10;
    String LOG_TAG = "MainActivity";
    private boolean bShowDebug;
    private boolean bShowRect;
    private SharedPreferences preference;
    RectangleToken rectangleTokenSpec = null;
    RectangleToken rectangleTokenGen = null;
    private Boolean setRectangleGen = false;
    private Boolean setRectangleSpec = false;
    private GeoPoint geoGenLeftTop = null;
    private GeoPoint geoGenRightBottom = null;
    private GeoPoint geoSpecLeftTop = null;
    private GeoPoint geoSpecRightBottom = null;
    private CurrentPositionToken token = null;
    private DrawWeatherReceiver mDrawWeatherDataReceiver = null;
    LocationManager mLocationManager = null;
    LocationListener locationListener = null;

    final static String PREFS_TILE_SOURCE = "map_tile_source";
    final static String PREFS_SCROLL_X = "map_scroll_x";
    final static String PREFS_SCROLL_Y = "map_scroll_y";
    final static String PREFS_ZOOM_LEVEL = "map_zoom_level";
    final static String PREFS_SHOW_LOCATION = "map_show_loc";


    void InitMap() {

        if (mapView != null) {
            rl.removeAllViews();
            mapView = null;
        }

        mapView = new MapView(this, 256, mResourceProxy);
        mapView.setUseSafeCanvas(true);

        mapView.setMapListener(new DelayedMapListener(new MapListener() {
            public boolean onZoom(final ZoomEvent e) {
                return true;
            }

            public boolean onScroll(final ScrollEvent e) {
                return true;

            }
        }, 1000));

        mapView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                float x = event.getX();
                float y = event.getY();

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (setRectangleGen == true) {
                        setRectangleGen = false;
                        bShowRect = true;
                        mapView.setClickable(true);
                        if(rectangleTokenGen == null)
                            return true;
                        geoGenLeftTop = new GeoPoint(rectangleTokenGen.getLocLeftTop().getLatitude(), rectangleTokenGen.getLocLeftTop().getLongitude());
                        geoGenRightBottom = new GeoPoint(rectangleTokenGen.getLocRighBottom().getLatitude(), rectangleTokenGen.getLocRighBottom().getLongitude());
                        saveSettings();
                        startWeatherTask();
                        setWeatherRectanglesVisible(bShowRect);
                        Toast.makeText(getApplicationContext(), "Данные обновяться в течении нескольких минут", Toast.LENGTH_LONG).show();
                        long currentTime = 0;
                        final SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        edit.putLong(getApplicationContext().getString(R.string.pref_last_data_update_key), currentTime);
                        edit.commit();
                    }


                    if (setRectangleSpec == true) {
                        setRectangleSpec = false;
                        bShowRect = true;
                        mapView.setClickable(true);
                        if(rectangleTokenSpec == null)
                           return true;
                        geoSpecLeftTop = new GeoPoint(rectangleTokenSpec.getLocLeftTop().getLatitude(), rectangleTokenSpec.getLocLeftTop().getLongitude());
                        geoSpecRightBottom = new GeoPoint(rectangleTokenSpec.getLocRighBottom().getLatitude(), rectangleTokenSpec.getLocRighBottom().getLongitude());
                        saveSettings();
                        startWeatherTask();
                        setWeatherRectanglesVisible(bShowRect);
                        Toast.makeText(getApplicationContext(), "Данные обновяться в течении нескольких минут", Toast.LENGTH_LONG).show();
                        long currentTime = 0;
                        final SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        edit.putLong(getApplicationContext().getString(R.string.pref_last_data_update_key), currentTime);
                        edit.commit();
                    }
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (setRectangleGen) {
                        mapView.setClickable(false);
                        IGeoPoint point = mapView.getProjection().fromPixels(x, y);

                        if (rectangleTokenGen == null) {
                            rectangleTokenGen = new RectangleToken(getApplicationContext(), Color.RED, new GeoPoint(point.getLatitude(), point.getLongitude()), null);
                            mapView.getOverlays().add(rectangleTokenGen);
                        } else {
                            rectangleTokenGen.setLocLeftTop(new GeoPoint(point.getLatitude(), point.getLongitude()));
                            rectangleTokenGen.setLocRighBottom(new GeoPoint(point.getLatitude(), point.getLongitude()));
                        }

                        setWeatherRectanglesVisible(true);
                        mapView.invalidate();
                        return true;
                    }

                    if (setRectangleSpec) {
                        mapView.setClickable(false);
                        IGeoPoint point = mapView.getProjection().fromPixels(x, y);

                        if (rectangleTokenSpec == null) {
                            rectangleTokenSpec = new RectangleToken(getApplicationContext(), Color.BLUE, new GeoPoint(point.getLatitude(), point.getLongitude()), null);
                            mapView.getOverlays().add(rectangleTokenSpec);
                        } else {
                            rectangleTokenSpec.setLocLeftTop(new GeoPoint(point.getLatitude(), point.getLongitude()));
                            rectangleTokenSpec.setLocRighBottom(new GeoPoint(point.getLatitude(), point.getLongitude()));
                        }
                        setWeatherRectanglesVisible(true);
                        mapView.invalidate();
                        return true;
                    }
                }

                if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    if (setRectangleGen && rectangleTokenGen != null) {
                        IGeoPoint point = mapView.getProjection().fromPixels(x, y);
                        rectangleTokenGen.setLocRighBottom(new GeoPoint(point.getLatitude(), point.getLongitude()));
                        mapView.invalidate();
                        return true;
                    }

                    if (setRectangleSpec && rectangleTokenSpec != null) {
                        IGeoPoint point = mapView.getProjection().fromPixels(x, y);
                        rectangleTokenSpec.setLocRighBottom(new GeoPoint(point.getLatitude(), point.getLongitude()));
                        mapView.invalidate();
                        return true;
                    }
                }



                return false;
            }
        });

        mapView.setMultiTouchControls(true);
        rl.addView(mapView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        mapView.getController().setZoom(preference.getInt(PREFS_ZOOM_LEVEL, 1));
        mapView.scrollTo(preference.getInt(PREFS_SCROLL_X, 0), preference.getInt(PREFS_SCROLL_Y, 0));
        mapView.setKeepScreenOn(true);
        AddMapButtons(rl);



    }

    private void startGps()
    {
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Boolean isGPSEnabled = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        Boolean isNetworkEnabled = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isGPSEnabled && !isNetworkEnabled)
        {
            Toast.makeText(getApplicationContext(), "Нет подключения к GPS!", Toast.LENGTH_LONG).show();
        }

        Double latFirst = new Double(preference.getFloat(getString(R.string.pref_current_latitude_key), new Float(getString(R.string.pref_current_latitude_default))));
        Double lonFirst = new Double(preference.getFloat(getString(R.string.pref_current_longitude_key), new Float(getString(R.string.pref_current_longitude_default))));

        if (token == null) {
            token = new CurrentPositionToken(this, new GeoPoint(latFirst, lonFirst), BitmapFactory.decodeResource(getResources(), R.mipmap.current_position_no_azimuth));

        }
        mapView.getOverlays().add(token);

        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                if (token == null) {
                    token = new CurrentPositionToken(getApplicationContext(), new GeoPoint(location.getLatitude(), location.getLongitude()), BitmapFactory.decodeResource(getResources(), R.mipmap.current_position_no_azimuth));
                    mapView.getOverlays().add(token);
                } else
                    token.setLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));

                mapView.invalidate();

                /*if (bShowDebug) {
                    String str =
                            "Положение обновлено\n" +
                                    "Долгота: " + new Double(location.getLongitude()).toString() + "\n" +
                                    "Широта: " + new Double(location.getLatitude()).toString() + "\n" +
                                    "Скорость: " + new Double(location.getSpeed()).toString() + "\n" +
                                    "Высота: " + new Double(location.getAltitude()).toString();
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();

                }*/

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);

    }

    public void drawRectangles()
    {
        Double latFirst = new Double(preference.getFloat(getString(R.string.general_left_top_lat_key), new Float(getString(R.string.general_left_top_lat_default))));
        Double lonFirst = new Double(preference.getFloat(getString(R.string.general_left_top_lon_key), new Float(getString(R.string.general_left_top_lon_default))));
        Double latSecond = new Double(preference.getFloat(getString(R.string.general_right_bottom_lat_key), new Float(getString(R.string.general_right_bottom_lat_default))));
        Double lonSecond = new Double(preference.getFloat(getString(R.string.general_right_bottom_lon_key), new Float(getString(R.string.general_right_bottom_lon_default))));
        geoGenLeftTop = new GeoPoint(latFirst,lonFirst);
        geoGenRightBottom = new GeoPoint(latSecond,lonSecond);

        latFirst = new Double(preference.getFloat(getString(R.string.special_left_top_lat_key), new Float(getString(R.string.special_left_top_lat_default))));
        lonFirst = new Double(preference.getFloat(getString(R.string.special_left_top_lon_key), new Float(getString(R.string.special_left_top_lon_default))));
        latSecond = new Double(preference.getFloat(getString(R.string.special_right_bottom_lat_key), new Float(getString(R.string.special_right_bottom_lat_default))));
        lonSecond = new Double(preference.getFloat(getString(R.string.special_right_bottom_lon_key), new Float(getString(R.string.special_right_bottom_lon_default))));

        geoSpecLeftTop = new GeoPoint(latFirst,lonFirst);
        geoSpecRightBottom = new GeoPoint(latSecond,lonSecond);

        if(rectangleTokenGen != null) {
            mapView.getOverlays().remove(rectangleTokenGen);
            rectangleTokenGen = null;
        }
        if(rectangleTokenSpec != null) {
            mapView.getOverlays().remove(rectangleTokenSpec);
            rectangleTokenSpec = null;
        }

        try {


            rectangleTokenGen = new RectangleToken(this, Color.RED, geoGenLeftTop, geoGenRightBottom);
            mapView.getOverlays().add(rectangleTokenGen);

            rectangleTokenSpec = new RectangleToken(this, Color.BLUE, geoSpecLeftTop, geoSpecRightBottom);
            mapView.getOverlays().add(rectangleTokenSpec);
        }
        catch (Exception e)
        {}

        setWeatherRectanglesVisible(bShowRect);
    }

    private void setWeatherRectanglesVisible(Boolean show)
    {
        if(rectangleTokenSpec != null)
            rectangleTokenSpec.setVisible(show);

        if(rectangleTokenGen != null)
            rectangleTokenGen.setVisible(show);

        mapView.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_data_base_prefs) {
            startActivity(new Intent(this, DataControlActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void AddMapButtons(RelativeLayout rl) {
        final ImageView ivZoomIn = new ImageView(this);
        ivZoomIn.setImageResource(R.mipmap.zoom_in);
        ivZoomIn.setId(R.mipmap.zoom_in);

        final ImageView ivZoomOut = new ImageView(this);
        ivZoomOut.setImageResource(R.mipmap.zoom_out);
        ivZoomOut.setId(R.mipmap.zoom_out);

        final ImageView infoButton = new ImageView(this);
        infoButton.setImageResource(R.mipmap.info_icon);
        infoButton.setId(R.mipmap.info_icon);

        final ImageView currentPosButton = new ImageView(this);
        currentPosButton.setImageResource(R.mipmap.current_position_no_azimuth);
        currentPosButton.setId(R.mipmap.current_position_no_azimuth);

        ivZoomIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mapView.getController().zoomIn();
                ivZoomOut.getDrawable().setAlpha(255);
                if (!mapView.canZoomIn()) {
                    ivZoomIn.getDrawable().setAlpha(50);
                }
            }
        });

        ivZoomOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mapView.getController().zoomOut();

                ivZoomIn.getDrawable().setAlpha(255);
                if (!mapView.canZoomOut()) {
                    ivZoomOut.getDrawable().setAlpha(50);
                }
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                StartCurrentPositionForecast();
            }
        });

        currentPosButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mapView.getController().setCenter(new GeoPoint(token.getLatitude(), token.getLongitude()));
            }
        });

        final RelativeLayout.LayoutParams RightParams1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RightParams1.setMargins(margings, margings, margings, margings);
        RightParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        RightParams1.addRule(RelativeLayout.ALIGN_PARENT_TOP);//ALIGN_PARENT_TOP
        rl.addView(ivZoomIn, RightParams1);

        final RelativeLayout.LayoutParams RightParams2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        RightParams2.setMargins(margings, margings, margings, margings);
        RightParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        RightParams2.addRule(RelativeLayout.BELOW, R.mipmap.zoom_in);
        rl.addView(ivZoomOut, RightParams2);

        final RelativeLayout.LayoutParams RightParams3 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        RightParams3.setMargins(margings, margings, margings, margings);
        RightParams3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        RightParams3.addRule(RelativeLayout.BELOW, R.mipmap.zoom_out);
        rl.addView(infoButton, RightParams3);

        final RelativeLayout.LayoutParams RightParams4 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        RightParams4.setMargins(margings, margings, margings, margings);
        RightParams4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        RightParams4.addRule(RelativeLayout.BELOW, R.mipmap.info_icon);
        rl.addView(currentPosButton, RightParams4);
    }

    private void StartCurrentPositionForecast()
    {
        Intent myIntent = new Intent(this, CurrentPositionForecastActivity.class);
        startActivity(myIntent);
    }

    private void startWeatherTask()
    {

            FetchWeatherData task;
            task = new FetchWeatherData(this);
            task.execute();

    }


    public void registerBroadcastReceiver() {

        if(mDrawWeatherDataReceiver == null)
            mDrawWeatherDataReceiver = new DrawWeatherReceiver(mapView);

        this.registerReceiver(mDrawWeatherDataReceiver, new IntentFilter(
                getString(R.string.action_map_redraw_tokens)));
    }

    public void unregisterBroadcastReceiver() {
        this.unregisterReceiver(mDrawWeatherDataReceiver);
    }



    //endregion

    //region

    //endregion

    //region Lifecycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preference = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = getIntent();
        String setGenRect = intent.getStringExtra("set_general_rect");
        String setSpecRect = intent.getStringExtra("set_special_rect");
        String setRefreshBd = intent.getStringExtra("set_refresh_bd");

        setRectangleGen = new Boolean(setGenRect);
        setRectangleSpec = new Boolean(setSpecRect);

        bShowDebug = preference.getBoolean(getString(R.string.pref_show_debug_key), new Boolean(getString(R.string.pref_show_debug_default)));
        bShowRect = preference.getBoolean(getString(R.string.pref_show_rect_key), new Boolean(getString(R.string.pref_show_rect_default)));
        intent.putExtra("set_general_rect", "false");
        intent.putExtra("set_special_rect", "false");
        intent.putExtra("set_refresh_bd", "false");

        //setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        rl = new RelativeLayout(this);
        mResourceProxy = new ResourceProxyImpl(this);



        try {
            InitMap();
        }
        catch (Exception e)
        {
            Log.e("InitMap()","Не удалось");
        }

        setContentView(rl);

        Log.d(LOG_TAG, "MainActivity: onCreate");

        if(new Boolean(setRefreshBd))
        {
            startWeatherTask();
        }

        ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);

        boolean bRes = false;
        for (int i=0; i<rs.size(); i++)
        {
            ActivityManager.RunningServiceInfo rsi = rs.get(i);
            if(rsi.service.getClassName().equals("ru.gleb.manyagin.weathermap.services.WeatherService")) {
                bRes = true;
                break;
            }

        }

        if(!bRes) {

            Intent srvIntent = new Intent(this, WeatherService.class);
            startService(srvIntent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();

    }

    @Override
    protected void onResume() {
        super.onResume();

        bShowDebug = preference.getBoolean(getString(R.string.pref_show_debug_key), new Boolean(getString(R.string.pref_show_debug_default)));
        bShowRect = preference.getBoolean(getString(R.string.pref_show_rect_key), new Boolean(getString(R.string.pref_show_rect_default)));

        final String tileSourceName = preference.getString(PREFS_TILE_SOURCE, TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);
            mapView.setTileSource(tileSource);
        } catch (final IllegalArgumentException ignore) {
        }

        try {
            drawRectangles();
        }
        catch (Exception e)
        {
            Log.e("drawRectangles","Не удалось");
        }

        try {
            startGps();
        }
        catch (Exception e)
        {
            Log.e("startGps","Не удалось");
        }

        DrawWeatherData task = null;
        task = new DrawWeatherData(this, mapView);
        task.execute();

    }

    private void saveSettings()
    {

        final SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString(PREFS_TILE_SOURCE, mapView.getTileProvider().getTileSource().name());
        edit.putInt(PREFS_SCROLL_X, mapView.getScrollX());
        edit.putInt(PREFS_SCROLL_Y, mapView.getScrollY());
        edit.putInt(PREFS_ZOOM_LEVEL, mapView.getZoomLevel());

        edit.putBoolean(getString(R.string.pref_show_rect_key), bShowRect);


        if (token != null) {
            edit.putFloat(getString(R.string.pref_current_latitude_key), new Double(token.getLatitude()).floatValue());
            edit.putFloat(getString(R.string.pref_current_longitude_key), new Double(token.getLongitude()).floatValue());
        }

        if (geoGenLeftTop != null) {
            edit.putFloat(getString(R.string.general_left_top_lat_key), new Double(geoGenLeftTop.getLatitude()).floatValue());
            edit.putFloat(getString(R.string.general_left_top_lon_key), new Double(geoGenLeftTop.getLongitude()).floatValue());
        }

        if (geoGenRightBottom != null) {
            edit.putFloat(getString(R.string.general_right_bottom_lat_key), new Double(geoGenRightBottom.getLatitude()).floatValue());
            edit.putFloat(getString(R.string.general_right_bottom_lon_key), new Double(geoGenRightBottom.getLongitude()).floatValue());
        }

        if (geoSpecLeftTop != null) {
            edit.putFloat(getString(R.string.special_left_top_lat_key), new Double(geoSpecLeftTop.getLatitude()).floatValue());
            edit.putFloat(getString(R.string.special_left_top_lon_key), new Double(geoSpecLeftTop.getLongitude()).floatValue());
        }

        if (geoSpecRightBottom != null) {
            edit.putFloat(getString(R.string.special_right_bottom_lat_key), new Double(geoSpecRightBottom.getLatitude()).floatValue());
            edit.putFloat(getString(R.string.special_right_bottom_lon_key), new Double(geoSpecRightBottom.getLongitude()).floatValue());
        }
        edit.commit();

    }


    @Override
    protected void onPause() {
        try {
        saveSettings();
        }
        catch (Exception e){}

        //
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
       try {
           mapView.getOverlays().clear();
       }
       catch (Exception e){}

        try {
            unregisterBroadcastReceiver();
        }
        catch (Exception e){}
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}
