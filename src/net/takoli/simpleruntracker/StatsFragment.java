package net.takoli.simpleruntracker;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.CheckBox;
import android.widget.TextView;

public class StatsFragment extends Fragment {

	private MainActivity mainActivity;
	private static View thisView;
	private static RunDB runDB;
	private static boolean active;
	private String unit;
	private CheckBox mileChB, kmChB, bothChB;
	private TextView statPeriod, distAvg, distMax, distTotal, paceSpeedAvg, paceSpeedMax;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mainActivity = (MainActivity) getActivity();
		this.unit = mainActivity.getUnit();
		active = true;
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
		mainActivity.findViewById(R.id.stats_left).setAlpha(0);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!active)
			return;
		statPeriod = (TextView) mainActivity.findViewById(R.id.stats_for_period);
		distAvg = (TextView) mainActivity.findViewById(R.id.stats_distance_avg);
		distMax = (TextView) mainActivity.findViewById(R.id.stats_distance_max);
		distTotal = (TextView) mainActivity.findViewById(R.id.stats_distance_total);
		paceSpeedAvg = (TextView) mainActivity.findViewById(R.id.stats_pace_speed_avg);
		paceSpeedMax = (TextView) mainActivity.findViewById(R.id.stats_pace_speed_max);
		if (unit.compareTo("km") == 0) {
			kmChB.setChecked(true);
			onStatsInKm(); }
		else {
			mileChB.setChecked(true);
			onStatsInMi(); }
		animateIn();
	}
	
	public void animateOut() {
		active = false;
		int width = getResources().getDisplayMetrics().widthPixels;
		thisView.animate().translationX(width).setDuration(700);
		mainActivity.findViewById(R.id.stats_left).setAlpha(0);		
	}
	
	public void animateIn() {
		active ? (return : active) = true;
		thisView.animate().translationX(0).setDuration(700);
		mainActivity.findViewById(R.id.stats_left).animate().alpha(0.5f)
				.setDuration(2000).setInterpolator(new AccelerateInterpolator());
	}
	
	public void onStatsInMi() {
		kmChB.setChecked(false);
		bothChB.setChecked(false);
	}
	public void onStatsInKm() {
		mileChB.setChecked(false);
		bothChB.setChecked(false);
	}
	public void onStatsInMiAndKm() {
		kmChB.setChecked(false);
		mileChB.setChecked(false);
	}
}
