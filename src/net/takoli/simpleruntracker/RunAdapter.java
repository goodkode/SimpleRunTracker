package net.takoli.simpleruntracker;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RunAdapter extends BaseAdapter {

	private Context thisContext;
	private ArrayList<Run> runList;
	private TextView rDate, rDist, rTime, rPace;
	private Run run;
	
	public RunAdapter(Context context, int layoutResourceId, RunDB runListDB) {
		thisContext = context;
		runList = runListDB.getList();
		runList.add(new Run(1, 2, "m", 1, 22, 33));  // testing
		//TODO runDB - read in data
	}

	public void addNewRun(Run newRun) {
		runList.add(newRun);
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View oneRun;
		LayoutInflater inflater = (LayoutInflater) thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// show summary - DEFAULT
		if (!runList.get(pos).expanded) {
			oneRun = inflater.inflate(R.layout.one_run, parent, false);
			run = runList.get(pos);
			rDate = (TextView) oneRun.findViewById(R.id.run_date);
			rDist = (TextView) oneRun.findViewById(R.id.run_dist);
			rTime = (TextView) oneRun.findViewById(R.id.run_time);
			rDate.setText("today");
			rDist.setText(run.getDistance());
			rTime.setText(run.getTime());
			return oneRun;
		}
		// show details - if it is clicked
		else {
			oneRun = inflater.inflate(R.layout.one_run_details, parent, false);
			return oneRun;
		}
	}

	@Override
	public int getCount() {
		return runList.size();
	}

	@Override
	public Object getItem(int pos) {
		return runList.get(pos);
	}
	
	public Run getRunItem(int pos) {
		return runList.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
}
