// Board.java
package tetris;

/**
 CS108 Tetris Board.
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
	
	private int[] widths;
	private int[] heights;
	private int maxHeight;
	
	// backups
	private boolean[][] xGrid;
	private int[] xWidths;
	private int[] xHeights;
	private int xMaxHeight;
	
	
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
		
		widths = new int[height];
		heights = new int[width];
		maxHeight = 0;
		
		xWidths = new int[widths.length];
		xHeights = new int[heights.length];
		xGrid = new boolean[width][height];
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
			// check maxHeight
			int sMaxHeight = 0;
			for (int i = 0; i < heights.length; i++) {
				sMaxHeight = Math.max(sMaxHeight, heights[i]);
			}
			if (maxHeight != sMaxHeight) {
				throw new RuntimeException(String.format("maxHeight = %d, should be %d.",
						maxHeight, sMaxHeight));
			}
			
			// check heights
			for (int i = 0; i < heights.length; i++) {
				int min = 0;
				int j = height - 1;
				for (; j >= 0; j--) {
					if (grid[i][j]) {
						break;
					}
				}
				min = j + 1;
				
				if (min != heights[i]) {
					throw new RuntimeException(String.format("heights[%d] = %d, should be %d.",
							i, heights[i], min));
				}
			}
			
			// check widths
			for (int i = 0; i < widths.length; i++) {
				int sum = 0;
				for (int j = 0; j < width; j++) {
					sum += (grid[j][i] ? 1 : 0);
				}
				if (sum != widths[i]) {
					throw new RuntimeException(String.format("widths[%d] = %d, should be %d.",
							i, widths[i], sum));
				}
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
		int result = 0;
		for (int i = 0; i < piece.getWidth(); i++) {
			result = Math.max(result, heights[x + i] - piece.getSkirt()[i]);
		}
		return result;
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		return heights[x];
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		 return widths[y];
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return true;
		} else {
			return grid[x][y];
		}
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
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");
		
		System.arraycopy(widths, 0, xWidths, 0, widths.length);
		System.arraycopy(heights, 0, xHeights, 0, heights.length);
		for (int i = 0; i < width; i++) {
			System.arraycopy(grid[i], 0, xGrid[i], 0, height);
		}
		xMaxHeight = maxHeight;
		
		committed = false;
		
		if (x < 0 || y < 0 || x >= width || y >= height
				|| piece.getWidth() + x > width || piece.getHeight() + y > height) {
			return PLACE_OUT_BOUNDS;
		}
		
		int result = PLACE_OK;
		
		TPoint[] body = piece.getBody();
		for (int i = 0; i < body.length; i++) {
			if (grid[x + body[i].x][y + body[i].y]) {
				return PLACE_BAD;
			}
			
			grid[x + body[i].x][y + body[i].y] = true;
			widths[body[i].y + y] += 1;
			if (widths[body[i].y + y] == width) {
				result = PLACE_ROW_FILLED;
			}
			
			heights[x + body[i].x] = Math.max(heights[x + body[i].x], y + body[i].y  + 1);
			maxHeight = Math.max(maxHeight, heights[body[i].x + x]);
		}
		
		sanityCheck();
		
		return result;
	}
	
	
	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		if (committed) {
			System.arraycopy(widths, 0, xWidths, 0, widths.length);
			System.arraycopy(heights, 0, xHeights, 0, heights.length);
			for (int i = 0; i < width; i++) {
				System.arraycopy(grid[i], 0, xGrid[i], 0, height);
			}
			xMaxHeight = maxHeight;
			
			committed = false;
		}
		
		int to = 0;
		while (to < maxHeight && widths[to] < width) {
			to += 1;
		}
		
		int from = to;
		for (; to < maxHeight; to++) {
			do {
				from++;
			} while (from < maxHeight && widths[from] == width);
				
			if (from < maxHeight) {
				widths[to] = widths[from];
				for (int i = 0; i < width; i++) {
					grid[i][to] = grid[i][from];
				}
			} else {
				widths[to] = 0;
				for (int i = 0; i < width; i++) {
					grid[i][to] = false;
				}
			}
		}
		
		int rowsCleared = from - to;
		maxHeight = 0;
		
		for (int i = 0; i < heights.length; i++) {
			int j;
			for (j = heights[i] - rowsCleared - 1; j >= 0 && !grid[i][j]; j--) { }
			heights[i] = j + 1;
			maxHeight = Math.max(maxHeight, heights[i]);
		}
		
		sanityCheck();
		
		return rowsCleared;
	}



	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		if (!committed) {
			committed = true;
			
			maxHeight = xMaxHeight;
			
			// swap pointers! Very smart! :)
			Object tmp;
			
			tmp = heights;
			heights = xHeights;
			xHeights = (int[]) tmp;
			
			tmp = widths;
			widths = xWidths;
			xWidths = (int[]) tmp;
			
			tmp = grid;
			grid = xGrid;
			xGrid = (boolean[][]) tmp;
		}
		
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
	
	public static void main(String[] args) {
		Board board= new Board(6,6);
		Piece p1 = Piece.getPieces()[Piece.SQUARE];
		board.place(p1, 0, 0);
		System.out.println(board.toString());
	}
	
}


