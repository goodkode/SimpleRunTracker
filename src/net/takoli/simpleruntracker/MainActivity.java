package net.takoli.simpleruntracker;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.simpleruntracker.R;

public class MainActivity extends Activity {
	
	Fragment enterRun;
	FragmentTransaction fragTrans;
	FrameLayout fragLayout;
	RelativeLayout mainLayout;
	DisplayMetrics dm;
	int screenHeight, screenWidth;
	
	GestureDetector mGestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dm = getResources().getDisplayMetrics();
		screenHeight = (int)(dm.heightPixels);
		screenWidth = (int)(dm.widthPixels);
		mainLayout = (RelativeLayout) findViewById(R.id.main);
		fragLayout = new FrameLayout(this);
		enterRun = new EnterRun();
		
		fragLayout.setLayoutParams(new FrameLayout.LayoutParams(screenWidth, screenHeight / 2));
		fragLayout.setId(R.id.enter_run_frame);
		fragLayout.setBackgroundColor(Color.YELLOW);
		mainLayout.addView(fragLayout);
		fragTrans = getFragmentManager().beginTransaction();
		fragTrans.add(R.id.enter_run_frame, enterRun);
		fragTrans.commit();
		
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
		View.OnTouchListener mGestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        };
		
		findViewById(R.id.my_runs).setBackgroundColor(Color.LTGRAY);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}
	
	@Override
	protected void onResume() {		
		super.onResume();
		fragLayout.setY(screenHeight * -3/10);
		slideDown();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void slideUp() {
		fragLayout.animate().setDuration(1000).translationYBy(screenHeight * -3/10);
	}
	public void slideDown() {
		fragLayout.animate().setDuration(1000).translationYBy(screenHeight * 3/10);
	}
}
