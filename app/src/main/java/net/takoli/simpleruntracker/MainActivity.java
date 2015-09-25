package net.takoli.simpleruntracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import net.takoli.simpleruntracker.adapter.RunAdapter;
import net.takoli.simpleruntracker.adapter.RunAdapterObserver;
import net.takoli.simpleruntracker.graph.GraphViewFull;
import net.takoli.simpleruntracker.graph.GraphViewSmall;
import net.takoli.simpleruntracker.model.SettingsManager;

public class MainActivity extends AppCompatActivity {

    // Enter run and stats fragment
    protected Fragment enterRun;
    private View enterRunFrame;
	private StatsFragment statsFragment;
    private VerticalTextView distance, time;
    private RadioGroup dateRadioGroup;
    private Button enterRunButton;
    private boolean enterRunIsOpen;
    private static final AnticipateOvershootInterpolator slideUpInterpolator =
                                                new AnticipateOvershootInterpolator(0.8f);
    private static final AnticipateOvershootInterpolator slideDownInterpolator =
                                                new AnticipateOvershootInterpolator(0.92f);

    // Run list view
    private RunDB runDB;
    private RecyclerView runListView;
    private RunAdapter runAdapter;
    private RecyclerView.LayoutManager runListLM;
    private LinearLayout.LayoutParams originalRunListParams;
    private LinearLayout.LayoutParams shiftedRunListParams;


    // Graphs
	private GraphViewSmall graphSmall;
	private GraphViewFull graphFull;
	private ChartFullScreenDialog graphFullFragment;

    // other
    public SettingsManager settingsManager;
    private FragmentManager fragMngr;
    protected GestureDetector gestDect;
    private int screenHeight;
    private int enterRunBottom;
    private final float SLIDE_UP_RATIO = 0.7f;
    private int enterRunSlideDistance;
    private int listTop;
    private int listBottom;
	private int listGap;
    private int shiftedDown;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.i("run", "MAIN onCreate()");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

		//getActionBar().setDisplayShowTitleEnabled(false);
		settingsManager = new SettingsManager(this);
        runDB = new RunDB(this);
        runDB.setDBLimit(settingsManager.getDBLimit());
        fragMngr = getFragmentManager();

