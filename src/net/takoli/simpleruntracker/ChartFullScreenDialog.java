package net.takoli.simpleruntracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ChartFullScreenDialog extends DialogFragment {
	
	private MainActivity main;
	private AlertDialog chartFullScreenView;
	private RunDB runDB;
	private String unit;
	private TextView currentNumber;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	main = (MainActivity) getActivity();
        chartFullScreenView =  new AlertDialog.Builder(getActivity())
        		.setView(getActivity().getLayoutInflater().inflate(R.layout.chart_full_screen_dialog, null))
        		.setTitle("")
                .setPositiveButton("Done",null)
                .create();
        return chartFullScreenView;
	}
    
    @Override
    public void onResume() {
    	super.onResume();
    	// set sizes based on orientation
    	DisplayMetrics dm = getResources().getDisplayMetrics();
    	int width = (int) (dm.widthPixels * 0.98);
    	int height = (int) (dm.heightPixels * 0.4);
    	Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Configuration.ORIENTATION_PORTRAIT)
        	height = (int) (height * 1.8);
    	getDialog().getWindow().setLayout(width, height);
    	// get resources and listeners
    	currentNumber = (TextView) findViewById(R.id.chart_run_number);
    	final GraphViewFull graph = (GraphViewFull) chartFullScreenView.findViewById(R.id.chart_full_screen);
    	SeekBar seekbar = (SeekBar) chartFullScreenView.findViewById(R.id.seekBar);
    	seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int n = progress / 3 + 7
				Log.i("run", "progress: " + n);
				showCurrentNumber(n);
				graph.updateData(n);
				graph.invalidate();
			}
		});
    	MainActivity main = (MainActivity) getActivity();
    	graph.setRunList(main.getRunDB(), main.getUnit());
    }
    
    private void showCurrentNumber(int n) {
    	if (currentNumber == null)
    	    return;
    	currentNumber.setText("" + n);
    	currentNumber.setVisibility(VISIBLE);
    	currentNumber.animate().setAlpha(0).setDuration(700);
    }
}
