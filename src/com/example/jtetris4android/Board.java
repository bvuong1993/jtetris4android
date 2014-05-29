package com.example.jtetris4android;

// Board.java

/**
	Tetris Board.
   Represents a Tetris board -- essentially a 2-d grid
   of booleans. Supports tetris pieces and row clearing.
   Has an "undo" feature that allows clients to add and remove pieces efficiently.
   Does not do any drawing or have any idea of pixels. Instead,
   just represents the abstract 2-d board.
*/
public class Board	{
    // Some ivars are stubbed out for you:
    private int width;
    private int height;
    private boolean[][] grid;
    private boolean DEBUG = true;
    boolean committed;
    //Additional meterial
    int[] widths;
    int[] heights;
    int maxHeight;
    boolean[][] grid_backup;
    int[] widths_backup;
    int[] heights_backup;
    int maxHeight_backup;
    
    // Here a few trivial methods are provided:
    
    /**
       Creates an empty board of the given width and height
       measured in blocks.
    */
    public Board(int width, int height) {
	this.width = width;
	this.height = height;
	grid = new boolean[width][height];
	committed = true;
	
	// YOUR CODE HERE
	for (int x = 0; x < width; x++)
	    for (int y = 0; y < height; y++)
		grid[x][y] = false;
	
	widths = new int[height];
	heights = new int[width];
	for (int i = 0; i < height; i++)
	    widths[i] = 0;
	for (int i = 0; i < width; i++)
	    heights[i] = 0;
	maxHeight = 0;
	grid_backup = new boolean[width][height];
	widths_backup = new int[height];
	heights_backup = new int[width];
	
    }
    
    
    /**
       Returns the width of the board in blocks.
    */
    public int getWidth() {
	return width;
    }
    
    
    /**
       Returns the height of the board in blocks.
    */
    public int getHeight() {
	return height;
    }
    
    
    /**
       Returns the max column height present in the board.
       For an empty board this is 0.
    */
    public int getMaxHeight() {
	return maxHeight;
    }
    
    
    /**
       Checks the board for internal consistency -- used
       for debugging.
    */
    public void sanityCheck() {
	if (DEBUG) {
	    // YOUR CODE HERE
	    for (int x = 0; x < width; x++) {
		int high = getColumnHeightManual(x);
		if (heights[x] != high) {
		    System.out.println(toString());
		    throw new RuntimeException("heights[" + x + "] = " + heights[x] + ", actually " + high);
		}
	    }
	    
	    for (int y = 0; y < height; y++) {
		int total = getRowWidthManual(y);
		if (total != widths[y]) {
		    System.out.println(toString());
		    throw new RuntimeException("widths[" + y + "] = " + widths[y] + ", actually " + total);
		}
	    }

	    int max = getMaxHeightManual();
	    if (max != maxHeight) {
		System.out.println(toString());
		throw new RuntimeException("maxHeight = " + maxHeight + ", actually " + max);
	    }
	}
    }
    
    /**
       Given a piece and an x, returns the y
       value where the piece would come to rest
       if it were dropped straight down at that x.
       
       <p>
       Implementation: use the skirt and the col heights
       to compute this fast -- O(skirt length).
    */
    public int dropHeight(Piece piece, int x) {
	// YOUR CODE HERE
	int[] skirt = piece.getSkirt();
	int max_index = 0;
	for (int i = 1, j = x+1; i < skirt.length; i++, j++) {
	    if (heights[j] - skirt[i] > heights[x+max_index] - skirt[max_index])
		max_index = i;
	}
	return heights[max_index + x] - skirt[max_index];
	// SO CORRECT

    }
    
    /**
       Returns the height of the given column --
       i.e. the y value of the highest block + 1.
       The height is 0 if the column contains no blocks.
    */
    public int getColumnHeight(int x) {
	// YOUR CODE HERE
	return heights[x];
    }
    
    
    /**
       Returns the number of filled blocks in
       the given row.
    */
    public int getRowWidth(int y) {
	// YOUR CODE HERE
	return widths[y];
    }
    
    
    /**
       Returns true if the given block is filled in the board.
       Blocks outside of the valid width/height area
       always return true.
    */
    public boolean getGrid(int x, int y) {
	// YOUR CODE HERE
	if (x < 0 || x > width || y < 0 || y > height)
	    return true; // review later
	return grid[x][y];
    }
    
    
    public static final int PLACE_OK = 0;
    public static final int PLACE_ROW_FILLED = 1;
    public static final int PLACE_OUT_BOUNDS = 2;
    public static final int PLACE_BAD = 3;
    
