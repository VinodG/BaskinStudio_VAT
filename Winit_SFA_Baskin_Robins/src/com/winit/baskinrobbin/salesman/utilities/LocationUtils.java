package com.winit.baskinrobbin.salesman.utilities;

import android.location.Location;


public class LocationUtils
{
	public static float getDist(double lat1, double lng1, double lat2, double lng2)
	 {
		 float [] dist = new float[1];
		 Location.distanceBetween(lat1, lng1, lat2, lng2, dist);
		 return dist[0];
	 }
}
