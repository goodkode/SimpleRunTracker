package net.takoli.simpleruntracker.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.RadioButton;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.RunApp;
import net.takoli.simpleruntracker.model.RunDB;

public class StatsFragment extends Fragment {

	private static final String KM = "km";
	private static final String MI = "mi";
	private static final String MIN_PER_MI = "min/mi";
	private static final String MPH = "mph";
	private static final String MIN_PER_KM = "min/km";
	private static final String KMPH = "km/h";

	private RunApp app;
	private boolean active = true;
	private View thisView, leftOverlay;
	private RunDB runDB;
	private String unit;
	private RadioButton mileChB, kmChB;
	private TextView statPeriod, distAvg, distMax, distTotal, paceSpeedAvg, paceSpeedMax, dailyAvg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		active = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		app = (RunApp) getActivity().getApplication();
		runDB = app.getRunDB();
		thisView = inflater.inflate(R.layout.stats_layout, container, false);
		return thisView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		View statsPanel = getActivity().findViewById(R.id.stats_panel);
		View leftPanel = getActivity().findViewById(R.id.stats_left);
		int width = getResources().getDisplayMetrics().widthPixels;
		OnTouchListener touchToClose = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				animateOut();
				return true;
			} };
		thisView.setX(width); 
		leftPanel.setAlpha(0);
		statsPanel.setOnTouchListener(touchToClose);
		leftPanel.setOnTouchListener(touchToClose);
	}

	@Override
	public void onResume() {
		super.onResume();
		leftOverlay = getActivity().findViewById(R.id.stats_left);
		statPeriod = (TextView) getActivity().findViewById(R.id.stats_for_period);
		distAvg = (TextView) getActivity().findViewById(R.id.stats_distance_avg);
		distMax = (TextView) getActivity().findViewById(R.id.stats_distance_max);
		distTotal = (TextView) getActivity().findViewById(R.id.stats_distance_total);
		paceSpeedAvg = (TextView) getActivity().findViewById(R.id.stats_pace_speed_avg);
		paceSpeedMax = (TextView) getActivity().findViewById(R.id.stats_pace_speed_max);
		dailyAvg = (TextView) getActivity().findViewById(R.id.stats_daily_avg_portrait_only);
		mileChB = (RadioButton) getActivity().findViewById(R.id.stats_mi);
		kmChB = (RadioButton) getActivity().findViewById(R.id.stats_km);
		mileChB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) 
					onStatsInMi(); } });
		kmChB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) 
					onStatsInKm(); } });
		unit = app.settingsManager.getUnit();
		if (unit.compareTo(KM) == 0)
			kmChB.setChecked(true);
		else 							
			mileChB.setChecked(true);
		if (active)	
			animateIn();
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void animateOut() {
		active = false;
		leftOverlay.animate().alpha(0f).setDuration(100);		
		int width = getResources().getDisplayMetrics().widthPixels;
		thisView.animate().translationX(width).setDuration(700);
	}
	
	public void animateIn() {
		active = true;
		thisView.animate().translationX(0).setDuration(700);
		leftOverlay.animate().alpha(0.5f).setDuration(2000).setInterpolator(new AccelerateInterpolator());
		if (kmChB.isChecked())
			onStatsInKm();
		else if (mileChB.isChecked())
			onStatsInMi();
	}
	
	public void onStatsInMi() {
		kmChB.setChecked(false);
		if (noStats())
			return;
		statPeriod.setText(String.format(getResources().getString(R.string.stats_since),
				runDB.getRunList().get(0).getDateString(app),
				runDB.getRunList().size()));
		distAvg.setText(String.format(getResources().getString(R.string.stats_avg_distance),
				runDB.getAvgDistString(MI), MI));
		distMax.setText(String.format(getResources().getString(R.string.stats_longest_distance),
				runDB.getMaxDistString(MI), MI));
		distTotal.setText(String.format(getResources().getString(R.string.stats_total_distance),
				runDB.getTotalDistString(MI), MI));
		paceSpeedAvg.setText(String.format(getResources().getString(R.string.stats_avg_pace_and_speed),
                runDB.getAvgPaceString(MI), MIN_PER_MI,
				runDB.getAvgSpeedString(MI), MPH));
        paceSpeedMax.setText(String.format(getResources().getString(R.string.stats_max_pace_and_speed),
                runDB.getMaxPaceString(MI), MIN_PER_MI,
                runDB.getMaxSpeedString(MI), MPH));
		if (dailyAvg != null)
			dailyAvg.setText(String.format(getResources().getString(R.string.stats_average_overall),
                    runDB.getWeeklyAvgString(MI),
                    runDB.getDailyAvgString(MI),
                    runDB.getRunFrequencyString()));
	}
	public void onStatsInKm() {
		mileChB.setChecked(false);
		if (noStats())
			return;
		statPeriod.setText(String.format(getResources().getString(R.string.stats_since),
				runDB.getRunList().get(0).getDateString(app),
				runDB.getRunList().size()));
		distAvg.setText(String.format(getResources().getString(R.string.stats_avg_distance),
				runDB.getAvgDistString(KM), KM));
		distMax.setText(String.format(getResources().getString(R.string.stats_longest_distance),
				runDB.getMaxDistString(KM), KM));
		distTotal.setText(String.format(getResources().getString(R.string.stats_total_distance),
				runDB.getTotalDistString(KM), KM));
        paceSpeedAvg.setText(String.format(getResources().getString(R.string.stats_avg_pace_and_speed),
                runDB.getAvgPaceString(KM), MIN_PER_KM,
                runDB.getAvgSpeedString(KM), KMPH));
        paceSpeedMax.setText(String.format(getResources().getString(R.string.stats_max_pace_and_speed),
                runDB.getMaxPaceString(KM), MIN_PER_KM,
                runDB.getMaxSpeedString(KM), KMPH));
		if (dailyAvg != null)
            dailyAvg.setText(String.format(getResources().getString(R.string.stats_average_overall),
                    runDB.getWeeklyAvgString(KM),
                    runDB.getDailyAvgString(KM),
                    runDB.getRunFrequencyString()));
	}
	
	public boolean noStats() {
		if (runDB.isEmpty()) {
			statPeriod.setText(getResources().getString(R.string.stats_not_yet));
            distAvg.setText(String.format(getResources().getString(R.string.stats_avg_distance), "-", ""));
            distMax.setText(String.format(getResources().getString(R.string.stats_longest_distance), "-", ""));
            distTotal.setText(String.format(getResources().getString(R.string.stats_total_distance), "-", ""));
            paceSpeedAvg.setText(String.format(getResources().getString(R.string.stats_avg_distance), "-", ""));
            paceSpeedMax.setText(null);
			return true;
		}
		else
			return false;
	}
}
