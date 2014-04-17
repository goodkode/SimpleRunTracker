package net.takoli.simpleruntracker;

import android.util.Log;

public class Run {
	int dd, _dd, h, mm, ss;
	String unit;
	boolean expanded;
	
	public Run(int dd, int _dd, String unit, int h, int mm, int ss) {
		this.dd = dd;
		this._dd = _dd;
		this.unit = unit;
		this.h = h;
		this.mm = mm;
		this.ss = ss;
		expanded = false;
	}
	
	public Run(String line) {
		String[] fields = line.split(",");
		Log.i("run", fields[0] +fields[1] + fields[2]);
		String[] distSt = fields[0].split("\\.");
		String[] timeSt = fields[2].split("\\:");
		this.dd = Integer.parseInt(distSt[0]);
		this._dd = Integer.parseInt(distSt[1]);
		this.unit = fields[1];
		this.h = Integer.parseInt(timeSt[0]);
		this.mm = Integer.parseInt(timeSt[1]);
		this.ss = Integer.parseInt(timeSt[2]);
		expanded = false;
	}
	
	public String getDate() {
		return "today";
	}
	
	public String getDistance() {
		return dd + "." + _dd;
	}
	
	public String getTime() {
		String sTime = h + ":";
		if (mm < 10)	sTime += "0" + mm;
		else			sTime += mm + ":";
		if (ss < 10)	sTime += "0" + ss;
		else			sTime += ss;
		return  sTime;
	}
	
	public String getPace() {
		int totalSec = h * 60*60 + mm * 60 + ss;
		if (totalSec < 30 || (dd + _dd == 0))
			return "N/A";
		int paceInSec = totalSec * 100 / (dd * 100 + _dd);
		return  (paceInSec / 60) + ":" + ((paceInSec % 60) < 10 ? "0"+paceInSec % 60 : paceInSec % 60);
	}
	
	public void switchDetails() {
		if (expanded)   expanded = false;
		else expanded = true;
	}
	
	public String toString() {
		return dd+"."+_dd+","+unit+","+h+":"+mm+":"+ss;
	}
}
