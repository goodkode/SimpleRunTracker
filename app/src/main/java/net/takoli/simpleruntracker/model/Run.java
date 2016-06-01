package net.takoli.simpleruntracker.model;

import android.content.Context;

import net.takoli.simpleruntracker.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Run {

    public static final String KM = "km";
    private static final float KM_TO_MI = 1.60934f;
	private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

    public int dd, _dd;		// distance in units and in decimals
	public int h, mm, ss;   // timeU in hour, min, and sec
	int distU;		 // COMMON UNIT (100 * MI)
	int timeU;		 // COMMON UNIT (SEC)
	int paceU;		 // COMMON UNIT (SEC / MI)
	String unit;     // miles or kilometers
    Calendar date;

	public Run(Calendar date, int dd, int _dd, String unit, int h, int mm, int ss) {
		this.date = date;
		this.dd = dd;
		this._dd = _dd;
		this.unit = unit;
		this.h = h;
		this.mm = mm;
		this.ss = ss;
		// Common units:
		distU = 100 * dd + _dd;
		if (unit.compareTo(KM) == 0)
			distU = km2mi(distU);
		timeU = 60 * 60 * h + 60 * mm + ss;
		paceU = 100 * timeU / distU;
	}
	
	public Run(String line) {
		String[] fields = line.split(",");
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
		// Common units:
		distU = 100 * dd + _dd;
		if (unit.compareTo(KM) == 0)
			distU = km2mi(distU);
		timeU = 60 * 60 * h + 60 * mm + ss;
		paceU = 100 * timeU / distU;
	}
	
	public void updateRun(int dd, int _dd, int h, int mm, int ss) {
		this.dd = dd;
		this._dd = _dd;
		this.h = h;
		this.mm = mm;
		this.ss = ss;
		// Common units:
		distU = 100 * dd + _dd;
		if (unit.compareTo(KM) == 0)
			distU = km2mi(distU);
		timeU = 60 * 60 * h + 60 * mm + ss;
		paceU = 100 * timeU / distU;
	}
	
	public String getDateString(Context context) {
		Calendar today = Calendar.getInstance();
		if (date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
				date.get(Calendar.YEAR) == today.get(Calendar.YEAR))
			return context.getResources().getString(R.string.today);
		else if (date.get(Calendar.DAY_OF_YEAR) == (today.get(Calendar.DAY_OF_YEAR) - 1) &&
				date.get(Calendar.YEAR) == today.get(Calendar.YEAR))
			return context.getResources().getString(R.string.yesterday);
		else
			return dateFormat.format(date.getTimeInMillis());
	}
	
	public String getDistanceString() {
		return ((dd < 10 ? " " + dd : dd) + "." + 
					(_dd < 10 ? ("0" + _dd) : _dd) + " " + unit);
	}
	
	public String getTimeString() {
		String sTime = h + ":";
		if (mm < 10)   sTime += "0" + mm + ":";
		else   sTime += mm + ":";
		if (ss < 10)   sTime += "0" + ss;
		else   sTime += ss;
		return  sTime + "s ";
	}
	
	public String getPaceString() {
		int paceSec = (100 * timeU) / (100 * dd + _dd);
		return  sec2MMss(paceSec) + " min/" + unit;
	}
	
	public String getSpeedString() {
		int speed = Math.round((100 * dd + _dd) / (timeU / 60f / 60f));
		return (speed / 100) + "." + ((speed % 100) < 10 ? "0" + (speed % 100) : (speed % 100)) +
				(unit.compareTo(KM) != 0 ? " mph" : " km/h");
	}
	
	
	// For STATISTICS:
	
	public long getDistUNIT() {
		return distU; }
	public int getTimeUNIT() {
		return timeU; }
	public int getPaceUNIT() {
		return paceU; }
	public int getSpeedUNIT() {
		return Math.round(distU / (timeU / 60f / 60f)); }

	
	// For PERFORMANCE SCORE - subjective score '3' - '10':
	
	public String getPerfScore(int avgDistDec, int avgPaceSec) {
		if (avgDistDec == 0 || avgPaceSec == 0)	return "";
		// each 20% over average distance is a point extra
		double dPrcnt = (100.0 * getDistUNIT() / avgDistDec) - 100;
		long dScore = Math.round(dPrcnt / 20);
		// each 2% over average speed is a point extra
		double sPrcnt = avgPaceSec / ((60.0 * 60 * h + 60 * mm + ss) / (dd * 100 + _dd)) - 100;
		long pScore = Math.round(sPrcnt / 2);
		// factor this score to be between 3 and 10 (7 as average)
		long perfScore = 7 + dScore + pScore;
		if (perfScore < 3)	return "3";
		if (perfScore > 10) return "10";
		return "" + perfScore;
	}
	public String getPerfDist(int avgDistDec) {
		if (avgDistDec == 0)
			return "-";
		return 100 * getDistUNIT() / avgDistDec + "%";
	}
	public String getPerfPace(int avgPaceSec) {
		int totalSec = (60 * 60 * h + 60 * mm + ss);
		if (dd + _dd == 0)  return "-";
		int paceInSec = totalSec * 100 / (int) getDistUNIT();
		if (paceInSec == 0)  return "-";		
		int prct = 100 *  avgPaceSec / paceInSec;
		return prct + "%";
	}
	
	public String toString() {
		return (date.get(Calendar.MONTH) + 1)+"/"+date.get(Calendar.DAY_OF_MONTH)+"/"+
					date.get(Calendar.YEAR)+","+dd+"."+_dd+","+unit+","+h+":"+mm+":"+ss;
	}
	
	// utility functions
	public static String dec2string(long NNnn) {
		long whole = NNnn / 100;
		long dec = NNnn % 100;
		if (dec < 10)
			return whole + ".0" + dec;
		else
			return whole + "." + dec;
	}

	public static int km2mi(float km) {
		int m =  Math.round(km / KM_TO_MI);
		if (m % 100 == 99)	m++;
		if (m % 100 == 1)  m--;
		return m;
	}

	public static int mi2km(float mi) {
		int km = Math.round(mi * KM_TO_MI);
		if (km % 100 == 99)	km++;
		if (km % 100 == 1)  km--;
		return km;
	}

	public static String sec2MMss(int sec) {
		String MM = "" + (sec / 60);
		sec %= 60;
		if (sec < 10)
			return MM + ":0" + sec;
		else
			return MM + ":" + sec;
	}

	public static String sec2hMMss(int sec) {
		String h = "" + (sec / 60 / 60);
		sec /= 60;
		String MM = (sec / 60) < 10 ? ("0" + (sec/60)) : ("" + (sec/60));
		sec %= 60;
		if (sec < 10)
			return h + ":" + MM + ":0" + sec;
		else
			return h + ":" + MM + ":" + sec;
	}

	public static String getFullStringDate(int year, int month, int day) {
		final Calendar customDate = setCustomDate(month, day, year);
		return dateFormat.format(customDate.getTimeInMillis());
	}

	public static String getFullStringDate(String stringDate) {
		String[] stringDateArray = stringDate.split("/");
		try {
			return getFullStringDate(Integer.parseInt(stringDateArray[2]), 
									 Integer.parseInt(stringDateArray[0]) - 1,
									 Integer.parseInt(stringDateArray[1]));
		} catch (Exception e) {
			return getFullStringDate(2010, 0, 1);
		}
	}

	public static Calendar string2calendar(String stringDate) {
		Calendar date = Calendar.getInstance();
		String[] stringDateArray = stringDate.split("/");
		try {
			date.setTimeInMillis(0);
			date.set(Calendar.MONTH, (Integer.parseInt(stringDateArray[0]) - 1));
			date.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(stringDateArray[1]));
			date.set(Calendar.YEAR, Integer.parseInt(stringDateArray[2]));
			return date;
		} catch (Exception e) {
			date.setTimeInMillis(0);
			date.set(Calendar.MONTH, 0);
			date.set(Calendar.DAY_OF_MONTH, 1);
			date.set(Calendar.YEAR, 2010);
			return date;
		}
	}

	public static Calendar setTodayDate() {
		Calendar today =  Calendar.getInstance();
		return today;
	}

	public static Calendar setYesterdayDate() {
		Calendar yesterday =  Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);
		return yesterday;
	}

	public static Calendar setCustomDate(int month, int day, int year) {
		Calendar date =  Calendar.getInstance();
		date.set(year, month, day);
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		return date;
	}
}
