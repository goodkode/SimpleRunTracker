package net.takoli.simpleruntracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SettingsDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
        		.setMessage("Do you want to enter distance in miles or kilometers")
                .setNegativeButton("Kilometers",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	((MainActivity) getActivity()).setUnit("km");
                        	((MainActivity) getActivity()).updateGraph(); }
                    }
                )
                .setPositiveButton("Miles",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	((MainActivity) getActivity()).setUnit("m");
                        	((MainActivity) getActivity()).updateGraph(); }
                    }
                ).create();
    }
}