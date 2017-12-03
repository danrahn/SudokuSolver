/*
 * The ParseMenneske class opens a connection to menneske.no and parses the
 * Sudoku puzzles out of it. The files are categorized into puzzles size and
 * the difficulty listed by the site. Not strictly speaking legal, but hey, I'm
 * not making any money off of it. I do feel kinda bad though =/
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ParseMenneske {
	
	private static Map<Integer, int[]> values;			// # of specific files created
	private static Map<Integer, List<Integer>> created; // puzzle numbers already solved
	private static String folder;						// Save folder
	private static String difficulty;					// Save difficulty
	private static String size;							// puzzle size
	private static String web;							// Web size url string
	private static int mapValue;						// Value associated with map keys
	private static int intSize;							// Int representation of puzzle size (x*y)

	// Initiates URL parsing
	public static void main(String[] args) throws FileNotFoundException {
		created = new HashMap<Integer, List<Integer>>();
		values = new HashMap<Integer, int[]>();
		getCreated();
		getValues();
		for (int i = 0; i < 10; i++) {
			for (int j = 1; j < 10; j++) {
				getFiles(i, j);
				File f = new File(folder);
				for (int k = 1; k <= 500; k++)
					if (f.exists())
						createSudokuBoards(j, k, i);
			}
		}
		updateIndexes();
	}
	
	// Create a map of puzzle numbers already solved
	public static void getCreated() throws FileNotFoundException {
		Scanner input = new Scanner(new File("Menneske2/alreadySolved.txt"));
		while (input.hasNext()) {
			int current = -1;
			if (!input.hasNextInt()) {
				input.next();
				current = input.nextInt();
				created.put(current, new ArrayList<Integer>());
			}
			while (input.hasNextInt()) {
				created.get(current).add(input.nextInt());
			}
		}
		input.close();
	}
	
	// Create a map of the number of puzzles per size/difficulty already created
	public static void getValues() throws FileNotFoundException {
		Scanner input = new Scanner(new File("Menneske2/index.txt"));
		while (input.hasNextInt()) {
			int value = input.nextInt();
			int[] array = new int[9];
			for (int i = 0; i < 9; i++) {
				array[i] = input.nextInt();
			}
			values.put(value, array);
		}
		input.close();
	}
	
	// Rewrite the values/puzzles already parsed to the output file
	public static void updateIndexes() throws FileNotFoundException {
		PrintStream output = new PrintStream(new File("Menneske2/index.txt"));
		for (int n : values.keySet()) {
			int[] value = values.get(n);
			output.println(n);
			for (int i = 0; i < value.length; i++) {
				output.println(value[i]);
			}
			output.println();
		}
		output = new PrintStream(new File("Menneske2/alreadySolved.txt"));
		for (int n : created.keySet()) {
			output.println("_ " + n);
			List<Integer> solved = created.get(n);
			for (int i : solved) 
				output.println(i);
		}
		output.println();
		output.close();
	}
	
	// Get a specific puzzle size and difficulty. If the puzzle has not already been
	// parsed, get the solution URL and parse out the solution table as well.
	// Save a .sudoku files
	@SuppressWarnings("resource")
	public static void createSudokuBoards(int j, int k, int n) throws FileNotFoundException {
		int puzzleNumber = -1;
		String content = null;
		URLConnection connection = null;
		// Get unsolved board
		try {
			connection = new URL("http://www.menneske.no/sudoku/" 
								+ web + "eng/random.html?diff=" + j).openConnection();
			Scanner input = new Scanner(connection.getInputStream());
			input.useDelimiter("\\Z");	// Just parse the entire thing, no breaks
			content = input.next();
			input.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String table = "";
		Scanner input = new Scanner(content);
		while (input.hasNextLine()) {
			String line = input.nextLine();
			if (line.startsWith("<td class=")) {	// Start of a Sudoku cell
				table += line + "\n";
			} else if (line.startsWith("</table>Showing puzzle number")) {
				Scanner data = new Scanner(line);
				while (data.hasNext()) {
					String num = data.next();
					// Probably could have just used a Regex, but I halfheartedly tried
					// for a bit and it didn't work, so I did it the sloppy way
					if (num.startsWith("1") || num.startsWith("2") || num.startsWith("3") ||num.startsWith("4") ||
							num.startsWith("5") ||num.startsWith("6") ||num.startsWith("7") ||
							num.startsWith("9") ||num.startsWith("8")) {
						String puzNum = "";
						for (int i = 0; i < num.length(); i++) {
							if (num.charAt(i) > 47 && num.charAt(i) < 58) {
								puzNum += num.charAt(i);
							}
						}
						// number to write to alreadySolved
						puzzleNumber = Integer.parseInt(puzNum);
						break;
					}
				}
				data.close();
			}
		}
		input.close();
		// If the puzzle number is new, get it and add it to the map. Also, 
		// increment the index of the specific puzzle type
		if (!created.get(mapValue).contains(puzzleNumber)) {
			List<String> initial = new ArrayList<String>();
			String fileName = folder + size + difficulty;
			fileName += (values.get(mapValue)[j - 1] + 1);
			fileName += ".sudoku";
			PrintStream output = new PrintStream(new File(fileName));
			created.get(mapValue).add(puzzleNumber);
			values.get(mapValue)[j - 1]++;
			String finalData = "";
			input = new Scanner(table);
			int count = 0;
			while (input.hasNextLine()) {
				String line = input.nextLine();
				// Indicates an empty cell, so if it isn't empty, get the value
				// in the cell
				if (!line.contains("&nbsp")) {	
					String value = "";
					for (int i = 0; i < line.length(); i++) {
						if (line.charAt(i) > 47 && line.charAt(i) < 58) {
							value += line.charAt(i);
							finalData += line.charAt(i);
						}
					}
					// String for writing to the .sudoku file
					finalData += " " + (count % intSize) + " " + (count / intSize);
					initial.add(value + " " + (count % intSize) + " " + (count / intSize));
					finalData += " false\n";
				}
				count++;
			}
			// Write the initial puzzle to the output file
			output.println((mapValue / 10) + " " + (mapValue % 10));
			output.println(finalData);
			System.out.println("(" + n + ", " + j + ", " + k + ") " + fileName + " saved");
			output.close();
			
			// Essentially the same process, but get the solution instead
			try {
				connection = new URL("http://www.menneske.no/sudoku/" 
									+ web + "eng/solution.html?number=" + puzzleNumber).openConnection();
				input = new Scanner(connection.getInputStream());
				input.useDelimiter("\\Z");
				content = input.next();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			String solveTable = "";
			Scanner solveInput = new Scanner(content);
			while (solveInput.hasNextLine()) {
				String line = solveInput.nextLine();
				if (line.startsWith("<td class=")) {
					solveTable += line + "\n";
				}
			}
			solveInput.close();
			String solvedFileName = folder + "Solutions/" +  size + difficulty;
			solvedFileName += (values.get(mapValue)[j - 1]);
			solvedFileName += "_solution.sudoku";
			PrintStream output2 = new PrintStream(new File(solvedFileName));
			finalData = "";
			input = new Scanner(solveTable);
			count = 0;
			while (input.hasNextLine()) {
				String line = input.nextLine();
				if (!line.contains("&nbsp")) {
					String value = "";
					for (int i = 0; i < line.length();i++) {
						if (line.charAt(i) > 47 && line.charAt(i) < 58) {
							value += line.charAt(i);
							finalData += line.charAt(i);
						}
					}
					finalData += " " + (count % intSize) + " " + (count / intSize);
					if (initial.contains(value + " " + (count % intSize) + " " + (count / intSize)))
						finalData += " false\n";
					else
						finalData += " true\n";
				}
				count++;
			}
			output2.println((mapValue / 10) + " " + (mapValue % 10));
			output2.println(finalData);
			output2.close();
		}
	}
	
	// No easy way to associate a single int with all the different values it
	// goes to, so this ugly method is born.
	public static void getFiles(int i, int j) {
		folder = "Menneske2/";
		difficulty = "";
		size = "";
		web = "";
		mapValue = -1;
		intSize = -1;
		if (i == 0) {
			folder += "22/";
			web = "2/";
			size = "2x2_";
			mapValue = 22;
			intSize = 4;
		}
		else if (i == 1) {
			folder += "23/";
			web = "2x3/";
			size = "2x3_";
			mapValue = 23;
			intSize = 6;
		}
		else if (i == 2) {
			folder += "24/";
			web = "2x4/";
			size = "2x4_";
			mapValue = 24;
			intSize = 8;
		}
		else if (i == 3) {
			folder += "33/";
			web = "";
			size = "3x3_";
			mapValue = 33;
			intSize = 9;
		}
		else if (i == 4) {
			folder += "25/";
			web = "2x5/";
			size = "2x5_";
			mapValue = 25;
			intSize = 10;
		}
		else if (i == 5) {
			folder += "34/";
			web = "3x4/";
			size = "3x4";
			mapValue = 34;
			intSize = 12;
		}
		else if (i == 6) {
			folder += "35/";
			web = "3x5/";
			size = "3x5_";
			mapValue = 35;
			intSize = 15;
		}
		else if (i == 7) {
			folder += "44/";
			web = "4/";
			size = "4x4_";
			mapValue = 44;
			intSize = 16;
		}
		else if (i == 8) {
			folder += "55/";
			web = "5/";
			size = "5x5_";
			mapValue = 55;
			intSize = 25;
		}
		else if (i == 9) {
			folder += "66/";
			web = "6/";
			size = "6x6_";
			mapValue = 66;
			intSize = 36;
		}
		else if (i == 10) {
			folder += "77/";
			web = "7/";
			size = "7x7_";
			mapValue = 77;
			intSize = 49;
		}
		// Difficulties
		if (j == 1) {
			folder += "SuperEasy/";
			difficulty = "SuperEasy_";
		} else if (j == 2) {
			folder += "VeryEasy/";
			difficulty = "VeryEasy_";
		} else if (j == 3) {
			folder += "Easy/";
			difficulty = "Easy_";
		} else if (j == 4) {
			folder += "Medium/";
			difficulty = "Medium_";
		} else if (j == 5) {
			folder += "Hard/";
			difficulty = "Hard_";
		} else if (j == 6) {
			folder += "Harder/";
			difficulty = "Harder_";
		} else if (j == 7) {
			folder += "VeryHard/";
			difficulty = "VeryHard_";
		} else if (j == 8) {
			folder += "SuperHard/";
			difficulty = "SuperHard_";
		} else if (j == 9) {
			folder += "Impossible/";
			difficulty = "Impossible_";
		}
	}
}
