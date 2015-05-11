package ru.gleb.manyagin.weathermap.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;

import ru.gleb.manyagin.weathermap.R;

/**
 * Created by user on 09.05.2015.
 */
public class NotifiClass {

    private Context context = null;

    private final int NOTIFY_ID;

    public NotifiClass(Context context, Class<?> _class, String ticker, String header, String body, int id) {

        NOTIFY_ID = id;
        Intent notificationIntent = new Intent(context, _class);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher_2)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher_2))
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(header)
                .setContentText(body);

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }
}
