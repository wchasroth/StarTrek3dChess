package com.caucus.st3d.pieces;

import static com.caucus.st3d.Board.B;
import static com.caucus.st3d.Board.file;
import static com.caucus.st3d.Board.level;
import static com.caucus.st3d.Board.rank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.caucus.st3d.Board;
import com.caucus.st3d.Color;
import com.caucus.st3d.Move;
import com.caucus.st3d.MoveTranslator;
import com.caucus.st3d.Position;
import com.caucus.st3d.TestUtils;

public class RookTest {
	
	private static final String [] BOARD_PILOT_PAWN = {
		"Default",
		"0",
		"La2(4)/i",
		"Rb1(2)",
		"Pb2(2)",
		"Pb3(4)",
		"pb6(4)",
	};
	
	@Test
	public void shouldMoveAttackBoard_thenAllowRookMoveOnAlternatePathOnAttackBoardAboveTargetSquare_onMainBoard() {
		MoveTranslator translator = new MoveTranslator();
		Board testBoard = new Board(BOARD_PILOT_PAWN);
		
		List<Piece> attackBoards = translator.getPiecesMatching('l', testBoard.getPieces(Color.White));
		assertEquals (1, attackBoards.size());
		AttackBoard ab = (AttackBoard) attackBoards.get(0);
		List<Move> abMoves = ab.generateMoves(testBoard, Piece.NO_PROMOTE);
		
		Move makeUpright = TestUtils.extractMoveThatMatches(abMoves, "a2(4)", Position.IS_AB);
		assertNotNull(makeUpright);
		testBoard.makeMove(makeUpright);
		TestUtils.printBoard(testBoard);
		assertNotNull (testBoard.getSquare(ab.getPosition()).getAttackBoard(true));
		
		List<Piece> rooks = translator.getPiecesMatching('r', testBoard.getPieces(Color.White));
		assertEquals (1, rooks.size());
		Piece rook = rooks.get(0);
		List<Move> moves = rook.generateMoves(testBoard, Piece.NO_PROMOTE);
		Move goal = new Move(rook, new Position(file(B), rank(6), level(4)), null);
		assertTrue (moves.contains(goal));
	}
	
	private static final String [] BOARD_ROOK_FILE = {
		"Default",
		"0",
		"Rb1(2)"
	};
	
	@Test
	public void shouldMoveRookAlongCorrectPathToGetToTargetSquare() {
		MoveTranslator translator = new MoveTranslator();
		Board testBoard = new Board(BOARD_ROOK_FILE);
		
		List<Piece> rooks = translator.getPiecesMatching('r', testBoard.getPieces(Color.White));
		assertEquals (1, rooks.size());
		List<Move> moves = rooks.get(0).generateMoves(testBoard, Piece.NO_PROMOTE);
		
		Move b44 = TestUtils.extractMoveThatMatches(moves, "b4(4)", Position.NOT_AB);
		assertEquals (2, b44.getPath().size());
		assertEquals("b2(2)", b44.getPath().get(0).englishNotation(false));
		assertEquals("b3(4)", b44.getPath().get(1).englishNotation(false));
	}

}
