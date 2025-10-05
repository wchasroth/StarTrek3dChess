package com.caucus.st3d.pieces;

import java.util.ArrayList;
import java.util.List;

import com.caucus.st3d.Board;
import com.caucus.st3d.Color;
import com.caucus.st3d.Move;

/**
 * The Zen piece is a "no such piece" piece.  It is used instead of null, because
 * nulls are a pain in the butt.
 * 
 */
public class Zen extends Piece {

	public Zen(Color color) {
		super(color);
	}

	@Override
	public char toChar() {
		return 'z';
	}

	@Override
	public List<Move> generateMoves(Board board, char promotion) {
		return new ArrayList<Move>();
	}

}
