package net.takoli.simpleruntracker;

public class Graph {
	
	private ArrayList<Run> graphRunList;
	private int fullSize, plotSize;
	private final int MAX_PLOTS = 10;
	
	// nothing is really needed here
	public Graph() { }
	
	public void update(RunDB runListDB) {
		readInGraphRuns(runListDB);
		measureViewAndGraph();
		drawPlots();
	}
	
	// Utility for drawing:
	private void readInGraphRuns(RunDB runListDB) {
		ArrayList<Run> runList = runListDB.getRunList();
		this.fullSize = runList.size();
		if (fullSize > MAX_PLOTS)
			plotSize = MAX_PLOTS;
		else
			plotSize = fullSize;
		graphRunList = new ArrayList<Run>();
		for (int i = fullSize - 1; i >= fullSize - plotSize; i--)
			graphRunList.add(runList.get(i);
	}
	
	private void measureViewAndGraph() {
		
	}
	
	private void drawPlots() {
		
	}
}