    /**
       Attempts to add the body of a piece to the board.
       Copies the piece blocks into the board grid.
       Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
       for a regular placement that causes at least one row to be filled.
       
       <p>Error cases:
       A placement may fail in two ways. First, if part of the piece may falls out
       of bounds of the board, PLACE_OUT_BOUNDS is returned.
       Or the placement may collide with existing blocks in the grid
       in which case PLACE_BAD is returned.
       In both error cases, the board may be left in an invalid
       state. The client can use undo(), to recover the valid, pre-place state.
    */
    void backup() {
	for (int x = 0; x < width; x++)
	    System.arraycopy(grid[x], 0, grid_backup[x], 0, height);
	System.arraycopy(widths, 0, widths_backup, 0, height);
	System.arraycopy(heights, 0, heights_backup, 0, width);
	maxHeight_backup = maxHeight;
    }	
    public int place(Piece piece, int x, int y) {
	// flag !committed problem
	if (!committed) throw new RuntimeException("place commit problem");
	committed = false;
	
	int result = PLACE_OK;
	
	// YOUR CODE HERE
	
	backup();
	TPoint[] points = piece.getBody();
	for (int i = 0; i < points.length; i++) {
	    int xx = x + points[i].x;
	    int yy = y + points[i].y;
	    if ((xx < 0) || (xx >= width)
		|| (yy < 0) || (yy >= height))
		return PLACE_OUT_BOUNDS;
	    if (grid[xx][yy])
		return PLACE_BAD;
	    
	    grid[xx][yy] = true;
	    
	    maxHeight = Math.max(maxHeight, yy+1);
	    widths[yy]++;
	    heights[xx] = Math.max(heights[xx], yy+1);
	    if (widths[yy] == width)
		result = PLACE_ROW_FILLED;
	}
	sanityCheck(); // check on success
	return result;
    }
    
    
    /**
       Deletes rows that are filled all the way across, moving
       things above down. Returns the number of rows cleared.
    */
    
    public int clearRows() {	    
	int rowsCleared = 0;
	// YOUR CODE HERE
	
	if (committed) backup();
	committed = false;
	
	int nextToMove = 0;
	
	for (int y = 0; y < maxHeight; y++) {
	    if (widths[y] < width) {
		if (nextToMove != y) {
		    for (int i = 0; i < width; i++)
			grid[i][nextToMove] = grid[i][y];
		    widths[nextToMove] = widths[y];
		}
		nextToMove++;
	    }
	}
	
	rowsCleared = maxHeight - nextToMove;
	

	for (int y = nextToMove; y < maxHeight; y++) {
	    widths[y] = 0;
	    for (int x = 0; x < width; x++)
		grid[x][y] = false;
	}
	for (int x = 0; x < width; x++)
	    heights[x] = getColumnHeightManual(x); // must follow widths

	maxHeight = nextToMove;
	
	sanityCheck();
	return rowsCleared;
    }
    int getColumnHeightManual(int x) {
	int y;
	for (y = height-1; y>=0 && !grid[x][y]; y--)
	    ;
	return y+1;
    }
    int getRowWidthManual(int y) {
	int total = 0;
	for (int x = 0; x < width; x++)
	    total += grid[x][y] ? 1 : 0;
	return total;
    }
    int getMaxHeightManual() {
	int max=0;
	for (int x = 0; x < width; x++)
	    max = Math.max(max, getColumnHeightManual(x));
	return max;
    }
    

	/**
	   Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
	    // YOUR CODE HERE
	    if (committed)
		return ;
	    
	    int[] temp;
	    temp = widths; widths = widths_backup; widths_backup = temp;
	    temp = heights; heights = heights_backup; heights_backup = temp;
	    boolean[][] temp2D;
	    temp2D = grid; grid = grid_backup; grid_backup = temp2D;

	    maxHeight = maxHeight_backup;

	    committed = true;
	    sanityCheck();
	}
	
	
	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		committed = true;
	}


	
	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}

			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}


