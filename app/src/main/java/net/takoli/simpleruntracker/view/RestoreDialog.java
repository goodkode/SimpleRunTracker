package net.takoli.simpleruntracker.view;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

import net.takoli.simpleruntracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestoreDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.restore_from_sd))
                .setMessage(getResources().getString(R.string.restore_message))
                .setPositiveButton(getResources().getString(R.string.restore_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final MainActivity main = (MainActivity) getActivity();
                        main.tryStorageTask(MainActivity.RESTORE_FROM_SD);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .create();
    }

}
