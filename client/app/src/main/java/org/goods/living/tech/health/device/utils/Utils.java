/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.goods.living.tech.health.device.utils;


//import android.app.NotificationChannel;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.goods.living.tech.health.device.R;
import org.goods.living.tech.health.device.UI.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility methods used in this sample.
 */
public class Utils {

    //  public final static String KEY_LOCATION_UPDATES_REQUESTED = "location-updates-requested";
    //  public final static String KEY_LOCATION_UPDATES_RESULT = "location-update-result";
    public final static String CHANNEL_ID = "channel_01";
    final static String TAG = Utils.class.getSimpleName();//BaseService.class.getSimpleName();
    final static String KEY_INSTALL_ID = "KEY_INSTALL_ID";

    public final static String DATE_FORMAT_TIMEZONE = "MM-dd-yyyy HH:mm:ss Z";//"MM-dd-yyyy HH:mm:ss"
    static SimpleDateFormat dateFormatWithTimezone = new SimpleDateFormat(DATE_FORMAT_TIMEZONE, Locale.getDefault());
    public final static String TIMEZONE_UTC = "UTC";


/*
    public static void setRequestingLocationUpdates(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_LOCATION_UPDATES_REQUESTED, value)
                .apply();
    }T

    public static boolean getRequestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_LOCATION_UPDATES_REQUESTED, false);
    }
*/

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    public static void sendNotification(Context context, String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.putExtra("from_notification", true);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle("Location update")
                .setContentText(notificationDetails)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);

            // Channel ID
            builder.setChannelId(CHANNEL_ID);
        }

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    public static String getAndroidId(@NonNull Context context) {

        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static String getSetInstallId(Context context, List<Location> locations) {

        String uuid = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_INSTALL_ID, null);

        if (uuid == null) {
            Crashlytics.log(Log.DEBUG, TAG, "creating new uuid: " + uuid);

            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString(KEY_INSTALL_ID, uuid)
                    .apply();
        }

        return uuid;
    }

    public static Date getDateFromTimeStampWithTimezone(String date, TimeZone timezone) {

        try {
            dateFormatWithTimezone.setTimeZone(timezone);
            return dateFormatWithTimezone.parse(date);
        } catch (Exception e) {
            Crashlytics.logException(e);
            return null;
        }

    }

    /**
     * Converting from Date to String
     **/
    public static String getStringTimeStampWithTimezoneFromDate(Date date, TimeZone timezone) {
        try {
            dateFormatWithTimezone.setTimeZone(timezone);//TimeZone.getTimeZone("UTC"
            return dateFormatWithTimezone.format(date);
        } catch (Exception e) {
            Crashlytics.logException(e);
            return null;
        }

    }

    public static Handler getHandlerThread() {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //this runs on the UI thread
            }
        });
        boolean isUiThread = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ? Looper.getMainLooper().isCurrentThread()
                : Thread.currentThread() == Looper.getMainLooper().getThread();
        HandlerThread handlerThread = new HandlerThread("NetworkOperation");
        handlerThread.start();
        Handler requestHandler = new Handler(handlerThread.getLooper());

        return requestHandler;


    }
}
