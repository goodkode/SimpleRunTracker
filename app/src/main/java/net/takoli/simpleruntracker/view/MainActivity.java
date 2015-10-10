package net.takoli.simpleruntracker.view;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import android.widget.RadioGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.RunApp;
import net.takoli.simpleruntracker.adapter.RunAdapter;
import net.takoli.simpleruntracker.adapter.animator.FadeInUpAnimator;
import net.takoli.simpleruntracker.model.RunDB;
import net.takoli.simpleruntracker.model.SettingsManager;
import net.takoli.simpleruntracker.view.dialog.ChartFullScreenDialog;
import net.takoli.simpleruntracker.view.dialog.ConfirmDeleteDialog;
import net.takoli.simpleruntracker.view.dialog.FirstRunDialog;
import net.takoli.simpleruntracker.view.dialog.SettingsDialog;
import net.takoli.simpleruntracker.view.graph.GraphViewSmall;
import net.takoli.simpleruntracker.view.widget.VerticalTextView;

public class MainActivity extends AppCompatActivity {

    // Enter run and stats fragment
    protected Fragment enterRun;
    private View enterRunFrame;
	private StatsFragment statsFragment;
    private VerticalTextView distance, time;
    private RadioGroup dateRadioGroup;
    private Button enterRunButton;
    private View enterRunOpenIcon;
    private float moveTextBy;
    private boolean isEnterRunOpen;
    private static final AnticipateOvershootInterpolator slideUpInterpolator =
                                                new AnticipateOvershootInterpolator(0.8f);
    private static final AnticipateOvershootInterpolator slideDownInterpolator =
                                                new AnticipateOvershootInterpolator(0.92f);

    // Run list view
    private RunDB runDB;
    private RecyclerView runListView;
    private RunAdapter runAdapter;
    private RecyclerView.LayoutManager runListLM;

    // Graphs
	private GraphViewSmall graphSmall;
	private ChartFullScreenDialog graphFullFragment;

    // other
    public Tracker gTracker;
    public SettingsManager settingsManager;
    private FragmentManager fragMngr;
    protected GestureDetector gestDect;
    private int enterRunBottom;
    private int enterRunSlideDistance;
    private int listTop;
    private int listBottom;
	private int listLength;
	private int listGap;
    private int shiftedDown;
	private int graphHeight;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

		//getActionBar().setDisplayShowTitleEnabled(false);
        gTracker = ((RunApp) getApplication()).getDefaultTracker();
        gTracker.setScreenName("MainScreen");
        gTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
        enterRunOpenIcon = findViewById(R.id.enter_run_open_icon);
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
        runListLM = new LinearLayoutManager(MainActivity.this);
		runListView = (RecyclerView) findViewById(R.id.my_runs);
        runListView.setLayoutManager(runListLM);
        runListView.setAdapter(runAdapter);
        runListView.setItemAnimator(new FadeInUpAnimator(runAdapter, getResources()));
		runListView.setHasFixedSize(true);

