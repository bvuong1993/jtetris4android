package com.example.jtetris4android;

// TetrisUI.java
//import java.awt.*;
//import javax.swing.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;
//import java.awt.event.*;
//import javax.swing.event.*;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

//import java.awt.Toolkit;


/**
 TetrisUI presents a tetris game in a window.
 It handles the GUI and the animation.
 The Piece and Board classes handle the
 lower-level computations.
 This code is provided in finished, working form for the students.
 
 Use Keys j-k-l to move, n to drop (or 4-5-6 0)
 During animation, filled rows draw as green.
 Clearing 1-4 rows scores 5, 10, 20, 40 points.
 Clearing 4 rows at a time beeps!
*/

/*
 Implementation notes:
 -The "currentPiece" points to a piece that is
 currently falling, or is null when there is no piece.
 -tick() moves the current piece
 -a timer object calls tick(DOWN) periodically
 -keystrokes call tick() with LEFT, RIGHT, etc.
 -Board.undo() is used to remove the piece from its
 old position and then Board.place() is used to install
 the piece in its new position.
*/

public class TetrisUI extends View /*JComponent*/ {	

	// size of the board in blocks
	public static final int WIDTH = 10;
	public static final int HEIGHT = 20;
	
	// Extra blocks at the top for pieces to start.
	// If a piece is sticking up into this area
	// when it has landed -- game over!
	public static final int TOP_SPACE = 4;
	
	// When this is true, plays a fixed sequence of 100 pieces
	protected boolean testMode = false;
	public final int TEST_LIMIT = 100;
	
	// Is drawing optimized
	// (default false, so debugging is easier)
	protected boolean DRAW_OPTIMIZE = false;
	
	// Board data structures
	protected Board board;
	protected Piece[] pieces;
	
	
	// The current piece in play or null
	protected Piece currentPiece;
	protected int currentX;
	protected int currentY;
	protected boolean moved;	// did the player move the piece
	
	
	// The piece we're thinking about playing
	// -- set by computeNewPosition
	// (storing this in ivars is slightly questionable style)
	protected Piece newPiece;
	protected int newX;
	protected int newY;
	
	// State of the game
	protected boolean gameOn;	// true if we are playing
	protected int count;		 // how many pieces played so far
	protected long startTime;	// used to measure elapsed time
	protected Random random;	 // the random generator for new pieces
		
	// Controls
//	protected JLabel countLabel;
//	protected JLabel scoreLabel;
//	protected int score;
//	protected JLabel timeLabel;
//	protected JButton startButton;
//	protected JButton stopButton;
//	protected javax.swing.Timer timer;
//	protected JSlider speed;
//	protected JCheckBox testButton;

	protected TextView countLabel;
	protected TextView scoreLabel;
	protected int score;
	protected TextView timeLabel;
//	protected Button startButton;
//	protected Button stopButton;
//	protected ToggleButton powerButton;
	protected MyTimer timer;
//	protected JSlider speed;
	protected SeekBar speed;
	protected CheckBox testButton;
	protected ToggleButton powerButton;
	
	protected String IPAddress;
	protected int portNumber;
	protected String userName;

	public final int DELAY = 500;	// milliseconds per tick
	private Socket socket;
	
	private long seed;
	private PrintWriter outToServer;
	private BufferedReader inFromServer;
	
	private MediaPlayer backgroundMusic;
	private MediaPlayer clearRowsMusic;
	
	/**
	 * Creates a new TetrisUI where each tetris square
	 * is drawn with the given number of pixels.
	 */
//	TetrisUI(int pixels) {
//		super();

	public void setCountLabel(TextView v) {
		countLabel = v;
	}
	
	public void setScoreLabel(TextView v) {
		scoreLabel = v;
	}
	
	public void setTimeLabel(TextView v) {
		timeLabel = v;
	}
	
	public void setPowerButton(ToggleButton button) {
		powerButton = button;
	}
	
	public void setUserName(String name) {
		userName = name;
	}
	public void setIPAddress(String addr) {
		IPAddress = addr;
		// WARNING: VALID ON AVDS ONLY
		// IPAddress = "10.0.2.2";
	}
	public void setPortNumber(int port) {
		portNumber = port;
	}
	
