package com.caucus.st3d.pieces;

import static com.caucus.st3d.Color.*;

import java.util.ArrayList;
import java.util.List;

import com.caucus.st3d.Board;
import com.caucus.st3d.Color;
import com.caucus.st3d.Move;
import com.caucus.st3d.Position;

public class Knight extends Piece {
	
	private static Position [] DIRECTIONS = {
		new Position( 2,  1, 0), 
		new Position( 2, -1, 0), 
		new Position( 1,  2, 0), 
		new Position( 1, -2, 0), 
		new Position(-2,  1, 0), 
		new Position(-2, -1, 0), 
		new Position(-1,  2, 0), 
		new Position(-1, -2, 0), 
	};

	public Knight(Color color) {
		super(color);
	}
	
	@Override
	public char toChar() {
		return (color.equals(White) ? 'N' : 'n');
	}
	
	@Override
	public List<Move> generateMoves(Board board, char promote) {
		List<Move> moves = new ArrayList<Move>();
		addMoves(moves, board, DIRECTIONS, ONE_SQUARE);
		return moves;
	}

}
