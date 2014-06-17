package net.takoli.simpleruntracker;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.RadioButton;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class StatsFragment extends Fragment {

	private boolean active;
	private MainActivity mainActivity;
	private View thisView, leftOverlay;
	private RunDB runDB;
	private String unit;
	private RadioButton mileChB, kmChB;
	private TextView statPeriod, distAvg, distMax, distTotal, paceSpeedAvg, paceSpeedMax;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainActivity = (MainActivity) getActivity();
		runDB = mainActivity.getRunDB();
		active = true;
		//Log.i("run", "created: " + this.hashCode() + ", active: " + active);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.stats_layout, container, false);
		return thisView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		int width = getResources().getDisplayMetrics().widthPixels;
		thisView.setX(width); 
		getActivity().findViewById(R.id.stats_left).setAlpha(0);
	}

	@Override
	public void onResume() {
		super.onResume();
		//Log.i("run", "onresume " + this.hashCode() + "; main: " + getActivity().hashCode());
		leftOverlay = getActivity().findViewById(R.id.stats_left);
		statPeriod = (TextView) getActivity().findViewById(R.id.stats_for_period);
		distAvg = (TextView) getActivity().findViewById(R.id.stats_distance_avg);
		distMax = (TextView) getActivity().findViewById(R.id.stats_distance_max);
		distTotal = (TextView) getActivity().findViewById(R.id.stats_distance_total);
		paceSpeedAvg = (TextView) getActivity().findViewById(R.id.stats_pace_speed_avg);
		paceSpeedMax = (TextView) getActivity().findViewById(R.id.stats_pace_speed_max);
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
		unit = ((MainActivity) getActivity()).getUnit();
		if (unit.compareTo("km") == 0)   kmChB.setChecked(true);
		else 							mileChB.setChecked(true);
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
		leftOverlay.animate().alpha(0.5f)
				.setDuration(2000).setInterpolator(new AccelerateInterpolator());
		if (kmChB.isChecked())
			onStatsInKm();
		else if (mileChB.isChecked())
			onStatsInMi();
	}
	
	public void onStatsInMi() {
		kmChB.setChecked(false);
		if (noStats())
			return;
		statPeriod.setText("Since " + runDB.getRunList().get(0).getDateString() + 
					" (" + runDB.getRunList().size() + " runs)");
		distAvg.setText("Average: " + runDB.getAvgDistString("mi") + " mi");
		distMax.setText("Longest: " + runDB.getMaxDistString("mi") + " mi");
		distTotal.setText("Total: " + runDB.getTotalDistString("mi") + " mi");
		paceSpeedAvg.setText("Average: " + runDB.getAvgPaceString("mi") + " min/mi" +
									" (" + runDB.getAvgSpeedString("mi") + " mph)");
		paceSpeedMax.setText("Fastest: " + runDB.getMaxPaceString("mi") + " min/mi" +
									" (" + runDB.getMaxSpeedString("mi") + " mph)");
	}
	public void onStatsInKm() {
		mileChB.setChecked(false);
		statPeriod.setText("Since " + runDB.getRunList().get(0).getDateString() + 
				" (" + runDB.getRunList().size() + " runs)");
		distAvg.setText("Average: " + runDB.getAvgDistString("km") + " km");
		distMax.setText("Longest: " + runDB.getMaxDistString("km") + " km");
		distTotal.setText("Total: " + runDB.getTotalDistString("km") + " km");
		paceSpeedAvg.setText("Average: " + runDB.getAvgPaceString("km") + " min/km" +
									" (" + runDB.getAvgSpeedString("km") + " km/h)");
		paceSpeedMax.setText("Fastest: " + runDB.getMaxPaceString("km") + " min/km" +
									" (" + runDB.getMaxSpeedString("km") + " km/h)");
	}
	
	public boolean noStats() {
		if (runDB.isEmpty()) {
			statPeriod.setText("No stats to display yet");
			distAvg.setText("Average: -");
			distMax.setText("Longest: -");
			distTotal.setText("Total: -");
			paceSpeedAvg.setText("Average: -");
			paceSpeedMax.setText("Fastest: -");
			return true;
		}
		else
			return false;
	}
}
