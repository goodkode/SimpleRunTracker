package net.takoli.simpleruntracker;

import android.app.Application;

import net.takoli.simpleruntracker.model.RunDB;
import net.takoli.simpleruntracker.model.SettingsManager;

/**
 * Created by takoli on 10/9/15.
 */
public class RunApp extends Application {

    public SettingsManager settingsManager;
    private RunDB runDB;

    public RunDB getRunDB() {
        if (runDB == null)
            runDB = new RunDB(this);
        return runDB;
    }
}