	public void setSpeedSeekBar(SeekBar sb) {
		speed = sb;
		
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				updateTimer();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				updateTimer();
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				updateTimer();
			}
		});
		
		updateTimer();
	}
	public TetrisUI(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Set component size to allow given pixels for each block plus
		// a 1 pixel border around the whole thing.
//		setPreferredSize(new Dimension((WIDTH * pixels)+2,
//				(HEIGHT+TOP_SPACE)*pixels+2));
		gameOn = false;
		
		pieces = Piece.getPieces();
		board = new Board(WIDTH, HEIGHT + TOP_SPACE);


		/*
		 Register key handlers that call
		 tick with the appropriate constant.
		 e.g. 'j' and '4'  call tick(LEFT)
		 
		 I tried doing the arrow keys, but the JSliders
		 try to use those too, causing problems.
		*/
		
		// LEFT
//		registerKeyboardAction(
//			new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					tick(LEFT);
//				}
//			}, "left", KeyStroke.getKeyStroke('4'), WHEN_IN_FOCUSED_WINDOW
//		);
//		registerKeyboardAction(
//			new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					tick(LEFT);
//				}
//			}, "left", KeyStroke.getKeyStroke('j'), WHEN_IN_FOCUSED_WINDOW
//		);
//		
//		
//		// RIGHT
//		registerKeyboardAction(
//			new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					tick(RIGHT);
//				}
//			}, "right", KeyStroke.getKeyStroke('6'), WHEN_IN_FOCUSED_WINDOW
//		);
//		registerKeyboardAction(
//			new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					tick(RIGHT);
//				}
//			}, "right", KeyStroke.getKeyStroke('l'), WHEN_IN_FOCUSED_WINDOW
//		);
//		
//		
//		// ROTATE	
//		registerKeyboardAction(
//			new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					tick(ROTATE);
//				}
//			}, "rotate", KeyStroke.getKeyStroke('5'), WHEN_IN_FOCUSED_WINDOW
//		);
//		registerKeyboardAction(
//			new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					tick(ROTATE);
//				}
//			}, "rotate", KeyStroke.getKeyStroke('k'), WHEN_IN_FOCUSED_WINDOW
//		);
//		
//		
//		// DROP
//		registerKeyboardAction(
//			new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					tick(DROP);
//				}
//			}, "drop", KeyStroke.getKeyStroke('0'), WHEN_IN_FOCUSED_WINDOW
//		);
//		registerKeyboardAction(
//			new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					tick(DROP);
//				}
//			}, "drop", KeyStroke.getKeyStroke('n'), WHEN_IN_FOCUSED_WINDOW
//		);
//		
		
		// Create the Timer object and have it send
		// tick(DOWN) periodically
//		timer = new javax.swing.Timer(DELAY, new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				tick(DOWN);
//			}
//		});
		
		timer = new MyTimer(new Runnable() {						
			@Override
			public void run() {
				tick(DOWN);
				updateTimer();
				// TODO Auto-generated method stub				
			}
		}, DELAY);
//		requestFocusInWindow(); 
		
		//assignViews();		
	}
	



	/**
	 Sets the internal state and starts the timer
	 so the game is happening.
	*/
	
	public boolean competitiveMode() {
		return userName != null && IPAddress != null;
	}
	public void startGame() {
		
		MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.startagamealready);
		mp.start();
		
		backgroundMusic = MediaPlayer.create(getContext(), R.raw.background);
		backgroundMusic.setLooping(true);		

		backgroundMusic.start();
		
		if (competitiveMode()) {
            InetAddress serverAddr;
			try {				
				serverAddr = InetAddress.getByName(IPAddress);
	            socket = new Socket(serverAddr, portNumber);
				Toast.makeText(getContext(), "compe mode " + userName + " " + IPAddress + " " + seed,
						Toast.LENGTH_LONG).show();
	            inFromServer =
	                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
	            outToServer =
	                    new PrintWriter(socket.getOutputStream(), true);
	            outToServer.println(userName);
	            String seedFromServer = inFromServer.readLine();
				seed = Long.parseLong(seedFromServer);
				Log.d("seed", "seed is " + seed);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("startGame", "socket error");
				Toast.makeText(getContext(), "socket err startGame" + e.toString(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}		
		}
		
		//powerButton.setChecked(true);
		// cheap way to reset the board state
		board = new Board(WIDTH, HEIGHT + TOP_SPACE);
		
		// draw the new board state once
//		repaint();
		invalidate();
		Log.d("", "debug in lai duoc hinh");
		count = 0;
		score = 0;
		updateCounters();
		Log.d("", "debug in duoc counter");
		gameOn = true;
		
		// Set mode based on checkbox at start of game
//		testMode = testButton.isSelected();
		testMode = false;
		
		if (testMode) random = new Random(0);	// same seq every time
		else if (competitiveMode()) {
			random = new Random(seed); // diff seq each game
		} else {
			random = new Random();
		}
		
		enableButtons();
		timeLabel.setText(" ");
		addNewPiece();
		Log.d("", "debug them duoc piece moi");
		timer.start();
		startTime = System.currentTimeMillis();
		
	}
	
	
	/**
	 Sets the enabling of the start/stop buttons
	 based on the gameOn state.
	*/
	private void enableButtons() {
//		startButton.setEnabled(!gameOn);
//		stopButton.setEnabled(gameOn);
		
//		powerButton.setChecked(gameOn);
	}
	
	/**
	 Stops the game.
	*/
	public void stopGame() {
		backgroundMusic.stop();
		
		gameOn = false;
		powerButton.setChecked(gameOn);
		enableButtons();
		timer.stop();
		
		long delta = (System.currentTimeMillis() - startTime)/10;
		timeLabel.setText(Double.toString(delta/100.0) + " seconds");
		
		//powerButton.setChecked(false);
		
		Context context = getContext();		
		Intent i = new Intent(context, ResultActivity.class);
		String result = "Score: " + score + "\n# pieces: " + count
				+ "\nTime: " + delta/100.0 + " s";
		i.putExtra(ResultActivity.RESULT_TAG, result);
		if (competitiveMode()) {
			outToServer.println("" + score);
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(getContext(), "socket err closing", Toast.LENGTH_LONG).show();

			}
		}
		
		context.startActivity(i);		
	}
	
	
	/**
	 Given a piece, tries to install that piece
	 into the board and set it to be the current piece.
	 Does the necessary repaints.
	 If the placement is not possible, then the placement
	 is undone, and the board is not changed. The board
	 should be in the committed state when this is called.
	 Returns the same error code as Board.place().
	*/
	public int setCurrent(Piece piece, int x, int y) {
		int result = board.place(piece, x, y);
		
		if (result <= Board.PLACE_ROW_FILLED) { // SUCESS
			// repaint the rect where it used to be
			if (currentPiece != null) repaintPiece(currentPiece, currentX, currentY);
			currentPiece = piece;
			currentX = x;
			currentY = y;
			// repaint the rect where it is now
			repaintPiece(currentPiece, currentX, currentY);
		}
		else {
			board.undo();
		}
		
		return(result);
	}


	/**
	 Selects the next piece to use using the random generator
	 set in startGame().
	*/
	public Piece pickNextPiece() {
		int pieceNum;
		
		pieceNum = (int) (pieces.length * random.nextDouble());
		
		Piece piece	 = pieces[pieceNum];
		
		return(piece);
	}
	
			
	/**
	 Tries to add a new random piece at the top of the board.
	 Ends the game if it's not possible.
	*/
	public void addNewPiece() {
		count++;
		score++;
		
		if (testMode && count == TEST_LIMIT+1) {
			 //stopGame();
			powerButton.setChecked(false);
			 return;
		}

		// commit things the way they are
		board.commit();
		currentPiece = null;

		Piece piece = pickNextPiece();
		
		// Center it up at the top
		int px = (board.getWidth() - piece.getWidth())/2;
		int py = board.getHeight() - piece.getHeight();
		
		// add the new piece to be in play
		int result = setCurrent(piece, px, py);
		
		// This probably never happens, since
		// the blocks at the top allow space
		// for new pieces to at least be added.
		if (result>Board.PLACE_ROW_FILLED) {
			//stopGame();
			powerButton.setChecked(false);
		}

		updateCounters();
	}
	
	/**
	 Updates the count/score labels with the latest values.
	 */
	private void updateCounters() {
		Log.d("", "debug vua vao updateCounter xong");
		countLabel.setText("Pieces " + count);
		Log.d("", "debug in duoc # pieces");
		scoreLabel.setText("Score " + score);
	}
	
	
	/**
	 Figures a new position for the current piece
	 based on the given verb (LEFT, RIGHT, ...).
	 The board should be in the committed state --
	 i.e. the piece should not be in the board at the moment.
	 This is necessary so dropHeight() may be called without
	 the piece "hitting itself" on the way down.

	 Sets the ivars newX, newY, and newPiece to hold
	 what it thinks the new piece position should be.
	 (Storing an intermediate result like that in
	 ivars is a little tacky.)
	*/
	public void computeNewPosition(int verb) {
		// As a starting point, the new position is the same as the old
		newPiece = currentPiece;
		newX = currentX;
		newY = currentY;
		
		// Make changes based on the verb
		switch (verb) {
			case LEFT: newX--; break;
			
			case RIGHT: newX++; break;
			
			case ROTATE:
				newPiece = newPiece.fastRotation();
				
				// tricky: make the piece appear to rotate about its center
				// can't just leave it at the same lower-left origin as the
				// previous piece.
				newX = newX + (currentPiece.getWidth() - newPiece.getWidth())/2;
				newY = newY + (currentPiece.getHeight() - newPiece.getHeight())/2;
				break;
				
			case DOWN: newY--; break;
			
			case DROP:
			 newY = board.dropHeight(newPiece, newX);
			 
			 // trick: avoid the case where the drop would cause
			 // the piece to appear to move up
			 if (newY > currentY) {
				 newY = currentY;
			 }
			 break;
			 
			default:
				 throw new RuntimeException("Bad verb");
		}
	
	}



		
	public static final int ROTATE = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int DROP = 3;
	public static final int DOWN = 4;
	/**
	 Called to change the position of the current piece.
	 Each key press calls this once with the verbs
	 LEFT RIGHT ROTATE DROP for the user moves,
	 and the timer calls it with the verb DOWN to move
	 the piece down one square.

	 Before this is called, the piece is at some location in the board.
	 This advances the piece to be at its next location.
	 
	 Overriden by the brain when it plays.
	*/
	public void tick(int verb) {
		if (!gameOn) return;
		
		if (currentPiece != null) {
			board.undo();	// remove the piece from its old position
		}
		
		// Sets the newXXX ivars
		computeNewPosition(verb);
		
		// try out the new position (rolls back if it doesn't work)
		int result = setCurrent(newPiece, newX, newY);
		
		// if row clearing is going to happen, draw the
		// whole board so the green row shows up
		if (result ==  Board.PLACE_ROW_FILLED) {
//			repaint();
			invalidate();
		}
		
		

		boolean failed = (result >= Board.PLACE_OUT_BOUNDS);
		
		// if it didn't work, put it back the way it was
		if (failed) {
			if (currentPiece != null) board.place(currentPiece, currentX, currentY);
			repaintPiece(currentPiece, currentX, currentY);
		}
		
		/*
		 How to detect when a piece has landed:
		 if this move hits something on its DOWN verb,
		 and the previous verb was also DOWN (i.e. the player was not
		 still moving it),	then the previous position must be the correct
		 "landed" position, so we're done with the falling of this piece.
		*/
		if (failed && verb==DOWN && !moved) {	// it's landed
		
			int cleared = board.clearRows();
			if (cleared > 0) {
				// score goes up by 5, 10, 20, 40 for row clearing
				// clearing 4 gets you a beep!
				clearRowsMusic =  MediaPlayer.create(getContext(), R.raw.big_smile);
				clearRowsMusic.start();
				switch (cleared) {
					case 1: score += 5;	 break;
					case 2: score += 10;  break;
					case 3: score += 20;  break;
					case 4: score += 40; /*Toolkit.getDefaultToolkit().beep(); */ break;
					default: score += 50;  // could happen with non-standard pieces
				}
				updateCounters();
//				repaint();	// repaint to show the result of the row clearing
				invalidate();
			}
			
			
			// if the board is too tall, we've lost
			if (board.getMaxHeight() > board.getHeight() - TOP_SPACE) {
				//stopGame();
				powerButton.setChecked(false);
			}
			// Otherwise add a new piece and keep playing
			else {
				addNewPiece();
			}
		}
		
		// Note if the player made a successful non-DOWN move --
		// used to detect if the piece has landed on the next tick()
		moved = (!failed && verb!=DOWN);
	}



	/**
	 Given a piece and a position for the piece, generates
	 a repaint for the rectangle that just encloses the piece.
	*/
	public void repaintPiece(Piece piece, int x, int y) {
		if (DRAW_OPTIMIZE) {
//			int px = xPixel(x);
//			int py = yPixel(y + piece.getHeight() - 1);
//			int pwidth = xPixel(x+piece.getWidth()) - px;
//			int pheight = yPixel(y-1) - py;
//			
//			repaint(px, py, pwidth, pheight);
			invalidate();
		}
		else {
			// Not-optimized -- rather than repaint
			// just the piece rect, repaint the whole board.
//			repaint();
			invalidate();
		}
	}
	
	
	/*
	 Pixel helpers.
	 These centralize the translation of (x,y) coords
	 that refer to blocks in the board to (x,y) coords that
	 count pixels. Centralizing these computations here
	 is the only prayer that repaintPiece() and paintComponent()
	 will be consistent.
	 
	 The +1's and -2's are to account for the 1 pixel
	 rect around the perimeter.
	*/
	
	
	// width in pixels of a block
	private final float dX() {
		return( ((float)(getWidth()-2)) / board.getWidth() );
	}

	// height in pixels of a block
	private final float dY() {
		return( ((float)(getHeight()-2)) / board.getHeight() );
	}
	
	// the x pixel coord of the left side of a block
	private final int xPixel(int x) {
		return(Math.round(1 + (x * dX())));
	}
	
	// the y pixel coord of the top of a block
	private final int yPixel(int y) {
		return(Math.round(getHeight() -1 - (y+1)*dY()));
	}


	/**
	 Draws the current board with a 1 pixel border
	 around the whole thing. Uses the pixel helpers
	 above to map board coords to pixel coords.
	 Draws rows that are filled all the way across in green.
	*/
