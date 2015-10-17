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
        		.setTitle("Choose unit of distance")
        		.setMessage("Do you want to enter distance in miles or kilometers")
                .setNegativeButton("Miles",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	updated = unit.compareTo("ni") != 0;
                        	unit = "mi"; }
                    }
                )
                .setPositiveButton("Kilometers",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	updated = unit.compareTo("km") != 0;
                        	unit = "km"; }
                    }
                ).create();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	if (updated) {
    	app.settingsManager.setUnit(unit);
	    	Toast.makeText(main, "New runs will be entered in " + app.settingsManager.getUnitInFull(), Toast.LENGTH_SHORT).show();
	    	unitTV = ((TextView) main.findViewById(R.id.dist_unit));
	    	unitTV.setText(unit);
	    	main.getGraphView().setRunList(app.getRunDB(), app.settingsManager.getUnit());
	    	main.updateGraph();}
    }
}
