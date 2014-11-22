package in.abmulani.importanthadees.utils;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import in.abmulani.importanthadees.R;
import in.abmulani.importanthadees.activity.HomeScreenActivity;
import timber.log.Timber;

/**
 * Created by Aabid on 08/07/14.
 */
public class GcmIntentService extends IntentService {
    private final int NOTIFY_REQUEST_CODE = 1001;
    private NotificationManager mNotificationManager;
    private Bundle extras;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        Timber.e("Bundle Msg : " + extras.getString("message"));
        if (!extras.isEmpty()) {
            computeReminderNotification(extras);
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void computeReminderNotification(Bundle extras) {
        Timber.e("alert : " + extras.getString("message"));
        String alertMsg = extras.getString("message");
        Intent contentIntent;
        contentIntent = new Intent(this, HomeScreenActivity.class);
        contentIntent.putExtra(HomeScreenActivity.EXTRA_REFRESH, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFY_REQUEST_CODE, contentIntent, PendingIntent.FLAG_ONE_SHOT);
        showNotification(++AppConstants.NOTIFICATION_ID, alertMsg, pendingIntent);
    }

    private void showNotification(int notifyId, String alertMsg, PendingIntent intent) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(alertMsg))
                        .setAutoCancel(true)
                        .setContentIntent(intent)
                        .setSound(alarmSound);
        if (Build.VERSION.SDK_INT >= 16) {
            mBuilder.setPriority(Notification.PRIORITY_MAX);
        }
        mNotificationManager.notify(notifyId, mBuilder.build());
    }

}