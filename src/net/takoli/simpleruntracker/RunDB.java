package net.takoli.simpleruntracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class RunDB {

	private ArrayList<Run> runList;
	private final String FILE_NAME = "SimpleRunTracker_runList.csv";
	File intDir, extDownloadsDir;

	public RunDB(Context context) {
		runList = new ArrayList<Run>();
		Run nRun;
		// /data/data/net.takoli.simpleruntracker/files/ - where OpenFileOutput saves
		try {
			FileOutputStream outputStream = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
			outputStream.close();     // we just made sure the file existed in a tricky way
			FileInputStream inputStream = context.openFileInput(FILE_NAME);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				nRun = processLine(line);
				runList.add(nRun);
				Log.i("run", runList.size() + ": " + line);
			}
			inputStream.close();
		} catch (Exception e) {
			Toast.makeText(context,"Can't read SimpleRunTracker_runList.csv", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	public Run processLine(String line) {
		String[] fields = line.split(",");
		Log.i("run", fields[0] +fields[1] + fields[2]);
		String[] distSt = fields[0].split("\\.");
		String[] timeSt = fields[2].split("\\:");
		return new Run(Integer.parseInt(distSt[0]), Integer.parseInt(distSt[1]), fields[1], 
				Integer.parseInt(timeSt[0]), Integer.parseInt(timeSt[1]), Integer.parseInt(timeSt[2]));
	}
	
	public ArrayList<Run> getList() {
		return runList;
	}

	public void addNewRun(Context context, String line) {
		runList.add(processLine(line));
		// to append the file with new runs
		try {
			FileOutputStream outputStream = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
			outputStream.write((line+"\n").getBytes());
			outputStream.close();
		} catch (Exception e) {
			Toast.makeText(context,"SimpleRunTracker_runList.csv is not reachable to append",Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	public void saveToExternal(Context context) {
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
        		
			Toast.makeText(context, "Saved on SD Card",Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(context, "File write error", Toast.LENGTH_LONG)
					.show();
		}
	}

}
