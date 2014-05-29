package net.takoli.simpleruntracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class Statistics extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.statistics);
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.main_return, R.anim.stats_exit);
	}
	
	public void backToMain(View view) {
		finish();
		overridePendingTransition(R.anim.main_return, R.anim.stats_exit);
	}
}
