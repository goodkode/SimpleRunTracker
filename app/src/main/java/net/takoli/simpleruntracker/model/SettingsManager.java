package net.takoli.simpleruntracker.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;


public class SettingsManager {

    private Activity context;
    private SharedPreferences settings;
    private boolean isAppStart;
    private int screenHeight;
    private int screenWidth;

    public SettingsManager(Activity context) {
        this.context = context;
        this.settings = context.getPreferences(Context.MODE_PRIVATE);
        this.isAppStart = true;
    }

    public boolean isAppStart() {
        boolean appStart = isAppStart;
        isAppStart = false;
        return appStart;
    }

    public void setMainScreenHeight(int height) {
        this.screenHeight = height;
    }

    public int getMainScreenHeight() {
        return screenHeight;
    }

    public void setMainScreenWidth(int width) {
        this.screenWidth = width;
    }

    public int getMainScreenWidth() {
        return screenWidth;
    }

    public void setUnit(String unit) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("unit", unit);
        editor.commit();
    }

    public String getUnit() {
        return settings.getString("unit", "mi");
    }

    public String getUnitInFull() {
        String u = settings.getString("unit", "mi");
        if (u.compareTo("mi") == 0)
            return "miles";
        else if (u.compareTo("km") == 0)
            return "kilometers";
        else return "";
    }

    public void setDBLimit(RunDB runDB, String limit) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("limit", limit);
        editor.commit();
        runDB.setDBLimit(limit);
        runDB.ensureDBLimit();
    }
    public String getDBLimit() {
        return settings.getString("limit", "300");
    }

    public int[] getLastRunDatePicked() {
        final int[] lastDatePicked = new int[3];
        final Calendar defaultDate = Calendar.getInstance();
        defaultDate.roll(Calendar.DAY_OF_YEAR, -2);
        lastDatePicked[0] = settings.getInt("day", defaultDate.get(Calendar.DAY_OF_MONTH));
        lastDatePicked[1] = settings.getInt("month", defaultDate.get(Calendar.MONTH));
        lastDatePicked[2] = settings.getInt("year", defaultDate.get(Calendar.YEAR));
        Log.i("run", "getLastRunDatePicked: " + (lastDatePicked[1] + 1) + "/" + lastDatePicked[0] + "/" + lastDatePicked[2]);
        return lastDatePicked;
    }
    public void setLastRunDatePicked(int day, int month, int year) {
        SharedPreferences.Editor editor = settings.edit();
        final Calendar defaultDate = Calendar.getInstance();
        defaultDate.roll(Calendar.DAY_OF_YEAR, -2);
        if (defaultDate.get(Calendar.DAY_OF_MONTH) == day &&
                defaultDate.get(Calendar.MONTH) == month &&
                    defaultDate.get(Calendar.YEAR) == year) {
            editor.remove("day");
            editor.remove("month");
            editor.remove("year");
            Log.i("run", "setLastRunDatePicked: default");
        } else {
            editor.putInt("day", day);
            editor.putInt("month", month);
            editor.putInt("year", year);
            Log.i("run", "setLastRunDatePicked: " + (month + 1) + "/" + day + "/" + year);
        }
        editor.commit();
    }
}
