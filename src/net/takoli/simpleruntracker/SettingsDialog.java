package net.takoli.simpleruntracker;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsDialog extends DialogFragment {
	
	private MainActivity main;
	private AlertDialog settingsView;
	private RadioButton rMiles, rKm, r100, r300, r500, rDate;
	private Calendar startDate;
	private String origUnit;
	private String origLimit;
	private String newUnit;
	private String newLimit;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	main = (MainActivity) getActivity();
    	View view = getActivity().getLayoutInflater().inflate(R.layout.settings_dialog, null);
        settingsView =  new AlertDialog.Builder(getActivity())
        		.setView(view)
        		.setTitle("Settings")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	updateValues(); }
                    }
                ).create();
        return settingsView;
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	rMiles = (RadioButton) settingsView.findViewById(R.id.settings_miles);
    	rKm = (RadioButton) settingsView.findViewById(R.id.settings_kilometers);
    	r100 = (RadioButton) settingsView.findViewById(R.id.settings_100);
    	r300 = (RadioButton) settingsView.findViewById(R.id.settings_300);
    	r500 = (RadioButton) settingsView.findViewById(R.id.settings_500);
    	rDate = (RadioButton) settingsView.findViewById(R.id.settings_starting_date);
    	origUnit = main.getUnit();
    	origLimit = main.getDBLimit();
    	// origUnit set-up
    	if (origUnit.compareTo("km") == 0)
    		rKm.setChecked(true);
    	else
    		rMiles.setChecked(true);
    	// origLimit set-up
    	if (origLimit.compareTo("100") == 0)
    		r100.setChecked(true);
    	else if (origLimit.compareTo("300") == 0)
    		r300.setChecked(true);
    	else if (origLimit.compareTo("500") == 0)
    		r500.setChecked(true);
    	else {
    		startDate = Run.string2date(origLimit);
    		rDate.setText(origLimit);
    	}
    	OnClickListener deselectRDate = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (((RadioButton) v).isChecked())
					rDate.setChecked(false); 
		} };
    	r100.setOnClickListener(deselectRDate);
    	r300.setOnClickListener(deselectRDate);
    	r500.setOnClickListener(deselectRDate);
    	rDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (((RadioButton) v).isChecked()) {
					r100.setChecked(false);
					r300.setChecked(false);
					r500.setChecked(false); }
				SettingsDatePickerFragment datePicker = new SettingsDatePickerFragment();
				datePicker.setParentView(settingsView);
				datePicker.show(getFragmentManager(), "settingsDatePicker");
		} });
    }
    
    private void updateValues() {
    	// distance origUnit
    	if (rMiles.isChecked())
    		newUnit = "mi";
    	else
    		newUnit = "km";
    	if (newUnit.compareTo(origUnit) != 0) {
    		main.setUnit(newUnit);
    		Toast.makeText(main, "New runs will be entered in " + main.getUnitInFull(), Toast.LENGTH_SHORT).show();
	    	((TextView) main.findViewById(R.id.dist_unit)).setText(newUnit);
	    	main.getGraphView().setRunList(main.getRunDB(), newUnit);
	    	main.updateGraph();
    	}
    	// DB origLimit
    	if (r100.isChecked())
    		newLimit = "100";
    	else if (r300.isChecked())
    		newLimit = "300";
    	else if (r500.isChecked())
    		newLimit = "500";
    	else
    		newLimit = "TO DO";
    	if (newLimit.compareTo(origLimit) != 0) {
    		main.setDBLimit(newLimit);
    		Toast.makeText(main, "New size of the DB is " + newLimit, Toast.LENGTH_SHORT).show();
    	}
    }
    
	public static class SettingsDatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		
		DatePickerDialog datePickerDialog;
		AlertDialog settingsDialog;
		MainActivity main;
		RadioButton startDate;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			datePickerDialog = new DatePickerDialog(getActivity(), this, 2010, 0, 1);
			DatePicker datePicker = datePickerDialog.getDatePicker();
			datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
			datePickerDialog.setTitle("Save workouts from this date on");
			return datePickerDialog;
		}
		
		public void setParentView(AlertDialog parent) {
			settingsDialog = parent;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			main = (MainActivity) getActivity();
			startDate = (RadioButton) settingsDialog.findViewById(R.id.settings_starting_date);
			if (startDate == null)
				Log.i("run", "null");
			else
			startDate.setText(Run.getFullStringDate(year, month, day));
		}
	}
}