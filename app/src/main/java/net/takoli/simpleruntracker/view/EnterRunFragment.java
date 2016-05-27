package net.takoli.simpleruntracker.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.RunApp;
import net.takoli.simpleruntracker.adapter.RunAdapter;
import net.takoli.simpleruntracker.model.Run;
import net.takoli.simpleruntracker.model.RunDB;
import net.takoli.simpleruntracker.model.SettingsManager;
import net.takoli.simpleruntracker.view.widget.BigNumberPicker;
import net.takoli.simpleruntracker.view.widget.VerticalTextView;

import java.lang.reflect.Field;
import java.util.Calendar;


public class EnterRunFragment extends Fragment {

	private RunApp app;
	private BigNumberPicker dist10, dist1, dist_1, dist_01;
	private BigNumberPicker hour, min10, min1, sec10, sec1;
	private RadioGroup dateGroup;
	private RadioButton dateRadioButton;
	private Calendar runDate;
	private TextView div_d, div_th, div_tm;
	private TextView distance, time, distUnit;
	private Button enterRunButton;
	private long lastHitTime;
	private DisplayMetrics dm;
	private MainActivity main;
	private RunDB runListDB;
	private RecyclerView runListView;
	private RunAdapter runAdapter;
	private Animation shake;
	private Animation blowup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (RunApp) getActivity().getApplication();
		main = (MainActivity) getActivity();
        shake = AnimationUtils.loadAnimation(main, R.anim.shake);
        blowup = AnimationUtils.loadAnimation(main, R.anim.blowup);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.enter_run, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();

        runListDB = app.getRunDB();
        runListView = main.getRunList();
        runAdapter = main.getRunAdapter();
		dm = getResources().getDisplayMetrics();
		
		// Set up DISTANCE fields
		dist10 = (BigNumberPicker) getView().findViewById(R.id.dist10);
		dist1 = (BigNumberPicker) getView().findViewById(R.id.dist1);
		div_d = ((TextView) getView().findViewById(R.id.div_d));
			div_d.setTextSize(dist1.getTextSize());
		dist_1 = (BigNumberPicker) getView().findViewById(R.id.dist_1);
		dist_01 = (BigNumberPicker) getView().findViewById(R.id.dist_01);
		distUnit = (TextView) getView().findViewById(R.id.dist_unit);
			distUnit.setText(app.settingsManager.getUnit());
			distUnit.setTextSize(dist1.getTextSize() * 0.8f);
			distUnit.animate().translationYBy(4);
			
		// Set up TIME fields
		hour = (BigNumberPicker) getView().findViewById(R.id.hour);
		div_th = ((TextView) getView().findViewById(R.id.div_th));
			div_th.setTextSize(hour.getTextSize());
		min10 = (BigNumberPicker) getView().findViewById(R.id.min10);
			min10.setMaxValue(5);
			min10.setWrapSelectorWheel(true);
		min1 = (BigNumberPicker) getView().findViewById(R.id.min1);
		div_tm = ((TextView) getView().findViewById(R.id.div_tm));
			div_tm.setTextSize(hour.getTextSize());
		sec10 = (BigNumberPicker) getView().findViewById(R.id.sec10);
			sec10.setMaxValue(5);
			sec10.setWrapSelectorWheel(true);
		sec1 = (BigNumberPicker) getView().findViewById(R.id.sec1);
		
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
		
		// Set up listeners that change the time pickers to the runner's average (expected) time
		OnScrollListener distScrollListener = new OnScrollListener() {
			int secPerMile = runListDB.getAvgPaceUNIT();
			int secs = 0;
			int dist = 0;
			boolean inMile = app.settingsManager.getUnit().compareTo("mi") == 0;
			@Override
			public void onScrollStateChange(NumberPicker view, int scrollState) {
				dist = dist10.getValue() * 1000 + dist1.getValue() * 100 + dist_1.getValue() * 10 + dist_01.getValue();
				if (inMile)
					secs = dist * secPerMile / 100;
				else
					secs = (int) (dist * secPerMile / 100 / 1.609);
				int h = secs / 60 / 60;
				int mins = (secs - h * 60 * 60) / 60;
				int sec = secs - h * 60 * 60 - mins * 60;
				hour.setValue(h % 10);
				min10.setValue(mins / 10 % 10);
				min1.setValue(mins % 10);
				sec10.setValue(sec / 10 % 10);
				sec1.setValue(sec % 10);
			}
		};
		dist10.setOnScrollListener(distScrollListener);
		dist1.setOnScrollListener(distScrollListener);
		dist_1.setOnScrollListener(distScrollListener);
		dist_01.setOnScrollListener(distScrollListener);
		
		// "Distance" and "Time"; divider line width
		distance = (VerticalTextView) getView().findViewById(R.id.distance);
		distance.setTextColor(0xaaFF0000);
		distance.setTextSize(dist1.getTextSize());
		time = (VerticalTextView) getView().findViewById(R.id.time);
		time.setTextColor(0xaaFF0000);
		time.setTextSize(dist1.getTextSize());
		
