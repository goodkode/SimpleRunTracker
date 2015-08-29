package net.takoli.simpleruntracker.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.Run;
import net.takoli.simpleruntracker.RunDB;

import java.util.ArrayList;


public class RunAdapter2 extends RecyclerView.Adapter<RunAdapter2.RunViewHolder> {

    private RunDB runListDB;
    private ArrayList<Run> runList;

    public RunAdapter2(RunDB runListDB) {
        this.runListDB = runListDB;
        this.runList = runListDB.getRunList();
    }

    @Override
    public RunViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.one_run, viewGroup, false);
        return new RunViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RunViewHolder runViewHolder, int i) {
        int avgDistU = runListDB.getAvgDistUNIT();
        int avgPaceU = runListDB.getAvgPaceUNIT();
        Run run = runList.get(i);
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

    @Override
    public int getItemCount() {
        return runList.size();
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
}

/** Missing:
 * animation onclick
 * animation on add
 * header
 * new layout after onclick
 * edit button and edit action
 */
