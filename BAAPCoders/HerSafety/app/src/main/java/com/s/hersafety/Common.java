package com.s.hersafety;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static final String KEY_REQUESTING_LOCATION_UPDATES = "LocationUpdateEnable";

    public static String getLocationText(Location mLocation) {
        return mLocation ==null ? "Unknown Location" : new StringBuilder()
                .append(mLocation.getLatitude())
                .append("/")
                .append(mLocation.getLongitude())
                .toString();
    }

    public static CharSequence getLocationTitile(MyBackgroundService myBackgroundService) {
        return getDate(System.currentTimeMillis());
        //return String.format("Location Updated: %1$s", DateFormat.getDateInstance().format(new Date()));
    }

    public static void setRequestingLocationUpdates(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_REQUESTING_LOCATION_UPDATES,value)
                .apply();
    }

    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES,false);
    }
    public static String getDate(long time)
    {
        Calendar calendar= Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        return android.text.format.DateFormat.format("dd-MM-yyyy HH:mm", calendar).toString();
    }
}
