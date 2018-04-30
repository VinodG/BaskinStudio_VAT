package com.winit.baskinrobbin.salesman.map;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;

public class RouteMapActivity extends BaseActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener
		{
	private LinearLayout llMapView;
	private GoogleMap Map;
	private SupportMapFragment mMapFragment;
	private RouteDirection Routedirection;
	private String distance, duration, start_address, end_address;
	private LatLng from_latlang;
	private Bundle bundle;
	private LocationClient locationClient;
	private LocationRequest locationRequest;
	private double latitude, longitude, customerlat, customerlong;
	private ArrayList<String> geopiontsDis, maneuver, dir;
	private ArrayList<LatLng> geopionts, steps;
	private ImageView iv_action_right, iv_action_left, iv_directions;
	private int j = 0;
	private Polyline polyline;
	private TextView title;
	private ProgressDialog progressdialog;
	private TextToSpeech textToSpeech;
	private Preference preference;
	
	@Override
	public void initialize() 
	{
		llMapView = (LinearLayout) inflater.inflate(R.layout.activity_main, null);
		llBody.addView(llMapView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		locationRequest = new LocationRequest();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		preference = new Preference(this);
		if (getIntent().getExtras() != null)
		{
			customerlat 	= getIntent().getExtras().getFloat("Latitude");
			customerlong 	= getIntent().getExtras().getFloat("Longitude");
		}

		textToSpeech = new TextToSpeech(this, new OnInitListener() {
			@Override
			public void onInit(int status)
			{
				LogUtils.errorLog("status", "" + status);
			}
		});

		locationRequest.setInterval(5000);
		locationClient = new LocationClient(this, this, this);
		locationClient.connect();
		dir = new ArrayList<String>();
		steps = new ArrayList<LatLng>();
		iv_action_left = (ImageView) llMapView.findViewById(R.id.iv_action_left);
		iv_action_right = (ImageView) llMapView.findViewById(R.id.iv_action_right);
		iv_directions = (ImageView) llMapView.findViewById(R.id.iv_directions);
		title = (TextView) llMapView.findViewById(R.id.title);
		title.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		title.setText("Route Map");
		
		iv_action_right.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(dir == null || dir.size() <= 0 || maneuver == null || maneuver.size() <= 0 || steps == null || steps.size() <= 0 )
				{
					Toast.makeText(RouteMapActivity.this, "Unable to get location, Please check your internet connection.", Toast.LENGTH_SHORT).show();
				}
				else
				{
					LatLng geopointLatlang = null;
					float angle = 0;
					PolylineOptions rectLine = new PolylineOptions().width(12).color(Color.GREEN);
	
					if (j >= 0 && j < maneuver.size())
					{
						if (j > 0) 
						{
							polyline.remove();
						}
						iv_action_left.setVisibility(View.VISIBLE);
	
						LogUtils.errorLog("j =", "" + j);
						if(steps.size() > j)
							geopointLatlang = steps.get(j);
	
						int drawable = 0;
	
						String dir1 = dir.get(j);
	
						String[] list = dir1.split("\n");
	
						dir1 = list[0];
						if (j == 0) {
							drawable = (R.drawable.continue_head_white);
							title.setText(dir.get(j));
	
						} else if (maneuver.get(j).equals("default")) {
							if (dir1.contains("Continue")
									|| dir1.contains("continue")
									|| dir1.contains("Head")) {
								drawable = (R.drawable.continue_head_white);
	
							} else if (dir1.contains("Slight left")
									|| dir1.contains("slights left")) {
								drawable = (R.drawable.slight_left_white);
	
							} else if (dir1.contains("Slight right")
									|| dir1.contains("slights right")) {
								drawable = (R.drawable.slight_right_white);
	
							} else if (dir1.contains("Turn left")
									|| dir1.contains("turns left")
									|| dir1.contains("left")) {
								drawable = (R.drawable.turn_left_white);
	
							} else if (dir1.contains("Turn right")
									|| dir1.contains("turns right")
									|| dir1.contains("right")) {
								drawable = (R.drawable.turn_right_white);
	
							} else if (dir1.contains("roundabout")) {
								drawable = (R.drawable.roundabout_right_white);
	
							} else if (dir1.contains("U-turn")
									|| dir1.contains("u-turn")) {
								drawable = (R.drawable.u_turn_white);
	
							} else if (dir1.contains("left")) {
								drawable = (R.drawable.turn_left_white);
	
							} else if (dir1.contains("keep-left")
									|| dir1.contains("keep left")) {
								drawable = (R.drawable.continue_head_white);
	
							} else if (dir1.contains("keep-right")
									|| dir1.contains("keep right")) {
								drawable = (R.drawable.continue_head_white);
	
							}
	
							else if (dir1.contains("ramp-left")) {
								drawable = (R.drawable.continue_head_white);
	
							} else if (dir1.contains("ramp-right")) {
								drawable = (R.drawable.continue_head_white);
	
							} else if (dir1.contains("ramp")
									|| dir1.contains("Take the ramp")) {
								drawable = (R.drawable.continue_head_white);
	
							} else if (dir1.contains("exit")) {
								drawable = (R.drawable.continue_head_white);
	
							} else if (dir1.contains("merge")
									|| dir1.contains("Merge")) {
								drawable = (R.drawable.continue_head_white);
	
							}
							textToSpeech.speak(dir1, TextToSpeech.QUEUE_FLUSH, null);
							title.setText(dir1);
	
						} else {
							if (maneuver.get(j).contains("turn-left")) {
								drawable = (R.drawable.turn_left_white);
	
							} else if (maneuver.get(j).contains("turn-right")) {
								drawable = (R.drawable.turn_right_white);
	
							} else if (maneuver.get(j).contains("turn-slight-left")) {
								drawable = (R.drawable.slight_left_white);
	
							} else if (maneuver.get(j)
									.contains("turn-slight-right")) {
								drawable = (R.drawable.slight_right_white);
	
							} else if (maneuver.get(j).contains("straight")) {
								drawable = (R.drawable.continue_head_white);
	
							} else if (maneuver.get(j).contains("roundabout-left")) {
								drawable = (R.drawable.round_about_left_white);
	
							} else if (maneuver.get(j).contains("roundabout-right")) {
								drawable = (R.drawable.round_about_white);
	
							} else if (maneuver.get(j).contains("turn-sharp-left")) {
								drawable = (R.drawable.sharp_left_white);
	
							} else if (maneuver.get(j).contains("turn-sharp-right")) {
								drawable = (R.drawable.sharp_right_white);
	
							} else if (maneuver.get(j).contains("uturn-right")) {
								drawable = (R.drawable.right_u_turn_white);
	
							} else if (maneuver.get(j).contains("uturn-left")) {
								drawable = (R.drawable.left_u_turn_white);
	
							} else if (maneuver.get(j).contains("straight")) {
								drawable = (R.drawable.continue_head_white);
	
							}
	
							else if (maneuver.get(j).contains("keep-left")
									|| maneuver.get(j).contains("keep left")) {
								drawable = (R.drawable.continue_head_white);
	
							} else if (maneuver.get(j).contains("keep-right")
									|| maneuver.get(j).contains("keep right")) {
								drawable = (R.drawable.continue_head_white);
	
							}
	
							else if (maneuver.get(j).contains("ramp-left")) {
								drawable = (R.drawable.continue_head_white);
	
							} else if (maneuver.get(j).contains("ramp-right")) {
								drawable = (R.drawable.continue_head_white);
	
							} else if (maneuver.get(j).contains("ramp")
									|| maneuver.get(j).contains("Take the ramp")) {
								drawable = (R.drawable.continue_head_white);
	
							} else if (maneuver.get(j).contains("exit")) {
								drawable = (R.drawable.continue_head_white);
	
							} else if (maneuver.get(j).contains("merge")
									|| maneuver.get(j).contains("Merge")) {
								drawable = (R.drawable.continue_head_white);
	
							}
							textToSpeech.speak(dir1, TextToSpeech.QUEUE_FLUSH, null);
							title.setText(dir1);
						}
	
						if (j != (steps.size())) {
	
							for (int i = 0; i < geopionts.size(); i++) {
								if (geopointLatlang.latitude == geopionts.get(i).latitude
										&& geopointLatlang.longitude == geopionts
												.get(i).longitude) {
									LogUtils.errorLog("SUCCESS", "SUCCESS");
	
									int k = i + 10, m = i - 10;
									if (j != 0 && (m >= 10)) {
										for (int p = m; p < i; p++) {
											rectLine.add(geopionts.get(p));
										}
									}
									if (k < geopionts.size()) {
										for (int q = i; q < k; q++) {
											rectLine.add(geopionts.get(q));
										}
									}
	
									break;
								}
							}
	
							polyline = Map.addPolyline(rectLine);
						}
	
						updateCamera(angle, geopointLatlang, false);
	
						iv_action_left.setBackgroundResource(drawable);
	
						j++;
					} 
					else 
					{
						// polyline.remove();
						if (j >= dir.size())
							j = dir.size() - 1;
	
						title.setText(dir.get(j));
						textToSpeech.speak(dir.get(j), TextToSpeech.QUEUE_FLUSH, null);
						Map.animateCamera(CameraUpdateFactory.newLatLngZoom(
								steps.get(j), 12.0f));
						// title.setTextColor(Color.RED);
						iv_action_left.setVisibility(View.GONE);
	
						Toast.makeText(RouteMapActivity.this, "Destination Came",
								Toast.LENGTH_SHORT).show();
						j = 0;
					}
				}
			}
		});

		iv_directions.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				if(dir == null || dir.size() <= 0 || maneuver == null || maneuver.size() <= 0 || geopiontsDis == null || geopiontsDis.size() <= 0 )
				{
					Toast.makeText(RouteMapActivity.this, "Unable to get location, Please check your internet connection.", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Intent in = new Intent(RouteMapActivity.this, RouteAdapter.class);
					in.putExtra("arr", dir);
					in.putExtra("man", maneuver);
					in.putExtra("geopiontsDis", geopiontsDis);
					startActivity(in);
				}
			}
		});

		mMapFragment = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map1));
		Map = mMapFragment.getMap();
		
		if(Map != null)
		{
			// Map.setTrafficEnabled(true);
			Map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	
			UiSettings mUiSettings;
			mUiSettings = Map.getUiSettings();
			mUiSettings.setCompassEnabled(true);
			mUiSettings.setMyLocationButtonEnabled(true);
			mUiSettings.setZoomControlsEnabled(true);
		}
		else
			Toast.makeText(RouteMapActivity.this, "Your device doesn't support Google map v2. Kindly download the Google play services/Google Settings for google Play.", Toast.LENGTH_SHORT).show();
	
	}

	@Override
	protected void onDestroy() {
		locationClient.disconnect();
		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
	}

	@Override
	public void onConnected(Bundle arg0) {
		Location loc = locationClient.getLastLocation();
		if (loc != null) {
			latitude = loc.getLatitude();
			longitude = loc.getLongitude();
			getRoute();
		}
		else
		{
			latitude = preference.getDoubleFromPreference(Preference.CUREENT_LATTITUDE, 25.271139);
			longitude = preference.getDoubleFromPreference(Preference.CUREENT_LONGITUDE, 55.307485000);
			getRoute();
			Toast.makeText(RouteMapActivity.this, "Unable to get current location. You are finding the route from your last location.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDisconnected() {
	}

	boolean isNetworkAvailable() {
		boolean netwokStatus = false;
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		if (nInfo != null) {
			netwokStatus = nInfo.isConnected();
		}
		return netwokStatus;
	}

	/**
	 * This method is to show the loading progress dialog when some other
	 * functionality is taking place.
	 **/
	public void showLoader(String msg) {
		runOnUiThread(new RunShowLoader(msg, ""));
	}

	// This is to show the loading progress dialog when some other functionality
	// is taking place.
	class RunShowLoader implements Runnable {
		private String strMsg;
		private String title;

		public RunShowLoader(String strMsg, String title) {
			this.strMsg = strMsg;
			this.title = title;
		}

		@Override
		public void run() {
			try {
				if (progressdialog == null
						|| (progressdialog != null && !progressdialog
								.isShowing())) {
					progressdialog = ProgressDialog.show(RouteMapActivity.this,
							title, strMsg);
				} else if (progressdialog == null
						|| (progressdialog != null && progressdialog
								.isShowing())) {
					progressdialog.setMessage(strMsg);
				}
			} catch (Exception e) {
				progressdialog = null;
			}
		}
	}

	/** For hiding progress dialog (Loader ). **/
	public void hideLoader() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if (progressdialog != null && progressdialog.isShowing())
						progressdialog.dismiss();
					progressdialog = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	class GetRouteAsyntask extends AsyncTask<Void, Void, Void> {
		String status;
		LatLng desti_Latlang;

		@Override
		protected void onPreExecute() {
			showLoader("Please wait...");
		}

		@Override
		protected Void doInBackground(Void... params) {
			desti_Latlang = new LatLng(customerlat,
					customerlong);

//			desti_Latlang = new LatLng(17.4833,
//					78.4167);
			if (desti_Latlang != null) {
				from_latlang = new LatLng(latitude, longitude);
				Routedirection = new RouteDirection();
				status = Routedirection.setLatlang(from_latlang, desti_Latlang);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if (status.equals("OK")) {
							bundle = Routedirection.getDirection();
							dir = bundle.getStringArrayList("inst");
							distance = bundle.getString("dis");
							duration = bundle.getString("dur");
							start_address = bundle.getString("start_address");
							end_address = bundle.getString("end_address");
							geopiontsDis = bundle
									.getStringArrayList("geopointsdis");
							maneuver = bundle.getStringArrayList("maneuver");
							geopionts = new ArrayList<LatLng>();
							geopionts = bundle.getParcelableArrayList("geo");
							steps = bundle.getParcelableArrayList("steps");
							LogUtils.errorLog("steps size", "" + steps.size());

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Map.clear();
									Map.addMarker(new MarkerOptions()
											.position(from_latlang)
											.title(start_address)
											.icon(BitmapDescriptorFactory
													.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
									Map.animateCamera(CameraUpdateFactory
											.newLatLngZoom(from_latlang, 12.0f));
									Map.addMarker(new MarkerOptions()
											.position(desti_Latlang)
											.title(end_address)
											.icon(BitmapDescriptorFactory
													.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
									Map.animateCamera(CameraUpdateFactory
											.newLatLngZoom(desti_Latlang, 12.0f));

									PolylineOptions rectLine = new PolylineOptions()
											.width(12).color(Color.RED);

									for (int i = 0; i < geopionts.size(); i++) {
										rectLine.add(geopionts.get(i));
									}

									Map.addPolyline(rectLine);
									final Toast toast = Toast.makeText(
											RouteMapActivity.this, "DIstance:"
													+ distance + "\n"
													+ "Duration:" + duration,
											Toast.LENGTH_LONG);
									toast.show();
									iv_action_right.setVisibility(View.VISIBLE);
									iv_directions.setVisibility(View.VISIBLE);
									LatLng latlang = new LatLng(latitude,
											longitude);
									updateCamera(0, latlang, true);
									hideLoader();
								}
							});

						} else
							runOnUiThread(new Runnable()
							{
								@Override
								public void run()
								{
									hideLoader();
									Toast.makeText(RouteMapActivity.this,
											"Error in finding Destination",
											Toast.LENGTH_SHORT).show();
								}
							});

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}).start();

			super.onPostExecute(result);
		}
	}

	class Myadapter extends BaseAdapter {
		Context context;
		ArrayList<String> searchAreas;

		public Myadapter(Context context, ArrayList<String> searchAreas) {
			this.context = context;
			this.searchAreas = searchAreas;
		}

		@Override
		public int getCount() {
			return searchAreas.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.search_listcell, null);
			TextView tv = (TextView) convertView.findViewById(R.id.textView1);
			tv.setText(searchAreas.get(position));
			return convertView;
		}
	}

	void getRoute() {
		mMapFragment.getView().setVisibility(View.VISIBLE);
		if (customerlat > 0
				&& customerlong > 0) {
			j = 0;
			new GetRouteAsyntask().execute();
		} else
			Toast.makeText(RouteMapActivity.this,
					"Unable to get location, Please check your internet connection.",
					Toast.LENGTH_SHORT).show();
	}

	public void updateCamera(float bearing, LatLng latlng, boolean isFirstTime)
	{
		if (j < steps.size() - 1 && Map != null)
		{
			double angle = Math.atan2(steps.get(j).longitude
					- steps.get(j + 1).longitude,
					steps.get(j).latitude - steps.get(j + 1).latitude);
			angle = (angle) * (180 / Math.PI) + 180;
			float temp = Float.parseFloat(angle + "");
			LogUtils.errorLog("angle", angle + "");
			if (isFirstTime)
				Map.animateCamera(CameraUpdateFactory
						.newCameraPosition(new CameraPosition(latlng, 12, 40, temp)));
			else
				Map.animateCamera(CameraUpdateFactory
						.newCameraPosition(new CameraPosition(latlng, 17, 40, temp)));
		}
	}
}
