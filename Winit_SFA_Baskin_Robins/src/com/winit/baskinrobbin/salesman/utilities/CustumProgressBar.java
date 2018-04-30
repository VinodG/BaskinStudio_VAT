package com.winit.baskinrobbin.salesman.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class CustumProgressBar extends ProgressBar {
	private int inital, maxValue, achived;
	public static int firstValue, inatantValue, lastValue;
	private long delay = 10;
	private static final int HANDLER_WHAT = 1000;
	private CustomHandler h;
	private CustomThread th;

	public CustumProgressBar(Context context) {
		super(context);
	}
	public CustumProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public synchronized void setProgress(int progress) 
	{
		super.setProgress(progress);
	}
	@Override
	public synchronized void setMax(int max) 
	{
		super.setMax(max);
	}
	public void moveProgress(int initial, int target, int achived, int maximum) 
	{
		this.inital = initial;
		this.maxValue = maximum;
		this.achived = achived;
		setMax(100);
		if (h == null) 
		{
			h = new CustomHandler();
		} else 
		{
			h.removeMessages(HANDLER_WHAT);
		}
		h.postDelayed(null, delay);

		if (th != null && th.isAlive()) 
		{
			th.terminate();
			th = null;
		}
		th = new CustomThread(inital, achived, maxValue, h);
		th.start();
	}

	@SuppressLint("HandlerLeak")
	private class CustomHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle b = msg.getData();
//			int startValue = b.getInt("start_value", firstValue);
			int currentValue = b.getInt("sweep_value", inatantValue);
//			int maxValue = b.getInt("max_value", lastValue);

//			int progress = currentValue * 100 / maxValue;
			setProgress(currentValue);
		}
	}

	private class CustomThread extends Thread 
	{
		private float startValue, stopValue, maximum;
		private CustomHandler h;
		public volatile boolean running = true;

		public CustomThread(int startValue, int stopValue, int maxx,CustomHandler h) {
			this.startValue = startValue;
			this.stopValue = stopValue;
			this.maximum = maxx;
			this.h = h;
		}

		public void terminate() {
			running = false;
		}

		@Override
		public void run() {
			super.run();

			firstValue = 0;
			inatantValue = 0;
			lastValue = (int) ((stopValue*100)/maximum);

			for (int i = (int) firstValue; i <= lastValue && running; i++) {
				try {
					if (i <= lastValue) {
						Thread.sleep(15);

						Message msg = new Message();
						Bundle b = new Bundle();
						b.putInt("sweep_value", inatantValue = i);

						msg.setData(b);
						msg.what = HANDLER_WHAT;
						if (running)
							h.sendMessage(msg);

					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void onDestroy() {
		if (h != null)
			h.removeMessages(HANDLER_WHAT);
		if (th != null && th.isAlive()) {
			th.terminate();
			th = null;
		}
	}

}
