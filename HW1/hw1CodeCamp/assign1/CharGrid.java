// HW1 2-d array Problems
// CharGrid encapsulates a 2-d grid of chars and supports
// a few operations on the grid.

package assign1;

public class CharGrid {
	private char[][] grid;

	/**
	 * Constructs a new CharGrid with the given grid.
	 * Does not make a copy.
	 * @param grid
	 */
	public CharGrid(char[][] grid) {
		this.grid = grid;
	}
	
	/**
	 * Returns the area for the given char in the grid. (see handout).
	 * @param ch char to look for
	 * @return area for given char
	 */
	public int charArea(char ch) {
		int minRow = Integer.MAX_VALUE;
		int minCol = Integer.MAX_VALUE;
		int maxRow = -1;
		int maxCol = -1;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (grid[i][j] == ch) {
					if (i < minRow) {
						minRow = i;
					}
					if (i > maxRow) {
						maxRow = i;
					}
					if (j < minCol) {
						minCol = j;
					}
					if (j > maxCol) {
						maxCol = j;
					}
				}
			}
		}
		if (maxRow >= minRow && maxCol >= minCol) {
			return (maxCol - minCol + 1) * (maxRow - minRow + 1);
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns the count of '+' figures in the grid (see handout).
	 * @return number of + in grid
	 */
	public int countPlus() {
		return 0; // TODO ADD YOUR CODE HERE
	}
	
}
