package net.takoli.simpleruntracker;

import android.animation.ObjectAnimator;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

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
	private boolean toAnimate;
	
	public RunAdapter(Context context, int layoutResourceId, RunDB runListDB, FragmentManager fragMngr) {
		this.context = context;
		this.runList = runListDB.getRunList();
		this.runListDB = runListDB;
		this.fragMngr = fragMngr;
		this.toAnimate = false;
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		TextView headerText = (TextView) parent.findViewById(R.id.header_info);
		String header;
		if (runList.size() == 0) 
			header = "Empty list";
		else {
			String limit = ((MainActivity) context).getDBLimit();
			boolean numberLimitUsed = true;
			try {
				Integer.parseInt(limit);
			} catch (NumberFormatException nfe) {
				numberLimitUsed = false; }
			if (numberLimitUsed) {
				header = "Showing " + runList.size() + " of " + limit + " workouts";
			}
			else {
				header = "Showing " + runList.size() + " workout";
				if (runList.size() != 1) 
		 			header += "s";
				header += " since " + Run.getFullStringDate(limit);
			}
		}
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
			rDate.setText(run.getDateString());
			rDist.setText(run.getDistanceString());
			rTime.setText(run.getTimeString() + "s ");
			rPace.setText(run.getPaceString());
			if (pos == runList.size() - 1) {
				oneRun.setBackgroundColor(0xFF88C1FC);
				if (toAnimate) {
					ObjectAnimator colorAnim = ObjectAnimator.ofFloat(oneRun, "alpha", 0f, 1f);
					colorAnim.setInterpolator(new DecelerateInterpolator());
					colorAnim.setDuration(2500);
					colorAnim.start();
					toAnimate = false; } }
			return oneRun;
		}
		// show details - if it is clicked
		else {
			oneRun = inflater.inflate(R.layout.one_run_details, parent, false);
			if (pos == runList.size() - 1)
				oneRun.setBackgroundColor(0xFF88C1FC);
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
			rDate.setText(run.getDateString());
			rDist.setText(run.getDistanceString());
			rTime.setText(run.getTimeString() + "s ");
			rPace.setText(run.getPaceString());
			rSpeed.setText("(" + run.getSpeedString() + ")");
			int avgDistU = runListDB.getAvgDistUNIT();
			int avgPaceU = runListDB.getAvgPaceUNIT();
			rPerformScore.setText(run.getPerfScore(avgDistU, avgPaceU));
			rPerfDist.setText(run.getPerfDist(avgDistU) + " of average distance,  ");
			rPerfPace.setText(run.getPerfPace(avgPaceU) + " of average speed");
			
			ImageView edit = (ImageView) oneRun.findViewById(R.id.run_edit);
			edit.setOnClickListener(new OnClickListener() {
				int position = index;
				Run editRun = run;
				@Override
				public void onClick(View v) {
					RunUpdateDialog updateRunDialog = new RunUpdateDialog();
					Bundle bundle = new Bundle();
					bundle.putInt("pos", position);
					bundle.putString("date", editRun.getDateString());
					bundle.putString("dd", "0" + editRun.dd);
					bundle.putString("_dd", "0" + editRun._dd);
					bundle.putString("h", "" + editRun.h);
					bundle.putString("mm", "0" + editRun.mm);
					bundle.putString("ss", "0" + editRun.ss);
					updateRunDialog.setArguments(bundle);
					updateRunDialog.show(fragMngr, "editRun");
				}
			});
			if (pos >= runList.size() - 2) {
				//listView = (ListView) parent.findViewById(R.id.my_runs);
				//listView.smoothScrollToPosition(pos + 1);
			}
			return oneRun;
		}
	}
	
	public void aninmateNewRun() {
		toAnimate = true;
		notifyDataSetChanged();
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
