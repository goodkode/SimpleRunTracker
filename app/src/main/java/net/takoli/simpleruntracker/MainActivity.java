package net.takoli.simpleruntracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import net.takoli.simpleruntracker.adapter.RunAdapter;
import net.takoli.simpleruntracker.adapter.RunAdapterObserver;
import net.takoli.simpleruntracker.adapter.animator.FadeInUpAnimator;
import net.takoli.simpleruntracker.graph.GraphViewFull;
import net.takoli.simpleruntracker.graph.GraphViewSmall;

public class MainActivity extends AppCompatActivity {
	
	private SharedPreferences settings;
	
	private RecyclerView runListView;
    private FrameLayout runFragLayout;
	protected Fragment enterRun;
	private StatsFragment statsFragment;
	private FragmentTransaction fragTrans;
	private FragmentManager fragMngr;
	private boolean runFragOpen;

	private RunDB runDB;
    private RunAdapter runAdapter;

	private GraphViewSmall graphSmall;
	private GraphViewFull graphFull;
	private ChartFullScreenDialog graphFullFragment;
	
	private DisplayMetrics dm;
	private int screenHeight, screenWidth;
	protected GestureDetector gestDect;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);
		Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);

		//getActionBar().setDisplayShowTitleEnabled(false);
		settings = getPreferences(MODE_PRIVATE);
		
		// Set up variables and fields
		dm = getResources().getDisplayMetrics();
		screenHeight = (dm.heightPixels);
		screenWidth = (dm.widthPixels);
		enterRun = new EnterRun();
		
		// "Enter Run" top fragment setup:
        runFragLayout = (FrameLayout) findViewById(R.id.enter_run_fragment_frame);
		runFragLayout.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth, screenHeight / 2));
		runFragLayout.setId(R.id.enter_run_frame);
		runFragLayout.setBackgroundColor(Color.WHITE);
		fragMngr = getFragmentManager();
		fragTrans = fragMngr.beginTransaction();
		fragTrans.replace(R.id.enter_run_frame, enterRun);
		fragTrans.commit();
		// enable fling up and down to open/close the top panel
		gestDect = new GestureDetector(this, new MainGestureListener());
		runFragLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestDect.onTouchEvent(event);
            }
        });
			
		// List of Runs setup:
		runDB = new RunDB(this);
		runDB.setDBLimit(getDBLimit());

        runAdapter = new RunAdapter(this, runDB);
        runAdapter.registerAdapterDataObserver(new RunAdapterObserver(runAdapter, runDB));
		runListView = (RecyclerView) findViewById(R.id.my_runs);
        runListView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        runListView.setAdapter(runAdapter);
        runListView.setItemAnimator(new FadeInUpAnimator());
        runListView.getItemAnimator().setAddDuration(500);
        runListView.getItemAnimator().setRemoveDuration(500);
		runListView.setHasFixedSize(true);

		// Graph initial setup
		graphSmall = (GraphViewSmall) findViewById(R.id.graph);
		graphSmall.setRunList(runDB, getUnit());
		
		// check for first run
		if (runDB.isEmpty())
			(new FirstRunDialog()).show(fragMngr, "FirstRunDialog");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (runFragLayout != null)
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
		runDB.saveRunDB(this);
		super.onStop();
	}
	
	@Override
	public void onBackPressed() {
		statsFragment = (StatsFragment) fragMngr.findFragmentByTag("statsFragment");
		if (statsFragment != null && statsFragment.isActive())
			statsFragment.animateOut();
		else
			super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case R.id.stats_icon:
	    	case R.id.statistics:
	    		statsFragment = (StatsFragment) fragMngr.findFragmentByTag("statsFragment");
	    		if (statsFragment == null) {
	    			statsFragment = new StatsFragment();
	    			statsFragment.setRetainInstance(true);
		    		fragTrans = fragMngr.beginTransaction();
		    		fragTrans.add(R.id.main, statsFragment, "statsFragment");
		    		fragTrans.commit();
		    	}
	    		else {
	    			if (statsFragment.isActive())
	    				statsFragment.animateOut();
	    			else
	    				statsFragment.animateIn();
	    		}
	            return true;
	    	case R.id.settings:
	    		(new SettingsDialog()).show(fragMngr, "SettingsDialog");
	            return true; 
	        case R.id.graph_it:
	    		graphFullFragment = (ChartFullScreenDialog) getFragmentManager().findFragmentByTag("ChartFullScreen");
			if (graphFullFragment == null) {
				graphFullFragment = new ChartFullScreenDialog();
				graphFullFragment.show(fragMngr, "ChartFullScreen");
			}
	            return true; 
	    	case R.id.export_list_of_runs:
	        	runDB.saveToExternalMemory(this);
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
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getY() / screenHeight > 0.8) {
			graphFullFragment = (ChartFullScreenDialog) getFragmentManager().findFragmentByTag("ChartFullScreen");
			if (graphFullFragment == null) {
				graphFullFragment = new ChartFullScreenDialog();
				graphFullFragment.show(fragMngr, "ChartFullScreen");
			}
			return true;
		}
		else
			return gestDect.onTouchEvent(event);
	}
	
	public GraphViewSmall getGraphView() {
		return graphSmall;
	}
	
	public RunDB getRunDB() {
		return runDB;
    }

	public RecyclerView getRunList() {
		return runListView;
	}
	
	public RunAdapter getRunAdapter() {
		return runAdapter;
    }
	
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
		runDB.setDBLimit(limit);
		runDB.ensureDBLimit();
	}
	public String getDBLimit() {
		return settings.getString("limit", "300");
	}
	public void updateGraph() {
		if (graphSmall != null) {
			graphSmall.updateData();
			graphSmall.invalidate(); }
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
		runFragLayout.animate()
                .setDuration(700)
                .setInterpolator(new AnticipateOvershootInterpolator(0.8f))
                .translationY(screenHeight * -35 / 100);
		runFragOpen = false;
	}
	public void slideDown() {
		// move Distance and Time texts in
		VerticalTextView distance = (VerticalTextView) findViewById(R.id.distance);
		VerticalTextView time = (VerticalTextView) findViewById(R.id.time);		
		float moveTextBy = dm.widthPixels / 5.5f - ((BigNumberPicker) findViewById(R.id.dist10)).getTextSize()*dm.density*2;
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
		runFragLayout.animate()
                .setDuration(700)
                .setInterpolator(new AnticipateOvershootInterpolator(0.92f))
                .translationY(0);
		runFragOpen = true;
	}
	
	
	
	// TO OPEN AND CLOSE TOP PANEL GestureListener
	class MainGestureListener extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DISTANCE = 20;
		private static final int SWIPE_BAD_MAX_DIST = 200;
	    private static final int SWIPE_THRESHOLD_VELOCITY = 20;
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1 == null || e2 == null)
				return false;
			float deltaY = e2.getY() - e1.getY();
			float deltaX = e2.getX() - e1.getX();
			if ((e1.getX() / screenWidth > 0.15 &&  e1.getX() / screenWidth < 0.85) 
					&&  e1.getY() / screenHeight < 0.33) {
				//Log.i("run", "onFling out of area");
				return false; }
			if (Math.abs(deltaX) > SWIPE_BAD_MAX_DIST || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
				//Log.i("run", "onFling invalid");
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