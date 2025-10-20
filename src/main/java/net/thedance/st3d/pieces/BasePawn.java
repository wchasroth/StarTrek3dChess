package net.thedance.st3d.pieces;

import static net.thedance.st3d.Color.White;

import java.util.ArrayList;
import java.util.List;

import net.thedance.st3d.Board;
import net.thedance.st3d.Color;
import net.thedance.st3d.Move;
import net.thedance.st3d.MoveType;
import net.thedance.st3d.Position;

public abstract class BasePawn extends Piece {
	
	public BasePawn(Color color) {
		super(color);
	}
	
	@Override
	public char toChar() {
		return (color.equals(White) ? 'P' : 'p');
	}
	
	public List<Move> generateMoves(Board board, char promote, Position [] pawnMoves, Position [] pawnTakes) {  
		//TODO CR use promote  (or do we really need to?)
		List<Move> candidates = new ArrayList<Move>();
		List<Move> moves = new ArrayList<Move>();
		
		// Add forward pawn moves that do not land on another piece.
		addMoves(candidates, board, pawnMoves, (this.moved ? 1 : 2));
		for (Move move: candidates) {
			if (board.getSquare(move).getPiece() == null)  {
				if (Math.abs(this.position.y - move.y) == 2)   move.setMoveType(MoveType.PawnOption);
				moves.add(move);
			}
		}
		
		//TODO HERE
		
		// Add pawn diagonal captures that actually capture something.
		candidates.clear();
		addMoves(candidates, board, pawnTakes, ONE_SQUARE);
		for (Move move: candidates) {
			if (board.getSquare(move).getPiece() != null)  moves.add(move);
		}
		
		addEnPassantMove(moves, board);
		
		// Weird rook pawn option moves.  How to control?
		
		return moves;
	}
	
	//TODO This does not handle the WEIRD case of a rook pawn which has taken it's 2-square option
	// to move LEFT or RIGHT... and then is captured by a normal pawn at the "virtual" square
	// where it would have landed if it moved only ONE square LEFT or RIGHT.  Yuck.
	protected void addEnPassantMove (List<Move> moves, Board board) {
		Move lastMoveMade = board.getLastMoveMade();
		if (lastMoveMade.getMoveType() == MoveType.PawnOption) {
			BasePawn other = (BasePawn) lastMoveMade.getPiece();
			int deltaX = this.position.x - other.position.x;
			if (this.position.y == other.position.y  &&  Math.abs(deltaX) == 1) {
				List<Position> enPassantPositions = lastMoveMade.getPath();
				if (enPassantPositions.size() == 1) {
					Move epMove1 = new Move(this, other.getPosition(), null);
					Move epMove2 = new Move(this, enPassantPositions.get(0), null);
					epMove1.setMoveType(MoveType.EnPassant);
					epMove1.setSecondary(epMove2);
					moves.add(epMove1);
				}
			}
			
		}
		
	}
}
