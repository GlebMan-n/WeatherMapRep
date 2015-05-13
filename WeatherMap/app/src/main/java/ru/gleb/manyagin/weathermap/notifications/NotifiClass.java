package ru.gleb.manyagin.weathermap.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;

import ru.gleb.manyagin.weathermap.MainActivity;
import ru.gleb.manyagin.weathermap.R;
import ru.gleb.manyagin.weathermap.activities.CurrentPositionForecastActivity;

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



        Intent resultIntent = null;
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        switch (NOTIFY_ID)
        {
            case 101:
                resultIntent = new Intent(context, MainActivity.class);
                stackBuilder.addParentStack(MainActivity.class);
                break;
            case 102:
                resultIntent = new Intent(context, CurrentPositionForecastActivity.class);
                stackBuilder.addParentStack(CurrentPositionForecastActivity.class);
                break;
            default:
                resultIntent = new Intent(context, MainActivity.class);
                stackBuilder.addParentStack(MainActivity.class);
                break;

        }

// Adds the back stack for the Intent (but not the Intent itself)

// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFY_ID, builder.build());
    }
}
