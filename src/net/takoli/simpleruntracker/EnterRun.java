package net.takoli.simpleruntracker;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.simpleruntracker.R;

public class EnterRun extends Fragment {

	ViewGroup vg;
	DistNumberPicker dist10, dist1, dist_1, dist_01;
	TimeNumberPicker hour, min10, min1, sec10, sec1;
	TextView div_d, div_th, div_tm;
	TextView distance, time;
	DisplayMetrics dm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.enter_run, container, false);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		dm = getResources().getDisplayMetrics();
		
		// Set up DISTANCE fields
		dist10 = (DistNumberPicker) getView().findViewById(R.id.dist10);
		dist1 = (DistNumberPicker) getView().findViewById(R.id.dist1);
		div_d = ((TextView) getView().findViewById(R.id.div_d));
			div_d.setTextSize(dist1.getTextSize());
		dist_1 = (DistNumberPicker) getView().findViewById(R.id.dist_1);
		dist_01 = (DistNumberPicker) getView().findViewById(R.id.dist_01);
		
		// Set up TIME fields
		hour = (TimeNumberPicker) getView().findViewById(R.id.hour);
		div_th = ((TextView) getView().findViewById(R.id.div_th));
			div_th.setTextSize(hour.getTextSize());
		min10 = (TimeNumberPicker) getView().findViewById(R.id.min10);
			min10.setMaxValue(5);
		min1 = (TimeNumberPicker) getView().findViewById(R.id.min1);
		div_tm = ((TextView) getView().findViewById(R.id.div_tm));
			div_tm.setTextSize(hour.getTextSize());
		sec10 = (TimeNumberPicker) getView().findViewById(R.id.sec10);
			sec10.setMaxValue(5);
		sec1 = (TimeNumberPicker) getView().findViewById(R.id.sec1);
		
		// Add "Distance" and "Time" on top
		distance = new TextView(getActivity());
		distance.setText("Distance");
		distance.setTextColor(0xaaFF0000);
		distance.setTextSize(dist1.getTextSize());
		time = new TextView(getActivity());
		time.setText("Time");
		time.setTextColor(0xaaFF0000);
		time.setTextSize(dist1.getTextSize());
		// OPTION 1: labels on top  (need to make changes in "enter_run.xml" file, too)
//		vg = (ViewGroup) getActivity().findViewById(R.id.top);
//		vg.addView(distance);
//		vg.addView(time);
//		getView().post(new Runnable() {
//			@Override
//			public void run() {
//				int[] pos = new int[2];
//				int w_d, w_t, h_d, h_t;
//				distance.measure(0, 0);
//				time.measure(0, 0);
//				w_d = distance.getMeasuredWidth();
//				w_t = time.getMeasuredWidth();
//				h_d = distance.getMeasuredHeight();
//				h_t = time.getMeasuredHeight();
//				div_d.getLocationInWindow(pos);
//				distance.setX(pos[0]-w_d/2);
//				distance.setY(dm.heightPixels/dm.density/10);
//				min1.getLocationInWindow(pos);
//				time.setX(pos[0]-w_t/2);
//				time.setY(dm.heightPixels/dm.density/10);
//			}
//		});
		// OPTION 2: labels on side  (need to make changes in "enter_run.xml" file, too)
		vg = (ViewGroup) getActivity().findViewById(R.id.enter_run_frame);
		vg.addView(distance);
		vg.addView(time);
		getView().post(new Runnable() {
			@Override
			public void run() {
				int[] pos = new int[2];
				int w_d, w_t, h;
				distance.animate().rotation(-90);
				distance.measure(0, 0);
				time.measure(0, 0);
				w_d = distance.getMeasuredHeight();
				w_t = time.getMeasuredWidth();
				dist10.measure(0, 0);
				dist10.getLocationInWindow(pos);
				h = dist10.getMeasuredHeight();
				distance.setX(dm.widthPixels/dm.density/15);
				distance.setY(pos[1] - h/2 - w_d/2);
				Log.i("run", "length: "+w_d+", pos: "+pos[1]);
			}
		});
		
	}
	
	

}
