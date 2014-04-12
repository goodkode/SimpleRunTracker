package net.takoli.simpleruntracker;

import android.content.Context;
import android.widget.Toast;

public class U {
	public static void slog(Context context, String toBeLogged) {
		Toast.makeText(context, toBeLogged, Toast.LENGTH_SHORT).show();
	}
}
