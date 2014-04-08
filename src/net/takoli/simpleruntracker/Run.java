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
}
