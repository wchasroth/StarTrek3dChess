package com.caucus.st3d.pieces;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.caucus.st3d.Board;
import com.caucus.st3d.Color;
import com.caucus.st3d.Move;
import com.caucus.st3d.MoveTranslator;
import com.caucus.st3d.TestUtils;


/**
 * Test the "special" side-ways Rook pawn moves.
 */
public class PawnTest {
private MoveTranslator translator;
	
	@Before
	public void setup() {
		translator = new MoveTranslator();
	}
	
	private static final String [] QUEEN_ROOK_PAWN = {
		"Default",
		"0",
		"La0(2)",
		"Pa1(3)",
	};
	
	@Test
	public void shouldMoveQueenRookPawnSidewaysRight() {
		Board testBoard = new Board(QUEEN_ROOK_PAWN);
		
		List<Piece> pawns = translator.getPiecesMatching('p', testBoard.getPieces(Color.White));
		Pawn pawn = (Pawn) pawns.get(0);
		
		List<Move> moves = pawn.generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (3, moves.size());
	}
	
	private static final String [] QUEEN_ROOK_ROTH_PAWN = {
		"Roth",
		"0",
		"La0(2)",
		"Pa1(3)",
	};
	
	@Test
	public void shouldNotMoveQueenRookRothPawnSidewaysRight() {
		Board testBoard = new Board(QUEEN_ROOK_ROTH_PAWN);
		
		List<Piece> pawns = translator.getPiecesMatching('p', testBoard.getPieces(Color.White));
		RothPawn pawn = (RothPawn) pawns.get(0);
		
		List<Move> moves = pawn.generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (0, moves.size());
	}
	
	private static final String [] QUEEN_ROOK_PAWN_CAPTURES = {
		"Default",
		"0",
		"La0(2)",
		"Pa1(3)",
		"bb0(3)",
		"nb2(2)",
	};
	
	@Test
	public void shouldAllowQueenRookPawnCapturesSidewaysRight() {
		Board testBoard = new Board(QUEEN_ROOK_PAWN_CAPTURES);
		
		List<Piece> pawns = translator.getPiecesMatching('p', testBoard.getPieces(Color.White));
		Pawn pawn = (Pawn) pawns.get(0);
		
		List<Move> moves = pawn.generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (5, moves.size());
	}
	
	private static final String [] KING_ROOK_PAWN = {
		"Default",
		"0",
		"Le8(6)",
		"pf8(7)",
	};
	
	@Test
	public void shouldMoveKingRookPawnSidewaysLeft() {
		Board testBoard = new Board(KING_ROOK_PAWN);
		
		List<Piece> pawns = translator.getPiecesMatching('p', testBoard.getPieces(Color.Black));
		Pawn pawn = (Pawn) pawns.get(0);
		
		List<Move> moves = pawn.generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (3, moves.size());
	}
	
	private static final String [] KING_ROOK_PAWN_CAPTURES = {
		"Default",
		"0",
		"Le8(6)",
		"pf8(7)",
		"Be9(7)",
		"Ne7(6)",
	};
	
	@Test
	public void shouldAllowKingRookPawnCapturesSidewaysLeft() {
		Board testBoard = new Board(KING_ROOK_PAWN_CAPTURES);
		TestUtils.printBoard(testBoard);
		
		List<Piece> pawns = translator.getPiecesMatching('p', testBoard.getPieces(Color.Black));
		Pawn pawn = (Pawn) pawns.get(0);
		
		List<Move> moves = pawn.generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (5, moves.size());
	}
	
	private static final String [] KING_ROOK_PAWN_MIDDLE = {
		"Default",
		"0",
		"le4(2)",
		"pf4(3)",
		"Pe5(6)",
	};
		
	@Test
	public void shouldAllowKingRookPawnCaptureSidewaysLeftAndBack() {
		Board testBoard = new Board(KING_ROOK_PAWN_MIDDLE);
		
		List<Piece> pawns = translator.getPiecesMatching('p', testBoard.getPieces(Color.Black));
		Pawn pawn = (Pawn) pawns.get(0);
		pawn.setMoved(true);
		
		List<Move> moves = pawn.generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (4, moves.size());
	}
		
}
