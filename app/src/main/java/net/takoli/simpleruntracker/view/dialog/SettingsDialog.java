package net.takoli.simpleruntracker.view.dialog;

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

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.RunApp;
import net.takoli.simpleruntracker.model.Run;
import net.takoli.simpleruntracker.view.MainActivity;

import java.util.Calendar;

public class SettingsDialog extends DialogFragment {

    private static final String SETTINGS_DATE_PICKER_TAG = "settingsDatePicker";
    private static final String KM = "km";
    private static final String MI = "mi";
    private static final String HUNDRED = "100";
    private static final String THREE_HUNDRED = "300";
    private static final String FIVE_HUNDRED = "500";

	private RunApp app;
	private MainActivity main;
	private AlertDialog settingsView;
	private RadioButton rMiles, rKm, r100, r300, r500, rDate;
	private String origUnit;
	private String origLimit;
	private String newUnit;
	private String newLimit;
    private String newLimitDate;
	private boolean rDateUpdated = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		app = (RunApp) getActivity().getApplication();
    	main = (MainActivity) getActivity();
        settingsView =  new AlertDialog.Builder(getActivity())
        		.setView(getActivity().getLayoutInflater().inflate(R.layout.settings_dialog, null))
        		.setTitle(getResources().getString(R.string.settings))
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(getResources().getString(R.string.ok),
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
    	origUnit = app.settingsManager.getUnit();
    	origLimit = app.settingsManager.getDBLimit();
    	// origUnit set-up
    	if (origUnit.compareTo(KM) == 0)
    		rKm.setChecked(true);
    	else
    		rMiles.setChecked(true);
    	// origLimit set-up
    	if (origLimit.compareTo(HUNDRED) == 0)
    		r100.setChecked(true);
    	else if (origLimit.compareTo(THREE_HUNDRED) == 0)
    		r300.setChecked(true);
    	else if (origLimit.compareTo(FIVE_HUNDRED) == 0)
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
				datePicker.show(getFragmentManager(), SETTINGS_DATE_PICKER_TAG);
		} });
    }
    
    private void updateValues() {
    	// distance origUnit
    	if (rMiles.isChecked())
    		newUnit = MI;
    	else
    		newUnit = KM;
    	if (newUnit.compareTo(origUnit) != 0) {
    		app.settingsManager.setUnit(newUnit);
            final String updateToast = String.format(getResources().getString(R.string.new_runs_in_toast), app.settingsManager.getUnitInFull());
            Toast.makeText(main, updateToast, Toast.LENGTH_SHORT).show();
	    	((TextView) main.findViewById(R.id.dist_unit)).setText(newUnit);
	    	main.getGraphView().setRunList(app.getRunDB(), newUnit);
	    	main.updateGraph();
    	}
    	// DB origLimit
    	if (r100.isChecked())
    		newLimit = HUNDRED;
    	else if (r300.isChecked())
    		newLimit = THREE_HUNDRED;
    	else if (r500.isChecked())
    		newLimit = FIVE_HUNDRED;
    	else if (rDateUpdated)
    		newLimit = newLimitDate;
    	else 
    		newLimit = origLimit;
    	if (newLimit.compareTo(origLimit) != 0) {
    		app.settingsManager.setDBLimit(app.getRunDB(), newLimit);
    		app.getRunDB().ensureDBLimit();
    		if (rDate.isChecked()) {
                final String toastMsg = String.format(getResources().getString(R.string.workouts_after), Run.getFullStringDate(newLimitDate));
                Toast.makeText(main, toastMsg, Toast.LENGTH_SHORT).show();
            } else {
                final String toastMsg = String.format(getResources().getString(R.string.last_workouts), newLimit);
                Toast.makeText(main, toastMsg, Toast.LENGTH_SHORT).show();
            }
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
			datePickerDialog.setTitle(getResources().getString(R.string.save_from_on));
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
			settingsDialog.newLimitDate = Run.getFullStringDate(year, month, day);
			settingsDialog.rDateUpdated = true;
		}
	}
}