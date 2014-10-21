package com.next.eswaraj;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.util.Log;

import com.eswaraj.web.dto.device.NotificationMessage;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            String messageType = gcm.getMessageType(intent);
            Log.i("eswaraj", "messageType=" + messageType);
            Log.i("eswaraj", "intent.getExtras().toString()=" + intent.getExtras().toString());
            String message = intent.getExtras().getString(NotificationMessage.MESSAGE);
            String appMessageType = intent.getExtras().getString(NotificationMessage.MESSAGE_TYPE);
            Log.i("eswaraj", "intent.getExtras().toString(msg)=" + message);
            Log.i("eswaraj", "intent.getExtras().toString(msg)=" + intent.getExtras().getString(NotificationMessage.MESSAGE_TYPE));

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                // sendNotification("Send error: " +
                // intent.getExtras().toString(),"Aam Aadmi Party",messageType,null);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                // sendNotification("Deleted messages on server: " +
                // intent.getExtras().toString(),"Aam Aadmi Party",messageType,null);
            } else {
                processNotfication(context, message, appMessageType, "DEFAULT_SOUND");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        setResultCode(Activity.RESULT_OK);
    }

    private void processNotfication(Context context, String msg, String msgType, String ringtoneKey) {
        Log.i("AAP", "New News message : " + msg);
        Intent targetIntent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("msg", msg);
        bundle.putString("msgType", msgType);
        targetIntent.putExtra("NotificationMessage", bundle);

        sendNotification(context, targetIntent, "eSwaraj", msg, 1, R.drawable.ic_launcher);
    }

    private void sendNotification(Context context, Intent targetIntent, String contentTitle, String notificationMessage, int notificationId, int iconId) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, targetIntent, 0);
        InboxStyle notificationStyle = new NotificationCompat.InboxStyle().addLine(notificationMessage);
        // notificationStyle.addLine("2. " + notificationMessage);
        // notificationStyle.addLine("3. " + notificationMessage);
        /*
        RemoteViews removeWidget = new RemoteViews(context.getPackageName(), R.layout.notification_generic);
        removeWidget.setOnClickPendingIntent(R.id.notification_main, contentIntent);
        removeWidget.setTextViewText(R.id.notification_message, notificationMessage);
        removeWidget.setImageViewResource(R.id.notification_image, R.drawable.ic_launcher);
        */
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(iconId).setContentTitle(contentTitle).setStyle(notificationStyle)
                .setContentText(notificationMessage);

        boolean notificationAllowed = true;// PreferencesUtil.getInstance().getBooleanSetting(ctx,
                                           // preferenceKey, false);
        if (notificationAllowed) {
            // vibratate(context, preferenceKey + "_vibrate");
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // if (preferenceKey != null && !preferenceKey.trim().isEmpty()) {
            /*
             * String ringToneKey = preferenceKey + "_ringtone"; String ringTone
             * = "DEFAULT_SOUND";//
             * PreferencesUtil.getInstance().getStringSetting(ctx, //
             * ringToneKey, // "DEFAULT_SOUND"); soundUri = Uri.parse(ringTone);
             */
            // }
            mBuilder.setSound(soundUri);
        }
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[6];
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Event tracker details:");
        // Moves events into the expanded layout
        for (int i = 0; i < events.length; i++) {

            inboxStyle.addLine(i + "-" + notificationMessage);
        }
        // Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);

        mNotificationManager.notify(notificationId, mBuilder.build());

        /*
         * Uri soundUri =
         * RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
         * Log.i("AAP", "ringtoneKey="+ringtoneKey); if(ringtoneKey != null &&
         * !ringtoneKey.trim().isEmpty()){ String ringTone =
         * PreferencesUtil.getInstance().getStringSetting(ctx, ringtoneKey,
         * "DEFAULT_SOUND"); Log.i("AAP", "ringTone="+ringTone); soundUri =
         * Uri.parse(ringTone);
         */

    }
}
