package net.takoli.simpleruntracker;

import java.lang.reflect.Field;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

public class MyNumberPicker extends NumberPicker {
	
	DisplayMetrics dispMet;
	float textSize = 20;
	
	public MyNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		dispMet = getResources().getDisplayMetrics();
		setMinValue(0);
		setMaxValue(9);
		setWrapSelectorWheel(false);
		Field[] pickerFields = NumberPicker.class.getDeclaredFields();
		for (Field pf : pickerFields) {
			if (pf.getName().equals("mSelectionDividersDistance")) {
				pf.setAccessible(true);
				try {
					int dist = pf.getInt(this);
					//Log.i("run", "mSelectionDividersDistance1: " + dist);
					pf.set(this, (int)(dispMet.heightPixels/dispMet.density / 10));
					dist = pf.getInt(this);
					Log.i("run", "mSelectionDividersDistance2: " + dist);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//			if (pf.getName().equals("mSelectionDivider")) {
//				pf.setAccessible(true);
//				try {
//					pf.set(this, null);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
		}
	}

	@Override
	public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
		updateView(child);
	}

	@Override
	public void addView(View child, android.view.ViewGroup.LayoutParams params) {
		super.addView(child, params);
		updateView(child);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int distance = top - bottom;
		int delta = distance /2;
		top = top - delta;
		bottom = bottom + delta;
		super.onLayout(changed, left, top, right, bottom);
	}

	private void updateView(View view) {
		if (view instanceof EditText) {
			dispMet = getResources().getDisplayMetrics();
			textSize = dispMet.heightPixels/dispMet.density / 30;
			Log.i("run", "updateView: "+textSize);
			((EditText) view).setTextSize(textSize);
		}
		
	}
}
