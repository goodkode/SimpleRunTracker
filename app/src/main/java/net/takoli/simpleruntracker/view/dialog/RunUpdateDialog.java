package net.takoli.simpleruntracker.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.RunApp;
import net.takoli.simpleruntracker.model.Run;
import net.takoli.simpleruntracker.model.RunDB;
import net.takoli.simpleruntracker.view.MainActivity;

public class RunUpdateDialog extends DialogFragment {

    public static final String POSITION = "position";
    private MainActivity main;
    private RunDB runDB;
    private AlertDialog runUpdateDialog;
    private int position;
    private Run run;
    private EditText dd;
    private EditText _dd;
    private EditText h;
    private EditText mm;
    private EditText ss;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
        main = (MainActivity) getActivity();
		position = getArguments().getInt(POSITION);
        runDB = ((RunApp) main.getApplication()).getRunDB();
		run = runDB.getRunList().get(position);
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	View view = getActivity().getLayoutInflater().inflate(R.layout.update_run, null);
    	runUpdateDialog = new AlertDialog.Builder(getActivity())
    			.setView(view)
                .setTitle("Update details of " + run.getDateString() + "'s run")
                .setPositiveButton("Delete Run", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	removeRun(position);
                        	return; }
                    }
                )
                .setNeutralButton("Update Run", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                updateRun(position);
                                return;
                            }
                        }
                )
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                return;
                            }
                        }
                ).create();
        return runUpdateDialog;
    }

	@Override
    public void onStart() {
    	super.onStart();
      	dd = (EditText) runUpdateDialog.findViewById(R.id.update_distance);
      	_dd = (EditText) runUpdateDialog.findViewById(R.id.update_distance_dec);
      	h = (EditText) runUpdateDialog.findViewById(R.id.update_time_h);
      	mm = (EditText) runUpdateDialog.findViewById(R.id.update_time_mm);
      	ss = (EditText) runUpdateDialog.findViewById(R.id.update_time_ss);
    	dd.setText(String.valueOf(run.dd < 10 ? "0" + run.dd : run.dd));
    	_dd.setText(String.valueOf(run._dd < 10 ? "0" + run._dd : run._dd));
    	h.setText(String.valueOf(run.h));
    	mm.setText(String.valueOf(run.mm < 10 ? "0" + run.mm : run.mm));
    	ss.setText(String.valueOf(run.ss < 10 ? "0" + run.ss : run.ss));
    	dd.setSelection(2);
    	_dd.setSelection(2);
    	h.setSelection(1);
    	mm.setSelection(2);
    	ss.setSelection(2);
    	dd.addTextChangedListener(new DigitTextWatcher(dd, 2));
        _dd.addTextChangedListener(new DigitTextWatcher(_dd, 2));
        h.addTextChangedListener(new DigitTextWatcher(h, 1));
        mm.addTextChangedListener(new DigitTextWatcher(mm, 2));
        ss.addTextChangedListener(new DigitTextWatcher(ss, 2));
    }
    
    private void updateRun(int position) {
        runDB.updateRun(position, new int[]{toInt(dd), toInt(_dd), toInt(h), toInt(mm), toInt(ss)});
        int adapterPosition = position + 1;
    	main.getRunAdapter().notifyItemChanged(adapterPosition);
        main.updateGraph();
    }
    
    private void removeRun(int position) {
    	runDB.removeRun(position);
        int adapterPosition = position + 1;
        main.getRunAdapter().notifyItemRemovedHelper();
        main.getRunAdapter().notifyItemRemoved(adapterPosition);
        main.updateGraph();
    }

    private static int toInt(EditText et) {
        return Integer.parseInt(et.getText().toString());
    }


    private static class DigitTextWatcher implements TextWatcher {

        private EditText et;
        private int len;

        public DigitTextWatcher(EditText et, int len) {
            this.et = et;
            this.len = len;
        }

        @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable s) {
            int length = s.length();
            if (len == 1) {
                if (length < len)
                    s.replace(0, 0, "0");
                if (length > len)
                    s.delete(0, length - 1);
            } else if (len == 2) {
                if (length < len)
                    s.replace(0, 0, "00");
                if (length > len)
                    s.delete(0, length - 2);
            }
            et.setSelection(len);
        }
    }

}
