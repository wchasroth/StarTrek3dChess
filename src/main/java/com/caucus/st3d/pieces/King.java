package com.caucus.st3d.pieces;

import static com.caucus.st3d.Color.White;

import java.util.ArrayList;
import java.util.List;

import com.caucus.st3d.Board;
import com.caucus.st3d.Color;
import com.caucus.st3d.Move;
import com.caucus.st3d.MoveType;
import com.caucus.st3d.Position;
import com.caucus.st3d.Square;

public class King extends Piece {
	
	private static Position [] DIRECTIONS = {
		new Position( 1,  0, 0), 
		new Position( 1,  1, 0), 
		new Position( 0,  1, 0), 
		new Position(-1,  1, 0), 
		new Position(-1,  0, 0), 
		new Position(-1, -1, 0), 
		new Position( 0, -1, 0), 
		new Position( 1, -1, 0)
	};

	public King(Color color) {
		super(color);
	}
	
	@Override
	public char toChar() {
		return (color.equals(White) ? 'K' : 'k');
	}
	
	@Override
	public List<Move> generateMoves(Board board, char promote) {
		List<Move> moves = new ArrayList<Move>();
		
		addMoves(moves, board, DIRECTIONS, ONE_SQUARE);
		
		if (! this.moved) {
			
			// King side castle. Doesn't rule out king-in-check, though.  Hmm.
			Piece kingRook = board.getSquare(this.position.x+1, this.position.y, this.position.z).getPiece();
			if (rookCanCastle(kingRook, this.color)) {
				Move kingSideCastle = new Move(this, kingRook.getPosition(), null);
				kingSideCastle.setMoveType(MoveType.KingSideCastle);  //TODO do we even need this anymore?
				kingSideCastle.setSecondary(new Move (kingRook, this.getPosition(), null));
				moves.add(kingSideCastle);
			}
			
			// Queen side castle. Doesn't rule out king-in-check, though.  Hmm.
			Piece queenRook = board.getSquare(this.position.x-4, this.position.y, this.position.z).getPiece();
			if (rookCanCastle (queenRook, this.color)) {
				Position originalQueenPos = new Position (this.position.x-3, this.position.y, this.position.z);
				Square queenSquare = board.getSquare(originalQueenPos);
				if (queenSquare.isLandable()  &&  queenSquare.getPiece() == null) {
					Move queenSideCastle = new Move(this, originalQueenPos, null);
					queenSideCastle.setMoveType(MoveType.QueenSideCastle);
					queenSideCastle.setSecondary(new Move(queenRook, this.position, null));
					moves.add(queenSideCastle);
				}
				
			}
		}
		
		return moves;
	}
	
	private boolean rookCanCastle (Piece rook, Color color) {
		return (rook != null  &&  rook instanceof Rook  &&  rook.getColor() == color  &&  ! rook.moved);
	}

}
