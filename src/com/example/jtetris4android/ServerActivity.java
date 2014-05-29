package com.example.jtetris4android;

import android.os.Bundle;
import android.app.Activity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ServerActivity extends Activity {

	private ServerSocket serverSocket;

	Handler updateConversationHandler;

	Thread serverThread = null;

	public static final int SERVERPORT = 6000;
	
	TextView serverInfo;
	ArrayList<String> resultList;
	ArrayAdapter<String> adapter;
	
	private int defaultSeed = 3;

	private ListView lv;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
		
		updateConversationHandler = new Handler();

		resultList = new ArrayList<String>();
		
		serverInfo = (TextView) findViewById(R.id.server_info_textView);		
		
		lv = (ListView) findViewById(R.id.result_list);
		adapter = new ArrayAdapter<String>(getBaseContext(), 
				R.layout.result_item, resultList);
		
		lv.setAdapter(adapter);		
		
		this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();

	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ServerThread implements Runnable {

		public void run() {
			Socket socket = null;
			try {
				serverSocket = new ServerSocket(SERVERPORT);				
				
				updateConversationHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						serverInfo.setText("Connect to me (" + serverSocket.getInetAddress()
								+ ":" + serverSocket.getLocalPort() + ")");
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (!Thread.currentThread().isInterrupted()) {

				try {
					socket = serverSocket.accept();				
					CommunicationThread commThread = new CommunicationThread(socket);
					new Thread(commThread).start();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class CommunicationThread implements Runnable {

		private Socket clientSocket;

		private BufferedReader input;
		private PrintWriter output;

		int position;
		TextView playerInfo;
		String result;
		String userName;
		public CommunicationThread(Socket clientSocket) {

			this.clientSocket = clientSocket;

			try {

				this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
				this.output = new PrintWriter(clientSocket.getOutputStream(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private String getCurrentTime() {			
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			return sdf.format(c.getTime());
		}
		public void run() {
			
			try {
				
				userName = input.readLine();
				
				updateConversationHandler.post(new Runnable() {
					
					@Override
					public void run() {						
						// TODO Auto-generated method stub

						String strDate = getCurrentTime();
						String msg = strDate + ": " + userName + " started a game";
						position = resultList.size();
						
						resultList.add(msg);						
						adapter.notifyDataSetChanged();
//						
//						playerInfo = (TextView) lv.getChildAt(lv.getLastVisiblePosition());
//						if (playerInfo == null)
//							Toast.makeText(getApplicationContext(), "err, last = " + lv.getLastVisiblePosition(), Toast.LENGTH_LONG).show();
//						else
						Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
					}
				});
				
				
				output.println("" + defaultSeed);
				String score = input.readLine();
				result = getCurrentTime() + ": " + userName + " got score of " + score;

				updateConversationHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
//						if (playerInfo != null)
//						playerInfo.setText(playerInfo.getText().toString() + "\n" + result);
						resultList.set(position, resultList.get(position) + "\n" + result);
//						Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
//						resultList.add(result);
						adapter.notifyDataSetChanged();
						
//						playerInfo = (TextView) lv.getChildAt(lv.getChildCount());
						Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();						
					}
				});

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

	}
}