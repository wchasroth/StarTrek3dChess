package net.thedance.st3d;

import static net.thedance.st3d.Board.*;

import net.thedance.st3d.pieces.AttackBoard;
import net.thedance.st3d.pieces.Piece;

public class LevelPrinter {
	private static final int ROW_SIZE = 25;
	private static final int COL_SIZE = 42;
	private static final int Y_ROWS   =  2;  // rows per y square increment
	private static final int X_COLS   =  4;  // cols per x square increment
	private static final int MAIN_BOARD_COL0 = 12;
	private static final int MAIN_BOARD_ROW0 =  8;
	char [][] grid;
	
	public LevelPrinter () {
		grid = new char [ROW_SIZE][COL_SIZE];
		for (int row=0;   row<ROW_SIZE;   ++row) {
			for (int col=0;  col<COL_SIZE;   ++col)
				grid[row][col] = ' ';
		}
	}
	
	private static final boolean ABOVE = true;
	private static final boolean BELOW = false;
	
	public void fillFrom (Board board, int levelNum) {
		int rankStart = -1 + levelNum;
		
		int z = level(levelNum);
		for (int gridRank=0;   gridRank<4;   ++gridRank) {
			int y = rank(rankStart) + gridRank;
			for (int gridFile=0;   gridFile<4;   ++gridFile) {
				int x = file(B) + gridFile;
				drawMainBoardSquare (gridFile, gridRank, board.squares[x][y][z]);
			}
		}
		
		fillFromAttackBoards (board, 18, ABOVE, rank(levelNum+2),     z);
		
		fillFromAttackBoards (board,  2, BELOW, rank(levelNum+2) - 4, z);
		
		grid[15][33] = rankNumber (levelNum, 4);
		grid[13][33] = rankNumber (levelNum, 3);
		grid[11][33] = rankNumber (levelNum, 2);
		grid[ 9][33] = rankNumber (levelNum, 1);
	}
	
	private char rankNumber (int levelNum, int rank) {
		int baseRank = (levelNum - 2) + rank;
		String num = "" + baseRank;
		return num.charAt(0);
	}
	
	private void fillFromAttackBoards (Board board, int rowBase, boolean above, int y, int z) {
		AttackBoard ab1, ab2, ab3, ab4;
		int invertedBase = rowBase + (above ? 2 : -2);
		int direction    = (above ? 1 : -1);
		int postBase     = (above ? rowBase - direction : rowBase + 5);
		
		ab1 = board.squares[file(A)][y][z].upAb;
		if (ab1 != null)  {
			drawAb (ab1, board, rowBase, 3, 0);
			drawSupport (slash(above), postBase,13);
			drawFileLabel (above ? rowBase+5 : rowBase-1, 13, 'a');
			drawFileLabel (above ? rowBase+5 : rowBase-1, 17, 'b');
		}
		
		ab2 = board.squares[file(A)][y][z].downAb;
		if (ab2 != null)  {
			drawAb (ab2, board, invertedBase, -6, 0);
			drawSupport (slash(!above), postBase,11, postBase+direction,10, postBase+2*direction,9, 
					postBase+3*direction,8, postBase+4*direction,7);
			drawFileLabel (above ? rowBase+1 : rowBase+3, 4, 'a');
			drawFileLabel (above ? rowBase+1 : rowBase+3, 8, 'b');
		}
		
		ab3 = board.squares[file(E)][y][z].upAb;
		if (ab3 != null)  {
			drawAb (ab3, board, rowBase, 13, 0);
			drawSupport (slash(!above), postBase,27);
			drawFileLabel (above ? rowBase+5 : rowBase-1, 23, 'e');
			drawFileLabel (above ? rowBase+5 : rowBase-1, 27, 'f');
		}
		
		ab4 = board.squares[file(E)][y][z].downAb;
		if (ab4 != null)  {
			drawAb (ab4, board, invertedBase, 22, 0);
			drawSupport (slash(above), postBase,29, postBase+direction,30, postBase+2*direction,31, postBase+3*direction,32, postBase+4*direction,33);
			drawFileLabel (above ? rowBase+1 : rowBase+3, 32, 'e');
			drawFileLabel (above ? rowBase+1 : rowBase+3, 36, 'f');
		}
		
		if (above  &&  ab1 == null  &&  ab2 == null  &&  ab3 == null  &&  ab4 == null) {
			grid[postBase][14] = 'b';
			grid[postBase][18] = 'c';
			grid[postBase][22] = 'd';
			grid[postBase][26] = 'e';
		}
	}
	
