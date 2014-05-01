package net.takoli.simpleruntracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class RunUpdateDialog extends DialogFragment {
	
	String date, dd, _dd, h, mm, ss;
	Run run;
	
	public RunUpdateDialog() { }
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		date = getArguments().getString("date");
		dd = getArguments().getString("dd");
		_dd = getArguments().getString("_dd");
		h = getArguments().getString("h");
		mm = getArguments().getString("mm");
		ss = getArguments().getString("ss");
	}

    //@Override
    //public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //        Bundle bundle) {
    //    View view = inflater.inflate(R.layout.update_run_dialog, container);        
    //    getDialog().setTitle("Update " + date  + "'s run's details");
    //    EditText updateDistance = (EditText) view.findViewById(R.id.update_distance);
    //    EditText updateTime = (EditText) view.findViewById(R.id.update_time);
    //    updateDistance.setText(distance);
    //    updateTime.setText(time);
    //    return view;
    //}
    
    @Override
    pubic Dialog onCreateDialog(Bundle savedInstanceState) {

        DialogFragment runUpdateFrag new AlertDialog.Builder(getActivity())
                //.setIcon(R.drawable.alert_dialog_icon)
                .setTitle("Update " + date  + "'s run's details")
                .setPositiveButton("Update",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //((FragmentAlertDialog)getActivity()).updateClick();
                        }
                    }
                )
                .setNeutralButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //((FragmentAlertDialog)getActivity()).cancelClick();
                        }
                    }
                )
                .setNegativeButton("Delete",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //((FragmentAlertDialog)getActivity()).deleteClick();
                        }
                    }
                )
                .create();
  //       Button okButton = runUpdateFrag.getButton(DialogInterface.BUTTON_POSITIVE);
	 //okButton.setTextColor(0xFF0000);  // change text color to red
         return runUpdateFrag;
    }

}
