package net.takoli.simpleruntracker.view;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestoreDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Restore from SD Card")
                .setMessage("You need to have a previously backed up RunTracker-runlist.csv file in your Download directory." +
                        "\n\nThis file will overwrite your current app data" +
                        "\n\nAre you sure you want to go ahead?")
                .setPositiveButton("Yes, restore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final MainActivity main = (MainActivity) getActivity();
                        main.tryStorageTask(MainActivity.RESTORE_FROM_SD);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

}
