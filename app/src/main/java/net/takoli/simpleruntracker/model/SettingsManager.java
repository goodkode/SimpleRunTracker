package net.takoli.simpleruntracker.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import net.takoli.simpleruntracker.R;

import java.util.Calendar;


public class SettingsManager {

    private static final String UNIT = "unit";
    private static final String MI = "mi";
    private static final String KM = "km";
    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private static final String LIMIT = "limit";
    private static final String DEFAULT_LIMIT_300 = "300";

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
        editor.putString(UNIT, unit);
        editor.commit();
    }

    public String getUnit() {
        return settings.getString(UNIT, MI);
    }

    public String getUnitInFull() {
        String u = settings.getString(UNIT, MI);
        if (u.compareTo(MI) == 0)
            return context.getResources().getString(R.string.miles);
        else if (u.compareTo(KM) == 0)
            return context.getResources().getString(R.string.kilometers);
        else return "";
    }

    public void setDBLimit(RunDB runDB, String limit) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(LIMIT, limit);
        editor.commit();
        runDB.setDBLimit(limit);
        runDB.ensureDBLimit();
    }
    public String getDBLimit() {
        return settings.getString(LIMIT, DEFAULT_LIMIT_300);
    }

    public int[] getLastRunDatePicked() {
        final int[] lastDatePicked = new int[3];
        final Calendar defaultDate = Calendar.getInstance();
        defaultDate.roll(Calendar.DAY_OF_YEAR, -2);
        lastDatePicked[0] = settings.getInt(DAY, defaultDate.get(Calendar.DAY_OF_MONTH));
        lastDatePicked[1] = settings.getInt(MONTH, defaultDate.get(Calendar.MONTH));
        lastDatePicked[2] = settings.getInt(YEAR, defaultDate.get(Calendar.YEAR));
        return lastDatePicked;
    }
    public void setLastRunDatePicked(int day, int month, int year) {
        SharedPreferences.Editor editor = settings.edit();
        final Calendar defaultDate = Calendar.getInstance();
        defaultDate.roll(Calendar.DAY_OF_YEAR, -2);
        if (defaultDate.get(Calendar.DAY_OF_MONTH) == day &&
                defaultDate.get(Calendar.MONTH) == month &&
                    defaultDate.get(Calendar.YEAR) == year) {
            editor.remove(DAY);
            editor.remove(MONTH);
            editor.remove(YEAR);
        } else {
            editor.putInt(DAY, day);
            editor.putInt(MONTH, month);
            editor.putInt(YEAR, year);
        }
        editor.commit();
    }
}
