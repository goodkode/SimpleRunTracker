package net.takoli.simpleruntracker;

import android.app.Fragment;


public class EnterRun extends Fragment {

  MainActivity mainActivity;
  View thisView;
  RunDB runDB;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mainActivity = ((MainActivity) getActivity());
		this.runDB = mainActivity.getRunDB();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisView =  inflater.inflate(R.layout.stats_layout, container, false);
		return thisView;
	}

	@Override
	public void onStart() {
		super.onStart();
		int width = getResources().getDisplayMetrics().width();
		thisView.setX(width)
	}
	
	@Override
	public void onResume() {
	  super.onResume();
	  thisView.animate().translateX(0).setDuration(700).start();
	  mainActivity.findViewById(R.id.stats_left).animate().alpha(0.5).setDuration(1000).start();
	}
		
}
