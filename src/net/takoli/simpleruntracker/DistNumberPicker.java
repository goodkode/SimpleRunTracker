package net.takoli.simpleruntracker;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.simpleruntracker.R;

public class DistNumberPicker extends NumberPicker {
	
	DisplayMetrics dispMet;
	float textSize;
	final int DIVIDER_SCALE = 9;
	final int TEXT_SCALE = 23;
	
	public DistNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		dispMet = getResources().getDisplayMetrics();
		setMinValue(0);
		setMaxValue(9);
		Field[] pickerFields = NumberPicker.class.getDeclaredFields();
		for (Field pf : pickerFields) {
			if (pf.getName().equals("mSelectionDividersDistance")) {
				pf.setAccessible(true);
				try {
					//Log.i("run", "mSelectionDividersDistance1: " + pf.getInt(this));
					pf.set(this, (int)(dispMet.heightPixels/dispMet.density / DIVIDER_SCALE));
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
			((EditText) view).setTextAppearance(getContext(), R.style.Distance);
		}
	}
	
	public float getTextSize() {
		dispMet = getResources().getDisplayMetrics();
		return dispMet.heightPixels/dispMet.density / TEXT_SCALE;
	}
}
