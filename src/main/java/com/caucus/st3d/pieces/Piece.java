package com.caucus.st3d.pieces;

import static com.caucus.st3d.Board.level;

import java.util.ArrayList;
import java.util.List;

import com.caucus.st3d.Board;
import com.caucus.st3d.Color;
import com.caucus.st3d.Move;
import com.caucus.st3d.Position;
import com.caucus.st3d.Square;

public abstract class Piece {
	public   static final char NO_PROMOTE = 0;
	
	protected static final int UNLIMITED = 100;
	protected static final int ONE_SQUARE =  1;
	
	protected boolean  moved;
	protected Position position;
	protected Color    color;
	
	public Piece (Color color) {
		this.color = color;
		this.moved = false;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setPosition (Position position) {
		this.position = position;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public abstract char toChar() ;
	
	// Overridden in all inheriting classes.
	public abstract List<Move> generateMoves(Board board, char promotion) ;
	
	// The way we generate moves is (mostly) in two phases:
	// 1. For each direction that a piece can move in...
	// 2. Generate the "candidate" moves, i.e. the squares the piece could move to on an empty board.
	// 3. Then check each candidate move to see if the piece canGetTo() it, i.e., can really get there, is there
	//    anything blocking it along the "highest path".
	
	protected void addMoves(List<Move> moves, Board board, Position [] directions, int maxPerDirection) {
		
		for (Position direction: directions) {
			List<Position> candidates = new ArrayList<Position>();
			addCandidateMovesInDirection(candidates, board, direction, maxPerDirection);
		
			for (Position candidate: candidates) {
				List<Position> path = new ArrayList<Position>();
				if (canGetTo(candidate, direction, path, board)) {
					moves.add(new Move(this, candidate, path));
				}
			}
		}
	}
	
	public void addCandidateMovesInDirection (List<Position> candidates, Board board, Position direction, int maxPerDirection) {
		Position candidate = new Position(this.position);
			
		for (int moves=1;   moves <= maxPerDirection;   ++moves) {
			candidate.increment(direction);
			
			boolean keepMoving = false;
			for (int z=level(1);   z<=level(7);   ++z) {
				Square square = board.getSquare(candidate.x, candidate.y, z);
				if (square.isLandable()) {
					keepMoving = true;
					candidates.add(new Position(candidate.x, candidate.y, z));
				}
			}
			
			if (! keepMoving)  return;
		}
	}
	
	protected boolean canGetTo (Position candidate, Position direction, List<Position> path, Board board) {
		boolean result = false;
		Piece pieceAtCandidate = board.getSquare(candidate).getPiece();
		if (pieceAtCandidate != null  &&  pieceAtCandidate.getColor() == this.getColor())  return false;
		
		int zMax = Math.max(this.position.z, candidate.z);
		if (canGetToCandidateWithZMax(candidate, direction, path, board, zMax))  result = true;
		
		// On a main board, look to see if alternate path on AB above is possible.
		else if (zMax % 2 == 1) {
			path.clear();
			if (canGetToCandidateWithZMax(candidate, direction, path, board, zMax+1))  result = true;
		}
		
		return result;
	}
	
	protected boolean canGetToCandidateWithZMax (Position candidate, Position direction, List<Position> path, Board board, int zMax) {
		Position nextPosition = new Position (this.position);
		nextPosition.increment(direction);
		
		while (nextPosition.x != candidate.x  ||  nextPosition.y != candidate.y) {
			nextPosition.z = findHighestLandableZ(board, nextPosition, zMax);
			path.add(new Position(nextPosition));
			Piece piece = board.getSquare(nextPosition).getPiece();
			if (piece != null)  return false;
			nextPosition.increment(direction);
		}
		
		return true;
	}
	
	protected int findHighestLandableZ (Board board, Position pos, int zMax) {
		for (int z=zMax;   z>0;   --z) {
			if (board.getSquare(pos.x, pos.y, z).isLandable())  return z;
		}
		return 0;
	}
	
	public boolean isA(char letter) {
		return (Character.toLowerCase(this.toChar()) == Character.toLowerCase(letter));
	}
	
	public String toString() {
		return this.color.name().substring(0,1) + toChar() + ":" + this.position.englishNotation(Position.NOT_AB);
	}
	
	public void setMoved (boolean moved) {
		this.moved = moved;
	}
	
	public boolean getMoved() {
		return moved;
	}
}
