package net.takoli.simpleruntracker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class GraphViewFull extends View {
	
	private RunDB runListDB;
	private ArrayList<Run> runList;
	private Path distPath, speedPath;
	private final int MAX_PLOTS = 50;
	private int plots;
	private long[] dists, speeds;
	private long distMin, distMax, speedMin, speedMax;
	private float[] dX, dY, sX, sY;
	private float fingerAt;
	private boolean inMiles;
	private String dUnit, sUnit;
	private final int MY_RED = 0xFFFFA4A4;
	private final int MY_DARKRED = 0xFFCF3C3C;
	private final int MY_BLUE = 0xFF9FC6FF;
	private final int MY_DARKBLUE = 0xFF3174D6;
	private final int MY_SHADOW = 0x88000000;
	private int width, height;
	private Paint avgLinePaint, distPaint, speedPaint, distLabelPaint, speedLabelPaint;
	private TextView avgText, runNumText;
	
	// set up the view
	public GraphViewFull(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        // initialize fields
	        avgLinePaint = new Paint();
	        distPaint = new Paint();
	        speedPaint = new Paint();
	        distLabelPaint = new Paint();
	        speedLabelPaint = new Paint();
	        avgLinePaint.setStyle(Style.STROKE);
	        avgLinePaint.setColor(Color.BLACK);
	        avgLinePaint.setPathEffect(new DashPathEffect(new float[] {10, 5}, 0));
	        distPaint.setStyle(Style.STROKE);
	        distPaint.setStrokeWidth(4);
	        distPaint.setAntiAlias(true);
	        distPaint.setShadowLayer(5, 3, 3, MY_SHADOW);
	        distPaint.setColor(MY_RED);
	        speedPaint.setStyle(Style.STROKE);
	        speedPaint.setStrokeWidth(4);
	        speedPaint.setAntiAlias(true);
	        speedPaint.setShadowLayer(5, 3, 3, MY_SHADOW);
	        speedPaint.setColor(MY_BLUE);
	        distLabelPaint.setColor(MY_DARKRED);
	        distLabelPaint.setStyle(Style.FILL);  
			distLabelPaint.setAntiAlias(true);
			distLabelPaint.setTypeface(Typeface.SERIF);
	        speedLabelPaint.setColor(MY_DARKBLUE);
	}

		
	public void setRunList(RunDB runListDB, String unit) {
		this.runListDB = runListDB;
		this.runList = runListDB.getRunList();
		inMiles = (unit.compareTo("mi") == 0);
		if (inMiles)	{ dUnit = "mi"; sUnit = "mph";}
		else			{ dUnit = "km"; sUnit = "km/h"; }
		updateData(15);
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				fingerAt = event.getX();
				GraphViewFull.this.invalidate();
				return true; } } );
	}
	
	public void updateData(int plotSize) {
		plots = plotSize < getMaxPlots() ? plotSize : getMaxPlots();
		plots++;  // an extra for plot[0] for average
        dists = new long[plots];
        speeds = new long[plots];
        dX = new float[plots];
        dY = new float[plots];
        sX = new float[plots];
        sY = new float[plots];
        // set values
        int fullSize = runList.size();
        dists[0] = runListDB.getAvgDistUNIT();
		speeds[0] = runListDB.getAvgSpeedUNIT();
		int j = 1;
		for (int i = fullSize - plots + 1; i < fullSize; i++) {
			dists[j] = runList.get(i).getDistUNIT();
			speeds[j] = runList.get(i).getSpeedUNIT();
			//Log.i("run", "speed " + j + ": " + speeds[j]);
			j++;
		}
		// rescale for KM:
		if (!inMiles) for (int i = 0; i < plots; i++) {
			dists[i] = Run.mi2km(dists[i]);
			speeds[i] = Run.mi2km(speeds[i]);
		}
		// check for min and max values:
		distMax = distMin = dists[0];
		speedMax = speedMin = speeds[0];
		for (int i = 0; i < plots; i++) {
			if (dists[i] < distMin)	distMin = dists[i];
			if (dists[i] > distMax)	distMax = dists[i];
			if (speeds[i] < speedMin)	speedMin = speeds[i];
			if (speeds[i] > speedMax)	speedMax = speeds[i];
		}
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		width = this.getWidth();
		height = this.getHeight();
		if (plots <= 1) {
			runNumText = (TextView) getRootView().findViewById(R.id.chart_run_number);
			runNumText.setText("graph needs more data");
			runNumText.setVisibility(VISIBLE);
			return; }
		drawCoordSystem(canvas);
		setPlotCoordinates();
		drawPath(canvas, speedPath, speedPaint, sX, sY);
		drawPath(canvas, distPath, distPaint, dX, dY);
		showDetailsAtFinger(canvas);
	}

	private void drawCoordSystem(Canvas canvas) {
		int dataPlotSize = plots - 1;
		// line for average
		Path mPath = new Path();
	    	mPath.moveTo(0, height / 2);
	    	mPath.lineTo(width, height / 2);
		canvas.drawPath(mPath, avgLinePaint);
		avgText = (TextView) getRootView().findViewById(R.id.chart_avg_label);
		avgText.setVisibility(VISIBLE);
		// miles or km and mph or km/h
		distPaint.setTextSize(height * 0.1f);
		speedPaint.setTextSize(height * 0.1f);
		canvas.drawText("distance", 0, 0 + height * 0.1f, distPaint);
		canvas.drawText("speed", width - height * 0.3f, 0 + height * 0.1f, speedPaint);
	}
	
	private void drawPath(Canvas canvas, Path path, Paint pathPaint, float[] X, float[] Y) {
		float SMOOTH = 0.1f;
		path = new Path();
        path.moveTo(X[0], Y[0]);
        int dataPlotSize = plots - 1;
        if (dataPlotSize == 1) {
        	path.lineTo(X[1], Y[1]);
        } else {
        for (int i = 0; i < plots; i++) {
            float startdiffX = (X[i(i + 1)] - X[i(i - 1)]);
            float startdiffY = (Y[i(i + 1)] - Y[i(i - 1)]);
            float endDiffX = (X[i(i + 2)] - X[i(i)]);
            float endDiffY = (Y[i(i + 2)] - Y[i(i)]);
            float firstControlX = X[i] + (SMOOTH * startdiffX);
            float firstControlY = Y[i] + (SMOOTH * startdiffY);
            float secondControlX = X[i(i + 1)] - (SMOOTH * endDiffX);
            float secondControlY = Y[i(i + 1)] - (SMOOTH * endDiffY);
            path.cubicTo(firstControlX, firstControlY, secondControlX, secondControlY, X[i(i + 1)], Y[i(i + 1)]);
        } }
        canvas.drawPath(path, pathPaint);
	}
	
	private void showDetailsAtFinger(Canvas canvas) {
		float unit = (float) width / plots;
		int element = (int) (fingerAt / unit);
		if (element >= plots)
			element = plots - 1;
		// draw line and dots
		canvas.drawLine(fingerAt, height * 0.1f, fingerAt, height - height * 0.05f, avgLinePaint);
		PathMeasure pm = new PathMeasure(speedPath, false);
	    float pathCoord[] = {0f, 0f};
	    pm.getPosTan(fingerAt, pathCoord, null);
	    canvas.drawCircle(fingerAt, pathCoord[1], height / 100, speedLabelPaint);
	    pm = new PathMeasure(distPath, false);
	    pm.getPosTan(fingerAt, pathCoord, null);
	    canvas.drawCircle(pathCoord[0], pathCoord[1], height / 100, distLabelPaint);
		// print date and distance/speed measures
		Run currentRun = runList.get(runList.size() - plots + element);
		TextView dateLabel = (TextView) getRootView().findViewById(R.id.chart_date_label);
		dateLabel.setText(currentRun.getDateString());
		String distText = currentRun.getDistString();
		String speedText = currentRun.getSpeedString();
		float textSize = height * 0.1f;
		Rect textBounds = new Rect();
		distLabelPaint.setTextSize(textSize);
   		distLabelPaint.getTextBounds(distText, 0, distText.length(), textBounds);
   		float x = fingerAt - textBounds.width() - textSize / 2;
   		float y = textSize + textBounds.height();
   		if (x < 0) {
   			x = fingerAt + textSize / 2;
   			y += textBounds.height() + textSize / 2; }
   		canvas.drawText(distText, x, y, distLabelPaint);  // write distance
   		speedLabelPaint.setTextSize(textSize);
   		speedLabelPaint.getTextBounds(speedText, 0, speedText.length(), textBounds);
   		float x = fingerAt + textSize / 2;
   		float y = textSize + textBounds.height();
   		if (x > width) {
   			x = fingerAt + textSize / 2;
   			y += textBounds.height() + textSize / 2; }
   		canvas.drawText(distText, x, y, distLabelPaint);  // write speed
	}
	
	private void setPlotCoordinates() {
		float wUnit = width / (plots - 1);  //divide horizontally
		float dHeight = height * 0.8f;			//distance range will be 80% of chart
		float sHeight = height * 0.5f;			//speed range will be 50%
		float dTop = 0 + height * 0.1f;
		float sTop = 0 + height * 0.25f;
		float dRange = distMax - distMin;
		float sRange = speedMax - speedMin;
		for (int i = 0; i < plots; i++) {
			dX[i] = 0 + wUnit * i;
			dY[i] = dTop + dHeight * (distMax - dists[i]) / dRange;
			if (dRange == 0)	dY[i] = dTop + dHeight / 2;
			sX[i] = 0 + wUnit * i;
			sY[i] = sTop + sHeight * (speedMax - speeds[i]) / sRange;
			if (sRange == 0)	sY[i] = sTop + sHeight / 2;
		}
	}
	
	public int getMaxPlots() {
		return runList.size() > MAX_PLOTS ? MAX_PLOTS : runList.size();
	}
	
	private int i(int i) {
		if (i >= plots)	return plots - 1;
	        else if (i < 0)		return 0;
	        return i;
	    }
	
	private String form(long XXxx) {
		String formatted = "" + ++XXxx / 100;
		formatted += "." + (XXxx % 100) / 10;
		return formatted;
	}
}
