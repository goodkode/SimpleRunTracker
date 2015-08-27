package net.takoli.simpleruntracker;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import net.takoli.simpleruntracker.R;

public class BigNumberPicker extends NumberPicker {
	
	private DisplayMetrics dm;
	private float textSize;
	final int DIVIDER_SCALE = 15;
	final int TEXT_SCALE = 22;
	
	public BigNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		dm = getResources().getDisplayMetrics();
		setMinValue(0);
		setMaxValue(9);
		setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		Field[] pickerFields = NumberPicker.class.getDeclaredFields();
		for (Field pf : pickerFields) {
			if (pf.getName().equals("mSelectionDividersDistance")) {
				pf.setAccessible(true);
				try {
					//Log.i("run", "mSelectionDividersDistance1: " + pf.getInt(this));
					pf.set(this, dm.heightPixels / DIVIDER_SCALE);
					//Log.i("run", "mSelectionDividersDistance2: " + pf.getInt(this));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (pf.getName().equals("mSelectionDivider")) {
				pf.setAccessible(true);
				try {
					pf.set(this, getResources().getDrawable(R.drawable.div));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
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
	
	private void updateView(View view) {
		if (view instanceof EditText) {
			textSize = getTextSize();
			//Log.i("run", "updateView: "+textSize);
			((EditText) view).setTextSize(textSize);
		}
	}
	
	public float getTextSize() {
		dm = getResources().getDisplayMetrics();
		return dm.heightPixels / dm.density / TEXT_SCALE;
	}
}
