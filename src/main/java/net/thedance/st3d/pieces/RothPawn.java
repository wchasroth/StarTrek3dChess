package net.thedance.st3d.pieces;

import static net.thedance.st3d.Color.White;

import java.util.List;

import net.thedance.st3d.Board;
import net.thedance.st3d.Color;
import net.thedance.st3d.Move;
import net.thedance.st3d.Position;

/**
 * RothPawn only has normal 2-D style moves.
 */

public class RothPawn extends BasePawn {

	private static Position [] WHITE_PAWN_MOVES = { new Position(0,  1, 0) } ;
	private static Position [] WHITE_PAWN_TAKES = { new Position(1,  1, 0), new Position(-1,  1, 0) };
	private static Position [] BLACK_PAWN_MOVES = { new Position(0, -1, 0) } ;
	private static Position [] BLACK_PAWN_TAKES = { new Position(1, -1, 0), new Position(-1, -1, 0) };

	public RothPawn(Color color) {
		super(color);
	}
	
	@Override
	public List<Move> generateMoves(Board board, char promote) {
		return generateMoves (board, promote, 
				(color.equals(White) ? WHITE_PAWN_MOVES : BLACK_PAWN_MOVES),
				(color.equals(White) ? WHITE_PAWN_TAKES : BLACK_PAWN_TAKES));
	}

}
