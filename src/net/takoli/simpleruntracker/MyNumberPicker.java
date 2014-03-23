package net.takoli.simpleruntracker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

public class MyNumberPicker extends NumberPicker {
	
	public MyNumberPicker(Context context, AttributeSet attrs) {
	     super(context, attrs);
	     setMinValue(0);
	     setMaxValue(9);
	     setWrapSelectorWheel(false);
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
     if(view instanceof EditText){
       ((EditText) view).setTextSize(25);
     }
   }
}
