/*
 * Prevents invalid characters from being entered into the Sudoku Board as
 * well as draws the heatmap if it is turned on. Has gotten a bit out of control
 */

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.*;

import javax.swing.JTextField;
import javax.swing.text.*;


public class PatternFilter extends DocumentFilter {
	
	private Pattern pattern;				// Regex of valid characters
	private JTextField text;				// Current text field
	private List<JTextField> texts;	// All text fields
	private SudokuGame g;					// Current game
	private SudokuBoard b;					// Current game board
	private int col;						// col of this text
	private int row;						// row of this text
	private int heatMapCount;				// Number of available options for a text field
	private List<Integer> unsafe;			// Array of unsafe spaces.
	
	// Initiate the filter
	public PatternFilter(String pat, SudokuGame g, SudokuBoard b, JTextField text, int col, int row) {
		pattern = Pattern.compile(pat);
		this.b = new SudokuBoard(b.getHeight(), b.getWidth());
		this.text = text;
		this.col = col;
		this.row = row;
		this.g = g;
		this.texts = g.getTexts();
		unsafe = new ArrayList<Integer>();
	}
	
	public void insertString(FilterBypass fb, int offset, String string, 
							 AttributeSet attr) throws BadLocationException {
		String newStr = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
		
		// Really only used if the user deleted something and the heatmap is not on
		if (newStr == null || newStr.length() == 0)	
			text.setBackground(Color.WHITE);
		// Locked in text has a gray background
		if (!text.isEditable())
			text.setBackground(new Color(220, 220, 220));
		// If not in the middle of a solve, clear the gameboard
		if (!g.getSolving()) {
			b = new SudokuBoard(g.getY(), g.getX());
			if (b.get(col, row) != 0)
				b.remove(b.get(col, row), col, row);
		}
		// Normally, 0's aren't allowed, but if the solution is invalid, allow it
		if (g.isError() && newStr.equals("0"))
			super.insertString(fb, offset, string, attr);
		int n = 0;
		Matcher m = pattern.matcher(newStr);
		if (m.matches()) {
			// If this is attempted during initialization, null pointer exception
			// as the text fields have not yet been drawn
			if (!g.isInit()) {
				super.insertString(fb, offset, string, attr);
				n = Integer.parseInt(newStr);
				if (n != 0) {
					int size = b.getSize();
					int count = 0;
					// Update each text based on the newly entered character
					for (JTextField t : texts) {
						int text = 0;
						if (t.getText().length() > 0 && !t.getText().equals("0"))
							text = Integer.parseInt(t.getText());
						// the board is two-dimensional, but the texts are one-dimensional
						// so % and / are needed
						if (b.get(count % size, count / size) != 0)
							b.remove(b.get(count % size, count / size), count % size, count / size);
						if (text != 0 && count != (size * row + col)) {
							if (!b.isSafe(text, count % size, count / size))
								unsafe.add(count);
							b.place(text, count % size, count / size);
							if (t.isEditable()) {
								if (b.isSafe(text, count % size, count / size))
									t.setBackground(Color.WHITE);
							} else
								t.setBackground(new Color(220, 220, 220));
						}
						count++;
					}
					// If unsafe, add it to the list of unsafe cells
					if (!b.isSafe(n, col, row)) {
						text.setBackground(Color.RED);
						unsafe.add(row * size + col);
					}
					else {
						b.place(n, col, row);
						if (text.isEditable())
							text.setBackground(Color.WHITE);	// Needed when heatmap is off
					}
					// Set each editable cell green if solved
					if (b.isSolved())
						for (JTextField t : texts)
							if (t.isEditable())
								t.setBackground(new Color(80, 255, 80));
				} else
					text.setBackground(Color.WHITE);
				// Safe to enter string now if initializing 
				if (g.isInit())
					super.insertString(fb, offset, string, attr);
				int count = 0;
				// Update the tooltip of each cell
				for (JTextField t : texts) {
					String options = getOptions(count, t);
					// Options2 currently is not working at all
	//				String options = getOptions2(count, t);
					// Update the background of each cell that is editable if
					// the heatmap is on
					if (t.isEditable() && g.getHeatMap()) {
						if (!options.equals("All") && !options.equals("None")) {
							double colorFactor = (heatMapCount * 1.0) / b.getSize() * 6;
							int newColor = 0;
							double[] scale = getScale();
							if (colorFactor < scale[0]) {
								colorFactor = Math.max(0, colorFactor / scale[0]);
								newColor = Math.min((int) (255 * colorFactor * colorFactor) + 40, 230);
								t.setBackground(new Color(newColor, 255, newColor));
							} else  if (colorFactor > scale[1]) {
								colorFactor = Math.max(0, (colorFactor) / 6);
								newColor = Math.min(325 - (int) (255 * colorFactor * colorFactor), 240);
								t.setBackground(new Color(255, newColor, newColor));
							} else {
								colorFactor = Math.max(0, (colorFactor) / scale[1]);
								newColor = Math.max(Math.min(255 - (int) (255 * colorFactor) + 60, 240), 0);
								t.setBackground(new Color(255, 255, newColor));
							}
						} else {
							t.setBackground(new Color(255, 80, 80));
						}
					} if (t.isEditable())
						t.setToolTipText(options);
					else
						t.setToolTipText("Locked In");
					if (options.equals("None") && t.getText().length() == 0 && g.getHeatMap())
						t.setBackground(new Color(127, 0, 127));
					else if (options.length() <= 2 && t.getText().length() == 0 && g.getHeatMap()) 
						t.setBackground(Color.CYAN);
					count++;
				}
				if (b.isSolved())
					for (JTextField t : texts) 
						t.setToolTipText("Solved");
				for (int i : unsafe)
					if (texts.get(i).isEditable())
						texts.get(i).setBackground(Color.RED);
				unsafe.clear();
			} else
				super.insertString(fb, offset, string, attr);
		}
	}
	
