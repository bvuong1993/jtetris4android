package com.example.jtetris4android;

import android.content.Context;
import android.media.MediaPlayer;

public class Music {
	Context context;
	int resouce;
	MediaPlayer player;
	
	public Music(Context context, int resouce) {
		this.context = context;
		this.resouce = resouce;
	}
}
