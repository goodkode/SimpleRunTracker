package net.takoli.simpleruntracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import net.takoli.simpleruntracker.graph.GraphViewFull;

public class ChartFullScreenDialog extends DialogFragment {
	
	private MainActivity main;
	private AlertDialog chartFullScreenView;
	private GraphViewFull graph;
	private int height;
	private int width;
	private TextView currentNumber;
	private TextView needMoreData;
    private ViewGroup view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	main = (MainActivity) getActivity();
        view = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.chart_full_screen_dialog, null);
        chartFullScreenView =  new AlertDialog.Builder(getActivity())
        		.setView(view)
        		.setTitle("")
                .create();
        return chartFullScreenView;
	}

    @Override
    public void onStart() {
        super.onStart();
        width = main.settingsManager.getScreenWidth() * 84 / 100;
        height = main.settingsManager.getScreenHeight() * 94 / 100;
        Log.i("run", "Graph: " + width + " * " + height);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onResume() {
    	super.onResume();
        view.setLayoutParams(new FrameLayout.LayoutParams(height * 95 / 100, width * 90 / 100, Gravity.CENTER));
        view.requestLayout();

    	// get resources and listeners
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
    	graph.setRunList(main.getRunDB(), main.settingsManager.getUnit());
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
