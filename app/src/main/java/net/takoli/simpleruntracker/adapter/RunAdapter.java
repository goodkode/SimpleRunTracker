package net.takoli.simpleruntracker.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.takoli.simpleruntracker.MainActivity;
import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.Run;
import net.takoli.simpleruntracker.RunDB;

import java.util.ArrayList;


public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunViewHolder> {

    private static final int HEADER = 0;
    private static final int RUN = 1;
    private final MainActivity main;
    private final RunDB runListDB;
    private final ArrayList<Run> runList;
    private HeaderViewHolder header;

    public RunAdapter(MainActivity main, RunDB runListDB) {
        this.main = main;
        this.runListDB = runListDB;
        this.runList = runListDB.getRunList();
    }

    @Override
    public RunViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == HEADER) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.run_header, viewGroup, false);
            header = new HeaderViewHolder(v);
            return header;
        }
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.one_run, viewGroup, false);
        return new RunViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RunViewHolder runViewHolder, int position) {
        if (getItemViewType(position) == HEADER) {
            updateHeader();
        } else {
            int avgDistU = runListDB.getAvgDistUNIT();
            int avgPaceU = runListDB.getAvgPaceUNIT();
            Run run = runList.get(position - 1);
            runViewHolder.rDist.setText(run.getDistanceString());
            runViewHolder.rDate.setText(run.getDateString());
            runViewHolder.rDist.setText(run.getDistanceString());
            runViewHolder.rTime.setText(run.getTimeString() + "s ");
            runViewHolder.rPace.setText(run.getPaceString());
//        runViewHolder.rSpeed.setText("(" + run.getSpeedString() + ")");
//        runViewHolder.rPerformScore.setText(run.getPerfScore(avgDistU, avgPaceU));
//        runViewHolder.rPerfDist.setText(run.getPerfDist(avgDistU) + " of average distance,  ");
//        runViewHolder.rPerfPace.setText(run.getPerfPace(avgPaceU) + " of average speed");
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ?  HEADER : RUN;
    }

    @Override
    public int getItemCount() {
        return runList.size() + 1;
    }

    public void updateHeader() {
        if (header == null)
            return;;
        if (runList.size() == 0)
            header.info.setText("Empty list");
        else {
            String limit = main.getDBLimit();
            boolean numberLimitUsed = true;
            try {
                Integer.parseInt(limit);
            } catch (NumberFormatException nfe) {
                numberLimitUsed = false; }
            if (numberLimitUsed) {
                header.info.setText("Showing " + runList.size() + " of " + limit + " workouts");
            }
            else {
                if (runList.size() != 1)
                    header.info.setText("Showing " + runList.size() + " workouts");
                else
                    header.info.setText("Showing " + runList.size() + " workout");
                header.info.setText(" since " + Run.getFullStringDate(limit));
            }
        }
    }


    public static class RunViewHolder extends RecyclerView.ViewHolder {
        TextView rDate;
        TextView rDist;
        TextView rTime;
        TextView rPace;
        TextView rSpeed;
        TextView rPerformScore;
        TextView rPerfDist;
        TextView rPerfPace;

        public RunViewHolder(View runView) {
            super(runView);
            rDate = (TextView) runView.findViewById(R.id.run_date);
            rDist = (TextView) runView.findViewById(R.id.run_dist);
            rTime = (TextView) runView.findViewById(R.id.run_time);
            rPace = (TextView) runView.findViewById(R.id.run_pace);
            rSpeed = (TextView) runView.findViewById(R.id.run_speed);
            rPerformScore = (TextView) runView.findViewById(R.id.perform_score);
            rPerfDist = (TextView) runView.findViewById(R.id.perf_dist);
            rPerfPace = (TextView) runView.findViewById(R.id.perf_pace);

            runView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("run", "Element clicked.");
                }
            });
        }
    }

    public static class HeaderViewHolder extends RunViewHolder {
        TextView info;

        public HeaderViewHolder(View headerView) {
            super(headerView);
            info = (TextView) headerView.findViewById(R.id.header_info);
            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("run", "Element clicked.");
                }
            });
        }
    }
}

/** Missing:
 * animation onclick
 * animation on add
 * header
 * new layout after onclick
 * edit button and edit action
 */