		// "Enter Run" top fragment setup:
        enterRunFrame = findViewById(R.id.enter_run_frame);
        enterRun = fragMngr.findFragmentByTag(getResources().getString(R.string.enter_run_tag));
        distance = (VerticalTextView) findViewById(R.id.distance);
        time = (VerticalTextView) findViewById(R.id.time);
        dateRadioGroup = (RadioGroup) findViewById(R.id.date_radiobuttons);
        enterRunButton = (Button) findViewById(R.id.enter_run_button);
		// enable fling up and down to open/close the top panel
		gestDect = new GestureDetector(this, new MainGestureListener());
        enterRunFrame.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestDect.onTouchEvent(event);
            }
        });

        // List of Runs setup:
        runAdapter = new RunAdapter(this, runDB);
        runAdapter.registerAdapterDataObserver(new RunAdapterObserver(runAdapter, runDB));
        runListLM = new LinearLayoutManager(MainActivity.this);
		runListView = (RecyclerView) findViewById(R.id.my_runs);
        runListView.setLayoutManager(runListLM);
        runListView.setAdapter(runAdapter);
        //runListView.setItemAnimator(new FadeInUpAnimator());
        runListView.getItemAnimator().setAddDuration(500);
        runListView.getItemAnimator().setRemoveDuration(500);
		runListView.setHasFixedSize(true);

        // Graph initial setup
		graphSmall = (GraphViewSmall) findViewById(R.id.graph);
		graphSmall.setRunList(runDB, settingsManager.getUnit());
		
		// check for first run
		if (runDB.isEmpty())
			(new FirstRunDialog()).show(fragMngr, "FirstRunDialog");
        Log.i("run", "MAIN onCreate() returned");
    }

    private void initScreenSizeVariables() {
        screenHeight = findViewById(R.id.main_layout).getHeight();
        // enterRun
        enterRunBottom = (int) (screenHeight * 0.58); // 58% height
        enterRunSlideDistance = (int) (enterRunBottom * SLIDE_UP_RATIO);
        // runList
        listTop = (int) (screenHeight * 0.18); // 18% margin
        listBottom = (int) (screenHeight * (0.18 + 0.62)); // 62% height
		listGap = listBottom - enterRunBottom;
        shiftedDown = 0;
        Log.i("run", "M: " + screenHeight + "|| " + enterRunBottom + ", " + enterRunSlideDistance + "| " +
                                                    listTop + ", " + listBottom + ", " + shiftedDown);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i("run", "MAIN onWindowFocusChanged()");
        if (hasFocus) {
            initScreenSizeVariables();
            enterRunFrame.setY(-enterRunSlideDistance);
            slideDown();
        } else {
            slideUp();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("run", "MAIN onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("run", "MAIN onResume()");
    }

    @Override
	protected void onStop() {
        super.onStop();
        runDB.saveRunDB(this);
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
                    FragmentTransaction fragTrans = fragMngr.beginTransaction();
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

	public void updateGraph() {
		if (graphSmall != null) {
			graphSmall.updateData();
			graphSmall.invalidate(); }
	}

    private void slideUp() {
		distance.animate().translationX(0).setDuration(1000);
		time.animate().translationX(0).setDuration(1000);
		// make the date radio buttons disappear
		dateRadioGroup.animate().setDuration(700).alpha(0);
		// change the button text
		enterRunButton.setText("Open");
		enterRunButton.setTextColor(0xFFFFFFFF);
		enterRunButton.animate().setDuration(700).alpha(0.5f);
		enterRunButton.setClickable(false);
		// slide the fragment up
		enterRunFrame.animate()
                .setDuration(700)
                .setInterpolator(slideUpInterpolator)
                .translationY(listTop - enterRunBottom);
		enterRunIsOpen = false;
        // adjust runList visibility
        shiftBackRunList();
	}
    private void slideDown() {
		float moveTextBy = 50; //todo: change this
		distance.animate().translationXBy(moveTextBy).setDuration(1000);
		time.animate().translationXBy(-moveTextBy).setDuration(1000);
		// make the date radio buttons reappear
		dateRadioGroup.animate().setDuration(700).alpha(1);
		// change the button text
		enterRunButton.setText("Enter Run");
		enterRunButton.setTextColor(0xFF000000);
		enterRunButton.animate().setDuration(700).alpha(1);
		enterRunButton.setClickable(true);
		// slide the fragment down
		enterRunFrame.animate()
                .setDuration(700)
                .setInterpolator(slideDownInterpolator)
                .translationY(0);
		enterRunIsOpen = true;
        // adjust runList visibility
        shiftDownRunListIfNeeded();
	}

    private void shiftBackRunList() {
        shiftedDown = 0;
        runListView.animate().translationY(0).setDuration(700);
    }

    public void shiftBackRunListByOneIfNeeded() {
    }

    private void shiftDownRunListIfNeeded() {
        runListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                final int noOfCards = runListLM.getChildCount();
                if (noOfCards < 1)
                    return;
                final int lastCardBottom = listTop + runListLM.getChildAt(noOfCards - 1).getBottom() + shiftedDown;
                if (lastCardBottom < enterRunBottom) {
					// not visible, we want to shift
					final int listLenght = lastCardBottom - listTop;
					if (listLenght > listGap) {
						shiftedDown = listBottom - lastCardBottom;
						runListView.animate().translationY(shiftedDown).setDuration(700);
						Log.i("run", " shift with longer list: " + shiftedDown);
					} else {
						shiftedDown = enterRunBottom - listTop;
						runListView.animate().translationY(shiftedDown).setDuration(700);
						Log.i("run", " shift with short list: " + shiftedDown);
					}
                }
            }
        }, 300);
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
			if ((e1.getX() / screenHeight > 0.15 &&  e1.getX() / screenHeight < 0.85)
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
				if (!enterRunIsOpen) slideDown();
					return true; }
			return false;
		}
		@Override
		public boolean onDown(MotionEvent e) {
			if (!enterRunIsOpen)
				slideDown();
			return true; }
	}
}
