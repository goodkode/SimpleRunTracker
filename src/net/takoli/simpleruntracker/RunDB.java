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
	File appIntDir, appSDCardDOCSDir;
	File intFile, extFile;
	FileReader fileReader;
	FileOutputStream outputStream;

	public RunDB(Context context) {
		File path1 = context.getFilesDir();  // where files created with openFileOutput(String, int) are stored.
		Log.i("run", "context.getFilesDir():" path1.toString());
		File path2 = context.getDir("getDir", Context.MODE_PRIVATE);  // data/data/net.takoli.simpleruntracker/getDir/..??
		Log.i("run", "context.getDir():" path2.toString());
		File path3 = context.getExternalFilesDir(Context.MODE_PRIVATE); //or:(null) // somewhere on Environment.getExternalStorageDirectory()
		Log.i("run", "context.getExternalFilesDir():" path3.toString());
		File path4 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);  // CHECK STATE!
		Log.i("run", "Environment.getExternalStoragePublicDirectory():" path4.toString());
		
		File f1 = new File(path1, "path1");
		File f2 = new File(path2, "path2");
		File f3 = new File(path3, "path3");
		File f4 = new File(path4, "path4");
		
		
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
			appSDCardDOCSDir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File src = new File(context.getFilesDir(), FILE_NAME);
			if (src == null) 
        			Toast.makeText(context, "src not found",Toast.LENGTH_LONG).show();
			File dst = new File(appSDCardDOCSDir, "test.csv");
			if (dst == null) 
        			Toast.makeText(context, "dst not found",Toast.LENGTH_LONG).show();
			
			InputStream is = new FileOutputStream(src);
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