	private void drawAb(AttackBoard ab, Board board, int rowStart, int colStart, int rank) {
		int x = ab.getPosition().x;
		int y = ab.getPosition().y;
		int z = ab.getPosition().z + ab.getDeltaZ();
		drawSquare (rowStart, colStart, file(A), rank,   board.getSquare(x,   y,   z), ab.getColor());
		drawSquare (rowStart, colStart, file(A), rank+1, board.getSquare(x,   y+1, z), ab.getColor());
		drawSquare (rowStart, colStart, file(B), rank+1, board.getSquare(x+1, y+1, z), ab.getColor());
		drawSquare (rowStart, colStart, file(B), rank,   board.getSquare(x+1, y,   z), ab.getColor());
	}
	
	private void drawFileLabel (int row, int col, char letter) {
		grid[row][col] = letter;
	}
	
	private void drawSupport (char marker, int ... rowCol) {
		for (int i=0;   i<rowCol.length;   i+=2) {
			grid[rowCol[i]][rowCol[i+1]] = marker;
		}
	}
	
	private char slash(boolean which) {
		return (which ? '/' : '\\');
	}
	
/*
           1         2         3
 123456789 123456789 123456789 123456789     
 4 +---+---+                   +---+---+
 3 |   |...|                   |   |...|
 2 +---+---++===+===+ +---+---++---+---+
21 |...|\  ||   |...| |   |...||../|   |
   +---+-\-++===+===+ +---+---++-/-+---+
 9        \ |...|   | |...|   | /
 8         \+===+===+ +---+---+/
 7          \ /             \ /
 6           +---+---+---+---+
 5           |   |.B.|   |...|
 4           +---+---+---+---+
 3           |...|   |.p.|   |
 2           +---+---+---+---+
11           | K |...| N |...|
             +---+---+---+---+
 9           |...|   |...| k |
 8           +---+---+---+---+
 7          / \             / \
 6         /+---+---+ +---+---+\
 5        / |...|   | |...|   | \
 4 +---+-/-++---+---+ +---+---++-\-+---+
 3 |...|/  ||   |...| |   |...||..\|   |
 2 +---+---++---+---+ +---+---++---+---+
 1 |   |...|                   |   |...|
 0 +---+---+                   +---+---+
*/
	
	
	public void print() {
		for (int row=ROW_SIZE-1;   row >=0;   --row) {
			String line = new String(grid[row]);
			if (line.trim().length() > 0)
				System.out.println (line);
		}
	}
	
	protected void drawSquare (int rowStart, int colStart, int file, int rank, Square square, Color color) {
		int col = colStart + (file * X_COLS);
		int row = rowStart + (rank * Y_ROWS);
		char border = color.getBorder();
		grid[row]  [col]   = '+';
		grid[row]  [col+4] = '+';
		grid[row+2][col]   = '+';
		grid[row+2][col+4] = '+';
		grid[row+1][col]   = '|';
		grid[row+1][col+4] = '|';
		grid[row]  [col+1] = border;
		grid[row]  [col+2] = border;
		grid[row]  [col+3] = border;
		
		grid[row+2][col+1] = border;
		grid[row+2][col+2] = border;
		grid[row+2][col+3] = border;
		if ( (rank + file) % 2 == 0) {
			grid[row+1][col+1] = '.';
			grid[row+1][col+2] = '.';
			grid[row+1][col+3] = '.';
		}
		
		Piece piece = square.getPiece();
		if (piece != null)   grid[row+1][col+2] = piece.toChar();
		char marker = square.getMarker();
		if (marker != 0)     grid[row+1][col+2] = marker;
	}
	
	protected void drawMainBoardSquare (int file, int rank, Square square) {
		drawSquare (MAIN_BOARD_ROW0, MAIN_BOARD_COL0, file, rank, square, Color.White);
	}
}
