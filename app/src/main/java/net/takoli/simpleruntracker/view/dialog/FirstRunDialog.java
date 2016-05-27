package net.takoli.simpleruntracker.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.RunApp;
import net.takoli.simpleruntracker.view.MainActivity;

public class FirstRunDialog extends DialogFragment {

	private static final String KM = "km";
	private static final String MI = "mi";

	private RunApp app;
	private MainActivity main;
	private TextView unitTV;
	private String unit;
	private boolean updated = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		app = (RunApp) getActivity().getApplication();
    	main = (MainActivity) getActivity();
    	unit = app.settingsManager.getUnit();
        return new AlertDialog.Builder(getActivity())
        		.setTitle(getResources().getString(R.string.choose_unit))
        		.setMessage(getResources().getString(R.string.choose_message))
                .setNegativeButton(getResources().getString(R.string.miles),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	updated = unit.compareTo(MI) != 0;
                        	unit = MI; }
                    }
                )
                .setPositiveButton(getResources().getString(R.string.kilometers),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	updated = unit.compareTo(KM) != 0;
                        	unit = KM; }
                    }
                ).create();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	if (updated) {
    	app.settingsManager.setUnit(unit);
            final String toastMsg = String.format(getResources().getString(R.string.new_runs_in_toast), app.settingsManager.getUnitInFull());
            Toast.makeText(main, toastMsg, Toast.LENGTH_SHORT).show();
	    	unitTV = ((TextView) main.findViewById(R.id.dist_unit));
	    	unitTV.setText(unit);
	    	main.getGraphView().setRunList(app.getRunDB(), app.settingsManager.getUnit());
	    	main.updateGraph();}
    }
}
