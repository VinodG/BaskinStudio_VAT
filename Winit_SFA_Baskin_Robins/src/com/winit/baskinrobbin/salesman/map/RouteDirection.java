package com.winit.baskinrobbin.salesman.map;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class RouteDirection
{
	private String url;
	private static String str;
	private ArrayList<String> MallDistance = new ArrayList<String>();
	private ArrayList<String> dir = new ArrayList<String>();
	private ArrayList<String> maneuver1;

	public String setLatlang(LatLng start, LatLng end) 
	{
		 url = "http://maps.googleapis.com/maps/api/directions/json?"
				+ "origin=" + start.latitude + "," + start.longitude
				+ "&destination=" + end.latitude + "," + end.longitude
				+ "&sensor=false&units=metric&mode=driving";
		 
		 String status = "";
			
			try
			{
				HttpClient httpClient = new DefaultHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpPost httpPost = new HttpPost(url);
				HttpResponse response = httpClient.execute(httpPost, localContext);
				str = EntityUtils.toString(response.getEntity());
				//Log.e("Response", ""+str.toString());
				
				JSONObject jsonObject = new JSONObject(str);
				
				status = jsonObject.getString("status");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return status;
	}
	
	
	public Bundle getDirection() throws JSONException 
	{
		JSONObject jsonObject 		= new JSONObject(str);
		JSONArray routesJsonArray 	= jsonObject.getJSONArray("routes");
		JSONObject routesJsonObject = routesJsonArray.getJSONObject(0);
		JSONArray legsJsonarray 	= routesJsonObject.getJSONArray("legs");
		JSONObject jsonobject1 		= legsJsonarray.getJSONObject(0);
		
		String start_address 		=jsonobject1.getString("start_address");
		String end_address 			=jsonobject1.getString("end_address");
		
		JSONObject jsonobjectDistance = jsonobject1.getJSONObject("distance");
		String Distance = jsonobjectDistance.getString("text");
		JSONObject jsonobjectDuration = jsonobject1.getJSONObject("duration");
		String Duration = jsonobjectDuration.getString("text");

		Bundle bundle = new Bundle();
		bundle.putString("dis",Distance);
		bundle.putString("dur",Duration);
		bundle.putString("start_address", start_address);
		bundle.putString("end_address", end_address);
		
		//Log.i("", Distance);

		JSONArray jsonArraySteps 	= jsonobject1.getJSONArray("steps");
		ArrayList<LatLng> geopoints = new ArrayList<LatLng>();
		ArrayList<LatLng> steps 	= new ArrayList<LatLng>();
		maneuver1 					= new ArrayList<String>();
		 
		for (int i = 0; i < jsonArraySteps.length(); i++) 
		{
			String maneuver="";
			JSONObject josnobject = jsonArraySteps.getJSONObject(i);

			String instruction =josnobject.getString("html_instructions");
			
			try
			{
				if(i!=0)
					maneuver = josnobject.getString("maneuver");
			}
			catch(JSONException e)
			{
				maneuver = "default";
			}
			
			dir.add(removeContentSpanObjects(instruction));
			
			maneuver1.add(maneuver);

			Log.i("", removeContentSpanObjects(instruction));
			
			JSONObject jsonobjectStartLocation = josnobject
					.getJSONObject("start_location");

			String lat = jsonobjectStartLocation.getString("lat");

			String lang = jsonobjectStartLocation.getString("lng");

			double latitude = Double.parseDouble(lat);

			double longitude = Double.parseDouble(lang);

			geopoints.add(new LatLng(latitude, longitude));
			
			steps.add(new LatLng(latitude, longitude));
			
			JSONObject jsonobjectDistance1 = josnobject.getJSONObject("distance");

			String geoDistance = jsonobjectDistance1.getString("text");
			
			MallDistance.add(geoDistance);

			JSONObject jsonobjectPolyline = josnobject
					.getJSONObject("polyline");

			String points = jsonobjectPolyline.getString("points");

			ArrayList<LatLng> arr = decodePoly(points);

			for (int j = 0; j < arr.size(); j++) 
			{
				geopoints.add(new LatLng(arr.get(j).latitude,
						arr.get(j).longitude));
			}

			JSONObject jsonobjectEndlocation = josnobject
					.getJSONObject("end_location");

			String lat1 = jsonobjectEndlocation.getString("lat");
			String lang1 = jsonobjectEndlocation.getString("lng");

			double latitude1 = Double.parseDouble(lat1);
			double longitude1 = Double.parseDouble(lang1);

			geopoints.add(new LatLng(latitude1, longitude1));

		}

		bundle.putParcelableArrayList("geo", geopoints);
		
		bundle.putParcelableArrayList("steps", steps);
		bundle.putStringArrayList("inst", dir);
		bundle.putStringArrayList("geopointsdis", MallDistance);
		bundle.putStringArrayList("maneuver", maneuver1);
		//Log.i("", dir.size()+"");

		Log.e("maneuver1", maneuver1+"");

		return bundle;

	}

	private ArrayList<LatLng> decodePoly(String encoded) {
		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;
		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;
			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			poly.add(position);
		}
		return poly;
	}
	
	 private String removeContentSpanObjects(String sb)
	 {
		 SpannableStringBuilder spannedStr = (SpannableStringBuilder) Html
		 .fromHtml(sb.toString().trim());
		 Object[] spannedObjects = spannedStr.getSpans(0, spannedStr.length(),
		 Object.class);
		 for (int i = 0; i < spannedObjects.length; i++) {
		 // if (!(spannedObjects[i] instanceof URLSpan) &&
		 // !(spannedObjects[i] instanceof StyleSpan))
		 if (spannedObjects[i] instanceof ImageSpan)
		 spannedStr.replace(spannedStr.getSpanStart(spannedObjects[i]),
		 spannedStr.getSpanEnd(spannedObjects[i]), "");
		 // spannedStr.removeSpan(spannedObjects[i]);
		 }
		 // spannedStr.clearSpans();
		 
		 String str= spannedStr.toString();
		 return str;
		 }

}
