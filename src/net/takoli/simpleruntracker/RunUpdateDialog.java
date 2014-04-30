package net.takoli.simpleruntracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class RunUpdateDialog extends DialogFragment {
	
	String date, distance, time;
	Run run;
	
	public RunUpdateDialog() { }
	
	public void setRun(Run run) {
		this.run = run;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        date = run.getDate();
        distance = run.getDistance();
        time = run.getTime();
        View view = inflater.inflate(R.layout.update_run_dialog, container);        
        getDialog().setTitle("Update " + date  + "'s run's details");
        EditText updateDistance = (EditText) view.findViewById(R.id.update_distance);
        EditText updateTime = (EditText) view.findViewById(R.id.update_time);
        updateDistance.setText(distance);
        updateTime.setText(time);
        return view;
    }

}
