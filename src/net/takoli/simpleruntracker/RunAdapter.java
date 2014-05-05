package net.takoli.simpleruntracker;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RunAdapter extends BaseAdapter {

	private Context context;
	private ListView listView;
	private ViewGroup parent;
	private FragmentManager fragMngr;
	private ArrayList<Run> runList;
	private RunDB runListDB;
	private TextView rDate, rDist, rTime, rPace, rSpeed;
	private TextView rPerformScore, rPerfDist, rPerfPace;
	private Run run;
	
	public RunAdapter(Context context, int layoutResourceId, RunDB runListDB, FragmentManager fragMngr) {
		this.context = context;
		this.runList = runListDB.getRunList();
		this.runListDB = runListDB;
		this.fragMngr = fragMngr;
	}
	
	public void addHeader(ViewGroup parent) {
		this.parent = parent;
		listView = (ListView) parent.findViewById(R.id.my_runs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout header = (RelativeLayout) inflater.inflate(R.layout.run_header, listView, false);
		listView.addHeaderView(header, null, false);
		notifyDataSetChanged();
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		TextView headerText = (TextView) parent.findViewById(R.id.header_info);
		String header = "Showing " + runList.size();
 		headerText.setText(header);
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
			final int index = pos;
			
			rDate = (TextView) oneRun.findViewById(R.id.run_date);
			rDist = (TextView) oneRun.findViewById(R.id.run_dist);
			rTime = (TextView) oneRun.findViewById(R.id.run_time);
			rPace = (TextView) oneRun.findViewById(R.id.run_pace);
			rSpeed = (TextView) oneRun.findViewById(R.id.run_speed);
			rPerformScore = (TextView) oneRun.findViewById(R.id.perform_score);
			rPerfDist = (TextView) oneRun.findViewById(R.id.perf_dist);
			rPerfPace = (TextView) oneRun.findViewById(R.id.perf_pace);
			rDate.setText(run.getDate());
			rDist.setText(run.getDistance());
			rTime.setText(run.getTime() + "s ");
			rPace.setText(run.getPace());
			rSpeed.setText("(" + run.getSpeed() + ")");
			int avgDist = runListDB.getAvgDistDec();
			int avgPace = runListDB.getAvgPaceSec();
			rPerformScore.setText(run.getPerfScore(avgDist, avgPace));
			rPerfDist.setText(run.getPerfDist(avgDist) + " of average distance,  ");
			rPerfPace.setText(run.getPerfPace(avgPace) + " of average speed");
			
			ImageView edit = (ImageView) oneRun.findViewById(R.id.run_edit);
			edit.setOnClickListener(new OnClickListener() {
				int position = index;
				Run editRun = run;
				@Override
				public void onClick(View v) {
					RunUpdateDialog updateRunDialog = new RunUpdateDialog();
					Bundle bundle = new Bundle();
					bundle.putInt("pos", position);
					bundle.putString("date", editRun.getDate());
					bundle.putString("dd", "0" + editRun.dd);
					bundle.putString("_dd", "0" + editRun._dd);
					bundle.putString("h", "" + editRun.h);
					bundle.putString("mm", "0" + editRun.mm);
					bundle.putString("ss", "0" + editRun.ss);
					updateRunDialog.setArguments(bundle);
					updateRunDialog.show(fragMngr, "editRun");
				}
			});
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
