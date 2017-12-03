/*
 * Completely out of control class. Started off small, but as more and more
 * features were added, it became this beast. Keeps track of a GUI sudoku board.
 * Files can be imported and exported, tooltips and heatmaps assist with solving
 * if wanted, save the screen at any given time. There are issues with speed if
 * the size gets above 4x4 if doing a clean solve (no predefined solution file)
 * Overall, a cool little program, but pretty poorly implemented.
 * 
 */

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AbstractDocument;

/*
 * Possibilities: 
 * Get rid of buttons, use menus
 * option to click on what squares to be revealed
 */

//TODO: Help button/menu option UPDATE
public class SudokuGame extends JFrame {
	private static final long serialVersionUID = 0L;
	private List<JTextField> finalTexts;		// Text cells for the board
	private int dimY;							// Rows per sudoku grid
	private int dimX;							// Columns per sudoku grid
	private int size;							// Elements per sudoku grid
	private int[][] finalData;					// When asking to solve, stores
												// pre-defined numbers
	private int sizeFact;						// Scale of UI
	private int gap;							// Gap between text cells
	private SudokuBoard b;						// Main sudoku board
	private boolean solving;					// true when solve button initiated
	private boolean heatMap;					// Determines whether to show the heat map
	private boolean menneskeImported;			// True if the current board is from the predefined set
	private SudokuBoard solvedBoard;			// Game board of solved puzzle assuming menneskeImported = true			
	private boolean setup;						// True if board has not been fully initialized yet
	private boolean toolTipInitial;
	private boolean errorSolve;
	private boolean small;
	private String difficulty;
//	private long delay = 100000000;


	// Opens up the size chooser to determine the size of the sudoku board
	public static void main(String[] args) {
		ToolTipManager.sharedInstance().setEnabled(false);
		drawPicker(false);
	}	

