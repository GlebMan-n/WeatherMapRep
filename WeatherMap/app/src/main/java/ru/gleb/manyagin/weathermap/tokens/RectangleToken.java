package ru.gleb.manyagin.weathermap.tokens;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.preference.PreferenceManager;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.SimpleLocationOverlay;

import ru.gleb.manyagin.weathermap.R;
import ru.gleb.manyagin.weathermap.utilities.UtilitiesClass;

/**
 * Created by user on 02.05.2015.
 */
public class RectangleToken extends SimpleLocationOverlay {

    private GeoPoint locFirstPoint = null;
    private GeoPoint locSecondPoint = null;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Boolean show = true;

    public Boolean isVisible()
    {return  show;}

    public void setVisible (Boolean visible)
    {
        show = visible;

    }

    public RectangleToken(Context ctx, int color, GeoPoint first, GeoPoint second) {
        super(ctx);
        this.paint.setAntiAlias(true);
        this.paint.setColor(color);
        this.paint.setAlpha(20);
        this.paint.setStrokeWidth(10);
        setLocLeftTop(first);
        setLocRighBottom(second);
        show = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(ctx.getString(R.string.pref_show_rect_key), new Boolean(ctx.getString(R.string.pref_show_rect_default)));
    }

    public  void setLocLeftTop(GeoPoint point)
    {
        locFirstPoint = point;

    }
    public void setLocRighBottom(GeoPoint point)
    {
        locSecondPoint = point;
    }

    public GeoPoint getLocLeftTop(){return locFirstPoint;}
    public GeoPoint getLocRighBottom(){return locSecondPoint;}

    @Override
    public void draw(Canvas c, MapView mapView, boolean shadow) {
        if(locSecondPoint == null || locFirstPoint == null|| !show) { return; }

        Point firstPoint = new Point();
        Point secondPoint = new Point();
        mapView.getProjection().toPixels(locFirstPoint, firstPoint);
        mapView.getProjection().toPixels(locSecondPoint, secondPoint);

        UtilitiesClass.swapIfNeed(firstPoint, secondPoint);

        c.drawRect(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y, this.paint);
    }
}
