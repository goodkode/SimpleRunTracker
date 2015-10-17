package net.takoli.simpleruntracker.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.TextView;

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.RunApp;
import net.takoli.simpleruntracker.view.graph.GraphViewFull;

public class GraphFullActivity extends AppCompatActivity {

    private RunApp app;
    private GraphViewFull graph;
    private TextView currentNumber;
    private TextView needMoreData;
    private Animation rotateAndOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_full_activity);

        app = (RunApp) getApplication();

        currentNumber = (TextView) findViewById(R.id.chart_run_number);
        rotateAndOut = AnimationUtils.loadAnimation(this, R.anim.rotate_and_out);
        findViewById(R.id.chart_full_screen_done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final View rotate = findViewById(R.id.chart_full_screen_rotate);
        rotate.startAnimation(rotateAndOut);
        rotate.postDelayed(new Runnable() {
            @Override
            public void run() {
                rotate.setVisibility(View.GONE);
            }
        },4000);

        // get resources and listeners
        int listSize = app.getRunDB().getRunList().size();
        if (listSize < GraphViewFull.MIN_PLOTS) {
            needMoreData = (TextView) findViewById(R.id.chart_avg_label);
            needMoreData.setTextSize(app.settingsManager.getMainScreenWidth() / 50);
            needMoreData.setTextColor(0xAA000000);
            needMoreData.setText("Get some running first!");
            needMoreData.setVisibility(View.VISIBLE);
            findViewById(R.id.seekBar).setVisibility(View.GONE);
            return;		// not enough data
        }
        graph = (GraphViewFull) findViewById(R.id.chart_full_screen);
        graph.setRunList(app.getRunDB(), app.settingsManager.getUnit());
        int listStart = listSize < GraphViewFull.START_PLOTS ? listSize : GraphViewFull.START_PLOTS;
        int listMax = graph.getMaxPlots();
        graph.updateData(listStart);
        graph.invalidate();

        currentNumber.setTextSize(app.settingsManager.getMainScreenWidth()  / 55);
        showCurrentNumber(listStart);

        SeekBar seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setMax(listMax - GraphViewFull.MIN_PLOTS);
        seekbar.setProgress(listStart - GraphViewFull.MIN_PLOTS);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int n = GraphViewFull.MIN_PLOTS + progress;
                showCurrentNumber(n);
                graph.updateData(n);
                graph.invalidate();
            }
        });
    }

    private void showCurrentNumber(int n) {
        currentNumber.setText("last " + n + " workouts");
        currentNumber.setVisibility(View.VISIBLE);
        currentNumber.setAlpha(1);
        currentNumber.animate().alpha(0).setDuration(1000);
    }
}