		// Datepicker 
		runDate = Run.setTodayDate();
		((RadioButton) getView().findViewById(R.id.date_today)).setTextSize(hour.getTextSize() / 1.6f);
		((RadioButton) getView().findViewById(R.id.date_yesterday)).setTextSize(hour.getTextSize() / 1.6f);
		((RadioButton) getView().findViewById(R.id.date_picker)).setTextSize(hour.getTextSize() / 1.6f);
		dateGroup = (RadioGroup) getView().findViewById(R.id.date_radiobuttons);
		dateGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.date_today:
					runDate = Run.setTodayDate();
					break;
				case R.id.date_yesterday:
					runDate = Run.setYesterdayDate();
					break;
				case R.id.date_picker:
					break;
				}
			}});
		((RadioButton) getView().findViewById(R.id.date_today)).setChecked(true);
		dateRadioButton = (RadioButton) getView().findViewById(R.id.date_picker);
		dateRadioButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EnterRunFragment.this.pickDate(); }
		});
		
		// Send run details to the RunDB database
		enterRunButton = (Button) getView().findViewById(R.id.enter_run_button);
		enterRunButton.setTextSize(dist1.getTextSize()*2/3);
		enterRunButton.setWidth(dm.widthPixels / 3);
		enterRunButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if (lastHitTime + 1500 > System.currentTimeMillis())
                    return;
                else
                    lastHitTime = System.currentTimeMillis();
				String unit = app.settingsManager.getUnit();
				int dd = dist10.getValue() * 10 + dist1.getValue();
				int _dd = dist_1.getValue() * 10 + dist_01.getValue();
				int h = hour.getValue();
				int mm = min10.getValue() * 10 + min1.getValue();
				int ss = sec10.getValue() * 10 + sec1.getValue();
				// save it to runDB and update the ListView
                if ((dd + _dd) == 0 || (h + mm + ss) == 0)  {
                    enterRunButton.startAnimation(shake);
				} else {
                    enterRunButton.startAnimation(blowup);
                    Run newRun = new Run(runDate, dd, _dd, unit, h, mm, ss);
					int lastAddedIndex = runListDB.addNewRun(main, newRun) + 1;
                    runAdapter.notifyItemAddedHelper(lastAddedIndex);
					runAdapter.notifyItemInserted(lastAddedIndex);
					main.shiftRunListAfterRun();
					runListView.smoothScrollToPosition(lastAddedIndex);
					main.updateGraph();
					// todo: off the UI thread
					runListDB.saveRunDB(main);
                    runListView.invalidate();

                    main.gTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Run!")
                            .setAction("run entered")
                            .build());
                    main.gTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("run stats")
                            .setAction("distance stats")
                            .setLabel("run distance in mile/100")
                            .setValue(newRun.getDistUNIT())
                            .build());
                    main.gTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("run stats")
                            .setAction("time stats")
                            .setLabel("run time in sec")
                            .setValue(newRun.getTimeUNIT())
                            .build());
                    main.gTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("run stats")
                            .setAction("pace stats")
                            .setLabel("run pace in sec/mile")
                            .setValue(newRun.getPaceUNIT())
                            .build());
                    //Log.i("new run", newRun.getDistUNIT() + ", " + newRun.getTimeUNIT() + ", " + newRun.getPaceUNIT());
				}
			}
		});
	}
	
	
	// this and the RedDatePickerFragment support picking a date
	public void pickDate() {
		DialogFragment datePickerFragment = new GreenDatePickerFragment();
		datePickerFragment.show(getFragmentManager(), "datePicker");
	}
	
	public static class GreenDatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

        private SettingsManager settingsManager;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final ColorDrawable greenDrawable = new ColorDrawable(getResources().getColor(R.color.green_light));
            settingsManager = ((RunApp) getActivity().getApplication()).settingsManager;
            int[] lastDatePickedOrDefault = settingsManager.getLastRunDatePicked();
			int year = lastDatePickedOrDefault[2];
			int month = lastDatePickedOrDefault[1];
			int day = lastDatePickedOrDefault[0];

			// Create a new instance of DatePickerDialog and return it
			DatePickerDialog datePickerDialog =  new DatePickerDialog(getActivity(), this, year, month, day);
			// Change all dividers to green
			DatePicker datePicker = datePickerDialog.getDatePicker();
			datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis() - 2 * 24 * 60 * 60 * 1000);
			datePickerDialog.setTitle(null);
			try {
				Field datePickerField = DatePicker.class.getDeclaredField("mDaySpinner");
				datePickerField.setAccessible(true);
				NumberPicker np = (NumberPicker) datePickerField.get(datePicker);
				try {
					Field numberPickerField = NumberPicker.class.getDeclaredField("mSelectionDivider");
					numberPickerField.setAccessible(true);
					numberPickerField.set(np, greenDrawable);
				} catch (Exception e) { }
			} catch (Exception e) { }
			try {
				Field datePickerField = DatePicker.class.getDeclaredField("mMonthSpinner");
				datePickerField.setAccessible(true);
				NumberPicker np = (NumberPicker) datePickerField.get(datePicker);
				try {
					Field numberPickerField = NumberPicker.class.getDeclaredField("mSelectionDivider");
					numberPickerField.setAccessible(true);
					numberPickerField.set(np, greenDrawable);
				} catch (Exception e) { }
			} catch (Exception e) { }
			try {
				Field datePickerField = DatePicker.class.getDeclaredField("mYearSpinner");
				datePickerField.setAccessible(true);
				NumberPicker np = (NumberPicker) datePickerField.get(datePicker);
				try {
					Field numberPickerField = NumberPicker.class.getDeclaredField("mSelectionDivider");
					numberPickerField.setAccessible(true);
					numberPickerField.set(np, greenDrawable);
				} catch (Exception e) { }
			} catch (Exception e) { }
			return datePickerDialog;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			EnterRunFragment enterRun = (EnterRunFragment) ((MainActivity) getActivity()).enterRun;
			enterRun.runDate = Run.setCustomDate(month, day, year);
			enterRun.dateRadioButton.setText(Run.getFullStringDate(year, month, day));
            settingsManager.setLastRunDatePicked(day, month, year);
		}
	}
}


