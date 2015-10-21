package com.omeletlab.argora.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Network {
	Context mContext;

	public Network(Context context) {
		this.mContext = context;

	}

	public boolean isNetworkConnected() {
		ConnectivityManager connectivity = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						// Toast.makeText(getApplicationContext(),
						// info[i].toString(), Toast.LENGTH_LONG).show();
						return true;
					}

		}
		return false;
	}

	public boolean isWifiConnected() {

		 ConnectivityManager connec = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		    NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		    // Here if condition check for wifi and mobile network is available or not.
		    // If anyone of them is available or connected then it will return true, otherwise false;

		    if (wifi.isConnected()) {
		        
		    	Toast.makeText(mContext, "wifi enavle", Toast.LENGTH_LONG).show();
		        
		        return true;
		    } else if (mobile.isConnected()) {
		    	
		    	Toast.makeText(mContext, "mobile network conntected", Toast.LENGTH_LONG).show();
		    	
		        return true;
		    }
		    return false;

}
}