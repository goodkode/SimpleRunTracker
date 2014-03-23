package net.takoli.simpleruntracker;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class MyGestureListener extends SimpleOnGestureListener {
	
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_DISTANCE = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		try {
			float deltaX = e2.getX() - e1.getX();
			float deltaY = e2.getY() - e1.getY();
			if (deltaY > SWIPE_MIN_DISTANCE && deltaY < SWIPE_MAX_DISTANCE 
					&& velocityY > SWIPE_THRESHOLD_VELOCITY)
				Log.i("run", "onFling");
		}
		catch (Exception e) {}
		return false;
	}

}