//	public void paintComponent(Graphics g) {
	protected void onDraw(Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setColor(Color.DKGRAY);
		paint.setStyle(Paint.Style.STROKE);
		// Draw a rect around the whole thing
//		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		canvas.drawRect(0, 0, getWidth()-1, getHeight()-1, paint);
		
		
		// Draw the line separating the top
		int spacerY = yPixel(board.getHeight() - TOP_SPACE - 1);
//		g.drawLine(0, spacerY, getWidth()-1, spacerY);
		// saii roi canvas.drawRect(0, spacerY, getWidth()-1, spacerY + spacerY, paint);
		canvas.drawLine(0, spacerY, getWidth()-1, spacerY, paint);


		// check if we are drawing with clipping
		//Shape shape = g.getClip();
//		Rectangle clip = null;
//		if (DRAW_OPTIMIZE) {
//			clip = g.getClipBounds();
//		}


		// Factor a few things out to help the optimizer
		final int dx = Math.round(dX()-2);
		final int dy = Math.round(dY()-2);
		final int bWidth = board.getWidth();

		int x, y;
		// Loop through and draw all the blocks
		// left-right, bottom-top
		for (x=0; x<bWidth; x++) {
			int left = xPixel(x);	// the left pixel
			
			// right pixel (useful for clip optimization)
//			int right = xPixel(x+1) -1;
			
			// skip this x if it is outside the clip rect
//			if (DRAW_OPTIMIZE && clip!=null) {
//				if ((right<clip.x) || (left>=(clip.x+clip.width))) continue;
//			}
			
			// draw from 0 up to the col height
			final int yHeight = board.getColumnHeight(x);

			for (y=0; y<yHeight; y++) {
				if (board.getGrid(x, y)) {
					boolean filled = (board.getRowWidth(y)==bWidth);
//					if (filled) g.setColor(Color.green);
					Paint tempPaint = new Paint();
					tempPaint.setColor(Color.BLUE);					
					if (filled)
						tempPaint.setColor(Color.RED);					
					
//					fillRect(left+1, yPixel(y)+1, dx, dy);	// +1 to leave a white border
					canvas.drawRect(left+1, yPixel(y)+1, left+1+dx, yPixel(y)+1+dy, tempPaint);
					
//					if (filled) g.setColor(Color.black);
				}
			}
		}
	}
	
	
	/**
	 Updates the timer to reflect the current setting of the 
	 speed slider.
	*/
	public void updateTimer() {
		double value = ((double)speed.getProgress())/speed.getMax();
		timer.setDelay((int)(DELAY - value*DELAY));		
	}	
	
	public boolean isPlaying() {
		return gameOn;
	}
}

