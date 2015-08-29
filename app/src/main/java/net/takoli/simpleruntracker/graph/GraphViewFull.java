package net.takoli.simpleruntracker.graph;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import net.takoli.simpleruntracker.R;
import net.takoli.simpleruntracker.Run;
import net.takoli.simpleruntracker.RunDB;

import java.util.ArrayList;

public class GraphViewFull extends View {
	
	private RunDB runListDB;
	private ArrayList<Run> runList;
	private Path distPath, speedPath;
	public static final int MAX_PLOTS = 50;
	public static final int MIN_PLOTS = 3;
	public static final int START_PLOTS = 15;
	private boolean initial = true;
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
	private Paint avgLinePaint, distPaint, speedPaint, cornerLabelPaint, distLabelPaint, speedLabelPaint;
	//private TextView avgText;
	
	// set up the view
	public GraphViewFull(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        // initialize fields
	        avgLinePaint = new Paint();
	        distPaint = new Paint();
	        speedPaint = new Paint();
	        cornerLabelPaint = new Paint();
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
	        speedLabelPaint.setStyle(Style.FILL);  
	        speedLabelPaint.setAntiAlias(true);
	        speedLabelPaint.setTypeface(Typeface.SERIF);
	}

		
	public void setRunList(RunDB runListDB, String unit) {
		this.runListDB = runListDB;
		this.runList = runListDB.getRunList();
		inMiles = (unit.compareTo("mi") == 0);
		if (inMiles)	{ dUnit = "mi"; sUnit = "mph";}
		else			{ dUnit = "km"; sUnit = "km/h"; }
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				initial = false;
				fingerAt = event.getX();
				//	```````1					`1Log.i("run", "fingerAt: " + fingerAt);
				GraphViewFull.this.invalidate();
				return true; } } );
		fingerAt = this.getWidth() * 0.8f;
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
		super.onDraw(canvas);
		if (plots <= MIN_PLOTS)
			return;
		width = this.getWidth();
		height = this.getHeight();
		drawCoordSystem(canvas);
		setPlotCoordinates();
		speedPath = new Path();
		drawPath(canvas, speedPath, speedPaint, sX, sY);
		distPath = new Path();
		drawPath(canvas, distPath, distPaint, dX, dY);
		if (initial)
			fingerAt = width * 0.8f;
		if (fingerAt < 0)
			fingerAt = 0;
		if (fingerAt >= width)
			fingerAt = width - 1;
		showDetailsAtFinger(canvas);
	}

	private void drawCoordSystem(Canvas canvas) {
		// line for average
		Path mPath = new Path();
		mPath.moveTo(0, height / 2);
		mPath.lineTo(width, height / 2);
		canvas.drawPath(mPath, avgLinePaint);
		// miles or km and mph or km/h
		cornerLabelPaint.setTextSize(height * 0.1f);
		cornerLabelPaint.setColor(MY_RED);
		canvas.drawText("distance", 0, 0 + height * 0.1f, cornerLabelPaint);
		cornerLabelPaint.setColor(MY_BLUE);
		canvas.drawText("speed", width - height * 0.3f, 0 + height * 0.1f, cornerLabelPaint);
	}
	
	private void drawPath(Canvas canvas, Path path, Paint pathPaint, float[] X, float[] Y) {
		float SMOOTH = 0.1f;
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
		// draw line and dots
		canvas.drawLine(fingerAt, height * 0.1f, fingerAt, height - height * 0.05f, avgLinePaint);
		float sPathCoords[] = findPathCoords(speedPath);
		float dPathCoords[] = findPathCoords(distPath);
	    canvas.drawCircle(fingerAt, sPathCoords[1], height / 45, speedLabelPaint);
	    canvas.drawCircle(fingerAt, dPathCoords[1], height / 45, distLabelPaint);
		// print date and distance/speed measures
		float textSize = height * 0.08f;
		int sections = (plots - 1) * 2;
		float unit = (float) width / sections;
		int element = (int) (fingerAt / unit);
		int runIndex = (runList.size() - 1) - (sections  - element) / 2;
		if (runIndex <= runList.size() - plots)	runIndex = runList.size() - plots + 1;
		if (runIndex >= runList.size()) 		runIndex = runList.size() - 1;
		if (runIndex < 0)						runIndex = 0;
		Run currentRun = runList.get(runIndex);
		TextView dateLabel = (TextView) getRootView().findViewById(R.id.chart_date_label);
		dateLabel.setText(currentRun.getDateString());
		String distText = currentRun.getDistanceString();
		String speedText = currentRun.getSpeedString();
		Rect distTextBounds = new Rect();
		Rect speedTextBounds = new Rect();
		distLabelPaint.setTextSize(textSize);
   		speedLabelPaint.setTextSize(textSize);
   		distLabelPaint.getTextBounds(distText, 0, distText.length(), distTextBounds);
   		speedLabelPaint.getTextBounds(speedText, 0, speedText.length(), speedTextBounds);
   		float xD = fingerAt - distTextBounds.width() - textSize;
   		float yD = height * 0.15f + distTextBounds.height();
   		float xS = fingerAt + textSize / 2f;
   		float yS = yD;
   		if (xD < 0) {
   			xD = xS;
   			yS += distTextBounds.height() + textSize / 2f; }
   		if (xS + speedTextBounds.width() > width) {
   			xS = fingerAt - distTextBounds.width() - textSize;
   			yS += speedTextBounds.height() + textSize / 2f; }
   		canvas.drawText(distText, xD, yD, distLabelPaint);  // write distance
   		canvas.drawText(speedText, xS, yS, speedLabelPaint);  // write speed
   		// if initial, arrow animation
   		int rMid = (int) fingerAt;
   		int rTop = (int) (height * 0.3f);
   		if (!initial)	return;
   		int rLength = distTextBounds.width();
   		int rHeight = (int) textSize;
   		int rGap = (int) (textSize * 0.1f);
   		Rect left = new Rect(rMid - rLength - rGap, rTop, rMid - rGap, rTop + rHeight);
   		Rect right = new Rect(rMid + rGap, rTop, rMid + rLength + rGap, rTop + rHeight);
   		Bitmap leftArrow = BitmapFactory.decodeResource(getResources(), R.drawable.left_arrow); 
   		Bitmap rightArrow = BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow);
   		canvas.drawBitmap(leftArrow, null, left, null);
   		canvas.drawBitmap(rightArrow, null, right, null);
	}
	
	private void setPlotCoordinates() {
		float wUnit = width / (plots - 1);  	//divide horizontally
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
	
	private float[] findPathCoords(Path path) {
		final int PROXIMITY = 1;
		float[] coords = new float[2];
		PathMeasure pm = new PathMeasure(path, false);
	    float estX = pm.getLength() * fingerAt / width;
	    pm.getPosTan(estX, coords, null);
	    float diff = fingerAt - coords[0];
	    int tries = 0;
	    while (Math.abs(diff) > PROXIMITY && tries < 25) {
	    	estX += diff / 2;
	    	if (estX < 0) 				estX = PROXIMITY;
	    	if (estX > pm.getLength())	estX = pm.getLength() - PROXIMITY;
	    	pm.getPosTan(estX, coords, null);
	    	diff = fingerAt - coords[0];
	    	//Log.i("run", "tries: " + tries + "; coord c[0]/estX/diff: " + coords[0] + " / " + estX + " / " + diff);
	    	tries++;
	    }
		return coords;
	}
	
	public int getMaxPlots() {
		return runList.size() > MAX_PLOTS ? MAX_PLOTS : runList.size();
	}
	
	private int i(int i) {
		if (i >= plots)	return plots - 1;
	        else if (i < 0)		return 0;
	        return i;
	    }
}
