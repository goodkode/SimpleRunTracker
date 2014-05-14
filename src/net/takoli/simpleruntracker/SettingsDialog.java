package net.takoli.simpleruntracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsDialog extends DialogFragment {
	
	MainActivity main;
	String unit;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	main = (MainActivity) getActivity();
    	unit = main.getUnit();
        return new AlertDialog.Builder(getActivity())
        		.setMessage("Do you want to enter distance in miles or kilometers")
                .setNegativeButton("Miles",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	unit = "m"; }
                    }
                )
                .setPositiveButton("Kilometers",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	unit = "km"; }
                    }
                ).create();
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	main.setUnit(unit);
    	Toast.makeText(main, "New runs will use " + main.getUnitInFull(), Toast.LENGTH_LONG).show();
    	((TextView) main.findViewById(R.id.dist_unit)).setText(unit);
    	main.getGraphView().setRunList(main.getRunDB().getRunList(), main.getUnit());
    	main.updateGraph();
    }
}