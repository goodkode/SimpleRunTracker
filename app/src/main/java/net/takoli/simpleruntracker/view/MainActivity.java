package net.takoli.simpleruntracker.view;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.RunApp;
import net.takoli.simpleruntracker.adapter.RunAdapter;
import net.takoli.simpleruntracker.adapter.animator.FadeInUpAnimator;
import net.takoli.simpleruntracker.model.SettingsManager;
import net.takoli.simpleruntracker.view.dialog.ConfirmDeleteDialog;
import net.takoli.simpleruntracker.view.dialog.FirstRunDialog;
import net.takoli.simpleruntracker.view.dialog.SettingsDialog;
import net.takoli.simpleruntracker.view.graph.GraphViewSmall;
import net.takoli.simpleruntracker.view.widget.VerticalTextView;

public class MainActivity extends AppCompatActivity {

    private static final String ENTER_RUN_FRAGMENT_TAG = "enterRunTag";
    private static final String STATS_FRAGMENT_TAG = "statsFragment";

    public static final int WRITE_TO_SD = 1;
    public static final int WRITE_TO_SD_DELETE_LOCAL = 2;
    public static final int RESTORE_FROM_SD = 3;

    private RunApp app;

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
    private RecyclerView runListView;
    private RunAdapter runAdapter;
    private RecyclerView.LayoutManager runListLM;

    // Graphs
	private GraphViewSmall graphSmall;

    // other
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

        app = (RunApp) getApplication();

		//getActionBar().setDisplayShowTitleEnabled(false);
        app.settingsManager = new SettingsManager(this);
        app.getRunDB().setDBLimit(app.settingsManager.getDBLimit());
        fragMngr = getFragmentManager();

		// "Enter Run" top fragment setup:
        enterRunFrame = findViewById(R.id.enter_run_frame);
        enterRun = fragMngr.findFragmentByTag(ENTER_RUN_FRAGMENT_TAG);
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
        runAdapter = new RunAdapter(this, app.getRunDB());
        runListLM = new LinearLayoutManager(MainActivity.this);
		runListView = (RecyclerView) findViewById(R.id.my_runs);
        runListView.setLayoutManager(runListLM);
        runListView.setAdapter(runAdapter);
        runListView.setItemAnimator(new FadeInUpAnimator(runAdapter, getResources()));
		runListView.setHasFixedSize(true);

