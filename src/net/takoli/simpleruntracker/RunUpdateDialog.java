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
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		date = getArguments().getString("date");
		distance = getArguments().getString("distance");
		time = getArguments().getString("time");
		Log.i("run", "oncreate");
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle bundle) {
        View view = inflater.inflate(R.layout.update_run_dialog, container);        
        getDialog().setTitle("Update " + date  + "'s run's details");
        EditText updateDistance = (EditText) view.findViewById(R.id.update_distance);
        EditText updateTime = (EditText) view.findViewById(R.id.update_time);
        updateDistance.setText(distance);
        updateTime.setText(time);
        return view;
    }

}
