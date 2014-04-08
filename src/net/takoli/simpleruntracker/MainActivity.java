package net.takoli.simpleruntracker;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.takoli.simpleruntracker.R;

public class MainActivity extends Activity {
	
	RunDB runList;
	Fragment enterRun;
	FragmentTransaction fragTrans;
	FrameLayout runFragLayout;
	boolean runOpen;
	RelativeLayout mainLayout;
	RunAdapter myAdapter;
	DisplayMetrics dm;
	int screenHeight, screenWidth;
	GestureDetector gestDect;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// instantiate the RunDB
		runList = new RunDB(this);
		runList.newRun(this);
		runList.saveToExternal(this);
		
		// Set up variables
		setContentView(R.layout.activity_main);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dm = getResources().getDisplayMetrics();
		screenHeight = (dm.heightPixels);
		screenWidth = (dm.widthPixels);
		mainLayout = (RelativeLayout) findViewById(R.id.main);
		runFragLayout = new FrameLayout(this);
		enterRun = new EnterRun();
		
		// "Enter Run" top fragment setup
		runFragLayout.setLayoutParams(new FrameLayout.LayoutParams(screenWidth, screenHeight / 2));
		runFragLayout.setId(R.id.enter_run_frame);
		runFragLayout.setBackgroundColor(Color.WHITE);
		fragTrans = getFragmentManager().beginTransaction();
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
		
		// "My Runs" mid-section setup
		findViewById(R.id.my_runs).setBackgroundColor(Color.LTGRAY); //for testing
		// CursorLoader for async load... change later??
		ListView myRuns = (ListView) findViewById(R.id.my_runs);
		myAdapter = new RunAdapter(this, R.layout.one_run, new ArrayList<String>(Arrays.asList(
				new String[] {"firstrun", "secondrun", "thirdrun", "fourthrun", "fifthrun"})));  // just testing
		myRuns.setAdapter(myAdapter);
		myRuns.setOnItemClickListener(new OnItemClickListener() {  // open items in two lines with details
			@Override
			public void onItemClick(AdapterView<?> parent, View runView, int pos, long id) {
				myAdapter.getRunItem(pos).switchDetails();
				myAdapter.notifyDataSetChanged();
//				runView.animate().setDuration(2000).alpha(0)
//	            .withEndAction(new Runnable() {
//	              @Override
//	              public void run() {
//	                list.remove(item);
//	                myAdapter.notifyDataSetChanged();
//	                runView.setAlpha(1);
//	              }
//	            });
			}
			
		});
		
		myRuns.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
					slideUp(); return true; }
				return false; }
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestDect.onTouchEvent(event);
	}
	
	@Override
	protected void onResume() {		
		super.onResume();
		runFragLayout.setY(screenHeight * -3/10);
		slideDown();
	}
	@Override
	protected void onPause() {
		slideUp();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void slideUp() {
		VerticalTextView distance = (VerticalTextView) findViewById(R.id.distance);
		VerticalTextView time = (VerticalTextView) findViewById(R.id.time);
		
		distance.animate().translationX(0).setDuration(1000);
		time.animate().translationX(0).setDuration(1000);
		runFragLayout.animate().setDuration(700).translationY(screenHeight * -3/10);
		runOpen = false;
	}
	public void slideDown() {
		VerticalTextView distance = (VerticalTextView) findViewById(R.id.distance);
		VerticalTextView time = (VerticalTextView) findViewById(R.id.time);
		float moveTextBy = dm.widthPixels / 5.5f - ((MyNumberPicker) findViewById(R.id.dist10)).getTextSize()*dm.density*2;
		
		distance.animate().translationXBy(moveTextBy).setDuration(1000);
		time.animate().translationXBy(- moveTextBy).setDuration(1000);
		runFragLayout.animate().setDuration(700).translationY(0);
		runOpen = true;
	}
	
	// TO OPEN AND CLOSE TOP PANEL gesturelistener
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
					slideDown();
					return true; }
				return false;
		}
		@Override
		public boolean onDown(MotionEvent e) {
			Log.i("run", "onDown pos: "+e.getX()+", "+e.getY());
			if (!runOpen)
				slideDown();
			return true; }
	}
}