        // Graph initial setup
		graphSmall = (GraphViewSmall) findViewById(R.id.graph);
		graphSmall.setRunList(app.getRunDB(), app.settingsManager.getUnit());
        graphSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullGraph();
            }
        });
		
		// check for first run
		if (app.getRunDB().isEmpty())
			(new FirstRunDialog()).show(fragMngr, "FirstRunDialog");
    }

    private void initScreenSizeVariables() {
        View mainScreen = findViewById(R.id.main_layout);
		app.settingsManager.setMainScreenHeight(mainScreen.getHeight());
		app.settingsManager.setMainScreenWidth(mainScreen.getWidth());
        // runList and enterRun
        listTop = (int) (app.settingsManager.getMainScreenHeight() * 0.18); // 18% margin
        listBottom = (int) (app.settingsManager.getMainScreenHeight() * (0.18 + 0.62)); // 62% height
		listLength = runListView.getHeight();
        shiftedDown = 0;
        enterRunBottom = (int) (app.settingsManager.getMainScreenHeight() * 0.58); // 58% height
        enterRunSlideDistance = enterRunBottom - listTop;
        listGap = listBottom - enterRunBottom;
		graphHeight = findViewById(R.id.graph_space).getHeight();
        moveTextBy = enterRun.getView().findViewById(R.id.left).getWidth() * 0.2f;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (app.settingsManager.isAppStart() && hasFocus) {
            initScreenSizeVariables();
            enterRunFrame.setY(-enterRunSlideDistance);
            runListView.smoothScrollToPosition(app.getRunDB().getRunList().size());
            runAdapter.notifyDataSetChanged();
            slideDown();
			openSmallGraph();
        }
    }

    @Override
	protected void onStop() {
        super.onStop();
        app.getRunDB().saveRunDB(this);
	}
	
	@Override
	public void onBackPressed() {
		statsFragment = (StatsFragment) fragMngr.findFragmentByTag(STATS_FRAGMENT_TAG);
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
	    		statsFragment = (StatsFragment) fragMngr.findFragmentByTag(STATS_FRAGMENT_TAG);
	    		if (statsFragment == null) {
	    			statsFragment = new StatsFragment();
	    			statsFragment.setRetainInstance(true);
                    FragmentTransaction fragTrans = fragMngr.beginTransaction();
		    		fragTrans.add(R.id.main, statsFragment, STATS_FRAGMENT_TAG);
		    		fragTrans.commit();
		    	} else {
	    			if (statsFragment.isActive())
						statsFragment.animateOut();
					else
						statsFragment.animateIn();
	    		}

	            return true;
	    	case R.id.settings:
	    		openSettings();
	            return true; 
	        case R.id.graph_it:
	    		openFullGraph();
	            return true; 
	    	case R.id.export_list_of_runs:
	        	Intent emailIntent = app.getRunDB().emailIntent(this);
	        	if (emailIntent != null)
	        		startActivity(emailIntent);
                tryStorageTask(WRITE_TO_SD);
	            return true;
            case R.id.restore_list_of_runs:
                RestoreDialog confirmRestoreDialog = new RestoreDialog();
                confirmRestoreDialog.show(fragMngr, "confirm");
                return true;
	        case R.id.delete_db:
                ConfirmDeleteDialog confirmDeleteDialog = new ConfirmDeleteDialog();
                confirmDeleteDialog.show(fragMngr, "confirmDeleteDB");
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
        Intent openFullGraphIntent = new Intent(this, GraphFullActivity.class);
        startActivity(openFullGraphIntent);
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

    public void tryStorageTask(int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        else if (requestCode == WRITE_TO_SD)
            writeToSd();
        else if (requestCode == WRITE_TO_SD_DELETE_LOCAL)
            writeToSdDeleteLocal();
        else if (requestCode == RESTORE_FROM_SD)
            app.getRunDB().restoreFromExternalMemory(this);
            runAdapter.resetRunDB();
            updateGraph();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == WRITE_TO_SD)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                writeToSd();
            } else
                Toast.makeText(this, getString(R.string.skip_backup), Toast.LENGTH_LONG).show();
        else if (requestCode == WRITE_TO_SD_DELETE_LOCAL)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                writeToSdDeleteLocal();
            }
            else {
                deleteLocalData();
                Toast.makeText(this, getString(R.string.skip_restore), Toast.LENGTH_LONG).show();
            }
        else if (requestCode == RESTORE_FROM_SD)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                restoreFromSd();
            }
            else
                Toast.makeText(this, getString(R.string.skip_restore), Toast.LENGTH_LONG).show();
    }

    private void deleteLocalData() {
        app.getRunDB().deleteDB(this);
        runAdapter.resetRunDB();
        updateGraph();
    }

    private void writeToSdDeleteLocal() {
        app.getRunDB().saveToExternalMemory(this);
        deleteLocalData();
    }

    private void writeToSd() {
        app.getRunDB().saveToExternalMemory(this);
    }

    private void restoreFromSd() {
        app.getRunDB().restoreFromExternalMemory(this);
        runAdapter.resetRunDB();
        updateGraph();
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
			if ((e1.getX() / app.settingsManager.getMainScreenHeight() > 0.15 &&  e1.getX() / app.settingsManager.getMainScreenHeight() < 0.85)
					&&  e1.getY() / app.settingsManager.getMainScreenHeight() < 0.33) {
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
