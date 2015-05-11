package ru.gleb.manyagin.weathermap.tokens;

import android.content.Context;
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

import ru.gleb.manyagin.weathermap.utilities.UtilitiesClass;

/**
 * Created by gleb.manyagin on 06.05.2015.
 */
public class CurrentPositionToken extends Overlay {
    //region Fields
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private GeoPoint location;
    private Bitmap currentPosBitmap;
    private int fontSize = 15;
    private Context context;
    //endregion

    //region Constructors
    public CurrentPositionToken(Context ctx, GeoPoint geoPoint, Bitmap bitmap)
    {
        super(ctx);
        this.paint.setAntiAlias(true);
        location = geoPoint;
        currentPosBitmap = bitmap;
        context = ctx;
    }
    public void setLocation(GeoPoint point) {
        location = point;
    }

    public Double getLatitude()
    {
        return location.getLatitude();
    }

    public Double getLongitude()
    {
        return location.getLongitude();
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
        if(viewportRect.contains(newPoint.x, newPoint.y)) {
            Point mapCenterPoint = new Point();
            mapView.getProjection().toPixels(location, mapCenterPoint);
            float xBitmapPos = (float) (mapCenterPoint.x - currentPosBitmap.getWidth() / 2);
            float yBitmapPos = (float) (mapCenterPoint.y - currentPosBitmap.getHeight() / 2);
            c.drawBitmap(currentPosBitmap, xBitmapPos, yBitmapPos, this.paint);
        }

    }

    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        final Rect viewportRect = new Rect();
        viewportRect.set(mapView.getProjection().getScreenRect());
        Point newPoint = new Point();
        mapView.getProjection().toPixels(location, newPoint);
        if(viewportRect.contains(newPoint.x, newPoint.y)) {
            IGeoPoint geoPoint = mapView.getProjection().fromPixels(e.getX(), e.getY());
            Point pointTap = new Point();
            mapView.getProjection().toPixels(geoPoint, pointTap);
            Point pointOverlay = new Point();
            mapView.getProjection().toPixels(location, pointOverlay);

            float mm = UtilitiesClass.calcPixelRasst(pointTap, pointOverlay, mapView.getContext());
            if (mm < 250) {
               /* Intent myIntent = new Intent(context, CurrentPositionForecastActivity.class);
                context.startActivity(myIntent);*/
            }
        }
        return super.onSingleTapConfirmed(e,mapView);
    }
    //endregion
}