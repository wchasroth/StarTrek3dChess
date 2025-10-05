package com.caucus.st3d.pieces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.caucus.st3d.Board;
import com.caucus.st3d.Color;
import com.caucus.st3d.Move;
import com.caucus.st3d.MoveTranslator;
import com.caucus.st3d.MoveType;
import com.caucus.st3d.Position;
import com.caucus.st3d.RuleSet;
import com.caucus.st3d.Square;
import com.caucus.st3d.TestUtils;

public class KingTest {
	
	@Test
	public void shouldFindKingSideCastleOnInitialBoard() {
		Board testBoard = new Board(Board.makeStartingBoardState(RuleSet.Default));
		MoveTranslator translator = new MoveTranslator();
		List<Piece> kings = translator.getPiecesMatching('k', testBoard.getPieces(Color.White));
		assertEquals (1, kings.size());
		
		List<Move> moves = kings.get(0).generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (1, moves.size());
		assertEquals (MoveType.KingSideCastle, moves.get(0).getMoveType());
	}
	
	private static final String [] KING_AND_ROOK = {
		"Default",
		"0",
		"Le0(2)",
		"Ke0(3)",
		"Rf0(3)"
	};
	
	@Test
	public void shouldPlaceKingAndRookOnProperSquares_afterKingSideCastle() {
		Board testBoard = new Board(KING_AND_ROOK);
		
		MoveTranslator translator = new MoveTranslator();
		List<Piece> kings = translator.getPiecesMatching('k', testBoard.getPieces(Color.White));
		King king = (King) kings.get(0);
		List<Move> kingMoves = king.generateMoves(testBoard, Piece.NO_PROMOTE);
		Move castleMove = getMoveOfType (kingMoves, MoveType.KingSideCastle);
		assertNotNull (castleMove);
		
		testBoard.makeMove(castleMove);
		Square kingSquare = testBoard.getSquare(king.getPosition());
		assertNotNull (kingSquare.getPiece());
		assertEquals (king, kingSquare.getPiece());
	}
	
	private Move getMoveOfType (List<Move> moves, MoveType type) {
		for (Move move: moves) {
			if (move.getMoveType() == type)  return move;
		}
		return null;
	}
		
}
