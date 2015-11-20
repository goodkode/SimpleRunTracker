package net.takoli.simpleruntracker.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import net.takoli.simpleruntracker.view.MainActivity;

public class ConfirmDeleteDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
        		.setTitle("Confirm delete")
        		.setMessage("This will remove all run records and cannot be undone." +
                        "\n\nYou might need to grant permission to safely back up your current list in the Download folder")
                .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return; }
                    } )
                .setPositiveButton("Delete",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            final MainActivity main = (MainActivity) getActivity();
                            main.tryStorageTask(MainActivity.WRITE_TO_SD_DELETE_LOCAL);
                        }
                    } )
                .create();
    }
}