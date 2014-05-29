package com.example.jtetris4android;

import android.os.Handler;

public class MyTimer {
	Runnable action;
	int delay;
	Handler timer;
	public MyTimer(Runnable r, int periodInMiliSec) {
		// TODO Auto-generated constructor stub
		action = r;
		delay = periodInMiliSec;
		timer = new Handler();
	}
	
	public void start() {
		new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				action.run();
				timer.postDelayed(this, delay);
			}
		}.run(); 
	}
	
	public void stop() {
		timer.removeCallbacksAndMessages(null);		
	}
	
	public void setDelay(int newPeriodInMiliSec) {
		delay = newPeriodInMiliSec;
	}
}
