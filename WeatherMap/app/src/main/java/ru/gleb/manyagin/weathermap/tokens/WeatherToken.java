package ru.gleb.manyagin.weathermap.tokens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import ru.gleb.manyagin.weathermap.activities.ForecastActivity;
import ru.gleb.manyagin.weathermap.domain.City;
import ru.gleb.manyagin.weathermap.domain.Weather;
import ru.gleb.manyagin.weathermap.utilities.UtilitiesClass;

/**
 * Created by gleb.manyagin on 29.04.2015.
 */
public class WeatherToken extends Overlay
{
    //region Fields
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private GeoPoint location;
    private Bitmap weatherBitmap;
    private int fontSize = 15;
    private String tokenText;
    private int iTokenZoomLvl;
    private City city;
    private Weather weather;
    private Context context;
    //endregion

    //region Constructors
    public WeatherToken(Context ctx, GeoPoint geoPoint, Bitmap bitmap, int fontSize, String tokenText, int tokenZoomLevel)
    {
        super(ctx);
        this.paint.setAntiAlias(true);
        location = geoPoint;
        weatherBitmap = bitmap;
        this.fontSize = fontSize;
        this.tokenText = tokenText;
        iTokenZoomLvl = tokenZoomLevel;
    }

    public WeatherToken(Context ctx, City _city, Weather _weather,Bitmap bitmap, String tokenText, int tokenZoomLevel) {
        super(ctx);
        this.paint.setAntiAlias(true);
        city = _city;
        weather = _weather;
        location = new GeoPoint(city.getLat(),city.getLon());
        weatherBitmap = bitmap;
        fontSize = 15;
        this.tokenText = tokenText;
        context = ctx;
        iTokenZoomLvl = tokenZoomLevel;


    }
    //endregion

    //region Methods

    @Override
    public void draw(Canvas c, MapView mapView, boolean shadow)
    {
        final Rect viewportRect = new Rect();
        viewportRect.set(mapView.getProjection().getScreenRect());
        Point newPoint = new Point();
        mapView.getProjection().toPixels(location, newPoint);
        if(viewportRect.contains(newPoint.x, newPoint.y) && iTokenZoomLvl <= mapView.getZoomLevel()) {
            Point mapCenterPoint = new Point();
            mapView.getProjection().toPixels(location, mapCenterPoint);
            float xBitmapPos = (float) (mapCenterPoint.x - weatherBitmap.getWidth() / 2);
            float yBitmapPos = (float) (mapCenterPoint.y - weatherBitmap.getHeight() / 2);
            c.drawBitmap(weatherBitmap, xBitmapPos, yBitmapPos, this.paint);
            this.paint.setTextSize(fontSize);
            int width = (int) paint.measureText(tokenText);
            float xTextPos = (float) (mapCenterPoint.x - width / 2);
            float yTextPos = (float) (mapCenterPoint.y + weatherBitmap.getHeight() * 0.4);
            c.drawText(tokenText, xTextPos, yTextPos, this.paint);
        }

    }

    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        final Rect viewportRect = new Rect();
        viewportRect.set(mapView.getProjection().getScreenRect());
        Point newPoint = new Point();
        mapView.getProjection().toPixels(location, newPoint);
        if(viewportRect.contains(newPoint.x, newPoint.y) && iTokenZoomLvl <= mapView.getZoomLevel()) {
            IGeoPoint geoPoint = mapView.getProjection().fromPixels(e.getX(), e.getY());
            Point pointTap = new Point();
            mapView.getProjection().toPixels(geoPoint, pointTap);
            Point pointOverlay = new Point();
            mapView.getProjection().toPixels(location, pointOverlay);

            float mm = UtilitiesClass.calcPixelRasst(pointTap, pointOverlay, mapView.getContext());
            if (mm < 250) {
                Intent myIntent = new Intent(context, ForecastActivity.class);
                myIntent.putExtra("place_id",city.getId()); //Optional parameters
                context.startActivity(myIntent);
                return true;
            }
        }
        return super.onSingleTapConfirmed(e,mapView);
    }
    //endregion
}
