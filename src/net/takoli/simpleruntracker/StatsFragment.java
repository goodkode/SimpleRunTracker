package net.takoli.simpleruntracker;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

public class StatsFragment extends Fragment {

	private MainActivity mainActivity;
	private View thisView;
	private RunDB runDB;
	private static boolean active = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mainActivity = ((MainActivity) getActivity());
		this.runDB = mainActivity.getRunDB();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.stats_layout, container, false);
		return thisView;
	}
	
	public static void setActive(boolean active) {
		StatsFragment.active = active;
	}
	public static boolean getActive() {
		return StatsFragment.active;
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
		thisView.animate().translationX(0).setDuration(700);
		mainActivity.findViewById(R.id.stats_left).animate().alpha(0.5f)
				.setDuration(2000)
				.setInterpolator(new AccelerateInterpolator());

	}
	
	public void animateOut() {
		int width = getResources().getDisplayMetrics().widthPixels;
		thisView.animate().translationX(width).setDuration(700);
	}
}
