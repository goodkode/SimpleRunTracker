package net.takoli.simpleruntracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class RunDB {

	private ArrayList<Run> listOfRuns;
	private final String FILE_NAME = "SimpleRunTracker_runList.csv";
	File intDir, extDownloadsDir;
	File intFile, extFile;
	FileReader fileReader;
	FileOutputStream outputStream;

	public RunDB(Context context) {
		// same as openFileOutput(String, int) /data/data/net.takoli.simpleruntracker/files/
		File intDir = context.getFilesDir();  
		//listOfRuns = new ArrayList<Run>();
	}

	public void addNewRun(Context context, String line) {
		// to append the file with new runs
		try {
			outputStream = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
			outputStream.write((line+"\n").getBytes());
			outputStream.close();
		} catch (Exception e) {
			Toast.makeText(context,
					"SimpleRunTracker_runList.csv is not reachable to append",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	public void saveToExternal(Context context) {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Toast.makeText(context, "SD Card is not available",Toast.LENGTH_LONG).show();
			return;
		}
		try {
			extDownloadsDir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File src = new File(context.getFilesDir(), FILE_NAME);
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
