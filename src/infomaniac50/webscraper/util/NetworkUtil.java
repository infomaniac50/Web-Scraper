package infomaniac50.webscraper.util;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkUtil {
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo().isConnected();
	}
}
