package com.winit.baskinrobbin.salesman.common;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

public class LocationUtility {
	private LocationManager lm;
	private LocationResult locationResult;
	private Location lastLocation = null;
	private boolean gps_enabled = false;
	private boolean network_enabled = false;
	private static final String TAG = LocationUtility.class.getSimpleName();
	private Object lock = new Object();
	private Context context;
	public LocationUtility(Context context) {
		this.context = context;
	}
	
	public boolean getLocation(LocationResult result) {
		Looper looper = Looper.myLooper();
		if (looper == null) {
			Looper.prepare();
		}
		
		lastLocation = null;
		
		// I use LocationResult callback class to pass location value from
		// MyLocation to user code.
		locationResult = result;
		if (lm == null) {
			lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		}

		try {
			network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

			if (!gps_enabled && !network_enabled) {
				return false;
			}
		} catch (Exception ex) {
			Log.w(TAG, "Exception checking location manager status", ex);
		}
		
		new Thread(new GetLastLocation()).start();
		
		synchronized (lock) {
			if (network_enabled) {
				lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 100,	locationListenerNetwork);
			}

			if (gps_enabled) {
				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100,	locationListenerGps);
			}
		}

		return true;
	}

	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location loc) {
			Object o1 = this;
			Object o2 = locationListenerGps;
			boolean same = o1 == o2;
//			Log.v(TAG, "this and locationListenerNetwork are equal: " + same);
			
			lastLocation = loc;
			if (lm != null) {
				lm.removeUpdates(this);
				if (locationListenerNetwork != null) {
					lm.removeUpdates(locationListenerNetwork);
				}
			}
			synchronized (lock) {
				lock.notifyAll();
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location loc) {
			Object o1 = this;
			Object o2 = locationListenerNetwork;
			boolean same = o1 == o2;
//			Log.v(TAG, "this and locationListenerNetwork are equal: " + same);
			
			lastLocation = loc;
			
			if (lm != null) {
				lm.removeUpdates(this);
				if (locationListenerGps != null) {
					lm.removeUpdates(locationListenerGps);
				}
			}
			synchronized (lock) {
				lock.notifyAll();
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	class GetLastLocation implements Runnable {
		@Override
		public void run() {
			try {
				synchronized(lock) {
//					Log.d(TAG, "About to wait for location");
					lock.wait(1000L);
				} 
			} catch (InterruptedException e) {
				// we are not interested
			}
			
			if (lastLocation != null) {
				locationResult.gotLocation(lastLocation);
				return;
			}

			Location net_loc = null, gps_loc = null;

			if (gps_enabled && lm != null) {
				gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				lm.removeUpdates(locationListenerGps);

			}

			if (network_enabled && lm != null && locationListenerNetwork!= null) {
				net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				lm.removeUpdates(locationListenerNetwork);

			}
			
			// if there are both values use the latest one
			if (gps_loc != null && net_loc != null) {
				if (gps_loc.getTime() > net_loc.getTime()) {
					locationResult.gotLocation(gps_loc);
				} else {
					locationResult.gotLocation(net_loc);

				}
				return;
			}

			if (net_loc != null) {
				locationResult.gotLocation(net_loc);
				return;
			}

			if (gps_loc != null) {
				locationResult.gotLocation(gps_loc);
				return;
			}

			locationResult.gotLocation(null);
		}
	}

	public void stopGpsLocUpdation() {
		if (lm != null) {
			lm.removeUpdates(locationListenerNetwork);
			lm.removeUpdates(locationListenerGps);
		}
		lm = null;
	}

	public interface LocationResult {
		public void gotLocation(Location loc);
	}

}
