package net.takoli.simpleruntracker;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	private SharedPreferences settings;
	
	private RelativeLayout mainLayout;
	private ListView runListLayout;
	private FrameLayout runFragLayout;
	protected Fragment enterRun;
	private StatsFragment statsFragment;
	private FragmentTransaction fragTrans;
	private FragmentManager fragMngr;
	private boolean runFragOpen;

	private RunDB runDB;       // ArrayList<Run> abstraction and file IO functions
	private RunAdapter myAdapter;  // Activity's ListView adapter - uses ArrayList<Run> received from runDB
	
	private GraphView graph;
	
	private DisplayMetrics dm;
	private int screenHeight, screenWidth;
	private GestureDetector gestDect;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayShowTitleEnabled(false);
		settings = getPreferences(MODE_PRIVATE);
		
		// Set up variables and fields
		setContentView(R.layout.activity_main);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dm = getResources().getDisplayMetrics();
		screenHeight = (dm.heightPixels);
		screenWidth = (dm.widthPixels);
		mainLayout = (RelativeLayout) findViewById(R.id.main);
		runFragLayout = new FrameLayout(this);
		enterRun = new EnterRun();
		
		// "Enter Run" top fragment setup:
		runFragLayout.setLayoutParams(new FrameLayout.LayoutParams(screenWidth, screenHeight / 2));
		runFragLayout.setId(R.id.enter_run_frame);
		runFragLayout.setBackgroundColor(Color.WHITE);
		fragMngr = getFragmentManager();
		fragTrans = fragMngr.beginTransaction();
		fragTrans.replace(R.id.enter_run_frame, enterRun);
		fragTrans.commit();
		mainLayout.addView(runFragLayout);
			// enable fling up and down to open/close the top panel
			gestDect = new GestureDetector(this, new MyGestureListener());
			runFragLayout.setOnTouchListener(new OnTouchListener() {
			    @Override
				public boolean onTouch(View v, MotionEvent event) {
			        return gestDect.onTouchEvent(event);
			    }
			});
			
		// List of Runs setup:
		runListLayout = (ListView) findViewById(R.id.my_runs);
		runDB = new RunDB(this);
		runDB.setDBLimit(getDBLimit());
		myAdapter = new RunAdapter(this, R.layout.one_run, runDB, fragMngr);
		myAdapter.addHeader(mainLayout);
		runListLayout.setAdapter(myAdapter);
		runListLayout.setOnItemClickListener(new OnItemClickListener() {  // open items in two lines with details
			@Override
			public void onItemClick(AdapterView<?> parent, View runView, int pos, long id) {
				pos--;  // to compensate for header
				//Log.i("run","onclick pos: " +pos);
				myAdapter.getRunItem(pos).switchDetails();
				myAdapter.notifyDataSetChanged(); }
		});
		runListLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
					slideUp(); return true; }
				return false; }
		});
		
		// Graph initial setup
		graph = (GraphView) findViewById(R.id.graph);
		graph.setRunList(runDB, getUnit());
		
		// check for first run
		// Initialize UNIT and MAXSIZE / FROMDATE
		if (runDB.isEmpty()) {
			(new FirstRunDialog()).show(fragMngr, "FirstRunDialog");
			Log.i("run", "first run dialog"); }

		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (StatsFragment.active){
			Log.i("run", "statsfragment gesture");
			return false; }
		else
			return gestDect.onTouchEvent(event);
	}
	
	@Override
	protected void onResume() {		
		super.onResume();
		Log.i("run", "main onresume");
		runFragLayout.setY(screenHeight * -3/10);
		slideDown();
	}
	@Override
	protected void onPause() {
		slideUp();
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		runDB.updateAndSaveRunDB(this);
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	    	case R.id.settings:
	    		(new SettingsDialog()).show(fragMngr, "SettingsDialog");
	            return true; 
	    	case R.id.statistics:
	    		statsFragment = (StatsFragment) fragMngr.findFragmentByTag("statsFragment");
	    		if (statsFragment == null) {
	    			Log.i("run", "new fragment");
	    			statsFragment = new StatsFragment();
		    		fragTrans = fragMngr.beginTransaction();
		    		fragTrans.add(R.id.main, statsFragment, "statsFragment");
		    		fragTrans.commit();
		    	}
	    		else {
	    			Log.i("run", "fragment reused");
	    			if (StatsFragment.active)
	    				statsFragment.animateOut();
	    			else
	    				statsFragment.animateIn();
	    		}
	            return true;
	    	case R.id.export_list_of_runs:
	        	runDB.saveToExternal(this);
	        	Intent emailIntent = runDB.emailIntent(this);
	        	if (emailIntent != null)
	        		startActivity(emailIntent);
	            return true;
	        case R.id.delete_db:
	        	(new ConfirmDeleteDialog()).show(fragMngr, "confirmDeleteDB");
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onBackPressed() {
		statsFragment = (StatsFragment) fragMngr.findFragmentByTag("statsFragment");
		if (statsFragment != null)
			statsFragment.animateOut();
		else
			super.onBackPressed();
	}
	
	public void slideUp() {
		// move Distance and Time texts out
		VerticalTextView distance = (VerticalTextView) findViewById(R.id.distance);
		VerticalTextView time = (VerticalTextView) findViewById(R.id.time);
		distance.animate().translationX(0).setDuration(1000);
		time.animate().translationX(0).setDuration(1000);
		// make the date radio buttons disappear
		RadioGroup dateRadioGroup = (RadioGroup) findViewById(R.id.date_radiobuttons);
		dateRadioGroup.animate().setDuration(700).alpha(0);
		// change the button text
		Button enterRunButton = (Button) findViewById(R.id.enter_run_button);
		enterRunButton.setText("Open");
		enterRunButton.setTextColor(0xFFFFFFFF);
		enterRunButton.animate().setDuration(700).alpha(0.5f);
		enterRunButton.setClickable(false);
		// slide the fragment up
		runFragLayout.animate().setDuration(700).translationY(screenHeight * -35/100);
		runFragOpen = false;
	}
	public void slideDown() {
		// move Distance and Time texts in
		VerticalTextView distance = (VerticalTextView) findViewById(R.id.distance);
		VerticalTextView time = (VerticalTextView) findViewById(R.id.time);		float moveTextBy = dm.widthPixels / 5.5f - ((MyNumberPicker) findViewById(R.id.dist10)).getTextSize()*dm.density*2;
		distance.animate().translationXBy(moveTextBy).setDuration(1000);
		time.animate().translationXBy(- moveTextBy).setDuration(1000);
		// make the date radio buttons reappear
		RadioGroup dateRadioGroup = (RadioGroup) findViewById(R.id.date_radiobuttons);
		dateRadioGroup.animate().setDuration(700).alpha(1);
		// change the button text
		Button enterRunButton = (Button) findViewById(R.id.enter_run_button);
		enterRunButton.setText("Enter Run");
		enterRunButton.setTextColor(0xFF000000);
		enterRunButton.animate().setDuration(700).alpha(1);
		enterRunButton.setClickable(true);
		// slide the fragment down
		runFragLayout.animate().setDuration(700).translationY(0);
		runFragOpen = true;
	}
	
	public GraphView getGraphView() {
		return graph;
	}
	
	public RunDB getRunDB() {
		return runDB; }
	
	public RunAdapter getRunAdapter() {
		return myAdapter; }
	
	public void setUnit(String unit) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("unit", unit);
		editor.commit();
	}
	public String getUnit() {
		return settings.getString("unit", "mi");
	}
	public String getUnitInFull() {
		String u = settings.getString("unit", "mi");
		if (u.compareTo("mi") == 0)
			return "miles";
		else if (u.compareTo("km") == 0)
			return "kilometers";
		else return "";
	}
	public void setDBLimit(String limit) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("limit", limit);
		editor.commit();
	}
	public String getDBLimit() {
		return settings.getString("limit", "100");
	}
	public void updateGraph() {
		if (graph != null) {
			graph.updateData();
			graph.invalidate(); }
	}
	
	
	
	
	// TO OPEN AND CLOSE TOP PANEL GestureListener
	class MyGestureListener extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DISTANCE = 20;
		private static final int SWIPE_BAD_MAX_DIST = 200;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 20;
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			float deltaY = e2.getY() - e1.getY();
			float absDeltaX = Math.abs(e2.getX() - e1.getX());
				if ((e1.getX() / screenWidth > 0.15 &&  e1.getX() / screenWidth < 0.85) 
						&&  e1.getY() / screenHeight < 0.33) {
					//Log.i("run", "onFling invalid");
					return false; }
				if (absDeltaX > SWIPE_BAD_MAX_DIST || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
					return false; }
				else if (deltaY < -(SWIPE_MIN_DISTANCE)) {
					//Log.i("run", "onFling UP");
					slideUp();
					return true; }
				else if (deltaY > SWIPE_MIN_DISTANCE) {
					//Log.i("run", "onFling DOWN");
					if (!runFragOpen) slideDown();
					return true; }
				return false;
		}
		@Override
		public boolean onDown(MotionEvent e) {
			if (!runFragOpen)
				slideDown();
			return true; }
	}
}
