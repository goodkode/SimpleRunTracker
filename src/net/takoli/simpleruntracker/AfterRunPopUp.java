package net.takoli.simpleruntracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class AfterRunPopUp extends DialogFragment {
	
	View view;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.after_run_popup, container, false);
        return view;
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	TextView snapshot = (TextView) view.findViewById(R.id.after_run_snapshot);
    	TextView pace = (TextView) view.findViewById(R.id.after_run_pace);
    	TextView someStats = (TextView) view.findViewById(R.id.after_run_stats);
    	Run last = ((MainActivity) getActivity()).getRunDB().getLastRun();
    	snapshot.setText("You ran " + last.getDistance() + " in " + last.getTime() + "s"); 
    	pace.setText("Your pace was " + last.getPace() + "(" + last.getSpeed() + ")");
    	someStats.setText("somestats");
    	
    	// dismiss after some time
    	Thread thread = new Thread() {
    	    @Override
    	    public void run() {
    	        try {
	                sleep(2500);
	                if (AfterRunPopUp.this != null)
	                	AfterRunPopUp.this.dismiss();
    	        } catch (Exception e) { }
    	    }
    	};
    	thread.start();
    }
}