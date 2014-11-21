//
// TetrisGrid encapsulates a tetris board and has
// a clearRows() capability.
package assign1;

public class TetrisGrid {
	private boolean[][] grid;
	
	/**
	 * Constructs a new instance with the given grid.
	 * Does not make a copy.
	 * @param grid
	 */
	public TetrisGrid(boolean[][] grid) {
		this.grid = grid;
	}
	
	
	/**
	 * Does row-clearing on the grid (see handout).
	 */
	public void clearRows() {
		boolean[] notFull = new boolean[grid[0].length];
		for (int i = 0; i < grid[0].length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if (!grid[j][i]) {
					notFull[i] = true;
					break;
				}
			}
		}
		
		for (int i = 0; i < notFull.length; i++) {
			if (!notFull[i]) {
				int j = i + 1;
				while (j < notFull.length && !notFull[j]) {
					j++;
				}
				if (j < notFull.length) {
					notFull[j] = false;
					for (int k = 0; k < grid.length; k++) {
						grid[k][i] = grid[k][j];
					}
				} else {
					for (int k = 0; k < grid.length; k++) {
						grid[k][i] = false;
					}
				}
			}
		}
	}
	
	/**
	 * Returns the internal 2d grid array.
	 * @return 2d grid array
	 */
	boolean[][] getGrid() {
		return this.grid;
	}
}
