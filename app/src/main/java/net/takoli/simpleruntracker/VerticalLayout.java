package net.takoli.simpleruntracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class VerticalLayout extends FrameLayout {

    private Matrix matrix;

	public VerticalLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
        matrix = this.getMatrix();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.translate(getWidth(), 0);
		canvas.rotate(90);
        canvas.getMatrix().invert(matrix);
	}

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.i("run", "+event: " + ev.getAction() + ": " + ev.getX() + ", " + ev.getY());
        final float[] orig = new float[2];
        final float[] touch = new float[2];
        orig[0] = ev.getX();
        orig[1] = ev.getY();
        touch[0] = orig[0];
        touch[1] = orig[1];

        matrix.mapPoints(touch);
        ev.setLocation(touch[0], touch[1]);
        if (ev.getX() != orig[0] && ev.getY() != orig[1]) {
            Log.i("run", " event: " + ev.getAction() + ": " + ev.getX() + ", " + ev.getY());
            return super.dispatchTouchEvent(ev);
        } else {
            Log.i("run", " event: same shit");
            return super.dispatchTouchEvent(ev);
        }
    }
}