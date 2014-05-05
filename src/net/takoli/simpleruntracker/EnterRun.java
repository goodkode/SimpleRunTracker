package net.takoli.simpleruntracker;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EnterRun extends Fragment {

	MyNumberPicker dist10, dist1, dist_1, dist_01;
	MyNumberPicker hour, min10, min1, sec10, sec1;
	RadioGroup dateGroup;
	RadioButton dateRadioButton;
	Calendar runDate;
	TextView div_d, div_th, div_tm;
	TextView distance, time;
	Button enterRunButton;
	RelativeLayout dividerLine;
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
		
		// Show last run values at start
		int[] lastValues = runListDB.getLastValues();
		dist10.setValue(lastValues[0] / 10);
		dist1.setValue(lastValues[0] % 10);
		dist_1.setValue(lastValues[1] / 10);
		dist_01.setValue(lastValues[1] % 10);
		hour.setValue(lastValues[2]);
		min10.setValue(lastValues[3] / 10);
		min1.setValue(lastValues[3] % 10);
		sec10.setValue(lastValues[4] / 10);
		sec1.setValue(lastValues[4] % 10);
		
		// "Distance" and "Time"; divider line width
		distance = (VerticalTextView) getActivity().findViewById(R.id.distance);
		distance.setTextColor(0xaaFF0000);
		distance.setTextSize(dist1.getTextSize());
		time = (VerticalTextView) getActivity().findViewById(R.id.time);
		time.setTextColor(0xaaFF0000);
		time.setTextSize(dist1.getTextSize());
		dividerLine = (RelativeLayout) getActivity().findViewById(R.id.divider_line);
		dividerLine.setPadding(dm.widthPixels / 5, 0, dm.widthPixels / 5, 0);
		
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
					runDate = Calendar.getInstance();
					break;
				}
			}});
		dateRadioButton = (RadioButton) getActivity().findViewById(R.id.date_picker);
		dateRadioButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EnterRun.this.pickDate(); }
		});
		
		// Send run details to the RunDB database
		enterRunButton = (Button) getActivity().findViewById(R.id.enter_run_button);
		enterRunButton.setTextSize(dist1.getTextSize()*2/3);
		enterRunButton.setWidth(dm.widthPixels / 3);
		enterRunButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String unit = ((MainActivity) getActivity()).getUnit();
				int dd = dist10.getValue() * 10 + dist1.getValue();
				int _dd = dist_1.getValue() * 10 + dist_01.getValue();
				int h = hour.getValue();
				int mm = min10.getValue() * 10 + min1.getValue();
				int ss = sec10.getValue() * 10 + sec1.getValue();
				// save it to runDB and update the ListView
				if ((dd + _dd) == 0 || (h + mm + ss) == 0)
					return; // not valid run
				runListDB.addNewRun(getActivity(), new Run(runDate, dd, _dd, unit, h, mm, ss));
				runListDB.updateAndSaveRunDB(getActivity());
				runAdapter.notifyDataSetChanged();
				ListView myListView = (ListView) getActivity().findViewById(R.id.my_runs);
				myListView.setSelection(runAdapter.getCount() - 1);
				(new AfterRunPopUp()).show(getFragmentManager(), "AfterRunPopup");
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	// this and the DatePickerFragment support picking a date
	public void pickDate() {
		DialogFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.show(getFragmentManager(), "datePicker");
	}
	
	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			DatePickerDialog datePickerDialog =  new DatePickerDialog(getActivity(), this, year, month, day);
			// Change all dividers to red
			DatePicker datePicker = datePickerDialog.getDatePicker();
			datePicker.setMaxDate(c.getTimeInMillis());
			datePickerDialog.setTitle("");
			try {
				Field datePickerField = DatePicker.class.getDeclaredField("mDaySpinner");
				datePickerField.setAccessible(true);
				NumberPicker np = (NumberPicker) datePickerField.get(datePicker);
				try {
					Field numberPickerField = NumberPicker.class.getDeclaredField("mSelectionDivider");
					numberPickerField.setAccessible(true);
					numberPickerField.set(np, getResources().getDrawable(R.drawable.div));
				} catch (Exception e) { e.printStackTrace(); }
			} catch (Exception e) { e.printStackTrace(); }
			try {
				Field datePickerField = DatePicker.class.getDeclaredField("mMonthSpinner");
				datePickerField.setAccessible(true);
				NumberPicker np = (NumberPicker) datePickerField.get(datePicker);
				try {
					Field numberPickerField = NumberPicker.class.getDeclaredField("mSelectionDivider");
					numberPickerField.setAccessible(true);
					numberPickerField.set(np, getResources().getDrawable(R.drawable.div));
				} catch (Exception e) { e.printStackTrace(); }
			} catch (Exception e) { e.printStackTrace(); }
			try {
				Field datePickerField = DatePicker.class.getDeclaredField("mYearSpinner");
				datePickerField.setAccessible(true);
				NumberPicker np = (NumberPicker) datePickerField.get(datePicker);
				try {
					Field numberPickerField = NumberPicker.class.getDeclaredField("mSelectionDivider");
					numberPickerField.setAccessible(true);
					numberPickerField.set(np, getResources().getDrawable(R.drawable.div));
				} catch (Exception e) { e.printStackTrace(); }
			} catch (Exception e) { e.printStackTrace(); }
			return datePickerDialog;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			MainActivity mainActivity = (MainActivity) getActivity();
			EnterRun enterRun = (EnterRun) mainActivity.enterRun;
			enterRun.runDate.set(year, month, day);
			String m;
			switch (month) {
				case 0: m = "Jan"; break;
				case 1: m = "Feb"; break;
				case 2: m = "Mar"; break;
				case 3: m = "Apr"; break;
				case 4: m = "May"; break;
				case 5: m = "Jun"; break;
				case 6: m = "Jul"; break;
				case 7: m = "Aug"; break;
				case 8: m = "Sep"; break;
				case 9: m = "Oct"; break;
				case 10: m = "Nov"; break;
				case 11: m = "Dec"; break;
				default: m = ""; break; }
			String y = year%100 < 10 ? ("'0"+year%100) : ("'"+year%100);
			enterRun.dateRadioButton.setText(m+" "+day+", "+y);
		}
	}
}


