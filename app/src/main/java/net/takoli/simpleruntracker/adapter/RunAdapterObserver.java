package net.takoli.simpleruntracker.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import net.takoli.simpleruntracker.Run;
import net.takoli.simpleruntracker.RunDB;

import java.util.ArrayList;


public class RunAdapterObserver extends RecyclerView.AdapterDataObserver {

    final RunAdapter adapter;
    final ArrayList<Run> runList;

    public RunAdapterObserver(RunAdapter runAdapter, RunDB runDb) {
        adapter = runAdapter;
        runList = runDb.getRunList();
    }


    public void onChanged() { }

    public void onItemRangeChanged(int positionStart, int itemCount) {
        Log.i("run", "onItemRangeChanged: " + positionStart + ", " + itemCount);
        adapter.updateHeader();
    }

    public void onItemRangeInserted(int positionStart, int itemCount) {
        Log.i("run", "onItemRangeInserted: " + positionStart + ", " + itemCount);
        adapter.updateHeader();
    }

    public void onItemRangeRemoved(int positionStart, int itemCount) {
        Log.i("run", "onItemRangeRemoved: " + positionStart + ", " + itemCount);
        adapter.updateHeader();
    }
}
