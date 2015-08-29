package net.takoli.simpleruntracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import net.takoli.simpleruntracker.graph.GraphViewFull;

public class ChartFullScreenDialog extends DialogFragment {
	
	private MainActivity main;
	private AlertDialog chartFullScreenView;
	private RunDB runDB;
	private String unit;
	private GraphViewFull graph;
	private int height;
	private int width;
	private TextView currentNumber, needMoreData;

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
    	width = (int) (dm.widthPixels * 0.98);
    	height = (int) (dm.heightPixels * 0.4);
    	Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_0)
        	height = (int) (height * 1.8);
    	getDialog().getWindow().setLayout(width, height);
    	// get resources and listeners
    	MainActivity main = (MainActivity) getActivity();
    	int listSize = main.getRunDB().getRunList().size();
    	if (listSize < GraphViewFull.MIN_PLOTS) {
    		needMoreData = (TextView) chartFullScreenView.findViewById(R.id.chart_avg_label);
    		needMoreData.setTextSize(width / 50);
    		needMoreData.setTextColor(0xAA000000);
    		needMoreData.setText("graph will show after\n3+ workouts");
    		needMoreData.setVisibility(View.VISIBLE);
    		chartFullScreenView.findViewById(R.id.seekBar).setVisibility(View.GONE);
    		return;		// not enough data
    	}
    	graph = (GraphViewFull) chartFullScreenView.findViewById(R.id.chart_full_screen);
    	graph.setRunList(main.getRunDB(), main.getUnit());
    	int listStart = listSize < GraphViewFull.START_PLOTS ? listSize : GraphViewFull.START_PLOTS;
    	int listMax = graph.getMaxPlots();
    	graph.updateData(listStart);
		graph.invalidate();
	    SeekBar seekbar = (SeekBar) chartFullScreenView.findViewById(R.id.seekBar);
    	seekbar.setMax(listMax - GraphViewFull.MIN_PLOTS);
    	seekbar.setProgress(listStart - GraphViewFull.MIN_PLOTS);
    	showCurrentNumber(listStart);
    	seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				int n = GraphViewFull.MIN_PLOTS + progress;
				showCurrentNumber(n);
				graph.updateData(n);
				graph.invalidate();
			}
		});
    }
    
    private void showCurrentNumber(int n) {
    	currentNumber = (TextView) chartFullScreenView.findViewById(R.id.chart_run_number);
    	if (currentNumber == null)
    	    return;
    	currentNumber.setTextSize(width / 55);
    	currentNumber.setText("last " + n + " workouts");
    	currentNumber.setVisibility(View.VISIBLE);
    	currentNumber.setAlpha(1);
    	currentNumber.animate().alpha(0).setDuration(1000);
    }
}
