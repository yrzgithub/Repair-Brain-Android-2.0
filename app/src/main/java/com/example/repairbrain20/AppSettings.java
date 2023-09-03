package com.example.repairbrain20;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {
    boolean auto_login = false;
    boolean show_notification = true;
    int hour, minute;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;

    AppSettings(Activity act) {
        preferences = act.getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = preferences.edit();

        auto_login = preferences.getBoolean("auto_login", false);
        show_notification = preferences.getBoolean("show_notification", true);
        hour = preferences.getInt("hour", 7);
        minute = preferences.getInt("minute", 0);
    }

    AppSettings(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = preferences.edit();

        auto_login = preferences.getBoolean("auto_login", false);
        show_notification = preferences.getBoolean("show_notification", true);
        hour = preferences.getInt("hour", 7);
        minute = preferences.getInt("minute", 0);
    }

    public SharedPreferences getSharedPreference() {
        return preferences;
    }

    public boolean isAuto_login() {
        return auto_login;
    }

    public void setAuto_login(boolean auto_login) {
        this.auto_login = auto_login;
        editor.putBoolean("auto_login", auto_login).commit();

    }

    public boolean isShow_notification() {
        return show_notification;
    }

    public void setShow_notification(boolean show_notification) {
        this.show_notification = show_notification;
        editor.putBoolean("show_notification", show_notification).commit();
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
        editor.putInt("hour", hour).commit();
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
        editor.putInt("minute", minute).commit();
    }
}