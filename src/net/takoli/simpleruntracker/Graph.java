package net.takoli.simpleruntracker;

public class GraphView extends View {
	
	private ArrayList<Run> runList;
	private final int MAX_PLOTS = 10;
	private int plotSize;
	private float[] dists, speeds;
	private float distMin, distMax, speedMin, speedMax;
	private boolean km;
	
	// set up the view
	public GraphView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        // initialize fields
	        runList = ((MainActivity) getActivity()).getRunDB().getRunList();
	        dists = new float[MAX_PLOTS];
	        speeds = new float[MAX_PLOTS];
	        km = 
	        refreshData();
	}
	
	public void refreshData() {
		int fullSize = runList.size();
		plotSize = fullSize > MAX_PLOTS ? MAX_PLOTS : fullSize;
		distMin = speedMin = 0f;
		distMax = speedMax = 0f;
		for (int i = fullSize - plotSize; i < fullSize; i++) {
			dists[i] = runList.get(i).
			speeds[i] = runList.get(i).
		}
	}
	
	@Override
    	protected void onDraw(Canvas canvas) {
	        drawCoordinates(canvas, distMin, distMax, speedMin, speedMax);
	        drawChart(canvas);
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
	
}
