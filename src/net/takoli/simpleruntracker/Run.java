package net.takoli.simpleruntracker;

import java.util.Date;

public class Run {
	boolean expanded = false;
	Date runDate;
	String test;
	int distM;
	int distmDec;
	
	public Run(String test) {
		this.test = test;
	}
	
	public void switchDetails() {
		if (expanded)
			expanded = false;
		else expanded = true;
	}
	
	public static class Time {
		private h, m, s;
		public Time(int hour, int min, int sec) {
			h = hour;
			m = min;
			s = sec;
		}
		
	}
	
	public static class Distance {
		private char unit;
		private float dist;
		private final float CONV = 1.609344f  // 1 mile in km
		
		public Distance(float distance, char unit) {
			dist = distance;
			this.unit = unit;
		}
		
		public void convertM2Km() {
			
		}
		
		public void convertKm2M() {
			
		}
	}
}
