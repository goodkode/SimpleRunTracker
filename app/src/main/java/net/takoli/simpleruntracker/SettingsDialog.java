package net.takoli.simpleruntracker;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class SettingsDialog extends DialogFragment {
	
	private MainActivity main;
	private AlertDialog settingsView;
	private RadioButton rMiles, rKm, r100, r300, r500, rDate;
	private String origUnit;
	private String origLimit;
	private String newUnit;
	private String newLimit, newLimitDate;
	private boolean rDateUpdated = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	main = (MainActivity) getActivity();
        settingsView =  new AlertDialog.Builder(getActivity())
        		.setView(getActivity().getLayoutInflater().inflate(R.layout.settings_dialog, null))
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
    	origUnit = main.settingsManager.getUnit();
    	origLimit = main.settingsManager.getDBLimit();
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
    		rDate.setText(Run.getFullStringDate(origLimit));
    		rDate.setChecked(true);
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
				datePicker.setParent(SettingsDialog.this);
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
    		main.settingsManager.setUnit(newUnit);
    		Toast.makeText(main, "New runs will be entered in " + main.settingsManager.getUnitInFull(), Toast.LENGTH_SHORT).show();
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
    	else if (rDateUpdated)
    		newLimit = newLimitDate;
    	else 
    		newLimit = origLimit;
    	if (newLimit.compareTo(origLimit) != 0) {
    		main.settingsManager.setDBLimit(main.getRunDB(), newLimit);
    		main.getRunDB().ensureDBLimit();
    		if (rDate.isChecked())
    			Toast.makeText(main, "Workouts after " + Run.getFullStringDate(newLimitDate) + " will be recorded", 
    					Toast.LENGTH_SHORT).show();
    		else
    			Toast.makeText(main, "Last " + newLimit + " workouts will be recorded", 
    					Toast.LENGTH_SHORT).show();
    		main.getRunAdapter().notifyDataSetChanged();
    	}
    }
    
	public static class SettingsDatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		
		DatePickerDialog datePickerDialog;
		SettingsDialog settingsDialog;
		AlertDialog settingsDialogView;
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
		
		public void setParent(SettingsDialog parent) {
			settingsDialog = parent;
		}
		
		public void setParentView(AlertDialog parentView) {
			settingsDialogView = parentView;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			main = (MainActivity) getActivity();
			startDate = (RadioButton) settingsDialogView.findViewById(R.id.settings_starting_date);
			startDate.setText(Run.getFullStringDate(year, month, day));
			settingsDialog.newLimitDate = (month + 1) + "/" + day + "/" + year;
			settingsDialog.rDateUpdated = true;
		}
	}
}