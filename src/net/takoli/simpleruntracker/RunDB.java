package net.takoli.simpleruntracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class RunDB {

	private ArrayList<Run> listOfRuns;
	private final String FILE_NAME = "SimpleRunTracker_runList.csv";
	File appIntDir, appSDDir, appSDCardDOCSDir;
	File intFile, extFile;
	FileReader fileReader;
	FileOutputStream outputStream;

	public RunDB(Context context) {
		appIntDir = context.getFilesDir();
		appSDDir = context.getDir("SimpleRunTracker", Context.MODE_WORLD_READABLE);
		// File f = context.getExternalFilesDir(type) see help in here
		appSDCardDOCSDir = new File(Environment.getExternalStoragePublicDirectory
										(Environment.DIRECTORY_DOWNLOADS), FILE_NAME);
		intFile = new File(appIntDir, FILE_NAME);
		listOfRuns = new ArrayList<Run>();
	}

	public void newRun(Context context) {
		// to append the file with new runs
		try {
			outputStream = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
			outputStream.write("1, 2, test, 1/2/3".getBytes());
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
			String sdCard = Environment.getExternalStorageDirectory().toString();	         
	        
			File src = new File(context.getFilesDir(), FILE_NAME);
			//File dst = new File (sdCard + "/SimpleRunTracker/SRT.txt");
			File dst = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "SimpleRunTracker");
			FileInputStream inStream = new FileInputStream(src);
			FileOutputStream outStream = new FileOutputStream(dst);
			FileChannel inChannel = inStream.getChannel();
			FileChannel outChannel = outStream.getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
			inStream.close();
			outStream.close();
			Toast.makeText(context, "Saved on SD Card",Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(context, "File write error", Toast.LENGTH_LONG)
					.show();
		}
	}

}
