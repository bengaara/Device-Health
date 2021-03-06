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

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.BatteryManager;
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
import android.view.Window;
import android.view.WindowManager;

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

    public final static String DATE_FORMAT_TIMEZONE = "dd-mm-yyyy HH:mm:ss Z";//"MM-dd-yyyy HH:mm:ss"
    static SimpleDateFormat dateFormatWithTimezone = new SimpleDateFormat(DATE_FORMAT_TIMEZONE, Locale.getDefault());
    static SimpleDateFormat dateFormatOnly = new SimpleDateFormat("dd-mm-yyyy", Locale.getDefault());
    public final static String TIMEZONE_UTC = "UTC";

    public final static String SMARTHEALTH = "livinggoods";//org.medicmobile.webapp.mobile.livinggoodskenya
    public final static String DEVICEHEALTH = "goods.living";

    static ProgressDialog progressDialog;


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

    public static String getStringDateFromDate(Date date) {
        try {
            return dateFormatOnly.format(date);
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

    public static void showProgressDialog(Context c, String message) {
        dismissProgressDialog();
        progressDialog = ProgressDialog.show(c, "Please Wait", message, true);

    }

    public static void showProgressDialog(Context c) {
        showProgressDialog(c, "Processing...");

    }

    public static void dismissProgressDialog() {
        try {
            if (progressDialog != null)
                progressDialog.dismiss();
        } catch (Exception e) {
            Crashlytics.logException(e);

        }
    }

    public static String getInstalledApps(Context c) {

        String apps = "";
        List<PackageInfo> packs = c.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);

            apps += "\n" + p.applicationInfo.loadLabel(c.getPackageManager()).toString() + " - " + p.packageName;
            //   newInfo.pname = p.packageName;
            //   newInfo.versionName = p.versionName;
            //   newInfo.versionCode = p.versionCode;
            //   newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
            // res.add(newInfo);
        }


        return apps;
    }

    public static boolean isSmartHealthApp(String packageName) {
        if (packageName == null) return false;
        return packageName.contains(SMARTHEALTH);
    }

    public static boolean isDeviceHealthApp(String packageName) {
        if (packageName == null) return false;
        return packageName.contains(DEVICEHEALTH);
    }


    public static boolean isForeground(Context ctx, String myPackage) {

        if (android.os.Build.VERSION.SDK_INT > 23) { /*Ask Dungerous Permissions here*/
            ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);

            ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().contains(myPackage)) {
                return true;
            }

        } else {

        }

        return false;
    }

    public static int getBatteryPercentage(Context context) {
        try {
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            float batteryPct = level * 100 / (float) scale;

            Crashlytics.log(Log.DEBUG, TAG, "batteryPct: " + batteryPct);

            return (int) batteryPct;
        } catch (Exception e) {
            Crashlytics.logException(e);
            return 0;
        }
    }

    public static Double getBatteryCapacity(Context c) {
        Object mPowerProfile_ = null;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(c);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            double batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)//mah
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
            return batteryCapacity;
        } catch (Exception e) {
            Crashlytics.logException(e);
            return null;
        }
    }


    public static Float getBrightness(Context context, Window window) {

        try {


            //    Settings.System.putInt(
            //            context.getContentResolver(),
            //            Settings.System.SCREEN_BRIGHTNESS_MODE,
            //            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Integer brightness =
                    Settings.System.getInt(
                            context.getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS);

            Float bright;
            WindowManager.LayoutParams lp = window.getAttributes();
            bright = lp.screenBrightness;
            // window.setAttributes(lp);
            Crashlytics.log(Log.DEBUG, TAG, "bright brightness:  " + bright + "" + brightness);

            return brightness == null || brightness == -1 ? bright : brightness;
        } catch (Exception e) {
            Crashlytics.logException(e);
            return null;
        }

    }

//    public static void turnGPSOn(Context context) {
//        try {
//            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
//            intent.putExtra("enabled", true);
//            context.sendBroadcast(intent);
//        } catch (Exception e) {
//            Crashlytics.logException(e);
//
//        }
//    }


//    public static void makeGooglePlayServicesAvailable(Activity c) {
//        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//        apiAvailability.makeGooglePlayServicesAvailable(c);
//
//        int resultCode = apiAvailability.isGooglePlayServicesAvailable(c);
//        if (resultCode != ConnectionResult.SUCCESS) {
//
//            Answers.getInstance().logCustom(new CustomEvent("Missing Correct Google PlayService"));
//            //.putCustomAttribute("Reason", ""));
//
//            // Or you can depend on library classes to handle these errors and show appropriate error message with action in dialog.
//            if (apiAvailability.isUserResolvableError(resultCode)) {
//                //  apiAvailability.getErrorDialog(c, resultCode, 2400).show();
//                apiAvailability.makeGooglePlayServicesAvailable(c);
//
//                Answers.getInstance().logCustom(new CustomEvent("Missing Correct Google PlayService")
//                        .putCustomAttribute("Reason", "Requested update"));
//            } else {
//                // Unrecoverable error.
//                Answers.getInstance().logCustom(new CustomEvent("Missing Correct Google PlayService")
//                        .putCustomAttribute("Reason", "Unrecoverable error"));
//            }
//            return false;
//        }
//        return true;
//    }

}
