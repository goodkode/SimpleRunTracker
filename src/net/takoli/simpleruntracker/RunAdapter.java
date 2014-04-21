package net.takoli.simpleruntracker;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RunAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Run> runList;
	private RunDB runListDB;
	private TextView rDate, rDist, rTime, rPace;
	private TextView rPerfAvg, rPerfDist, rPerfPace, rPerfScore;
	private Run run;
	
	public RunAdapter(Context context, int layoutResourceId, RunDB runListDB) {
		this.context = context;
		this.runList = runListDB.getRunList();
		this.runListDB = runListDB;
	}

	public void addNewRun(Run newRun) {
		runList.add(newRun);
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View oneRun;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// show summary - DEFAULT
		if (!runList.get(pos).expanded) {
			oneRun = inflater.inflate(R.layout.one_run, parent, false);
			run = runList.get(pos);
			rDate = (TextView) oneRun.findViewById(R.id.run_date);
			rDist = (TextView) oneRun.findViewById(R.id.run_dist);
			rTime = (TextView) oneRun.findViewById(R.id.run_time);
			rPace = (TextView) oneRun.findViewById(R.id.run_pace);
			rDate.setText(run.getDate());
			rDist.setText(run.getDistance());
			rTime.setText(run.getTime() + "s ");
			rPace.setText(run.getPace());
			return oneRun;
		}
		// show details - if it is clicked
		else {
			oneRun = inflater.inflate(R.layout.one_run_details, parent, false);
			run = runList.get(pos);
			rDate = (TextView) oneRun.findViewById(R.id.run_date);
			rDist = (TextView) oneRun.findViewById(R.id.run_dist);
			rTime = (TextView) oneRun.findViewById(R.id.run_time);
			rPace = (TextView) oneRun.findViewById(R.id.run_pace);
			rPerfAvg = (TextView) oneRun.findViewById(R.id.perf_avg);
			rPerfDist = (TextView) oneRun.findViewById(R.id.perf_dist);
			rPerfPace = (TextView) oneRun.findViewById(R.id.perf_pace);
			//rPerfScore = (TextView) oneRun.findViewById(R.id.run_pace);
			rDate.setText(run.getDate());
			rDist.setText(run.getDistance());
			rTime.setText(run.getTime() + "s ");
			rPace.setText(run.getPace());
			//rPerfAvg.setText(run.getPerfAvg());
			rPerfDist.setText(run.getPerfDist(runListDB.getSumDist(), runListDB.getNumOfRuns()));
			rPerfPace.setText(run.getPerfPace((double) runListDB.getSumTime() / runListDB.getNumOfRuns()));
			//rPerfScore.setText(run.getPerfScore());
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
