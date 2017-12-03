/*
 * Daniel Rahn
 * March 2014
 * 
 * The SudokuBoard class keeps track of a grid of integers representing the
 * layout of a sudoku board.
 */

public class SudokuBoard {

	private int[][] board;		// Entire board
	private int[][] row;		// Array of separate rows
	private int[][] column;		// Array of separate columns
	private int[][] grid;		// Array of individual grids
	private int dimX;			// Columns per grid
	private int dimY;			// Rows per grid
	private int gridSize;		// Elements per grid
	
	// Creates a new Sudoku board with the specified number of rows and columns
	public SudokuBoard(int y, int x) {
		dimX = x;
		dimY = y;
		gridSize = y * x;
		board = new int[gridSize][gridSize];
		row = new int[gridSize][gridSize];
		column = new int[gridSize][gridSize];
		grid = new int[gridSize][gridSize];
	}
	
	// Returns the current game board
	public int[][] getBoard() {
		return board;
	}
	
	// Returns the width/height of the board
	public int getSize() {
		return gridSize;
	}
	
	// Returns the value at the given row/column (0-based indexing)
	public int get(int x, int y) {
		return board[y][x];
	}
	
	// Returns whether the given value can be safely placed in the given spot
	// i.e., the same number is not in the same column, row, or grid.
	// Separate array fields were used for each category to save time, at a small
	// memory expense
	public boolean isSafe(int value, int x, int y) {
		if (value > gridSize || x < 0 || y < 0 || x >= gridSize || y >= gridSize) {
			System.out.println("grid size:" + gridSize);
			throw new IllegalArgumentException("Error: value out of range: {" 
												+ value + ", " + x + ", " + y + "}");
		}
		int gridLoc = (x / dimX) + (dimY * (y / dimY));
		if (row[y][value - 1] == 1) {
			return false;
		} else if (column[x][value - 1] == 1) {
			return false;
		} else if (grid[gridLoc][value - 1] == 1) {
			return false;
		}
		return true;
	}
	
	// If it is safe to place the given number at the given coordinate, put
	// it on the board
	public boolean place(int value, int x, int y) {
		if (!isSafe(value, x, y)) {
			System.out.println("Error: number " + value 
					+ " cannot be placed at (" + x + ", " + y + ")");
			return false;
		} else {
			board[y][x] = value;
			row[y][value - 1] = 1;
			column[x][value - 1] = 1;
			int gridLoc = (x / dimX) + (dimY * (y / dimY));
			grid[gridLoc][value - 1] = 1;
			return true;
		}
	}
	
	// Removes the specified element at the given coordinate
	// TODO: get rid of value parameter, get value from board
	// 		 before it is reset to 0.
	public void remove(int value, int x, int y) {
		board[y][x] = 0;
		column[x][value - 1] = 0;
		row[y][value - 1] = 0;
		int gridLoc = (x / dimX) + (dimY * (y / dimY));
		grid[gridLoc][value - 1] = 0;
	}
	
	// Returns whether the current game board is completed
	public boolean isSolved() {
		for (int i = gridSize - 1; i >= 0; i--) {
			for (int j = gridSize - 1; j >= 0; j--) {
				if (board[i][j] == 0)
					return false;
			}
		}
		return true;
	}
	
	// Prints the game board to the console
	public void print() {
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				if (board[i][j] == 0) {
					if (gridSize >= 10)
						System.out.print(" - ");
					else
						System.out.print("- ");
				} else {
					if (board[i][j] >= 10)
						System.out.print(board[i][j] + " ");
					else {
						if (gridSize >= 10)
							System.out.print(" " + board[i][j] + " ");
						else
							System.out.print(board[i][j] + " ");
					}
				}
			}
			System.out.println();
		}
		System.out.println();
	}

	// Return the number of columns per grid
	public int getWidth() {
		return dimY;
	}
	
	// Returns the number of rows per grid
	public int getHeight() {
		return dimX;
	}

	// Clears the current game board (iffy), probably shouldn't use
	public void clear() {
		for (int i = 0; i < dimY; i++) {
			for (int j = 0; j < dimX; j++) {
				board[i][j] = 0;
				column[j][i] = 0;
				row[i][j] = 0;
				grid[i][j] = 0;
			}
		}
	}
}
