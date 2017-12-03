_,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.,
|`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*|
||                                              ||
||               INSTALLATION NOTES             ||
||                                              ||
|,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.|
|`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*|
||                                              ||
|| Unzip Menneske2.zip into the same directory  ||
|| as SudokuSolver.jar. Also, make sure the res ||
|| folder stays in the same directory as well.  ||
|| Do not edit the Menneske2 folder in any way  ||
|| unless you know what you are doing.          ||
||                                              ||
|,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.|
|`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*|
||                                              ||
||                  SudokuSolver                ||
||             Written by Daniel Rahn           ||
||                                              ||
|,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.|
|`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*|
||                                              ||
|| SudokuSolver is a little program I wrote in  ||
|| Java that, as the name implies, solves       ||
|| Sudoku puzzles. When the program launches,   ||
|| there are four options. The first is to load ||
|| a random presolved puzzle of a specific size ||
|| that can be chosen from the drop-down list.  ||
|| The second option is to load a completely	||
|| random puzzle of any size. The third option	||
|| is to load a specific file: the file can     ||
|| either be a .sudoku file that is user        ||
|| created, or the user can browse the          ||
|| Menneske2 folder and load one of those       ||
|| puzzles. final option is to create a         ||
|| completely new Sudoku puzzle of the user     ||
|| specified size. This puzzle can have a       ||
|| maximum dimension columns time rows) of 50,  ||
|| due to size and performance issues.          ||
||                                              ||
|,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.|
|`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*|
||                                              ||
|| Once the main board has been loaded there 	||
|| multiple options:                            ||
||                                              ||
|| Help (F1)                                    ||
||  The help window will display a (very)       ||
||  condensed version of this help file.        ||
||                                              ||
|| Heatmap (F2)                                 ||
||  The heatmap, activated by pressing F2,      ||
||  changes the color of each cell depending on	||
||  how many number choices there are for that 	||
||  cell.                                       ||
||                                              ||
|| Tooltip (F3)                                 ||
||  When tooltip is turned on, a list of        ||
||  possible cell choices appears for whatever	|| 
||  cell the mouse is hovering over.            ||
||                                              ||
|| Test Cell (F4)                               ||
||  If the Sudoku puzzle has a dimension of 14	||
||  or less, or is presolved, pressing F4 will	||
||  inform the user if the value in the         ||
||  selected cell is correct. The cell in will	||
||  flash green if correct, and red if not. The ||
||  size is limited due to performance issues, 	||
||  but a dialog box appears if attempted on	||
||  unsupported boards that will override the	||
||  restriction. Just know that it could take 	||
||  days to solve a single 7x7 (49 by 49 cells,	||
||  with each cell having a maximum of 49       ||
||  possibilities means pretty much infinite	||
||  tests needing to be done in the worst case	||
||  scenario).                                  ||
||                                              ||
|| Reveal (F5)                                  ||
||  Stumped, but don't want to have the entire	||
||  puzzle solved for you? Press F5 when the 	||
||  cell you want to have solved is selected, 	||
||  and the correct number will appear.         ||
||  Currently only availble on puzzles with 	||
||  predefined solutions, as there is the       ||
||  possibility of multiple solutions on other	||
||  puzzles, which would cause issues           ||
||                                              ||
|| Solve (Button, Ctrl + Shift + S)             ||
||  Shows the solution to the puzzle. Again, 	||
||  only works with presolved puzzles, or those	||
||  with a dimension of less than 14. Can be 	||
||  overridden.                                 ||
||                                              ||
|| Reset (Button, Ctrl + R)                     ||
||  Resets the current puzzle, but not cells 	||
||  that are locked in.                         ||
||                                              ||
|| Reset All (Ctrl + Shift + R)                 ||
||  Resets the entire puzzle, including locked  ||
||  cells.                                      ||
||                                              ||
|| Save (Button, Ctrl + S)                      ||
||  Saves the current board as a .png image to  ||
||  a user-specified location. This options is  ||
||  really only available because it was part 	||
||  the program in its infancy, and I didn't	||
||  want to get rid of it, even though it isn't ||
||  very useful.                                ||
||                                              ||
|| Change Size (Button, Ctrl + N)               ||
||  Dispose of the current puzzle and open the	||
||  selection dialog again.                     ||
||                                              ||
|| Export (Ctrl + E)                            ||
||  Save the current board to a .sudoku file in ||
||  a user-specified location. These files can  ||
||  be opened at a later time.                  ||
||                                              ||
|| Open (Ctrl + O)                              ||
||  Open a specific .sudoku file to work on.	||
||  Also works in the selection screen          ||
||                                              ||
|| Open Random (Ctrl + Shift + O)               ||
||  Opens a random puzzle from the Menneske2 	||
||  folder. Also works in the selection screen  ||
||                                              ||
|| Lock Cell (Toggle) (Ctrl + T)                ||
||  Prevents a cell from being edited, and      ||
||  greys it out on the board. Useful for       ||
||  making puzzles, or locking in a option if 	||
||  you're sure that's where it goes.           ||
||                                              ||
|| Lock All (Alt + T)                           ||
||  Locks all cells taht have numbers in them.	||
||  Useful when making puzzles.                 ||
||                                              ||
|| Unlock All (Alt + Shift + T)                 ||
||  Unlocks all cells that are currently locked ||
||                                              ||
|,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.|
|`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*|
||                                              ||
|| Navigating Cells:                            ||
||                                              ||
|| Tab/Shift + Tab  : Right/Left                ||
|| Enter/Shift + Enter  : Down/Up               ||
|| Direction keys                               ||
||                                              ||
|,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.|
|`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*|
||                                              ||
||                     NOTES                    ||
||                                              ||
|,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.,_,.~*`*~.|
|`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*`*`*~.,.~*|
||                                              ||
|| Many thanks to the folks at                  ||
|| http://www.menneske.no/ for "allowing" me to ||
|| crawl their site and parse good Sudoku       ||
|| puzzles.                                     ||
||                                              ||
|| Creating your own puzzles with solution      ||
|| files:                                       ||
||  If you want to be able to load your own 	||
||  puzzles with a predefined solution puzzle,	||
||  just make sure to place the solution in a	||
||  folder named "Solutions", which should be	||
||  in the same directory as the non-solved 	||
||  version of the puzzle. If there is more 	||
||  one solution to the puzzle, it can cause	||
||  issues, as it will say a particular cell is	||
||  wrong when it could just be another solution||
||  When creating your solution, it is best to  ||
||  lock the cells that will contain the pre-	||
||  determined numbers before you export.       ||
||                                              ||
||                                              ||
|| About the Menneske2 folder:                  ||
||  The Menneske2 folder contains over 20,000	||
||  presolved puzzles. In that folder you will	||
||  find two additional text documents, index	||
||  and alreadySolved. Index is merely a        ||
||  listing of the number of puzzles per        ||
||  category (used when parsing new puzzles so 	||
||  the system know what to name the puzzle).	||
||  AlredySolved is a listing of the number 	||
||  associated with every puzzle that has been	||
||  parsed. Menneske.no did not have an easy 	||
||  way to sequentially go through each puzzle, ||
||  so I had to create a file with the index of ||
||  every puzzle that has been parsed to ensure ||
||  there are no repeats. If you want to add 	||
||  additional puzzles to the folder (see       ||
||  below), do not edit these files in any way. ||
||  If you want a specific difficulty of puzzle,||
||  you will have to manually import a specific ||
||  file, as I have not yet implemented that 	||
||  feature. (But as I write this I really want ||
||  to do that now). The folder structure is 	||
||  very straghtforward: directly in Menneske2	||
||  are numbered folders related to the puzzle	||
||  size (22 = 2x2, 35 = 3x5, etc). In each of 	||
||  those folders are the difficulty folders.	||
||  The difficulty is determined by menneske.no.||
||  If a folder is folowed by "Complete", that  ||
||  just means that all the possible puzzles 	||
||  have been parsed from that particular       ||
||  category, and I renamed the folder so my	||
||  parsing program would skip over that        ||
||  particualr puzzle type.                     ||
||                                              ||
|| Adding additional Menneske puzzles:          ||
||  If over 20000 predefinded puzzles just isn't||
||  enough for you, you can add your own if 	||
||  you're willing to take the trouble. The 	||
||  feature is not supported, so you're on your	||
||  own if things get messed up. I have not 	||
||  personally tried this, but I'm assuming you	||
||  could extract ParseMenneske.class from the 	||
||  .jar file and decompile it using a program  ||
||  like JD_GUI. Once you have the .java file,	||
||  make sure it is in the same folder as Men2  ||
||  and load it in a Java editor. There is a 	||
||  for loop that dictates the number of        ||
||  to fetch per category. It is currently at	||
||  500, which is what got me the initial 20000 ||
||  puzzles, so you may want to turn it down a  ||
||  notch. Also, menneske.no has a copyright 	||
||  claim on all of the puzzles, so maybe it's 	||
||  best to not try to add more puzzles unless 	||
||  you use a VPN so they can't catch you =D    ||
||                                              ||
|| Along the same lines as adding additional    ||
|| puzzles, if you do decide to extract class   ||
|| files from the .jar, don't judge my code. I  ||
|| know it's horrible. This project started out ||
|| as a console based solver, in which the user ||
|| manually entered the coordinates of the pre- ||
|| defined numbers and it immediately solved the||
|| puzzle. Adding the GUI, the ability to       ||
|| attempt to solve the puzzle, and all the     ||
|| other minor elements lead to a monstrosity of||
|| a program that in no way follows good coding ||
|| style guidelines, but hey, it works.         ||
||______________________________________________||