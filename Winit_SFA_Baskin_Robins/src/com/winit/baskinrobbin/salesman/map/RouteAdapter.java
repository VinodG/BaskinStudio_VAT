package com.winit.baskinrobbin.salesman.map;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.BaseActivity;
import com.winit.baskinrobbin.salesman.R;

public class RouteAdapter extends BaseActivity 
{
	private LinearLayout llLayout;
	private ListView lv;
	private ArrayList<String> str, maneuver;
	private ArrayList<String> dis;

	@Override
	public void initialize()
	{
		llLayout = 	(LinearLayout) inflater.inflate(R.layout.adapter, null);
		llBody.addView(llLayout, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		Intent in = getIntent();
		str = in.getStringArrayListExtra("arr");
		maneuver = in.getStringArrayListExtra("man");
		dis = in.getStringArrayListExtra("geopiontsDis");

		lv = (ListView) llLayout.findViewById(R.id.listView1);
		lv.setAdapter(new Adapter1(this, str, dis, maneuver));
		
	}
}

class Adapter1 extends BaseAdapter
{
	private ArrayList<String> str;
	private Context context;
	private TextView title;
	private ArrayList<String> dis;
	private ArrayList<String> maneuver;

	public Adapter1(Context context, ArrayList<String> str,
			ArrayList<String> dis, ArrayList<String> maneuver) {

		this.str = str;
		this.dis = dis;
		this.maneuver = maneuver;
		this.context = context;

	}

	@Override
	public int getCount() {
		return str.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView == null)
			convertView = LayoutInflater.from(context).inflate(R.layout.listcell, null);

		ImageView image = (ImageView) convertView.findViewById(R.id.imageView1);

		String dir = str.get(position);

		String[] list = dir.split("\n");

		dir = list[0];
		Log.i("split", "" + dir);

		if (position == 0) {
			image.setImageResource(R.drawable.start_marker);

		}
		int z = 0;
		if (maneuver.get(position).equals("default")) {
			if (dir.contains("Continue") || dir.contains("continue")
					|| dir.contains("Head")) {
				image.setImageResource(R.drawable.continue_head);

			} else if (dir.contains("Slight left")
					|| dir.contains("slights left")) {
				image.setImageResource(R.drawable.slight_left);

			} else if (dir.contains("Slight right")
					|| dir.contains("slights right")) {
				image.setImageResource(R.drawable.slight_right);

			} else if (dir.contains("Turn left") || dir.contains("turns left")
					|| dir.contains("left")) {
				image.setImageResource(R.drawable.turn_left);

			} else if (dir.contains("Turn right")
					|| dir.contains("turns right") || dir.contains("right")) {
				image.setImageResource(R.drawable.turn_right);
			} else if (dir.contains("roundabout")) {
				image.setImageResource(R.drawable.round_about);
			} else if (dir.contains("U-turn") || dir.contains("u-turn")) {
				image.setImageResource(R.drawable.right_u_turn);
			} else if (maneuver.get(position).contains("keep-left")
					|| maneuver.get(position).contains("keep left")
					|| maneuver.get(position).contains("Keep left")) {
				image.setImageResource(R.drawable.slight_left);

			} else if (maneuver.get(position).contains("keep-right")
					|| maneuver.get(position).contains("keep right")
					|| maneuver.get(position).contains("Keep right")) {
				image.setImageResource(R.drawable.slight_right);

			} else if (dir.contains("left")) {
				image.setImageResource(R.drawable.turn_left);

			} else if (maneuver.get(position).contains("ramp-left")) {
				image.setImageResource(R.drawable.da_turn_ramp_right);

			} else if (maneuver.get(position).contains("ramp-right")) {
				image.setImageResource(R.drawable.da_turn_ramp_right);

			} else if (maneuver.get(position).contains("Take the ramp")
					|| maneuver.get(position).contains("ramp")) {
				image.setImageResource(R.drawable.da_turn_ramp_right);

			} else if (maneuver.get(position).contains("exit")) {
				image.setImageResource(R.drawable.da_turn_roundabout_exit);

			} else if (maneuver.get(position).contains("merge")
					|| maneuver.get(position).contains("Merge")) {
				image.setImageResource(R.drawable.da_turn_generic_merge);

			} else
				z++;

		} else {
			if (maneuver.get(position).contains("turn-left")) {
				image.setImageResource(R.drawable.turn_left);

			} else if (maneuver.get(position).contains("turn-right")) {
				image.setImageResource(R.drawable.turn_right);

			} else if (maneuver.get(position).contains("turn-slight-left")) {
				image.setImageResource(R.drawable.slight_left);

			} else if (maneuver.get(position).contains("turn-slight-right")) {
				image.setImageResource(R.drawable.slight_right);

			} else if (maneuver.get(position).contains("straight")) {
				image.setImageResource(R.drawable.continue_head);

			} else if (maneuver.get(position).contains("roundabout-left")) {
				image.setImageResource(R.drawable.round_about_left);

			} else if (maneuver.get(position).contains("roundabout-right")) {
				image.setImageResource(R.drawable.roundabout_right);

			} else if (maneuver.get(position).contains("turn-sharp-left")) {
				image.setImageResource(R.drawable.sharp_left);

			} else if (maneuver.get(position).contains("turn-sharp-right")) {
				image.setImageResource(R.drawable.sharp_right);

			} else if (maneuver.get(position).contains("uturn-right")) {
				image.setImageResource(R.drawable.right_u_turn);

			} else if (maneuver.get(position).contains("uturn-left")) {
				image.setImageResource(R.drawable.left_u_turn);

			} else if (maneuver.get(position).contains("straight")) {
				image.setImageResource(R.drawable.continue_head);

			} else if (maneuver.get(position).contains("keep-left")
					|| maneuver.get(position).contains("keep left")) {
				image.setImageResource(R.drawable.slight_left);

			} else if (maneuver.get(position).contains("keep-right")
					|| maneuver.get(position).contains("keep right")) {
				image.setImageResource(R.drawable.slight_right);

			}

			else if (maneuver.get(position).contains("ramp-left")) {
				image.setImageResource(R.drawable.da_turn_ramp_right);

			} else if (maneuver.get(position).contains("ramp-right")) {
				image.setImageResource(R.drawable.da_turn_ramp_right);

			} else if (maneuver.get(position).contains("ramp")
					|| maneuver.get(position).contains("Take the ramp")) {
				image.setImageResource(R.drawable.da_turn_ramp_right);

			} else if (maneuver.get(position).contains("exit")) {
				image.setImageResource(R.drawable.da_turn_roundabout_exit);

			} else if (maneuver.get(position).contains("merge")
					|| maneuver.get(position).contains("Merge")) {
				image.setImageResource(R.drawable.da_turn_generic_merge);

			} else
				z++;
		}

		Log.e("Z= ", "" + z);
		if (position == (dis.size() - 1)) {
			image.setImageResource(R.drawable.end_marker);

		}
		TextView tv = (TextView) convertView.findViewById(R.id.textView1);
		TextView tvdis = (TextView) convertView.findViewById(R.id.tvdis);
		tvdis.setText(dis.get(position));
		tv.setText(dir);
		return convertView;
	}
}