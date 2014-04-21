package net.takoli.simpleruntracker;

import java.util.Calendar;
import java.util.Locale;

import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class EnterRun extends Fragment {

	MyNumberPicker dist10, dist1, dist_1, dist_01;
	MyNumberPicker hour, min10, min1, sec10, sec1;
	RadioButton today, yesterday, date;
	RadioGroup dateGroup;
	Calendar runDate;
	TextView div_d, div_th, div_tm;
	TextView distance, time;
	Button enterRunButton;
	DisplayMetrics dm;
	RunDB runListDB;
	RunAdapter runAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		runListDB = ((MainActivity) getActivity()).getRunDB();
		runAdapter = ((MainActivity) getActivity()).getRunAdapter();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.enter_run, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		dm = getResources().getDisplayMetrics();
		
		// Set up DISTANCE fields
		dist10 = (MyNumberPicker) getView().findViewById(R.id.dist10);
		dist1 = (MyNumberPicker) getView().findViewById(R.id.dist1);
		div_d = ((TextView) getView().findViewById(R.id.div_d));
			div_d.setTextSize(dist1.getTextSize());
		dist_1 = (MyNumberPicker) getView().findViewById(R.id.dist_1);
		dist_01 = (MyNumberPicker) getView().findViewById(R.id.dist_01);
		
		// Set up TIME fields
		hour = (MyNumberPicker) getView().findViewById(R.id.hour);
		div_th = ((TextView) getView().findViewById(R.id.div_th));
			div_th.setTextSize(hour.getTextSize());
		min10 = (MyNumberPicker) getView().findViewById(R.id.min10);
			min10.setMaxValue(5);
		min1 = (MyNumberPicker) getView().findViewById(R.id.min1);
		div_tm = ((TextView) getView().findViewById(R.id.div_tm));
			div_tm.setTextSize(hour.getTextSize());
		sec10 = (MyNumberPicker) getView().findViewById(R.id.sec10);
			sec10.setMaxValue(5);
		sec1 = (MyNumberPicker) getView().findViewById(R.id.sec1);
		
		// "Distance" and "Time" 
		distance = (VerticalTextView) getActivity().findViewById(R.id.distance);
		distance.setTextColor(0xaaFF0000);
		distance.setTextSize(dist1.getTextSize());
		time = (VerticalTextView) getActivity().findViewById(R.id.time);
		time.setTextColor(0xaaFF0000);
		time.setTextSize(dist1.getTextSize());
		
		// Datepicker 
		runDate = Calendar.getInstance(Locale.US);
		dateGroup = (RadioGroup) getActivity().findViewById(R.id.date_radiobuttons);
		dateGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.date_today:
					runDate = Calendar.getInstance();
					break;
				case R.id.date_yesterday:
					runDate = Calendar.getInstance();
					runDate.roll(Calendar.DAY_OF_YEAR, -1);
					break;
				default:
					runDate = EnterRun.this.pickDate();
					break;
				}
			}});
		
		// Send run details to the RunDB database
		enterRunButton = (Button) getActivity().findViewById(R.id.enter_run_button);
		enterRunButton.setTextSize(dist1.getTextSize()*2/3);
		enterRunButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String unit = "m";  // later get it from SharedPreferences
				int dd = dist10.getValue() * 10 + dist1.getValue();
				int _dd = dist_1.getValue() * 10 + dist_01.getValue();
				int h = hour.getValue();
				int mm = min10.getValue() * 10 + min1.getValue();
				int ss = sec10.getValue() * 10 + sec1.getValue();
				// save it to runDB and update the ListView
				runListDB.addNewRun(getActivity(), new Run(runDate, dd, _dd, unit, h, mm, ss));
				runAdapter.notifyDataSetChanged();
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	public Calendar pickDate() {
		return Calendar.getInstance();
	}
}
