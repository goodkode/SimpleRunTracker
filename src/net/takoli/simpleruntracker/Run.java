package net.takoli.simpleruntracker;

import java.util.Calendar;
import java.util.Locale;

import android.util.Log;

public class Run {
	int dd, _dd;   // distance in units and in decimals
	int h, mm, ss;   // time in hour, min, and sec
	String unit;   // miles or kilometers
	Calendar date;
	boolean expanded;
	
	public Run(Calendar date, int dd, int _dd, String unit, int h, int mm, int ss) {
		this.date = date;
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
		Log.i("run", "date:"+ fields[0] + ", dist: " + fields[1] + 
				fields[2] + ", time: " + fields[3]);
		String[] dateSt = fields[0].split("/");
		String[] distSt = fields[1].split("\\.");
		String[] timeSt = fields[3].split("\\:");
		this.date = Calendar.getInstance(Locale.US);
		this.date.set(Calendar.MONTH, (Integer.parseInt(dateSt[0]) - 1));
		this.date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateSt[1]));
		this.date.set(Calendar.YEAR, Integer.parseInt(dateSt[2]));
		this.dd = Integer.parseInt(distSt[0]);
		this._dd = Integer.parseInt(distSt[1]);
		this.unit = fields[2];
		this.h = Integer.parseInt(timeSt[0]);
		this.mm = Integer.parseInt(timeSt[1]);
		this.ss = Integer.parseInt(timeSt[2]);
		expanded = false;
	}
	
	public String getDate() {
		Calendar today = Calendar.getInstance();
		if (date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
				date.get(Calendar.YEAR) == today.get(Calendar.YEAR))
			return "Today";
		if (date.get(Calendar.DAY_OF_YEAR) == (today.get(Calendar.DAY_OF_YEAR) - 1) &&
				date.get(Calendar.YEAR) == today.get(Calendar.YEAR))
			return "Yesterday";
		String month;
		switch (date.get(Calendar.MONTH)) {
			case 0: month = "Jan"; break;
			case 1: month = "Feb"; break;
			case 2: month = "Mar"; break;
			case 3: month = "Apr"; break;
			case 4: month = "May"; break;
			case 5: month = "Jun"; break;
			case 6: month = "Jul"; break;
			case 7: month = "Aug"; break;
			case 8: month = "Sep"; break;
			case 9: month = "Oct"; break;
			case 10: month = "Nov"; break;
			case 11: month = "Dec"; break;
			default: month = ""; break; }
		int y = date.get(Calendar.YEAR) % 100;
		String year = y < 10 ? "'0"+y : "'"+y;
		return month + " " + date.get(Calendar.DAY_OF_MONTH) + ", "+year;
	}
	
	public String getDistance() {
		return ((dd < 10 ? " " + dd : dd) + "." + 
					(_dd < 10 ? ("0" + _dd) : _dd) + " " + unit);
	}
	
	public String getTime() {
		String sTime = h + ":";
		if (mm < 10)	sTime += "0" + mm + ":";
		else			sTime += mm + ":";
		if (ss < 10)	sTime += "0" + ss;
		else			sTime += ss;
		return  sTime;
	}
	
	public String getPace() {
		int totalSec = h * 60*60 + mm * 60 + ss;
		if (dd + _dd == 0)	return "-";
		int paceInSec = totalSec * 100 / (dd * 100 + _dd);
		return  (paceInSec / 60) + ":" + ((paceInSec % 60) < 10 ? 
				"0"+paceInSec % 60 : paceInSec % 60) + " min/" + unit;
	}
	
	public String getSpeed() {
		int totalSec = h * 60*60 + mm * 60 + ss;
		if (totalSec == 0) return "-";
		int speedPerSec = (int) ((dd * 100 + _dd) / (totalSec / 60.0 / 60.0));
		return (speedPerSec / 100) + "." + ((speedPerSec % 100) < 10 ? 
				"0" + speedPerSec % 100 : speedPerSec % 100) + 
				(unit.compareTo("m")==0 ? " mph" : " km/h");
	}
	
	// For STATISTICS:
	
	// utility:
	public long getDistDec() {
		return (100 * dd + _dd); }
	public long getTimeSec() {
		return (60 * 60 * h + 60 * mm + ss); }
	
	// Performance Score - subjective score '3' - '10'
	public String getPerfScore(int avgDist, int avgPace) {
		if (avgDist == 0 || avgPace == 0)	return "";
		long dScore = Math.round((((100.0 * dd + _dd) / avgDist) - 1.0) / 0.2);
		double thisPace = (60.0 * 60 * h + 60 * mm + ss) / (dd * 100 + _dd);
		//Log.i("run", "pScore RAW: " + (avgPace / thisPace - 100) / 10);
		long pScore = Math.round((avgPace / thisPace - 100) / 10);
		Log.i("run", "dScore: " + dScore + ", pScore: " + pScore);
		long perfScore = 7 + dScore + pScore;
		if (perfScore < 3)	return "3";
		if (perfScore > 10) return "10";
		return "" + perfScore;
	}
	
	// Distance performance - % or average
	public String getPerfDist(int avgDistDec) {
		if (avgDistDec == 0)
			return "-";
		return 100 * getDistDec() / avgDistDec + "%";
	}
	
	// Pace (speed) performance - % of average
	public String getPerfPace(int avgPaceSec) {
		int totalSec = (60 * 60 * h + 60 * mm + ss);
		if (dd + _dd == 0)  return "-";
		int paceInSec = totalSec * 100 / (dd * 100 + _dd);
		if (paceInSec == 0)  return "-";		
		int prct = 100 *  avgPaceSec / paceInSec;
		return prct + "%";
	}
	
	public String toString() {
		return (date.get(Calendar.MONTH) + 1)+"/"+date.get(Calendar.DAY_OF_MONTH)+"/"+
					date.get(Calendar.YEAR)+","+dd+"."+_dd+","+unit+","+h+":"+mm+":"+ss;
	}
	
	public void switchDetails() {
		if (expanded)   expanded = false;
		else expanded = true;
	}
}
