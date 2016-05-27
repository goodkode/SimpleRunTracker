package net.takoli.simpleruntracker.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.view.MainActivity;

public class ConfirmDeleteDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
        		.setTitle(getResources().getString(R.string.confirm_delete))
        		.setMessage(getResources().getString(R.string.delete_message))
                .setNegativeButton(getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return; }
                    } )
                .setPositiveButton(getResources().getString(R.string.delete),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            final MainActivity main = (MainActivity) getActivity();
                            main.tryStorageTask(MainActivity.WRITE_TO_SD_DELETE_LOCAL);
                        }
                    } )
                .create();
    }
}