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

public class ChartFullScreenDialog extends DialogFragment {
	
	private MainActivity main;
	private AlertDialog chartFullScreenView;
	private RunDB runDB;
	private String unit;

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
    	DisplayMetrics dm = getResources().getDisplayMetrics();
    	int width = (int) (dm.widthPixels * 0.98);
    	int height = (int) (dm.heightPixels * 0.4);
    	Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Configuration.ORIENTATION_PORTRAIT)
        	height = (int) (height * 1.8);
    	getDialog().getWindow().setLayout(width, height);
    	GraphView v = (GraphView) chartFullScreenView.findViewById(R.id.chart_full_screen);
    	MainActivity main = (MainActivity) getActivity();
    	v.setRunList(main.getRunDB(), main.getUnit());
    }
}