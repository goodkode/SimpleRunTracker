package net.takoli.simpleruntracker;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import net.takoli.simpleruntracker.model.RunDB;
import net.takoli.simpleruntracker.model.SettingsManager;

/**
 * Created by takoli on 10/9/15.
 */
public class RunApp extends Application {

    public SettingsManager settingsManager;
    private RunDB runDB;
    private Tracker mTracker;

    public RunDB getRunDB() {
        if (runDB == null)
            runDB = new RunDB(this);
        return runDB;
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
