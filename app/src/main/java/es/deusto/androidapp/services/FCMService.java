package es.deusto.androidapp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import es.deusto.androidapp.R;
import es.deusto.androidapp.activities.LoginActivity;

public class FCMService extends FirebaseMessagingService {

    private static final String TAG = FCMService.class.getName();

    public static final String MSG_DATA_KEY_TITLE = "title";
    public static final String MSG_DATA_KEY_DESC = "description";
    public static final String MSG_DATA_KEY_RECIPE = "recipe";

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // Notification ID.
    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        RemoteMessage.Notification msgNotif =  remoteMessage
                .getNotification();

        Map<String, String> msgData = remoteMessage.getData();
        if (!msgData.isEmpty()) {
            Log.d(TAG, "FCM Data Msg: Title = " + msgData.get(MSG_DATA_KEY_TITLE));
            Log.d(TAG, "FCM Data Msg: Description = " + msgData.get(MSG_DATA_KEY_DESC));
            Log.d(TAG, "FCM Data Msg: Recipe = " + msgData.get(MSG_DATA_KEY_RECIPE));

            createNotificationChannel();
            sendNotification(msgData.get(MSG_DATA_KEY_TITLE), msgData.get(MSG_DATA_KEY_DESC), msgData.get(MSG_DATA_KEY_RECIPE));
        } else {
            if (msgNotif != null) {
                Log.d(TAG, "FCM Not. Msg: Title=" + msgNotif.getTitle());
                Log.d(TAG, "FCM Not. Msg: Text=" + msgNotif.getBody());
                Log.d(TAG, "FCM Not. Msg: ImgURL=" + msgNotif.getImageUrl());

                createNotificationChannel();
                sendNotification(msgNotif.getTitle(), msgNotif.getBody(), "");

            }
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "onNewToken: token=" + token);
    }

    public static void printToken(final Context context){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.d(TAG, "Current FCM token:" + token);
                    }
                });
    }

    private void sendNotification(String title, String description, String recipe) {

        // Build the notification with all of the parameters using helper
        // method.

        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(title, description, recipe);

        // Deliver the notification.
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

    }

    private NotificationCompat.Builder getNotificationBuilder(String title, String description, String recipe) {

        Intent notificationIntent = new Intent(this, LoginActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity
                (this, NOTIFICATION_ID, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        if (recipe.equals("")) {

            // Build the notification with all of the parameters.
            NotificationCompat.Builder notifyBuilder = new NotificationCompat
                    .Builder(this, PRIMARY_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true).setContentIntent(notificationPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL);
            return notifyBuilder;

        } else {
            // Build the notification with all of the parameters.
            NotificationCompat.Builder notifyBuilder = new NotificationCompat
                    .Builder(this, PRIMARY_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(Html.fromHtml(description + "<br/> <br/> " + "<b>" + "New recipe: " + "</b>" + recipe))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true).setContentIntent(notificationPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL);
            return notifyBuilder;
        }



    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    private void createNotificationChannel() {

        // Create a notification manager object.
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            getString(R.string.notification_channel_name),
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription
                    (getString(R.string.notification_channel_description));

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }
}
