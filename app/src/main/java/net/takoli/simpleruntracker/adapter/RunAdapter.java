package net.takoli.simpleruntracker.adapter;

import android.os.Bundle;
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
import net.takoli.simpleruntracker.RunUpdateDialog;

import java.util.ArrayList;


public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunViewHolder> {

    private static final int HEADER = 0;
    private static final int RUN = 1;
    private static final int EXPANDED = 2;
    private final MainActivity main;
    private final RunDB runListDB;
    private final ArrayList<Run> runList;
    private HeaderViewHolder header;
    private int expanded = -1;

    public RunAdapter(MainActivity main, RunDB runListDB) {
        this.main = main;
        this.runListDB = runListDB;
        this.runList = runListDB.getRunList();
    }

    @Override
    public RunViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
       RunViewHolder viewHolder = null;
        if (viewType == HEADER) {
            View cardView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.run_header, viewGroup, false);
            viewHolder = new HeaderViewHolder(cardView);
        } else if (viewType == EXPANDED) {
            View cardView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.one_run_expanded, viewGroup, false);
            viewHolder = new ExtendedRunViewHolder(cardView, this);
        } else {
            View cardView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.one_run, viewGroup, false);
            viewHolder = new RunViewHolder(cardView, this);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RunViewHolder runViewHolder, int position) {
        if (getItemViewType(position) == HEADER) {
            updateHeader();
        } else {
            Run run = runList.get(position - 1);
            runViewHolder.rDist.setText(run.getDistanceString());
            runViewHolder.rDate.setText(run.getDateString());
            runViewHolder.rDist.setText(run.getDistanceString());
            runViewHolder.rTime.setText(run.getTimeString() + "s ");
            runViewHolder.rPace.setText(run.getPaceString());
            if (runViewHolder instanceof ExtendedRunViewHolder) {
                ExtendedRunViewHolder expRunViewHolder = (ExtendedRunViewHolder) runViewHolder;
                int avgDistU = runListDB.getAvgDistUNIT();
                int avgPaceU = runListDB.getAvgPaceUNIT();
                expRunViewHolder.rSpeed.setText(run.getSpeedString());
                expRunViewHolder.rPerformScore.setText(run.getPerfScore(avgDistU, avgPaceU));
                expRunViewHolder.rPerfDist.setText(run.getPerfDist(avgDistU) + " of average distance,  ");
                expRunViewHolder.rPerfPace.setText(run.getPerfPace(avgPaceU) + " of average speed");
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER;
        else if (position == expanded)
            return EXPANDED;
        else
            return RUN;
    }

    @Override
    public int getItemCount() {
        return runList.size() + 1;
    }

    public void notifyItemRemovedHelper() {
        expanded = -1;
    }

    public void expandItem(int position) {
        if (expanded == position) {
            expanded = -1;
            notifyItemChanged(position);
        } else {
            int prevExpanded = expanded;
            expanded = position;
            notifyItemChanged(prevExpanded);
            notifyItemChanged(expanded);
        }
        if (position == getItemCount() - 1) {
            main.shiftRunList();
        }
    }

    public void updateHeader() {
        if (header == null)
            return;;
        if (runList.size() == 0)
            header.info.setText("Empty list");
        else {
            String limit = main.settingsManager.getDBLimit();
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

    public void openRunEditDialog(int pos) {
        int position = pos - 1;
        RunUpdateDialog updateRunDialog = new RunUpdateDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(RunUpdateDialog.POSITION, position);
        updateRunDialog.setArguments(bundle);
        updateRunDialog.show(main.getFragmentManager(), "editRun");
    }


    public static class RunViewHolder extends RecyclerView.ViewHolder {

        TextView rDate;
        TextView rDist;
        TextView rTime;
        TextView rPace;

        public RunViewHolder(View runView) {
            super(runView);
        }

        public RunViewHolder(View runView, final RunAdapter adapter) {
            super(runView);
            rDate = (TextView) runView.findViewById(R.id.run_date);
            rDist = (TextView) runView.findViewById(R.id.run_dist);
            rTime = (TextView) runView.findViewById(R.id.run_time);
            rPace = (TextView) runView.findViewById(R.id.run_pace);

            runView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.expandItem(getAdapterPosition());
                }
            });
        }
    }

    public static class ExtendedRunViewHolder extends RunViewHolder {

        TextView rSpeed;
        TextView rPerformScore;
        TextView rPerfDist;
        TextView rPerfPace;
        View rRunEdit;

        public ExtendedRunViewHolder(View runView, final RunAdapter adapter) {
            super(runView, adapter);
            rSpeed = (TextView) runView.findViewById(R.id.run_speed);
            rPerformScore = (TextView) runView.findViewById(R.id.perform_score);
            rPerfDist = (TextView) runView.findViewById(R.id.perf_dist);
            rPerfPace = (TextView) runView.findViewById(R.id.perf_pace);
            rRunEdit = runView.findViewById(R.id.run_edit);
            runView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    adapter.openRunEditDialog(getAdapterPosition());
                    return true;
                }
            });
            rRunEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.openRunEditDialog(getAdapterPosition());
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
                    Log.d("run", "Header clicked.");
                }
            });
        }
    }
}
