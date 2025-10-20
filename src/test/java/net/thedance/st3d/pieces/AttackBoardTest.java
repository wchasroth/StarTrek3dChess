package net.thedance.st3d.pieces;

import static net.thedance.st3d.Position.IS_AB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.thedance.st3d.Board;
import net.thedance.st3d.Color;
import net.thedance.st3d.Move;
import net.thedance.st3d.MoveTranslator;
import net.thedance.st3d.TestUtils;

public class AttackBoardTest {
	
	private MoveTranslator translator;
	
	private static final String[] BOARD_1PAWN_3MOVES = { 
		"Default", 
		"0", 
		"La0(2)/i", 
		"Pa1(1)",
	};
	
	private static final String[] BOARD_2PAWNS_0MOVES = { 
		"Default", 
		"0", 
		"La0(2)/i", 
		"Pa1(1)",
		"Pb1(1)",
	};
	
	private static final String[] BOARD_0PAWNS_0MOVES = { 
		"Default", 
		"0", 
		"La0(2)/i", 
	};
	
	private static final String [] BOARD_PILOT_PAWN = {
		"Default",
		"0",
		"La0(2)",
		"Pb1(3)",
	};
	
	@Before
	public void setup() {
		translator = new MoveTranslator();
	}
	
	@Test
	public void shouldGenerate3MovesForInvertedLowerLeftAttackBoard() {
		Board testBoard = new Board(BOARD_1PAWN_3MOVES);
		List<Piece> pieces = testBoard.getPieces(Color.White);
		assertEquals (2, pieces.size());
		List<Piece> abs = translator.getPiecesMatching('l', testBoard.getPieces(Color.White));
		assertEquals (1, abs.size());
		List<Move> moves = abs.get(0).generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (3, moves.size());
		String resultingMoves = moves.get(0).englishNotation(IS_AB) + " " 
			+ moves.get(1).englishNotation(IS_AB) + " " 
			+ moves.get(2).englishNotation(IS_AB);
		assertContains (resultingMoves, "a0(2)");
		assertContains (resultingMoves, "a2(4)");
		assertContains (resultingMoves, "a2(4)i");
	}
	
	@Test
	public void shouldGenerateNoMovesForInvertedLowerLeftAttackBoardWith2Pawns() {
		Board testBoard = new Board(BOARD_2PAWNS_0MOVES);
		List<Piece> pieces = testBoard.getPieces(Color.White);
		assertEquals (3, pieces.size());
		List<Piece> abs = translator.getPiecesMatching('l', testBoard.getPieces(Color.White));
		assertEquals (1, abs.size());
		List<Move> moves = abs.get(0).generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (0, moves.size());
	}
	
	@Test
	public void shouldGenerateNoMovesForAttackBoardWithNoPawnsAnywhere() {
		Board testBoard = new Board(BOARD_0PAWNS_0MOVES);
		List<Piece> pieces = testBoard.getPieces(Color.White);
		assertEquals (1, pieces.size());
		List<Piece> abs = translator.getPiecesMatching('l', testBoard.getPieces(Color.White));
		assertEquals (1, abs.size());
		List<Move> moves = abs.get(0).generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (0, moves.size());
	}
	
	private void assertContains (String text, String search) {
		assertTrue (text.indexOf(search) >= 0);
	}
	
	@Test
	public void shouldGeneratePilotedABMoves() {
		Board testBoard = new Board (BOARD_PILOT_PAWN);
		TestUtils.printBoard(testBoard);
		List<Piece> abs = translator.getPiecesMatching('l', testBoard.getPieces(Color.White));
		assertEquals (1, abs.size());
		List<Move> moves = abs.get(0).generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (3, moves.size());
		List<String> secondaryMoves = new ArrayList<String>();
		for (Move move: moves) 
			secondaryMoves.add(move.getSecondary().toString().trim());
		assertTrue (secondaryMoves.contains("WP:b1(3)-b1(1)"));
		assertTrue (secondaryMoves.contains("WP:b1(3)-b3(5)"));
		assertTrue (secondaryMoves.contains("WP:b1(3)-b3(3)"));
	}
	
}
