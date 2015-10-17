package net.takoli.simpleruntracker.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import net.takoli.simpleruntracker.RunApp;
import net.takoli.simpleruntracker.model.RunDB;
import net.takoli.simpleruntracker.view.MainActivity;

public class ConfirmDeleteDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
        		.setTitle("Confirm delete")
        		.setMessage("This will remove all run records and cannot be undone.")
                .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            return; }
                    }
                )
                .setPositiveButton("Delete",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            final MainActivity main = (MainActivity) getActivity();
                            final RunDB runDB = ((RunApp) main.getApplication()).getRunDB();
                            runDB.deleteDB(getActivity());
                            main.getRunAdapter().notifyDataSetChanged();
                            runDB.saveToExternalMemory(getActivity());
                            main.updateGraph();
                			Toast.makeText(main, "Backed up in the Downloads folder",Toast.LENGTH_SHORT).show(); }
                    }
                ).create();
    }
}