	// Initiates the size chooser
	public static void drawPicker(final boolean heat) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SizePicker sp = new SizePicker(heat, ToolTipManager.sharedInstance().isEnabled());
				ImageIcon imgIcon = new ImageIcon("res/Sudoku2.png");
				sp.setIconImage(imgIcon.getImage());
				sp.setVisible(true);
				sp.toFront();
			}
		});
	}

	// Creates a new board based on the given file, and creates a solution board
	// if from the predefined Menneske2 folder
	public SudokuGame(File f, boolean heat, boolean tool, String difficulty) throws FileNotFoundException {
		this.difficulty = difficulty;
		heatMap = heat;
		toolTipInitial = tool;
		setup = true;
		File solved = new File(f.getParent() + "/Solutions/" + f.getName().substring(0, f.getName().indexOf(".")) + "_solution.sudoku");
		if (f.exists()) {
			menneskeImported = true;
		}
		Scanner input = new Scanner(f);
		dimY = input.nextInt();
		dimX = input.nextInt();
		size = dimY * dimX;
		getInitialData();
		if (!small) {
			if (menneskeImported)
				setupSolvedBoard(solved);
			while (input.hasNextInt()) {
				int value = input.nextInt();
				int xLoc = input.nextInt();
				int yLoc = input.nextInt();
				String edit = input.next();
				finalTexts.get(yLoc * size + xLoc).setText(value + "");
				if (edit.equals("false"))
					selfMode(finalTexts.get(yLoc * size + xLoc));
			}
			input.close();
			setup = false;
			if (heatMap)
				for (JTextField t : finalTexts)
					if (t.getText().length() != 0) {
						t.setText(t.getText());
						break;
					}
		}
	}
	
	// Creates a default 3x3 board
	public SudokuGame() {
		this(3, 3);
	}

	// Creates a fresh sudoku board based on the given dimensions
	public SudokuGame(int y, int x) {
		difficulty = "";
		dimY = y;
		dimX = x;
		size = x * y;
		getInitialData();
	}
	
	// Setup the solution board if available
	private void setupSolvedBoard(File f) throws FileNotFoundException {
		Scanner input = new Scanner(f);
		solvedBoard = new SudokuBoard(input.nextInt(), input.nextInt());
		while (input.hasNextInt()) {
			int value = input.nextInt();
			int xLoc = input.nextInt();
			int yLoc = input.nextInt();
			input.next();
			solvedBoard.place(value, xLoc, yLoc);
		}
		input.close();
	}


	// Set up and initialize fields
	public void getInitialData() {
		gap = 5;
		b = new SudokuBoard(dimY, dimX);
		finalTexts = new ArrayList<JTextField>();

		// Scale UI according to puzzle and screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double height = screenSize.getHeight();
		double heightFact = height / 1080;
		if (size < 20)
			sizeFact = 50;
		else if (size < 26)
			sizeFact = 40;
		else if (size < 35)
			sizeFact = 30;
		else if (size < 40)
			sizeFact = 25;
		else {
			gap = 2;
			sizeFact = 20;
		}
		if (size > 15)
			sizeFact = (int) (sizeFact * heightFact);
		small = false;
		if (heightFact != 1 && size >= 40)
			small = true;
		else if (heightFact < 0.8 && size >= 35)
			small = true;
		else if (heightFact < 0.6 && size >= 25)
			small = true;
		// If the puzzle to too large to display well on the monitor,
		// tell the user and go back to the selection screen.
		if (small) {
			tooSmall();
			drawPicker(heatMap);
			dispose();
		}
		if (!small)
			initUI();
	}
	
	// Warn the user that checking a position can take a long time
	private void tooSmall() {
		JOptionPane.showMessageDialog(this, "Your screen resolution appears to"
				+ " be too low to display this size \n puzzle correctly. Please"
				+ " try a smaller size.", "Error", JOptionPane.WARNING_MESSAGE);
//		JOptionPane.showConfirmDialog(this, "Your screen resolution appears to"
//				+ " be too low to display this size puzzle correctly.", "Error",
//				JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
	}

	// Main method for the GUI. Sets up the frame and all of its elements
	public void initUI() {
		JPanel panel = getPanel();
		getContentPane().add(panel);
		panel.setLayout(null);
		KeyListener shortcuts = getKeyboardShortcuts();
		List<JTextField> texts = getTextBoxes(shortcuts, panel);
		// Add all text cells to the panel
		for (JTextField f : texts)
			panel.add(f);

		JButton parseButton = getParseButton();
		JButton reset = getResetButton();
		JButton newSize = getNewSizeButton();
		JButton save = getSaveButton(panel);

		// Add keyboard shortcuts to all elements
		parseButton.addKeyListener(shortcuts);
		reset.addKeyListener(shortcuts);
		newSize.addKeyListener(shortcuts);
		save.addKeyListener(shortcuts);
		panel.addKeyListener(shortcuts);

		// Add elements to the panel
		panel.add(parseButton);
		panel.add(reset);
		panel.add(newSize);
		panel.add(save);
		String title = "";
		if (size > 6)
			title = "Sudoku Solver - ";
		setTitle(title + b.getWidth() + "x" + b.getHeight() + " - " + difficulty);
		if (size <= 6)
			setSize(size * sizeFact + 22, size * sizeFact + 130);
		else
			setSize(size * sizeFact + 22, size * sizeFact + 90);
		setResizable(false);
		setLocationRelativeTo(null);		// Center frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		finalTexts = texts;
	}

	// Returns a JPanel with Sudoku grid lines drawn
	private JPanel getPanel() {
		final int finalSize = dimX * dimY;
		return new JPanel() {
			private static final long serialVersionUID = 0L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, finalSize * sizeFact + 30, (finalSize + 3) * sizeFact);
				g.setColor(Color.BLACK);
				for (int i = 0; i <= dimY; i++) {
					// StartX, StartY, width, height
					g.fillRect((sizeFact * dimX * i) + (10 - gap), 
							10 - gap, gap, (finalSize * sizeFact) + gap);
				} for (int i = 0; i <= dimX; i++) {
					g.fillRect(10 - gap, (sizeFact * dimY * i + 10 - gap), 
							(finalSize * sizeFact) + gap, gap);
				}
			}
		};
	}

	// Returns variouis keyboard shortcuts. This in itself should probably be a class
	private KeyListener getKeyboardShortcuts() {
		// Must create final variable for override methods
		final JFrame finalFrame = this;
		KeyListener lsn = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				// SHIFT + CTRL + S = Solve
				if (e.getKeyCode() == KeyEvent.VK_S && 
						(e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
					if ((e.getModifiers() & InputEvent.SHIFT_MASK)!= 0 ) {
						if (dimY * dimX < 14 || menneskeImported)
							solveAction(true);
						else
							notEnough(new JTextField(0), true);
						// CTRL + S = Save as image
					} else if ((e.getModifiers() & InputEvent.SHIFT_MASK) == 0) { 
						saveAction();
					}
				}

				// CTRL + R = Reset
				if (e.getKeyCode() == KeyEvent.VK_R &&
						(e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
					if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
						resetAction(true);
					} else
						resetAction(false);
				}

				// CTRL + N = Create new board of different size
				if (e.getKeyCode() == KeyEvent.VK_N &&
						(e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
					changeSizeAction();
				}

				// CTRL + W = Exit program
				if (e.getKeyCode() == KeyEvent.VK_W &&
						(e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
					System.exit(0);
				}

				// CTRL + E = Export current board to .sudoku file for later use
				if (e.getKeyCode() == KeyEvent.VK_E &&
						(e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
					try {
						export();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}

				// CTRL + O = Import .sudoku file
				// CTRL + SHIFT + O = Import random .sudoku file
				if (e.getKeyCode() == KeyEvent.VK_O &&
						(e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
					if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
						try {
							importRandom();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					} else {
						try {
							importFile();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}

				// F1 = Help menu
				if (e.getKeyCode() == KeyEvent.VK_F1) {
					SizePicker.printHelpScreen(finalFrame);
				}

				// F2 = Toggle heatmap
				if (e.getKeyCode() == KeyEvent.VK_F2) {
					heatMapAction();
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {}
		};
		return lsn;
	}

	// Attempts to solve the Sudoku puzzle based on the current layout of the board
	private boolean solveAction(boolean printing) {			
		solving = true;
		// Reset the board. b.clear() doesn't seem to work
		b = new SudokuBoard(dimY, dimX);
		// If a cell is not safe to place (red background), stop solving and
		// flash the cell that is causing problems
		for (JTextField t : finalTexts) {
			if (t.getBackground() == Color.RED) {
				flash(t, Color.RED);
				solving = false;
			}
		}
		// If all cells are okay, solve
		boolean result = false;
		if (solving) 
			result = parsePressed(printing);
		solving = false;
		if (!result && printing) {
			errorSolve = true;
			for (JTextField t : finalTexts)
				if (t.getText().length() == 0)
					t.setText("0");
			errorSolve = false;
		}
		return result;
	}

	// Flash a text field the specified color a few times
	public void flash(JTextField text, Color c) {
		long time = System.nanoTime();
		for (int i = 0; i < 4; i++) {
			text.setBackground(Color.WHITE);
			text.update(text.getGraphics());
			while (System.nanoTime() - time < 100000000){}
			time = System.nanoTime();
			text.setBackground(c);
			text.update(text.getGraphics());
			while (System.nanoTime() - time < 100000000){}
			time = System.nanoTime();
		}
	}

	// Old name, but just sets up the solve process and then writes the solution
	// to the game board
	private boolean parsePressed(boolean printing) {
		if (menneskeImported) {
			int count = 0;
			boolean badSol = false;
			for (JTextField t : finalTexts) {
				if (!t.getText().equals(""))
					if (Integer.parseInt(t.getText()) != solvedBoard.get(count % size, count / size)) {
						badSol = true;
						flash(t, Color.RED);
						break;
					}
				count++;
			}
			if (!badSol) {
				count = 0;
				for (JTextField t : finalTexts) {
					t.setText("" + solvedBoard.get(count % size, count / size));
					count++;
				}
			}
			return true;
		} else {
			final int[] data = new int[finalTexts.size()];
			int count = 0;
			for (JTextField f : finalTexts) {
				if (printing)
					if (f.getText().length() > 0)
						data[count] = Integer.parseInt(f.getText());
					else
						data[count] = 0;
				count++;
			}
			boolean result = setInitial(data);
			boolean badSolution = false;
			if (printing) {
				final int[] finalData = getFinalData(size); // Get results of the solve
				count = 0;
				for (JTextField f : finalTexts) {
					// No solution found, make text red
					f.setText(finalData[count] + "");
					if (finalData[count] == 0) {
						if (!badSolution) {
							badSolution = true;
							for (JTextField t : finalTexts) {
								if (t.isEditable()) {
									t.setForeground(Color.BLACK);
									t.setBackground(new Color(255, 100, 100));
								}
							}
						}
					}
					// Solution found, make text blue if it was empty before
					else if (data[count] == 0)
						f.setForeground(Color.BLUE);
					count++;
				}
			}
			if (badSolution)
				for (JTextField t : finalTexts)
					if (t.isEditable())
						t.setBackground(new Color(255, 100, 100));
			return result;
		}
	}

	// Get the data from the text cells and solve based on that data
	public boolean setInitial(int[] data) {
		List<String> initial = parseData(data);
		// Base operation: Solve the given puzzle
		boolean result = solve(0, 0, size, initial);
		finalData = b.getBoard();
		return result;
	}

	// Search the text fields for non-zero entries, and return
	// those entries as an arraylist
	public List<String> parseData(int[] initialData) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < initialData.length; i++) {
			if (initialData[i] != 0) {
				// size is linear, but isSafe is two-dimensional, % size gets
				// x-coord, / size gets y-coord
				if (!b.isSafe(initialData[i], i % size, i / size))
					finalTexts.get(i).setBackground(Color.RED);
				b.place(initialData[i], i % size, i / size);
				result.add((i % size) + " " + (i / size));
			}
		}
		return result;
	}

	// Recursively solve the sudoku puzzle. Returns true if a solution is found
	//  Params x and y should not be confused with dimX and dimY
	//TODO: Incorporate this graphically? A slow down would probably be necessary
	//TODO: size param probably not necessary
	//TODO: Place based on number of initial options available
	private boolean solve(int x, int y, int size, List<String> initial) {
//		long time = System.nanoTime();
//		while (System.nanoTime() - time < delay) {}
		if (b.isSolved()) {
			return true;
		} else {
			// i = col, j = row, k = value
			for (int i = x; i < size; i++) {
				for (int j = y; j < size; j++) {
					// Make sure the current coordinates do not correlate to
					// and initial value
					if (!initial.contains(i + " " + j)) {
						boolean wasPlaced = false;
						for (int k = 1; k <= size; k++) {
							//TODO: Is second statement necessary? Already tested previously
							if (b.isSafe(k, i, j) && !initial.contains(i + " " + j)) {
								wasPlaced = true;
								b.place(k, i, j);
//								finalTexts.get(j * size + i).setText("" + k);
//								finalTexts.get(j * size + i).update(finalTexts.get(j * size + i).getGraphics());
								// if x/y is at the end of the column/row, go to next column/row 
								if (solve(i + (j + 1) / size, (j + 1) % size, size, initial))
									return true;
								else {
									b.remove(k, i, j);
//									finalTexts.get(j * size + i).setText("");
//									finalTexts.get(j * size + i).update(finalTexts.get(j * size + i).getGraphics());
									wasPlaced = false;
								}
							}
							// All numbers have been exhausted, and nothing
							// was ever placed. No solution found, return false
							if (k == size && !wasPlaced) 
								return false;
						}
						// If end of column reached, go to the next one
					} else if (j == size && i == size) {
						return solve(i + 1, 0, size, initial);
					}
					// If j = size - 1, end of the column reached, reset y to the
					// beginning of the column for the next iteration
					if (j == size - 1) 
						y = 0;
				}
			}
		}
		return true;
	}

	// Returns the solved puzzle as an array
	//TODO: Delete, could probably just use finalData
	public int[] getFinalData(int size) {
		finalData = b.getBoard();
		int[] data = new int[size * size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				data[(i * size) + j] = finalData[i][j];
			}
		}
		return data;
	}

	// Saves the current view to a .png file. Looked online to find how to do it
	public void saveAction() {
		// Create a container that holds the frame's content
		Container content = getContentPane();
		// Create a new image with the dimensions of the frame, RGB colors
		BufferedImage img = new BufferedImage(content.getWidth(), content.getHeight() - 45, BufferedImage.TYPE_INT_RGB);
		// Create a graphics object based on the new image
		Graphics2D g2d = img.createGraphics();
		// Draw the image to the container
		content.printAll(g2d);
		g2d.dispose();
		// Choose where to save the file
		//TODO: Overwrite protection
		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File currentDir = chooser.getCurrentDirectory();
			String fileName = chooser.getSelectedFile().getName();
			String savePath = currentDir + "\\" + fileName;
			if (!savePath.endsWith(".png"))
				savePath += ".png";
			try {
				ImageIO.write(img, "png", new File(savePath));
			} catch (IOException ex) {
				ex.printStackTrace();
			}    
			printSaved();
		}
	}

	// Create a dialog to inform the user that the file has been saved
	// TODO: make an actual java dialog like in overwrite protection
	public void printSaved() {
		final JFrame saved = new JFrame("File Saved");
		JPanel panel = new JPanel();
		saved.getContentPane().add(panel);
		panel.setLayout(null);
		JButton okay = new JButton("Okay");
		JLabel wasSaved = new JLabel("File Saved");
		wasSaved.setBounds(35, 10, 70, 30);
		okay.setBounds(95, 10, 70, 30);
		okay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saved.dispose();
			}
		});
		okay.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					saved.dispose();
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		panel.add(wasSaved);
		panel.add(okay);
		saved.setSize(220, 90);
		saved.setVisible(true);
		saved.setLocationRelativeTo(this);
	}

	// Reset the game board
	public void resetAction(boolean complete) {
		b = new SudokuBoard(dimY, dimX);
		// Make all the text fields editable and black font
		for (JTextField f : finalTexts) {
			if (!complete) {
				if (f.isEditable()) {
					f.setText("");
					f.setForeground(Color.BLUE);
					// Set background to a neutral yellow if heatmap is on, white otherwise
					if (heatMap)
						f.setBackground(new Color(255, 255, 220));
					else
						f.setBackground(Color.WHITE);
				}
			} else {
				f.setEditable(true);
				f.setText("");
				f.setForeground(Color.BLACK);
				// Set background to a neutral yellow if heatmap is on, white otherwise
				if (heatMap)
					f.setBackground(new Color(255, 255, 220));
				else
					f.setBackground(Color.WHITE);
			}
		}
		// Redraw the heatmap if the board is not empty
		if (!complete) {
			for (JTextField f : finalTexts) {
				if (f.getText().length() != 0) {
					f.setText(f.getText());
					break;
				}
			}
		} else {
			for (JTextField t : finalTexts)
				t.setToolTipText("All");
		}
		// Reset cursor to the first cell
		finalTexts.get(0).requestFocus();
	}

	// Initiates a new size picker and disposes of the old board GUI
	private void changeSizeAction() {
		drawPicker(heatMap);
		dispose();
	}

	// Horribly long method, but very simple. 
	// Exports the current board to a .sudoku file for later use
	private void export() throws FileNotFoundException {
		JFileChooser chooser = new JFileChooser() {
			private static final long serialVersionUID = 0L;

			@Override
			// Overwrite protection
			public void approveSelection() {
				if (getDialogType() == SAVE_DIALOG) {
					File selectedFile = getSelectedFile();
					if ((selectedFile != null) && selectedFile.exists()) {
						int response = JOptionPane.showConfirmDialog(this, "The file "
								+ selectedFile.getName() + " already exists. Do you want "
								+ "to replace the existing file?", "Overwrite file",
								JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						if (response != JOptionPane.YES_OPTION)
							return;
					}
				}
				super.approveSelection();
			}
		};
		// Only show folders and .sudoku files
		FileFilter filter = new ExtensionFileFilter("Sudoku File", "sudoku");
		chooser.setFileFilter(filter);
		chooser.setSelectedFile(new File("New_Puzzle.sudoku"));
		int returnVal = chooser.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File currentDir = chooser.getCurrentDirectory();
			String fileName = chooser.getSelectedFile().getName();
			String savePath = currentDir + "\\" + fileName;
			if (!savePath.endsWith(".sudoku"))
				savePath += ".sudoku";
			PrintStream output = new PrintStream(new File(savePath));
			int count = 0;
			// First line is the dimension of the board
			output.println(dimX + " " + dimY);
			for (JTextField t : finalTexts) {
				int x = count % size;
				int y = count / size;
				boolean editable = t.isEditable();
				String text = t.getText();
				if (!text.equals("")) {
					output.println(text + " " + x + " " + y + " " + editable);
				}
				count++;
			}
			output.close();
			printSaved();
		}
	}

	// Create a new SudokuGame based on the given file
	private void importFile(final File f) throws FileNotFoundException {
		final String diff = getDiff(f);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SudokuGame g;
				try {
					g = new SudokuGame(f, heatMap, ToolTipManager.sharedInstance().isEnabled(), diff);
					ImageIcon imgIcon = new ImageIcon("res/Sudoku2.png");
					g.setIconImage(imgIcon.getImage());
					g.setVisible(true);
					g.toFront();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		dispose();
	}

	// Creates a new Sudoku board based on the chosen .sudoku file
	//TODO: Ensure .sudoku hasn't been tampered with
	public void importFile() throws FileNotFoundException {
		JFileChooser chooser = new JFileChooser();
		FileFilter filter = new ExtensionFileFilter("Sudoku File", "sudoku");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			String path = f.getAbsolutePath();
			// Make sure the file is a .sudoku file (even though it's just a text file)
			if (!path.endsWith(".sudoku")) {
				int response = JOptionPane.showConfirmDialog(chooser, "The file "
						+ f.getName() + " does not appear to be a .sudoku file. Try again?", "File Type Error",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (response == JOptionPane.YES_OPTION)
					importFile();
			} else {
				importFile(f);
			}
		}
	}

	// Toggles the heat map
	private void heatMapAction() {
		heatMap = !heatMap;
		// If heatmap is now on, find the first cell with non-trivial
		// data and set the text. The PatternFilter does the rest
		if (heatMap) {
			for (JTextField t : finalTexts) {
				if (t.getText().length() != 0 && !t.getText().equals("0")) {
					if (t.getBackground() != Color.RED) {
						t.setText(t.getText());
						if (!t.isEditable())
							t.setBackground(new Color(220, 220, 220));
						break;
					}
				}
			}
			// If heatmap is turned off, set the background of each cell to white
			//TODO: check to see if bad cells remain red
		} else
			for (JTextField t : finalTexts)
				if (t.isEditable())
					if (t.getBackground() != Color.RED)
						t.setBackground(Color.WHITE);
	}

	// Probably the worst method you will ever see. Sets up each text cell with all
	// of its extra shortcuts other properties
	private List<JTextField> getTextBoxes(KeyListener shortcuts, final JPanel panel) {
		List<JTextField> result = new ArrayList<JTextField>();
		// Set up the regex pattern
		String pattern;
		if (dimY * dimX < 10)
			pattern = "[1-" + (dimY * dimX) + "]";
		else if (dimY * dimX < 20)
			pattern = "[1-9]|1[0-" + ((dimY * dimX) % 10) + "]";
		else
			pattern = "[1-9]|[1-" + ((dimY * dimX) / 10 - 1) + "][0-9]|" 
					+ ((dimY * dimX) / 10) + "[0-" + ((dimY * dimX) % 10) + "]";

		int count = -1;
		// Create array of cells
		for (int i = 0; i < dimY * dimX; i++) {
			for (int j = 0; j < dimY * dimX; j++) {
				count++;
				final int finalCount = count;
				final int finalI = i;
				final JTextField text = new JTextField();
				// Place cell based on UI scale
				text.setBounds(j * sizeFact + 10, i * sizeFact + 10, sizeFact - gap, sizeFact - gap);
				text.setFont(new Font("Arial", Font.BOLD, (int) (sizeFact * .36)));
				// Override font setting if gap is tiny
				if (gap == 2)
					text.setFont(new Font("Arial", Font.PLAIN, 10));
				text.setHorizontalAlignment(JTextField.CENTER);
				// Select text when given focus
				text.addFocusListener(new FocusAdapter() {
					@Override
					public void focusGained(FocusEvent evt) {
						text.selectAll();
					}
				});
				// Initialize tooltip to say all numbers are valid
				text.setToolTipText("All");
				text.addKeyListener(shortcuts);
				// Set up text-specific keyboard shortcuts
				text.addKeyListener(new KeyListener() {
					@Override
					public void keyTyped(KeyEvent e) {}
					@Override
					public void keyReleased(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
							if (finalCount % size == size - 1)
								finalTexts.get(finalCount - size + 1).requestFocus();
							else
								finalTexts.get(finalCount + 1).requestFocus();
						}
						if (e.getKeyCode() == KeyEvent.VK_LEFT) {
							if (finalCount % size == 0)
								finalTexts.get(finalCount + size - 1).requestFocus();
							else
								finalTexts.get(finalCount - 1).requestFocus();
						}
						if (e.getKeyCode() == KeyEvent.VK_DOWN) {
							if (finalCount >= size * (size - 1))
								finalTexts.get(finalCount % size).requestFocus();
							else
								finalTexts.get(finalCount + size).requestFocus();
						}
						if (e.getKeyCode() == KeyEvent.VK_UP) {
							if (finalCount < size)
								finalTexts.get(finalCount + (size * (size - 1))).requestFocus();
							else
								finalTexts.get(finalCount - size).requestFocus();
						}
						// CTRL + SHIFT + ENTER = solve (from old code, but still useful)
						if ((e.getKeyCode() == KeyEvent.VK_ENTER && 
								(e.getModifiers() & InputEvent.SHIFT_MASK) != 0)) {
							if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
								solveAction(true);
							}
							// If shift is pressed but control isn't, AND the first
							// cell has focus, move to the last box
							else if (finalCount == 0)
								finalTexts.get(size * size - 1).requestFocus();
							// If shift is still pressed and control isn't, and
							// the focus cell is at the top of the column, jump
							// to the top of the next column
							else if (finalI == 0)
								finalTexts.get(finalCount - 1 + (size * (size - 1))).requestFocus();
							// If shift is down and control isn't, and it's not an end-case, move up
							// one cell
							else	
								finalTexts.get(finalCount - size).requestFocus();
							// If shift isn't pressed when ENTER is...
						} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							// Move down one if not at the end of the column
							if (finalI != size - 1)
								finalTexts.get(finalCount + size).requestFocus();
							// Move to the first cell if at the last cell
							else if (finalCount == size * size - 1)
								finalTexts.get(0).requestFocus();
							// Move to the top of the next column if at the end of this one
							else if (finalI == size - 1)
								finalTexts.get(finalCount - size * (size - 1) + 1).requestFocus();
						}
						// The patternFilter is not automatically invoked when pressing backspace/delete,
						// So do it manually
						if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE ||
								e.getKeyCode() == KeyEvent.VK_DELETE) {
							refreshTexts(text);
						}
						// Toggle editableness of cells. T makes a single cell editable,
						// CTRL + T makes all current non-zero cells uneditable, and 
						// CTRL + SHIFT + T makes all cells editable
						if (e.getKeyCode() == KeyEvent.VK_T) {
							if ((e.getModifiers() & InputEvent.CTRL_MASK) == 0 &&
									((e.getModifiers() & InputEvent.ALT_MASK) != 0)) {
								if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
									setAllEditable();
								} else
									selfMode();
							} else if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0)
								selfMode(text);
						}
						// Toggle the tool-tip
						if (e.getKeyCode() == KeyEvent.VK_F3) {
							if (ToolTipManager.sharedInstance().isEnabled())
								ToolTipManager.sharedInstance().setEnabled(false);
							else 
								ToolTipManager.sharedInstance().setEnabled(true);
						}
						if (e.getKeyCode() == KeyEvent.VK_F4) {
							testField(text, finalCount);
						}
						if (e.getKeyCode() == KeyEvent.VK_F5) {
							reveal(text, finalCount);
						}
					}
					@Override
					public void keyPressed(KeyEvent e) {}
				});
				AbstractDocument doc = (AbstractDocument) text.getDocument();
				doc.setDocumentFilter(new PatternFilter(pattern, this, b, text, j, i));
				finalTexts.add(text);
				result.add(text);
			}
		}
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setEnabled(toolTipInitial);
		return result;
	}
	
	public void reveal(JTextField t, int count) {
		if (menneskeImported) {
			t.setText("" + solvedBoard.get(count % size, count / size));
		}
	}
	
	// If the current board has a solution file, immediately return
	// whether the field is correct or not. Otherwise, if the size is not
	// too large, solve the puzzle to see if the number goes there
	// NOTE: Only works if there is a unique solution to the puzzle
	private void testField(JTextField text, int finalCount) {
		if (text.isEditable()) {
			if (menneskeImported) {
				if (!text.getText().equals("")) {
					Color c = text.getBackground();
					int field = Integer.parseInt(text.getText());
					if (solvedBoard.get(finalCount % size, finalCount / size) != field) 
						flash(text, Color.RED);
					else
						flash(text, Color.GREEN);
					text.setBackground(c);
				}
			}
			else {
				if (size < 13) {
					Color c = text.getBackground();
					text.setBackground(Color.BLUE);
					text.update(text.getGraphics());
					if (solveAction(false))
						flash(text, Color.GREEN);
					else
						flash(text, Color.RED);
					text.setBackground(c);
				} else {
					notEnough(text, false);
				}
			}
		}
	}
	
	private void refreshTexts(JTextField text) {
		boolean foundText = false;
		if (text.isEditable()) {
			text.setText(text.getText());
			// PatternFilter only initiated when cell is not empty,
			// So if delete just made it empty, we have to find another cell
			// that isn't empty so the board can be refreshed
			if (text.getText().equals("")) {
				text.setBackground(Color.WHITE);
				for (JTextField t : finalTexts)
					if (t.getText().length() != 0 && !t.getText().equals("0")) {
						foundText = true;
						t.setText(t.getText());
						// Set background back to grey if the cell is not editable
						if (!t.isEditable())
							t.setBackground(new Color(220, 220, 220));
						break;
					}
			} else
				foundText = true;
			text.setText(text.getText());
		}
		if (heatMap)
			if (!foundText) {
				for (JTextField t : finalTexts)
					if (t.isEditable())
						t.setBackground(new Color(255, 255, 220));
			}
	}
	
	// Allow any other method to refresh the texts using the first text
	// box as a starting point
	public void refreshTexts() {
		for (JTextField t : finalTexts) {
			if (!t.getText().equals("") && !t.getText().equals("0")) {
				refreshTexts(t);
				break;
			}
		}
//		refreshTexts(finalTexts.get(0));
	}

	// Warn the user that checking a position can take a long time
	private void notEnough(JTextField text, boolean completeSolve) {
		int answer = JOptionPane.showConfirmDialog(this, "This feature works extremely"
				+ " poorly on boards that are not presolved\n        and greater"
				+ " than 3x4. Continue anyways? (It can take several \n             minutes to"
				+ " hours depending on the size of the board)", "Proceed?",
				JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
		if (answer == 0) {
			Color c = text.getBackground();
			text.setBackground(Color.BLUE);
			text.update(text.getGraphics());
			if (solveAction(false))
				flash(text, Color.GREEN);
			else
				flash(text, Color.RED);
			text.setBackground(c);
		}
		if (answer == 0 && completeSolve)
			solveAction(true);
	}

	// Make all the text cells editable
	private void setAllEditable() {
		for (JTextField t : finalTexts) {
			t.setEditable(true);
//			t.setBackground(Color.WHITE);
			t.setForeground(Color.BLACK);
		}
		for (JTextField t : finalTexts) {
			if (t.getText().length() != 0) {
				t.setText(t.getText());
				break;
			}
		}
	}

	// selfMode (no params), sets all the non-zero texts to be non-editable
	public void selfMode() {
		for (JTextField t : finalTexts) {
			if (t.isEditable())
				//TODO: Hmm, should use .equals(), but the program still works,
				// so maybe it's fine?
				if (t.getText().length() != 0)
					selfMode(t);
		}
		// Maybe not needed now that this method only sets to non-editable,
		// not toggling?
//		if (!modify)
//			for (JTextField t : finalTexts) {
//				if (t.isEditable())
//					t.setForeground(Color.BLUE);
//			}
	}

	// Toggle editableness of a text box
	public void selfMode(JTextField text) {
		// Make sure the current cell is a valid entry in the sudokuBoard
		if (text.getBackground() == Color.RED)
			flash(text, Color.RED);
		// If it was editable, make it uneditable, and vice-versa
		else {
			if (text.isEditable()) {
				// Only make it uneditable if it contains a non-zero entry
				if (!text.getText().equals("") && !text.getText().equals("0")) {
					text.setForeground(Color.BLACK);
					text.setBackground(new Color(220, 220, 220));
					text.setEditable(false);
				}
			} else {
				text.setForeground(Color.BLUE);
				text.setBackground(Color.WHITE);
				text.setEditable(true);
				for (JTextField t : finalTexts)
					if (t.getText().length() != 0) {
						t.setText(t.getText());
						break;
					}
			}
			// Scan to see if there is at least one uneditable cell
			boolean atLeastOne = false;
			for (JTextField t : finalTexts) {
				if (!t.isEditable()) {
					atLeastOne = true;
					break;
				}
			}
			// If there is at least one uneditable cell, make editable cells
			// have a blue font, and editable cells a black font (redundant?)
			// Otherwise make all text black
			if (atLeastOne) {
				for (JTextField t : finalTexts) {
					if (t.isEditable()) {
						t.setForeground(Color.BLUE);
					} else {
						t.setForeground(Color.BLACK);
					}
				}
			} else {
				for (JTextField t : finalTexts) {
					t.setForeground(Color.BLACK);
				}
			}
		}
	}

	// Returns the solve button
	private JButton getParseButton() {
		JButton parseButton = new JButton("Solve");
		parseButton.setBounds(size * sizeFact - 70, size * sizeFact + 20, 70, 30);
		// Solve when clicked
		parseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (dimX * dimY < 14 || menneskeImported)
					solveAction(true);
				else
					notEnough(new JTextField(0), true);
			}
		});
		// Solve when it has the focus and Enter is pressed
		parseButton.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					solveAction(true);
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		return parseButton;
	}

	// Returns the reset button
	private JButton getResetButton() {
		JButton reset = new JButton("Reset");
		// Change the bounds if the buttons won't fit in a single row
		if (size <= 6)
			reset.setBounds(10, size * sizeFact + 20, 70, 30);
		else
			reset.setBounds(size * sizeFact - 150, size * sizeFact + 20, 70, 30);
		// Reset board when clicked
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				resetAction(false);
			}
		});    
		// Reset board when it has the focus and enter is pressed
		reset.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					resetAction(false);
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		return reset;
	}

	// Returns the new size button
	private JButton getNewSizeButton() {
		final JFrame finalFrame = this;
		JButton newSize = new JButton("Change Size");
		// Change bounds if the frame is too small to fit all buttons on one line
		if (size <= 6) {
			newSize.setBounds(size * sizeFact - 110, (size + 1) * sizeFact + 10, 110, 30);
		} else
			newSize.setBounds(size * sizeFact - 270, size * sizeFact + 20, 110, 30);
		// Create a new size picker and dispose of this game if clicked or when
		// Enter is pressed and this button has the foucs
		newSize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				drawPicker(heatMap);
				finalFrame.dispose();
			}
		});
		newSize.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					drawPicker(heatMap);
					finalFrame.dispose();
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		return newSize;
	}

	// Returns the save button
	private JButton getSaveButton(JPanel panel) {
		JButton save = new JButton("Save");
		// Save when clicked or when Enter is pressed and this button
		// has the focus
		save.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAction();
			}
		});
		save.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					saveAction();
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		// Change bounds if too frame to small to fit button in one row
		if (size <= 6) 
			save.setBounds(10, size * sizeFact + 60, 70, 30);
		else
			save.setBounds(size * sizeFact - 350, size * sizeFact + 20, 70, 30);
		return save;
	}

	// Return the text boxes
	public List<JTextField> getTexts() {
		return finalTexts;
	}

	// Return the current frame
	public JFrame getFrame() {
		return this;
	}

	// Return the number of columns
	public int getX() {
		return dimX;
	}

	// return the number of rows
	public int getY() {
		return dimY;
	}

	// return whether the user has invoked the solve command
	public boolean getSolving() {
		return solving;
	}

	// return whether the board has the heatmap turned on
	public boolean getHeatMap() {
		return heatMap;
	}

	// Create a new SudokuGame based on a random file
	public void importRandom() throws FileNotFoundException {
		Random r = new Random();
		File f = new File("Menneske2/");
		File[] files = f.listFiles();
		int folder = r.nextInt(files.length);
		int n = r.nextInt(100);
		folder = getFolder(n, 100);
		f = files[folder];
		while (!f.isDirectory()) {
			folder = r.nextInt(files.length);
			f = files[folder];
		}
		files = f.listFiles();
		folder = r.nextInt(files.length);
		f = files[folder];
		files = f.listFiles();
		f = files[r.nextInt(files.length)];
		importFile(f);
	}

	// Probability of a size getting chosen
	// Assumes n = 100
	// TODO: Adjust based on n
	public static int getFolder(int n, int scale) {
		// 6% 2x2
		if (n < scale * 0.06)
			return 0;
		// 6% 2x3
		else if (n < scale * 0.12)
			return 1;
		// 6% 2x4
		else if (n < scale * 0.18)
			return 2;
		// 6% 2x5
		else if (n < scale * 0.24)
			return 3;
		// 56% 3x3
		else if (n < scale * 0.80)
			return 4;
		// 6% 3x4
		else if (n < scale * 0.86)
			return 5;
		// 4% 3x5
		else if (n < scale * 0.90)
			return 6;
		// 3% 4x4
		else if (n < scale * 0.93)
			return 7;
		// 3% 5x5
		else if (n < scale * 0.96)
			return 8;
		// 2% 6x6
		else if (n < scale * 0.98)
			return 9;
		// 2% 7x7
		else
			return 10;
	}

	// Returns whether the board has been initialized yet
	public boolean isInit() {
		return setup;
	}
	
	public boolean isError() {
		return errorSolve;
	}
	
	public static String getDiff(File f) {
		String path = f.getAbsolutePath();			
		String difficulty = "";
		if (path.contains("Menneske2")) {
			File dir = f.getParentFile();
			difficulty = dir.getName();
		}
		if (difficulty.contains("_"))
			difficulty = difficulty.substring(0, difficulty.indexOf("_"));
		String diff = difficulty.charAt(0) + "";
		for (int i = 1; i < difficulty.length(); i++) {
			if (difficulty.charAt(i) < 97)
				diff += " ";
			diff += difficulty.charAt(i);
		}
		System.out.println(diff);
		return diff;
	}
}
