package net.takoli.simpleruntracker.view;


import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.RunApp;
import net.takoli.simpleruntracker.model.RunDB;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestoreDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(getActivity()
                .getLayoutInflater().inflate(R.layout.restore_dialog_fragment, null))
                .setTitle("Restore from SD Card")
                .setPositiveButton("Yes, restore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final MainActivity main = (MainActivity) getActivity();
                        final RunDB runDB = ((RunApp) main.getApplication()).getRunDB();
                        runDB.restoreFromExternalMemory(getContext());
                        main.refreshListWithNewData();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        return dialog;
    }

}
