package net.takoli.simpleruntracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class RunDB {

	private ArrayList<Run> runList;
	private long sumDistU, sumTimeU;	
	private final String FILE_NAME = "RunTracker-runlist.csv";
	private int MAXSIZE;
	private Calendar FROMDATE;
	private boolean MAXSIZEisUsed;
	private File intDir, extDownloadsDir;

	// This will run every time the app starts up (or OnCreate is called...)
	public RunDB(Context context) {
		runList = new ArrayList<Run>();
		sumDistU = 0;
		sumTimeU = 0;
		Run nRun;
		// /data/data/net.takoli.simpleruntracker/files/ - where OpenFileOutput saves
		try {
			FileOutputStream outputStream = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
			outputStream.close();     // we just made sure the file existed in a tricky way
			FileInputStream inputStream = context.openFileInput(FILE_NAME);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				nRun = new Run(line);
				runList.add(nRun);
				// for statistics:
				sumDistU += nRun.getDistUNIT();
				sumTimeU += nRun.getTimeUNIT();
			}
			inputStream.close();
		} catch (Exception e) {
			Toast.makeText(context,"Can't read " + FILE_NAME, Toast.LENGTH_LONG).show();
			e.printStackTrace(); }
		//Log.i("run", "RunDB initiated");
	}
	
	public void setDBLimit(String limit) {
		MAXSIZEisUsed = true;
		try {
			MAXSIZE = Integer.parseInt(limit);
		} catch (NumberFormatException nfe) {
			MAXSIZEisUsed = false;
		} finally {
			if (!MAXSIZEisUsed) {
				Log.i("run", "DB: date");
				FROMDATE = Run.string2calendar(limit);
			}
			ensureDBLimit(); }
	}
	
	public void ensureDBLimit() {
		Collections.sort(runList, new Comparator<Run>() {
			public int compare(Run a, Run b) {
		        return a.date.compareTo(b.date);
		    }
		});
		int size = runList.size();
		if (MAXSIZEisUsed) {
			int toDelete = size - MAXSIZE;
			for (int i = 0; i < toDelete; i++) {
				sumDistU -= runList.get(i).getDistUNIT();
				sumTimeU -= runList.get(i).getTimeUNIT();
			}
			if (toDelete > 0) {
				runList.subList(0, toDelete).clear();
				Log.i("run", "ensureDBLimit NUM delete: " + toDelete); }
		}
		else {
			int toDelete = 0;
			while (toDelete < size && runList.get(toDelete).date.before(FROMDATE)) {
				sumDistU -= runList.get(toDelete).getDistUNIT();
				sumTimeU -= runList.get(toDelete).getTimeUNIT();
				toDelete++;
			}
			runList.subList(0, toDelete).clear();
			if (toDelete > 0)
				Log.i("run", "ensureDBLimit DATE delete: " + toDelete);
		}
	}
	
	public ArrayList<Run> getRunList() {
		return runList;
	}
	
	public boolean isEmpty() {
		return (runList.size() == 0);
	}
		
	public void addNewRun(Context context, Run newRun) {
		runList.add(newRun);
		sumDistU += newRun.getDistUNIT();
		sumTimeU += newRun.getTimeUNIT();
	}
	
	public void updateRun(int pos, int[] updates) {
		Run toUpdate = runList.get(pos);
		sumDistU -= toUpdate.getDistUNIT();
		sumTimeU -= toUpdate.getTimeUNIT();
		toUpdate.dd = updates[0];
		toUpdate._dd = updates[1];
		toUpdate.h = updates[2];
		toUpdate.mm = updates[3];
		toUpdate.ss = updates[4];
		sumDistU += toUpdate.getDistUNIT();
		sumTimeU += toUpdate.getTimeUNIT();
	}
	
	public void removeRun(int pos) {
		Run toUpdate = runList.get(pos);
		sumDistU -= toUpdate.getDistUNIT();
		sumTimeU -= toUpdate.getTimeUNIT();
		runList.remove(pos);
	}
	
	public Run getLastRun() {
		int lastIndex = runList.size() - 1;
		if (lastIndex == -1)
			return null;
		return runList.get(lastIndex);
	}
	
	public int[] getLastValues() {
		Run lastRun = this.getLastRun();
		if (lastRun != null)
			return new int[] {lastRun.dd, lastRun._dd, lastRun.h, lastRun.mm, lastRun.ss};
		else
			return new int[] {0, 0, 0, 0, 0};
	}
	

	
	// for STATISTICS:
	public int getAvgDistUNIT() {
		return Math.round(1f * sumDistU / runList.size());
	}
	public int getAvgPaceUNIT() {
		return Math.round(100f * sumTimeU / sumDistU);
	}
	public int getAvgSpeedUNIT() {
		return Math.round((sumDistU) / (sumTimeU / 60.0f / 60.0f));
	}
	
	
	// for STATISTICS return Strings
	public String getAvgDistString(String unit) {
		if (runList.size() == 0) return "-";
		if (unit.equals("mi"))
			return Run.dec2string(getAvgDistUNIT());
		if (unit.equals("km"))
			return Run.dec2string(Run.mi2km(getAvgDistUNIT()));
		else return "-";
	}
	public String getMaxDistString(String unit) {
		int size = runList.size();
		if (size == 0) return "-";
		long maxDistUnit = runList.get(0).getDistUNIT();
		for (int i = 0; i < size; i++)
			if (runList.get(i).getDistUNIT() > maxDistUnit)
				maxDistUnit = runList.get(i).getDistUNIT();
		if (unit.equals("mi"))
			return Run.dec2string(maxDistUnit);
		if (unit.equals("km"))
			return Run.dec2string(Run.mi2km(maxDistUnit));
		else return "-";
	}
	public String getTotalDistString(String unit) {
		if (unit.equals("mi")) 
			return Run.dec2string(sumDistU); 
		if (unit.equals("km")) 
			return Run.dec2string(Run.mi2km(sumDistU)); 
		else return "-";
	}
	public String getAvgPaceString(String unit) {
		if (unit.equals("mi")) 
			return Run.sec2MMss(getAvgPaceUNIT()); 
		if (unit.equals("km")) 
			return Run.sec2MMss(Run.km2mi(getAvgPaceUNIT())); 
		else return "-";
	}
	public String getAvgSpeedString(String unit) {
		if (unit.equals("mi")) 
			return Run.dec2string(getAvgSpeedUNIT()); 
		if (unit.equals("km")) 
			return Run.dec2string(Run.mi2km(getAvgSpeedUNIT())); 
		else return "-";
	}
	public String getMaxPaceString(String unit) {
		int size = runList.size();
		if (size == 0) return "-";
		int maxPaceU = runList.get(0).getPaceUNIT();
		for (int i = 1; i < size; i++) 
			if (runList.get(i).getPaceUNIT() < maxPaceU)
				maxPaceU = runList.get(i).getPaceUNIT();
		if (unit.equals("mi")) 
			return Run.sec2MMss(maxPaceU); 
		if (unit.equals("km")) 
			return Run.sec2MMss(Run.km2mi(maxPaceU));
		else return "-";
	}
	public String getMaxSpeedString(String unit) {
		int size = runList.size();
		if (size == 0) return "-";
		long maxSpeedU = runList.get(0).getSpeedUNIT();
		for (int i = 1; i < size; i++) 
			if (runList.get(i).getSpeedUNIT() > maxSpeedU)
				maxSpeedU = runList.get(i).getSpeedUNIT();
		if (unit.equals("mi")) 
			return Run.dec2string(maxSpeedU); 
		if (unit.equals("km")) 
			return Run.dec2string(Run.mi2km(maxSpeedU));
		else return "-";
	}
	
	// save all changes
	public void saveRunDB(Context context) {
		ensureDBLimit();
		try {
			FileOutputStream outputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			for (int i = 0; i < runList.size(); i++)
				outputStream.write((runList.get(i).toString()+"\n").getBytes());
			outputStream.close();
		} catch (Exception e) {
			Toast.makeText(context, FILE_NAME +" can't be saved :(",Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	

	// Save DB to SD Card. Also considering Google Docs sync in the future
	public void saveToExternalMemory(Context context) {
		saveRunDB(context);
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Toast.makeText(context, "SD Card is not available",Toast.LENGTH_LONG).show();
			return;
		}
		try {
			intDir = context.getFilesDir();
			extDownloadsDir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File src = new File(intDir, FILE_NAME);
			File dst = new File(extDownloadsDir, FILE_NAME);			
			InputStream is = new FileInputStream(src);
        	OutputStream os = new FileOutputStream(dst);
        	byte[] data = new byte[is.available()];
			is.read(data);
        	os.write(data);
        	is.close();
    		os.close();	
		} catch (Exception e) {
			Toast.makeText(context, "File write error", Toast.LENGTH_LONG).show(); }
	}
	
	// delete ALL records
	public void deleteDB(Context context) {
		runList.clear();
		sumDistU = 0;
		sumTimeU = 0;
		try {
			context.deleteFile(FILE_NAME);
		} catch (Exception e) {
			Toast.makeText(context,"Can't delete " + FILE_NAME, Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	
	public Intent emailIntent(Context context) {
		try {
	    	Uri fileUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILE_NAME));
	    	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
	    	emailIntent.setType("text/plain"); 
	    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "RunTracker runlist for Excel"); 
	    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "RunTracker-runlist.csv is attached.\n\n" +
	    		"The file can be opened with any spreadsheet application, like Excel, or with any text editior"); 
	    	emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri); 
	    	return emailIntent;
		} catch(Exception e) {
			Toast.makeText(context, "Email is not sent", Toast.LENGTH_LONG).show();
		}
		return null;
	}
}
