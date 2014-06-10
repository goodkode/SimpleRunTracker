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
	private long sumDistDec, sumTimeSec;
	
	private final String FILE_NAME = "RunTracker-runlist.csv";
	private int MAXSIZE;
	private Calendar FROMDATE;
	private boolean MAXSIZEisUsed;
	private File intDir, extDownloadsDir;

	// This will run every time the app starts up (or OnCreate is called...)
	public RunDB(Context context) {
		runList = new ArrayList<Run>();
		sumDistDec = 0;
		sumTimeSec = 0;
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
				sumDistDec += nRun.getDistDecInM();
				sumTimeSec += nRun.getTimeInSec();
			}
			inputStream.close();
		} catch (Exception e) {
			Toast.makeText(context,"Can't read " + FILE_NAME, Toast.LENGTH_LONG).show();
			e.printStackTrace(); }
		Log.i("run", "RunDB initiated");
	}
	
	public void setDBLimit(String limit) {
		MAXSIZEisUsed = true;
		try {
			MAXSIZE = Integer.parseInt(limit);
		} catch (NumberFormatException nfe) {
			MAXSIZEisUsed = false;
		}
		if (!MAXSIZEisUsed) {
			String[] dateSt;
			try {
				dateSt = limit.split("/");
			} catch (Exception e) {
				Log.i("run", "setDBLimit string split error");
				return;
			}
			FROMDATE.set(Calendar.MONTH, Integer.parseInt(dateSt[0]));
			FROMDATE.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateSt[1]));
			FROMDATE.set(Calendar.YEAR, Integer.parseInt(dateSt[2]));
		}
		ensureDBLimit();
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
				sumDistDec -= runList.get(i).getDistDecInM();
				sumTimeSec -= runList.get(i).getTimeInSec();
			}
			if (toDelete > 0) {
				runList.subList(0, toDelete).clear();
				Log.i("run", "ensureDBLimit NUM delete: " + toDelete); }
		}
		else {
			int toDelete = 0;
			while (toDelete < size && runList.get(toDelete).date.before(FROMDATE)) {
				sumDistDec -= runList.get(toDelete).getDistDecInM();
				sumTimeSec -= runList.get(toDelete).getTimeInSec();
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
		sumDistDec += newRun.getDistDecInM();
		sumTimeSec += newRun.getTimeInSec();
	}
	
	public void updateRun(int pos, int[] updates) {
		Run toUpdate = runList.get(pos);
		sumDistDec -= toUpdate.getDistDecInM();
		sumTimeSec -= toUpdate.getTimeInSec();
		toUpdate.dd = updates[0];
		toUpdate._dd = updates[1];
		toUpdate.h = updates[2];
		toUpdate.mm = updates[3];
		toUpdate.ss = updates[4];
		sumDistDec += toUpdate.getDistDecInM();
		sumTimeSec += toUpdate.getTimeInSec();
	}
	
	public void removeRun(int pos) {
		Run toUpdate = runList.get(pos);
		sumDistDec -= toUpdate.getDistDecInM();
		sumTimeSec -= toUpdate.getTimeInSec();
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
	public int getAvgDistDec() {
		if (runList.size() == 0) return 0;
		else return (int) (1.0 * sumDistDec / runList.size());
	}
	public int getAvgPaceSec() {
		if (sumDistDec == 0) return 0;
		else return (int) (sumTimeSec * 100 / sumDistDec);
	}
	public int getAvgSpeedDecInMPH() {
		if (sumDistDec == 0) return 0;
		else return (int) ((sumDistDec) / (sumTimeSec / 60.0 / 60.0));
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
		sumDistDec = 0;
		sumTimeSec = 0;
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
