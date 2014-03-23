package net.takoli.simpleruntracker;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.simpleruntracker.R;

public class EnterRun extends Fragment {

	MyNumberPicker d10, d1, d_1, d_01;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View frag =  inflater.inflate(R.layout.enter_run, container, false);
		return frag;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		d10 = (MyNumberPicker) getView().findViewById(R.id.d10);
		d1 = (MyNumberPicker) getView().findViewById(R.id.d1);
		d_1 = (MyNumberPicker) getView().findViewById(R.id.d_1);
		d_01 = (MyNumberPicker) getView().findViewById(R.id.d_01);
	}

}
