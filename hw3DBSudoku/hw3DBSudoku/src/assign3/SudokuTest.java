package assign3;

import static org.junit.Assert.*;

import org.junit.Test;

public class SudokuTest {

    @Test
    public void testToString() {
        Sudoku sudoku = new Sudoku(Sudoku.mediumGrid);
        String mediumGrid = 
                  "5 3 0 0 7 0 0 0 0" + System.lineSeparator()
                + "6 0 0 1 9 5 0 0 0" + System.lineSeparator()
                + "0 9 8 0 0 0 0 6 0" + System.lineSeparator()
                + "8 0 0 0 6 0 0 0 3" + System.lineSeparator()
                + "4 0 0 8 0 3 0 0 1" + System.lineSeparator()
                + "7 0 0 0 2 0 0 0 6" + System.lineSeparator()
                + "0 6 0 0 0 0 2 8 0" + System.lineSeparator()
                + "0 0 0 4 1 9 0 0 5" + System.lineSeparator()
                + "0 0 0 0 8 0 0 7 9";
        assertEquals(sudoku.toString(), mediumGrid);
    }
    
    @Test
    public void testSolve() {
        Sudoku sudoku = new Sudoku(Sudoku.easyGrid);
        String easyGridSolution = 
                  "1 6 4 7 9 5 3 8 2" + System.lineSeparator()
                + "2 8 7 4 6 3 9 1 5" + System.lineSeparator()
                + "9 3 5 2 8 1 4 6 7" + System.lineSeparator()
                + "3 9 1 8 7 6 5 2 4" + System.lineSeparator()
                + "5 4 6 1 3 2 7 9 8" + System.lineSeparator()
                + "7 2 8 9 5 4 1 3 6" + System.lineSeparator()
                + "8 1 9 6 4 7 2 5 3" + System.lineSeparator()
                + "6 7 3 5 2 9 8 4 1" + System.lineSeparator()
                + "4 5 2 3 1 8 6 7 9";
        int count = sudoku.solve();
        assertEquals(sudoku.getSolutionText(), easyGridSolution);
    }
    
    @Test
    public void testSolve2() {
        Sudoku sudoku = new Sudoku(Sudoku.arbitraryGrid);
        assertEquals(sudoku.solve(), Sudoku.MAX_SOLUTIONS);
    }
}
