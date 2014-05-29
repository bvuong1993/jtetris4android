package com.example.jtetris4android;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	TetrisUI tetris;
	private MediaPlayer leftPlayer;
	private MediaPlayer rightPlayer;
	private MediaPlayer dropPlayer;
	private MediaPlayer rotatePlayer;
	
	ToggleButton powerButton;
	CheckBox competitiveMode;
	LinearLayout layout;
	
	private void initiateSounds() {
		leftPlayer = MediaPlayer.create(this, R.raw.left);
		rightPlayer = MediaPlayer.create(this, R.raw.right);
		dropPlayer = MediaPlayer.create(this, R.raw.drop);
		rotatePlayer = MediaPlayer.create(this, R.raw.rotate);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 
		
		initiateSounds();
		
		Log.d("sound", "succ init");
		
		tetris = (TetrisUI) findViewById(R.id.view1);
		tetris.setCountLabel((TextView) findViewById(R.id.countLabel));
		tetris.setTimeLabel((TextView) findViewById(R.id.timeLabel));
		tetris.setScoreLabel((TextView) findViewById(R.id.scoreLabel));
		tetris.setSpeedSeekBar((SeekBar) findViewById(R.id.speed));
		tetris.setPowerButton((ToggleButton) findViewById(R.id.powerButton));
		competitiveMode = (CheckBox) findViewById(R.id.competitive_checkBox);
		competitiveMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {					
					AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
														
					LayoutInflater inflater = LayoutInflater.from(getBaseContext());
					layout = (LinearLayout) inflater.inflate(R.layout.ip, null);
					builder.setMessage("Enter the server IP address")
						.setTitle("Competitive mode")
						.setView(layout)
						.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								EditText ip = (EditText) layout.findViewById(R.id.addr_editText);
								EditText port = (EditText) layout.findViewById(R.id.port_editText);
								EditText userName = (EditText) layout.findViewById(R.id.userName_editText);
								
								tetris.setIPAddress(ip.getText().toString());
								tetris.setUserName(userName.getText().toString());
								tetris.setPortNumber(Integer.parseInt(port.getText().toString()));
							}

						});
					
					AlertDialog alert = builder.create();					
					alert.show();
					
					// TODO: GET THE IP & Username					
					
				} else {
					// TODO
					tetris.setUserName(null);
					tetris.setIPAddress(null);
				}
			}
		});
//		((TetrisUI)findViewById(R.id.view1)).assignViews();

		powerButton = (ToggleButton) findViewById(R.id.powerButton);
		//enableButtons();

		powerButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					//competitiveMode.setClickable(false);
					tetris.startGame();					
				}
				else {
					//competitiveMode.setClickable(true);
					tetris.stopGame();
			
				}
			}
		});
//		powerButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if (((CompoundButton) v).isChecked())
//					startGame();
//				else
//					stopGame();
//			}
//		});
		
		Log.d("assignViews", "reach here cai dat dc powerbutton");
		

		Button leftButton = (Button) findViewById(R.id.leftButton);
		leftButton.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub		
				leftPlayer.start();
				tetris.tick(tetris.LEFT);
			}
		});
//		findViewById(R.id.leftButton).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				tick(LEFT);
//			}
//		});
		Log.d("assignViews", "reach here xong left");
		Button rightButton = (Button) findViewById(R.id.rightButton);
		rightButton.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rightPlayer.start();
				tetris.tick(tetris.RIGHT);					
			}
		});
		Log.d("assignViews", "reach here xong right");
		Button dropButton = (Button) findViewById(R.id.dropButton);
		dropButton.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dropPlayer.start();
				tetris.tick(tetris.DROP);
			}
		});
		Log.d("assignViews", "reach here xong drop");
		Button rotateButton = (Button) findViewById(R.id.rotateButton);
		rotateButton.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rotatePlayer.start();
				tetris.tick(tetris.ROTATE);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