        // Graph initial setup
		graphSmall = (GraphViewSmall) findViewById(R.id.graph);
		graphSmall.setRunList(runDB, settingsManager.getUnit());
        graphSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullGraph();
                gTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Graph")
                        .setAction("graph via small graph")
                        .build());
            }
        });
		
		// check for first run
		if (runDB.isEmpty())
			(new FirstRunDialog()).show(fragMngr, "FirstRunDialog");
    }

    private void initScreenSizeVariables() {
        View mainScreen = findViewById(R.id.main_layout);
		settingsManager.setMainScreenHeight(mainScreen.getHeight());
		settingsManager.setMainScreenWidth(mainScreen.getWidth());
        // runList and enterRun
        listTop = (int) (settingsManager.getMainScreenHeight() * 0.18); // 18% margin
        listBottom = (int) (settingsManager.getMainScreenHeight() * (0.18 + 0.62)); // 62% height
		listLength = runListView.getHeight();
        shiftedDown = 0;
        enterRunBottom = (int) (settingsManager.getMainScreenHeight() * 0.58); // 58% height
        enterRunSlideDistance = enterRunBottom - listTop;
        listGap = listBottom - enterRunBottom;
		graphHeight = findViewById(R.id.graph_space).getHeight();
        moveTextBy = enterRun.getView().findViewById(R.id.left).getWidth() * 0.2f;
//        Log.i("run", "M screen: " + settingsManager.getMainScreenWidth() + " * " + settingsManager.getMainScreenHeight() +
//                " || run bottom and slide: " + enterRunBottom + ", " + enterRunSlideDistance +
//                " | list top, bottom, length, shift: " + listTop + ", " + listBottom + ", " + listLength + ", " + shiftedDown);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (settingsManager.isAppStart() && hasFocus) {
            initScreenSizeVariables();
            enterRunFrame.setY(-enterRunSlideDistance);
            runListView.smoothScrollToPosition(runDB.getRunList().size());
            runAdapter.notifyDataSetChanged();
            slideDown();
			openSmallGraph();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
		    	} else {
	    			if (statsFragment.isActive())
						statsFragment.animateOut();
					else
						statsFragment.animateIn();
	    		}
                gTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Stats")
                        .setAction("")
                        .build());
	            return true;
	    	case R.id.settings:
	    		openSettings();
	            return true; 
	        case R.id.graph_it:
	    		openFullGraph();
                gTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Graph")
                        .setAction("graph via menu")
                        .build());
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

    public void openFullGraph() {
        graphFullFragment = (ChartFullScreenDialog) getFragmentManager().findFragmentByTag("ChartFullScreen");
        if (graphFullFragment == null) {
            graphFullFragment = new ChartFullScreenDialog();
            graphFullFragment.show(fragMngr, "ChartFullScreen");
        }
    }

    public void openSettings() {
        (new SettingsDialog()).show(fragMngr, "SettingsDialog");
    }

	private void openSmallGraph() {
		graphSmall.getLayoutParams().height = graphHeight;
        graphSmall.setTranslationY(graphHeight);
        graphSmall.setVisibility(View.VISIBLE);
        graphSmall.animate().translationYBy(-graphHeight).setDuration(700);
    }

    private void slideUp() {
		distance.animate().translationX(0).setDuration(1000);
		time.animate().translationX(0).setDuration(1000);
		// make the date radio buttons disappear
		dateRadioGroup.animate().setDuration(700).alpha(0);
		// change the button text
        enterRunButton.setClickable(false);
		enterRunButton.animate().setDuration(700).alpha(0);
        enterRunOpenIcon.animate().setDuration(700).alpha(1);
		// slide the fragment up
		enterRunFrame.animate()
                .setDuration(700)
                .setInterpolator(slideUpInterpolator)
                .translationY(-enterRunSlideDistance);
		isEnterRunOpen = false;
        // adjust runList visibility
        shiftBackRunList();
	}
    private void slideDown() {
		distance.animate().translationXBy(moveTextBy).setDuration(1000);
		time.animate().translationXBy(-moveTextBy).setDuration(1000);
		// make the date radio buttons reappear
		dateRadioGroup.animate().setDuration(700).alpha(1);
		// change the button text
        enterRunButton.setClickable(true);
		enterRunButton.animate().setDuration(700).alpha(1);
        enterRunOpenIcon.animate().setDuration(700).alpha(0);
		// slide the fragment down
		enterRunFrame.animate()
                .setDuration(700)
                .setInterpolator(slideDownInterpolator)
                .translationY(0);
		isEnterRunOpen = true;
        // adjust runList visibility
        shiftRunList();
	}

    private void shiftBackRunList() {
        shiftedDown = 0;
        runListView.animate().translationY(0).setDuration(700);
    }

    public void shiftRunList() {
        runListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                final int noOfCards = runListLM.getChildCount();
                if (noOfCards < 1)
                    return;
                final int lastCardBottom = listTop + runListLM.getChildAt(noOfCards - 1).getBottom() + shiftedDown;
                if ((lastCardBottom - listGap / 2) < enterRunBottom) {
                    // barely visible, we want to shift
                    final int currListLength = lastCardBottom - listTop;
                    if (currListLength > listGap && isEnterRunOpen) {
                        shiftedDown = listBottom - lastCardBottom;
                        //Log.i("run", " shift with longer list: " + shiftedDown);
                    } else if (isEnterRunOpen) {
                        shiftedDown = enterRunBottom - listTop;
                        //Log.i("run", " shift with short list: " + shiftedDown);
                    }
                    runListView.animate().translationY(shiftedDown).setDuration(700);
                    findViewById(R.id.main_layout).invalidate();
                }
            }
        }, 300);
    }

    public void shiftRunListAfterRun() {
        if (shiftedDown == 0)
            return;
        runListView.post(new Runnable() {
            @Override
            public void run() {
                final int noOfCards = runListLM.getChildCount();
                if (noOfCards < 1)
                    return;
                final int lastCardBottom = listTop + runListLM.getChildAt(noOfCards - 1).getBottom() + shiftedDown;
                if (lastCardBottom > listBottom) {
                    // new element added
                    int shiftUp = lastCardBottom - listBottom;
                    shiftedDown -= shiftUp;
                    if (shiftedDown < 0) {
                        shiftUp += shiftedDown;
                        shiftedDown = 0;
                    }
                    //Log.i("run", " shift back a bit: " + shiftUp);
                    if (shiftUp != 0) {
                        runListView.animate().translationYBy(-shiftUp).setDuration(100);
                        findViewById(R.id.main_layout).invalidate();
                    }
                }
            }
        });
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
			if ((e1.getX() / settingsManager.getMainScreenHeight() > 0.15 &&  e1.getX() / settingsManager.getMainScreenHeight() < 0.85)
					&&  e1.getY() / settingsManager.getMainScreenHeight() < 0.33) {
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
				if (!isEnterRunOpen) slideDown();
					return true; }
			return false;
		}
		@Override
		public boolean onDown(MotionEvent e) {
			if (!isEnterRunOpen)
				slideDown();
			return true; }
	}
}
