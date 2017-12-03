/* Daniel Rahn
 * 16 March 2014
 * 
 * The SizePicker class prompts the user for the type of Sudoku puzzle they wish
 * to open, either a random puzzle of a specific size, a completely random puzzle,
 * a puzzle not a part of the predefined set, or to create their own
 * 
 */

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SizePicker extends JFrame {
	
	private boolean heatMap;
	private boolean toolTip;
	
	// Autogenerated UID
	private static final long serialVersionUID = 4298252872299612056L;

	// The constructer just initialzes the display
	public SizePicker() {
		this(false, false);
	}
	
	public SizePicker(boolean heatMap, boolean toolTip) {
		this.heatMap = heatMap;
		this.toolTip = toolTip;
		drawUI();
	}
	
	// Pick a random file of a specific size
	private void specificRandom(JComboBox<String> options) {
		String path = "Menneske2/";
		// Possible folders
		final String[] paths = {path + "22", path + "23", path + "24", path + "33",
								path + "25", path + "34", path + "35", path + "44",
								path + "55", path + "66", path + "77"};
		Random r = new Random();
		List<File> choices = new ArrayList<File>();
		File folder = new File(paths[options.getSelectedIndex()]);
		File[] diff = folder.listFiles();	// Difficulty folders
		for (File f : diff) {
			if (f.isDirectory()) {
				File[] puzzles = f.listFiles();	// The puzzles themselves
				for (File p : puzzles) {
					if (!p.isDirectory())
						choices.add(p);
				}
			}
		}
		File f = choices.get(r.nextInt(choices.size()));
		String difficulty = SudokuGame.getDiff(f);
		drawGame(f, heatMap, toolTip, difficulty);
		dispose();
	}
	
	// Action when "specific random" button is pressed
	private ActionListener getSpecificRandomAction(final JComboBox<String> options) {
		ActionListener lsn = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				specificRandom(options);
			}
		};
		return lsn;
	}
	
	// Action when "completely random" button is clicked
	private ActionListener getRandomAction() {
		ActionListener lsn = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					importRandom();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		};
		return lsn;
	}
	
	// Action when open specific file is clicked
	private ActionListener getSpecificAction() {
		ActionListener lsn = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					importFile();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		};
		return lsn;
	}
	
	// Draw main UI
	// TODO: Shrink, way too long and ugly
	public void drawUI() {
		JPanel panel = new JPanel() {
			// Autogenerated UID
			private static final long serialVersionUID = -1607614925598698367L;
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, 320, 50);
				g.fillRect(0, 100, 320, 50);
				g.fillRect(0, 222, 320, 20);
				g.setColor(Color.BLACK);
			}
		};
		getContentPane().add(panel);
		panel.setLayout(null);
		
		// Specific Random:
		//
		// Chooser
		final JComboBox<String> types = new JComboBox<String>();
		types.setFont(new Font("Arial", Font.BOLD, 14));
		((JLabel) types.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		// Possible pre-defined sizes, unfortunately can't easily loop it
		types.addItem("2 x 2");
		types.addItem("2 x 3");
		types.addItem("2 x 4");
		types.addItem("3 x 3");
		types.addItem("2 x 5");
		types.addItem("3 x 4");
		types.addItem("3 x 5");
		types.addItem("4 x 4");
		types.addItem("5 x 5");
		types.addItem("6 x 6");
		types.addItem("7 x 7");
		types.setSelectedIndex(3);	//Default is 3x3
		types.setBounds(80, 10, 80, 30);
		types.addKeyListener(getKeyListener());
		final JLabel random = new JLabel("Random                                puzzle");
		random.setBounds(25, 15, 300, 20);
		random.addKeyListener(getKeyListener());
		final JButton randomSubmit = new JButton("Submit");
		randomSubmit.setBounds(215, 10, 80, 30);
		randomSubmit.addActionListener(getSpecificRandomAction(types));
		randomSubmit.addKeyListener(getKeyListener());
		KeyListener randomActn = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					specificRandom(types);
				}
			}
			public void keyPressed(KeyEvent e) {}
		};
		types.addKeyListener(randomActn);
		randomSubmit.addKeyListener(randomActn);
		panel.add(types);
		panel.add(random);
		panel.add(randomSubmit);
		
		// Completely Random:
		final JLabel random2 = new JLabel("Completely random puzzle");
		random2.setBounds(55, 65, 300, 20);
		random2.addKeyListener(getKeyListener());
		final JButton randomSubmit2 = new JButton("Submit");
		randomSubmit2.setBounds(215, 60, 80, 30);
		randomSubmit2.addActionListener(getRandomAction());
		randomSubmit2.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						importRandom();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
			public void keyPressed(KeyEvent e) {}
		});
		panel.add(random2);
		panel.add(randomSubmit2);
		
		// Open Specific
		final JLabel specific = new JLabel("Open a specific file");
		specific.setBounds(95, 115, 300, 20);
		final JButton specSub = new JButton("Submit");
		specSub.setBounds(215, 110, 80, 30);
		specSub.addActionListener(getSpecificAction());
		specSub.addKeyListener(getKeyListener());
		specSub.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						importFile();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
			}
			public void keyPressed(KeyEvent e) {}
		});
		panel.add(specific);
		panel.add(specSub);
		
		// Create your own:
		final JLabel lastOption = new JLabel("Or create your own:");
		lastOption.setBounds(92, 150, 300, 20);
		final JLabel help = new JLabel("Press F1 at any time for help");
		help.setBounds(26, 220, 200, 20);
		final JLabel cols = new JLabel("Columns");
		cols.setBounds(25, 165, 100, 20);
		final JLabel rows = new JLabel("Rows");
		rows.setBounds(135, 165, 100, 20);
		final JComboBox<Integer> chooserY = new JComboBox<Integer>();
		for (int i = 2; i < 11; i++)
			chooserY.addItem(i);
		chooserY.setBounds(25, 185, 80, 30);
		chooserY.setSelectedItem(3);
		chooserY.setFont(new Font("Arial", Font.PLAIN, 18));
		final JComboBox<Integer> chooserX = new JComboBox<Integer>();
		for (int i = 2; i < 11; i++)
			chooserX.addItem(i);
		chooserX.setBounds(125, 185, 80, 30);
		chooserX.setSelectedItem(3);
		chooserX.setFont(new Font("Arial", Font.PLAIN, 18));
		final JLabel byLabel = new JLabel("by");
		byLabel.setFont(new Font("Arial", Font.PLAIN, 12));
		byLabel.setBounds(107, 185, 20, 30);
		ActionListener enter = getActionListener(chooserY, chooserX);
		KeyListener enterKey = getKeyListener();
		chooserY.addKeyListener(enterKey);
		chooserY.addKeyListener(customListener(chooserY, chooserX));
		chooserX.addKeyListener(enterKey);
		chooserX.addKeyListener(customListener(chooserY, chooserX));
		final JButton submit = new JButton("Submit");
		submit.setBounds(215, 185, 80, 30);
		submit.addActionListener(enter);
		submit.addKeyListener(enterKey);
		submit.addKeyListener(customListener(chooserY, chooserX));
		
		panel.add(submit);
		panel.add(lastOption);
		panel.add(help);
		panel.add(cols);
		panel.add(rows);
		panel.add(submit);
		panel.add(byLabel);
		panel.add(chooserY);
		panel.add(chooserX);
		setTitle("Puzzle Options");
		setSize(320, 270);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
	}
	
	// Key listener for "create your own"
	private KeyListener customListener(final JComboBox<Integer> chooserY,
									   final JComboBox<Integer> chooserX) {
		KeyListener lsn = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public  void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					int y = (int) chooserY.getSelectedItem();
					int x = (int) chooserX.getSelectedItem();
					if (x * y > 50)
						errorFrame();
					else {
						drawGame(y, x);
						dispose();
					}
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {}
		};
		return lsn;
	}
	
	// General keyboard shortcuts
	private KeyListener getKeyListener() {
		KeyListener lsn = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
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
				if (e.getKeyCode() == KeyEvent.VK_F1) {
					printHelpScreen(SizePicker.this);
				}
				if (e.getKeyCode() == KeyEvent.VK_W &&
						(e.getModifiers() & InputEvent.CTRL_MASK) != 0)
					dispose();
			}
			public void keyPressed(KeyEvent e) {}
		};
		return lsn;
	}
	
	// Import a pseudo-random file
	public void importRandom() throws FileNotFoundException {
		Random r = new Random();
		File f = new File("Menneske2/");
		File[] files = f.listFiles();
		int n = r.nextInt(100);	// choose size pseudo-randomly
		int folder = SudokuGame.getFolder(n, 100);
		f = files[folder];
		// Reassign "files" to difficulty options
		files = f.listFiles();
		// Choose random difficulty
		f = files[r.nextInt(files.length)];
		files = f.listFiles();
		// Choose random puzzle
		f = files[r.nextInt(files.length)];
		String difficulty = SudokuGame.getDiff(f);
		drawGame(f, heatMap, toolTip, difficulty);
		dispose();
	}
	
	// Create new SudokuGame based on user-given x and y dimension
	private ActionListener getActionListener(final JComboBox<Integer> chooserY, 
											 final JComboBox<Integer> chooserX) {
		ActionListener lsn = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int y = (int) chooserY.getSelectedItem();
				int x = (int) chooserX.getSelectedItem();
				if (x * y > 50)
					errorFrame();
				else {
					drawGame(y, x);
					dispose();
				}
			}
		};
		return lsn;
	}
	
	// Display the help screen (Just an image file)
	public static void printHelpScreen(JFrame loc) {
		final JFrame help = new JFrame();
		JPanel panel = new JPanel(new BorderLayout());
		JScrollPane scrPane = new JScrollPane(panel);
		scrPane.getVerticalScrollBar().setUnitIncrement(16);
		panel.setPreferredSize(new Dimension(600, 860));
		help.add(scrPane);
		// Different ways to close help screen
		KeyListener shortcuts = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					help.dispose();
				if (e.getKeyCode() == KeyEvent.VK_W &&
						(e.getModifiers() & InputEvent.CTRL_MASK) != 0)
					help.dispose();
				if (e.getKeyCode() == KeyEvent.VK_M &&
						(e.getModifiers() & InputEvent.CTRL_MASK) != 0)
					help.setState(JFrame.ICONIFIED);
				if (e.getKeyCode() == KeyEvent.VK_F1)
					help.dispose();
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
					help.dispose();
			}
			public void keyPressed(KeyEvent e) {}
		};
		ImageIcon imgIcon = new ImageIcon("res/Sudoku.png");
		help.setIconImage(imgIcon.getImage());
		panel.addKeyListener(shortcuts);
		help.addKeyListener(shortcuts);
		Icon image = new ImageIcon("res/help2.png");
		JLabel icon = new JLabel(image);
		icon.addKeyListener(shortcuts);
		panel.add(icon);
		help.pack();
		help.setSize(637, 500);
		help.setTitle("Help");
		help.setLocationRelativeTo(loc);
		help.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		help.setVisible(true);
	}
	
	// Select a file to import, only accepting .sudoku files
	private void importFile() throws FileNotFoundException {
		JFileChooser chooser = new JFileChooser();
		FileFilter filter = new ExtensionFileFilter("Sudoku File", "sudoku");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = chooser.getSelectedFile();
			String path = f.getAbsolutePath();
			String difficulty = SudokuGame.getDiff(f);
			if (!path.endsWith(".sudoku")) {
				int response = JOptionPane.showConfirmDialog(chooser, "The file "
						+ f.getName() + " does not appear to be a .sudoku file. Try again?", "File Type Error",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (response == JOptionPane.YES_OPTION)
					importFile();
			} else {
				drawGame(f, heatMap, toolTip, difficulty);
				dispose();
			}
		}
	}
	
	// Display an error message if the user tries to create a puzzle larger
	// than 50x50 (for performance and size reasons).
	public void errorFrame() {
		final JFrame error = new JFrame();
		JPanel panel = new JPanel();
		error.getContentPane().add(panel);
		panel.setLayout(null);
		JLabel label = new JLabel("Error: (Col * Row) must be 50 or less");
		label.setBounds(10, 10, 220, 20);
		JButton okay = new JButton("OKAY");
		okay.setBounds(85, 40, 60, 30);
		okay.addActionListener(new ActionListener() {
	        	@Override
	        	public void actionPerformed(ActionEvent e) {
	        		error.dispose();
	        	}
	        });
		okay.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					error.dispose();
			}
			public void keyPressed(KeyEvent e) {}
		});
		panel.add(label);
		panel.add(okay);
		error.setSize(240, 130);
		error.setVisible(true);
		error.setLocationRelativeTo(this);
	}
	
	// Create a new SudokuGame based on a given number of rows and columns
	public static void drawGame(final int y, final int x) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SudokuGame g;
				g = new SudokuGame(x, y);
				g.getFrame().setVisible(true);
				ImageIcon imgIcon = new ImageIcon("res/Sudoku2.png");
				g.getFrame().setIconImage(imgIcon.getImage());
				g.getFrame().toFront();
			}
		});
	}
	
	// create a new SudokuGame based on the given .sudoku file
	public static void drawGame(final File f, final boolean heat, final boolean tool, final String difficulty) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SudokuGame g;
				try {
					System.out.println(f);
					g = new SudokuGame(f, heat, tool, difficulty);
					g.getFrame().setVisible(true);
					ImageIcon imgIcon = new ImageIcon("res/Sudoku2.png");
					g.getFrame().setIconImage(imgIcon.getImage());
					g.getFrame().toFront();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
