package com.caucus.st3d;

import static com.caucus.st3d.Board.arrayOf;

import java.util.List;

import org.junit.Ignore;

@Ignore
public class TestUtils {
	public static void printBoard (Board board) {
		for (int level: arrayOf(6, 4, 2)) {
			LevelPrinter printer = new LevelPrinter();
			printer.fillFrom(board, level);
			printer.print();
			System.out.println();
		}
	}
	
	
	public static Move extractMoveThatMatches (List<Move> moves, String english, boolean isAb) {
		for (Move move: moves) {
			if (move.englishNotation(isAb).startsWith(english))  return move;
		}
		return null;
	}
}
