package net.takoli.simpleruntracker;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GraphView extends View {
	
	private ArrayList<Run> runList;
	private final int MAX_PLOTS = 15; 
	private int plotSize;
	private long[] dists, speeds;
	private long distMin, distMax, speedMin, speedMax;
	private float[] dX, dY, sX, sY;
	private boolean inMiles;
	private String dUnit, sUnit;
	private final double KM_TO_M = 1.60934;
	private final int MY_RED = 0xFFFFA4A4;
	private final int MY_BLUE = 0xFF9FC6FF;
	private final int MY_SHADOW = 0x88000000;

	private int width, height;
	private int sPad, tPad, bPad;
	private Paint coordPaint, distPaint, speedPaint, distLabelPaint, speedLabelPaint;
	
	// set up the view
	public GraphView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        // initialize fields
	        dists = new long[MAX_PLOTS];
	        speeds = new long[MAX_PLOTS];
	        dX = new float[MAX_PLOTS];
	        dY = new float[MAX_PLOTS];
	        sX = new float[MAX_PLOTS];
	        sY = new float[MAX_PLOTS];
	        inMiles = true;
	        dUnit = "m";
	        dUnit = "mph";
	        coordPaint = new Paint();
	        distPaint = new Paint();
	        speedPaint = new Paint();
	        distLabelPaint = new Paint();
	        speedLabelPaint = new Paint();
	        coordPaint.setStyle(Style.STROKE);
	        coordPaint.setColor(Color.BLACK);
	        coordPaint.setStrokeWidth(2);
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
	        distLabelPaint.setColor(MY_RED);
	        speedLabelPaint.setColor(MY_BLUE);
	}

		
	public void setRunList(ArrayList<Run> runList, String unit) {
		this.runList = runList; 
		inMiles = (unit.compareTo("m") == 0);
		if (inMiles)	{ dUnit = "m"; sUnit = "mph";}
		else			{ dUnit = "km"; sUnit = "km/h"; }
		updateData();
	}
	
	public void updateData() {
		int fullSize = runList.size();
		plotSize = fullSize > MAX_PLOTS ? MAX_PLOTS : fullSize;
		Log.i("run", "updateData "+plotSize + " / " + fullSize);
		if (plotSize == 0) {
			Log.i("run", "nothing to chart");
			return; }
		int j = 0;
		for (int i = fullSize - plotSize; i < fullSize; i++) {
			dists[j] = runList.get(i).getDistDecInM();
			Log.i("run", i + ": " + runList.get(i).getDistDecInM());
			speeds[j] = runList.get(i).getSpeedDecInMPH(dists[j]);
			j++;
		}
		// in case we want to see the graph in KM:
		if (!inMiles) for (int i = 0; i < plotSize; i++) {
			dists[i] = Math.round(dists[i] * KM_TO_M);
			speeds[i] = Math.round(speeds[i] * KM_TO_M);
		}
		// check for min and max values:
		distMax = distMin = dists[0];
		speedMax = speedMin = speeds[0];
		for (int i = 0; i < plotSize; i++) {
			if (dists[i] < distMin)	distMin = dists[i];
			if (dists[i] > distMax)	distMax = dists[i];
			if (speeds[i] < speedMin)	speedMin = speeds[i];
			if (speeds[i] > speedMax)	speedMax = speeds[i];
		}
	}
	
	@Override
    	protected void onDraw(Canvas canvas) {
		sPad = this.getPaddingLeft();
		bPad = this.getPaddingBottom();
		tPad = this.getPaddingTop();
		width = this.getWidth() - sPad * 2;
		height = this.getHeight() - tPad - bPad;
		drawCoordSystem(canvas, distMin, distMax, speedMin, speedMax);
		if (plotSize == 0)
			return;
		setPlotCoordinates();
		drawPath(canvas, distPaint, dX, dY);
		drawPath(canvas, speedPaint, sX, sY);
	    	//drawChart(canvas);
	}

	private void drawCoordSystem(Canvas canvas, long distMin, long distMax,
			long speedMin, long speedMax) {
		// left, bottom, right lines
		canvas.drawLine((float)sPad, (float)tPad, (float)sPad, (float)(tPad+height), coordPaint);
		canvas.drawLine((float)sPad, (float)(tPad+height), (float)(sPad+width), (float)(tPad+height), coordPaint);
		canvas.drawLine((float)(sPad+width), (float)(tPad+height), (float)(sPad+width), (float)tPad, coordPaint);
		// miles or km and mph or km/h
		distLabelPaint.setTextSize(sPad * 0.45f);
		speedLabelPaint.setTextSize(sPad * 0.45f);
		canvas.drawText(dUnit, sPad * 1.2f, 			tPad * 2.75f, 	distLabelPaint);
		canvas.drawText(sUnit, width - sPad * 0.278f, 	tPad * 2.75f, 	speedLabelPaint);
		// distance indicators
		distLabelPaint.setTextSize(sPad * 0.35f);
		canvas.drawText(form(distMax), 				sPad * 0.22f, tPad + height*0.1f, distLabelPaint);
		canvas.drawText(form((distMin+distMax)/2), 	sPad * 0.22f, tPad + height*0.5f, distLabelPaint);
		canvas.drawText(form(distMin), 				sPad * 0.22f, tPad + height*0.9f, distLabelPaint);
		// speed indicators
		speedLabelPaint.setTextSize(sPad * 0.35f);
		canvas.drawText(form(speedMax), 			width + sPad * 1.15f, tPad + height*0.2f, speedLabelPaint);
		canvas.drawText(form((speedMin+speedMax)/2),width + sPad * 1.15f, tPad + height*0.5f, speedLabelPaint);
		canvas.drawText(form(speedMin), 			width + sPad * 1.15f, tPad + height*0.8f, speedLabelPaint);
	}
	
	private void drawPath(Canvas canvas, Paint pathPaint, float[] X, float[] Y) {
		float SMOOTH = 0.15f;
		Path path = new Path();
	        path.moveTo(X[0], Y[0]);
	        for (int i = 0; i < plotSize; i++) {
	            float startdiffX = (X[i(i + 1)] - X[i(i - 1)]);
	            float startdiffY = (Y[i(i + 1)] - Y[i(i - 1)]);
	            float endDiffX = (X[i(i + 2)] - X[i(i)]);
	            float endDiffY = (Y[i(i + 2)] - Y[i(i)]);
	            float firstControlX = X[i] + (SMOOTH * startdiffX);
	            float firstControlY = Y[i] + (SMOOTH * startdiffY);
	            float secondControlX = X[i(i + 1)] - (SMOOTH * endDiffX);
	            float secondControlY = Y[i(i + 1)] - (SMOOTH * endDiffY);
	            path.cubicTo(firstControlX, firstControlY, secondControlX, secondControlY, X[i(i + 1)], Y[i(i + 1)]);
	        }
	        canvas.drawPath(path, pathPaint);
	}
	
	private void setPlotCoordinates() {
		float wUnit = width / (MAX_PLOTS - 1);  //divide horizontally
		float dHeight = height * 0.8f;			//distance range will be 80% of chart
		float sHeight = height * 0.5f;			//speed range will be 50%
		float dTop = tPad + height * 0.1f;
		float sTop = tPad + height * 0.25f;
		float dRange = distMax - distMin;
		float sRange = speedMax - speedMin;
		for (int i = 0; i < plotSize; i++) {
			dX[i] = sPad + wUnit * i + 2;
			dY[i] = dTop + dHeight * (distMax - dists[i]) / dRange; 
			sX[i] = sPad + wUnit * i + 2;
			sY[i] = sTop + sHeight * (speedMax - speeds[i]) / sRange;
		}
	}
	
	private int i(int i) {
		if (i >= plotSize)	return plotSize - 1;
	        else if (i < 0)		return 0;
	        return i;
	    }
	
	private String form(long XXxx) {
		String formatted = "" + ++XXxx / 100;
		formatted += "." + (XXxx % 100) / 10;
		return formatted;
	}
}
