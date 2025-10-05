package com.caucus.st3d.pieces;

import static com.caucus.st3d.Board.A;
import static com.caucus.st3d.Board.E;
import static com.caucus.st3d.Board.file;
import static com.caucus.st3d.Board.level;
import static com.caucus.st3d.Board.rank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caucus.st3d.Board;
import com.caucus.st3d.Color;
import com.caucus.st3d.Move;
import com.caucus.st3d.Position;

/**
 * Unlike the way in which attack boards are physically mounted on the ST3D board...
 * we track the position of an AB by the position (square) of its lower-left-hand
 * corner, as viewed by White.
 */
public class AttackBoard extends Piece {
	private static Map<Position,Position []> moveMap;
	
//	a8(6)  <-->  a6(4)  <-->  {a4(2) OR a4(6)}  <-->  a2(4)  <-->  a0(2)
//	e8(6)  <-->  e6(4)  <-->  {e4(2) OR e4(6)}  <-->  e2(4)  <-->  e0(2)
	static {
		moveMap = new HashMap<Position, Position[]>();
		
		for (int file : Board.arrayOf(file(A), file(E))) {
			Position rank8level6 = new Position(file, rank(8), level(6));
			Position rank6level4 = new Position(file, rank(6), level(4));
			Position rank4level6 = new Position(file, rank(4), level(6));
			Position rank4level2 = new Position(file, rank(4), level(2));
			Position rank2level4 = new Position(file, rank(2), level(4));
			Position rank0level2 = new Position(file, rank(0), level(2));
			
			moveMap.put(rank8level6,   arrayOf(rank6level4));
			moveMap.put(rank6level4,   arrayOf(rank8level6, rank4level2, rank4level6));
			moveMap.put(rank4level2,   arrayOf(rank4level6, rank6level4, rank2level4));
			moveMap.put(rank4level6,   arrayOf(rank4level2, rank6level4, rank2level4));
			moveMap.put(rank2level4,   arrayOf(rank4level2, rank4level6, rank0level2));
			moveMap.put(rank0level2,   arrayOf(rank2level4));
		}
	}
	
	private static Position [] arrayOf (Position ... pos) {
		return pos;
	}
	
	public AttackBoard(Color color) {
		super(color);
	}
	
	public void setColor (Color color) {
		this.color = color;
	}
	
	public void setPosition (Position position) {
		this.position = position;
	}
	
	public boolean isUpright() {
		return position.upright;
	}
	
	public void setUpright (boolean upright) {
		this.position.upright = upright;
	}
	
	public int getDeltaZ() {
		return (position.upright ? 1 : -1);
	}

	@Override
	public List<Move> generateMoves(Board board, char promote) {
		List<Move> moves = new ArrayList<Move>();
		
		if (attackBoardCarriesTooMuchToMove(board))  return moves;
		
		if (playerHasNoPawns(board))                 return moves;
		
		List<Piece> carriedPieces = piecesOnAttackBoard(board);
		Piece pilot = (carriedPieces.size() == 1 ? carriedPieces.get(0) : null);
		
		Position reversed = new Position (this.position.x, this.position.y, this.position.z, ! this.position.upright);
		addMove (moves, board, reversed, pilot, promote);
		for (Position target: moveMap.get(this.position)) {
			target.upright = true;
			addMove (moves, board, target, pilot, promote);
			
			target.upright = false;
			addMove (moves, board, target, pilot, promote);
		}
	
		return moves;
	}
	
	private boolean attackBoardCarriesTooMuchToMove (Board board) {
		int pieceCount = 0;
		for (Piece piece: piecesOnAttackBoard(board)) {
			if (++pieceCount >= 2 || 
				! (piece instanceof BasePawn) ||
				this.color != piece.getColor())  return true;
		}
		return false;
	}
	
	public List<Piece> piecesOnAttackBoard (Board board) {
		List<Piece> pieces = new ArrayList<Piece>();
		
		for (int deltaX: Board.arrayOf(0, 1)) {
			for (int deltaY: Board.arrayOf(0, 1)) {
				Piece piece = board.getSquare(this.position.x + deltaX, this.position.y + deltaY, this.position.z + getDeltaZ()).getPiece();
				if (piece != null)  pieces.add(piece);
			}
		}
		return pieces;
	}
	
	private boolean playerHasNoPawns(Board board) {
		for (Piece piece: board.getPieces(this.color)) {
			if (piece instanceof BasePawn)  return false;
		}
		return true;
	}
	
	private void addMove (List<Move> moves, Board board, Position position, Piece pilot, char promote) {
		AttackBoard targetSquareAB = board.getSquare(position).getAttackBoard(position.upright);
		if (targetSquareAB == null) {
			Move move = new Move(this, position, null);
			
			if (pilot != null) {
				Position oldABPosition    = move.getPiece().getPosition();
				Position newPilotPosition = new Position(pilot.getPosition());
				newPilotPosition.x += (move.x - oldABPosition.x);
				newPilotPosition.y += (move.y - oldABPosition.y);
				newPilotPosition.z += (move.z - oldABPosition.z);
				newPilotPosition.z += (! oldABPosition.upright  &&    move.upright ?  2 : 0);
				newPilotPosition.z += (  oldABPosition.upright  &&  ! move.upright ? -2 : 0);
				
				Move secondary = new Move(pilot, newPilotPosition, null);
				handlePromotingPilotedPawn(secondary, promote);
				move.setSecondary(secondary);
			}
			moves.add(move);
		}
	}
	
	private void handlePromotingPilotedPawn (Move pawnMove, char promoteLetter) {
		Piece pilot = pawnMove.getPiece();
		if (promoteLetter != 0  &&  pilot.getColor().isPromotedAt(pawnMove.y)) {
			pawnMove.setPromoted(promoteLetter);
		}
	}

	@Override
	public char toChar() {
		return 'l';
	}
	
	@Override
	public String toString() {
		return this.color.name().substring(0,1) + toChar() + ":" + this.position.englishNotation(Position.IS_AB);
	}
}