	// Different color scales depending on the size
	private double[] getScale() {
		if (b.getSize() < 7)
			return new double[] {3.3, 5};
		else if (b.getSize() == 8) 
			return new double[] {2.5, 4.5};
		else if (b.getSize() < 10)
			return new double[] {2, 4};
		else if (b.getSize() < 20)
			return new double[] {1.5, 3.5};
		else if (b.getSize() < 40)
			return new double[] {1, 3.2};
		else 
			return new double[] {0.6, 3};
	}
	
	// TODO: Quickly check same grid/col/row for the same number. If it's 
	// 		 the only one with that number, show that it's the only option
	private String getOptions(int count, JTextField t) {
		String result = "";
		boolean all = true;
		int n = 0;
		if (t.getText().length() != 0)
			n = Integer.parseInt(t.getText());
		int heatCount = 0;
		for (int i = 1; i <= b.getSize(); i++) {
			if (b.isSafe(i, count % b.getSize(), count / b.getSize()) || i == n) {
				result += i + ", ";
				heatCount++;
			} else
				all = false;
		}
		heatMapCount = heatCount;
		if (all)
			return "All";
		else
			if (result == "")
				return "None";
			return result.substring(0, result.length() - 2);
	}
	
	// TODO: Get it so it's OR, not AND for col/row/grid
	// WIP. Doesn't do anything as intended atm
	private String getOptions2(int count, JTextField text) {
		String result = "";
		int size = b.getSize();
		int[][][] possibilities = new int[size][size][size];
//		List<List<Integer>> options = new ArrayList<List<Integer>>();
		int c = 0;
		for (JTextField t : texts) {
			if (t.isEditable()) {
	//			List<Integer> inner = new ArrayList<Integer>();
				int n = 0;
				if (t.getText().length() != 0 && !t.getText().equals("0"))
					n = Integer.parseInt(t.getText());
				for (int i = 0; i < size; i++) {
					if (b.isSafe(i + 1, c % size, c / size) || i + 1 == n)
						possibilities[c % size][c / size][i] = 1;
				}
			}
			c++;
		}
//		System.out.println(Arrays.toString(possibilities[0][0]));
		int[] options = possibilities[count % size][count / size];
		System.out.println(Arrays.toString(options));
		int[] columnOptions = new int[size];
		for (int i = 0; i < size; i++) {
			if (i != count / size) {
	//			int[] row = possibilities[i][count / size];
				int[] column = possibilities[count % size][i];
//				if (count == 39)
//					System.out.println(Arrays.toString(column));
				for (int j = 0; i < size; i++)
	//				if (columnOptions[j] != 1)
	//					if (options[j] == 1)
							if (column[j] == 1)
								columnOptions[j] = 1;
			}
		}
		if (count == 39) {
			System.out.println(Arrays.toString(columnOptions));
//			System.out.println();
		}
		int[] rowOptions = new int[size];
		for (int i = 0; i < size; i++) {
			int[] row = possibilities[i][count / size];
//			int[] column = possibilities[count % size][i];
			for (int j = 0; i < size; i++)
//				if (columnOptions[j] != 1)
//					if (options[j] == 1)
						if (row[j] == 1 && j != count % size)
							rowOptions[j] = 1;
		}
		int gridLoc = ((count % size) / b.getHeight()) + (b.getWidth() * ((count / size) / b.getHeight()));
		int w = size / b.getWidth();
		int h = size / b.getHeight();
		int[] gridOptions = new int[size];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int[] gridT = possibilities[(gridLoc % w) * w + i][(gridLoc / h) * h + j];
				if ((gridLoc % w) * w + i != col && (gridLoc / h) * h + j != row)
					for (int k = 0; k < size; k++)
//						if (gridOptions[k] != 1)
//							if (options[k] == 1)
								if (gridT[k] == 1)
									gridOptions[k] = 1;
			}
		}
		int[] finalOptions = new int[size];
		for (int i = 0; i < size; i++) {
			if ((columnOptions[i] == 0 || rowOptions[i] == 0 || gridOptions[i] == 0)
					&& options[i] == 1)
				return "" + (i + 1);
			else if (options[i] == 1)
				finalOptions[i] = 1;
		}
		int heatCount = 0;
		boolean all = true;
		for (int i = 0; i < size; i++) {
			if (finalOptions[i] == 1) {
				result += (i + 1) + ", ";
				heatCount++;
			} else
				all = false;
		}
		heatMapCount = heatCount;
		if (all)
			return "All";
		else
			if (result == "")
				return "None";
			return result.substring(0, result.length() - 2);
	}
	
	public void replace(FilterBypass fb, int offset, int length, String string, 
						AttributeSet attr) throws BadLocationException {
		if (length > 0) fb.remove(offset,  length);
		insertString(fb, offset, string, attr);
	}

}
