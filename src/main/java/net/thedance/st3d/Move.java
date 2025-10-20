package net.thedance.st3d;

import net.thedance.st3d.pieces.BasePawn;
import net.thedance.st3d.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * A Move moves a Piece to a Position.
 * 
 * In certain special cases, a Move has an implied "secondary move" of another piece.
 * For example, a King-side castle primarily moves the King, but it also teleports a Rook.
 * Or, an AttackBoard move also moves it's solitary "piloting" pawn along with it.
 * 
 * A Move gets a MoveType, which is used to remember anything else special about
 * the move, e.g. castling, en-passant capture, etc.
 * 
 * And finally, a move may get a promotion, recorded as the letter of the piece
 * being promoted *to*.
 */
public class Move extends Position {
	private Piece    piece;
	private char     promotedLetter;
	private MoveType moveType;
	private Move     secondaryMove;
	private List<Position> path;
	
	public Move (Piece piece, Position position, List<Position> path) {
		this(piece, position, path, (char) 0);
	}
	
	public Move (Piece piece, Position position, List<Position> path, char promotedTo) {
		super(position);
		this.piece = piece;
		this.promotedLetter = promotedTo;
		this.secondaryMove = null;
		this.path = (path != null ? path : new ArrayList<Position>());
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public List<Position> getPath() {
		return path;
	}
	
	public void setPromoted (char promoted) {
		if (this.piece instanceof BasePawn)
			this.promotedLetter = promoted;
	}
	
	public char getPromoted() {
		return promotedLetter;
	}
	
	public MoveType getMoveType() {
		return moveType;
	}
	
	public void setMoveType (MoveType moveType) {
		this.moveType = moveType;
	}
	
	public void setSecondary (Move secondaryMove) {
		this.secondaryMove = secondaryMove;
	}
	
	public Move getSecondary() {
		return this.secondaryMove;
	}
	
	@Override
	public String toString() {
		return piece.toString() + "-" + super.englishNotation(IS_AB) + (promotedLetter != 0 ? ("=" + promotedLetter) : "")
				+ "   " + (secondaryMove != null ? secondaryMove.toString() : "").trim();
	}

}
