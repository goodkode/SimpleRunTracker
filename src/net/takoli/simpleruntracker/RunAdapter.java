package net.takoli.simpleruntracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import net.takoli.simpleruntracker.R;

public class RunAdapter extends ArrayAdapter<String> {

	private Context thisContext;
	private File runDb;
	private ArrayList<Run> runList = new ArrayList<Run>();

	// This is for testing, in practice CSV will be read in from FILE
	public RunAdapter(Context context, int layoutResourceId, List<String> justTest) {
		super(context, layoutResourceId, justTest);
		thisContext = context;
		for (String st: justTest)
			runList.add(new Run(st));
	}
	
	public RunAdapter(Context context, int layoutResourceId) {
		super(context, layoutResourceId);
		//runDB - read in data
	}

	public Run getRunItem(int pos) {
		return runList.get(pos);
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View oneRun;
		LayoutInflater inflater = (LayoutInflater) thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// show summary - DEFAULT
		if (!runList.get(pos).expanded) {
			oneRun = inflater.inflate(R.layout.one_run, parent, false);
			return oneRun;
		}
		// show details - if it is clicked
		else {
			oneRun = inflater.inflate(R.layout.one_run_details, parent, false);
			return oneRun;
		}
	}
}
