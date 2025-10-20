package net.thedance.st3d.pieces;

import static net.thedance.st3d.Color.White;

import java.util.ArrayList;
import java.util.List;

import net.thedance.st3d.Board;
import net.thedance.st3d.Color;
import net.thedance.st3d.Move;
import net.thedance.st3d.Position;

public class Rook extends Piece {
	
	private static Position [] DIRECTIONS = {
		new Position( 1,  0, 0), 
		new Position( 0,  1, 0), 
		new Position(-1,  0, 0), 
		new Position( 0, -1, 0), 
	};

	public Rook(Color color) {
		super(color);
	}
	
	@Override
	public char toChar() {
		return (color.equals(White) ? 'R' : 'r');
	}
	
	@Override
	public List<Move> generateMoves(Board board, char promote) {
		List<Move> moves = new ArrayList<Move>();
		
		addMoves(moves, board, DIRECTIONS, UNLIMITED);
		
		return moves;
	}

}
