package net.takoli.simpleruntracker;

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
	
	public String getDistance() {
		return dd + "." + _dd;
	}
	
	public String getTime() {
		String sTime = h + ":";
		if (mm < 10)	sTime += "0" + mm;
		else			sTime += mm;
		if (ss < 10)	sTime += "0" + ss;
		else			sTime += ss;
		return  sTime;
	}
	
	public void switchDetails() {
		if (expanded)   expanded = false;
		else expanded = true;
	}
}
