package com.example.jtetris4android;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends Activity {
	private MediaPlayer mp;
	public static final String RESULT_TAG = "RESULT";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		mp = MediaPlayer.create(this, R.raw.result);
		if (mp == null)
			Log.e("err", "mp null");
		mp.start();
		TextView result = (TextView) findViewById(R.id.result_textView);
		result.setText(getIntent().getStringExtra(RESULT_TAG));
		
		Button okButton = (Button) findViewById(R.id.ok_button);
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ResultActivity.this.finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mp.stop();
		mp.release();
		
		super.onDestroy();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}

}
