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
        matrix = new Matrix(getMatrix());
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
	}

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i("run", "getWidth: " + getWidth());
        final float[] temp = new float[2];
        temp[0] = event.getX() - 300;
        temp[1] = event.getY();
        event.setLocation(temp[1], temp[0]);
        return super.dispatchTouchEvent(event);
    }
}