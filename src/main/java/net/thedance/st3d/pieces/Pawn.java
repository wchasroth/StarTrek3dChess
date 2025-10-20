package net.thedance.st3d.pieces;

import static net.thedance.st3d.Color.White;

import java.util.List;

import net.thedance.st3d.Board;
import net.thedance.st3d.Color;
import net.thedance.st3d.Move;
import net.thedance.st3d.Position;

/**
 * "Default" Pawn has the sideways moves for Rook pawns, in addition
 * to the 2-D style "normal" moves.
 */
public class Pawn extends BasePawn {
	private static Position [] WHITE_PAWN_MOVES = { new Position(0,  1, 0) } ;
	private static Position [] WHITE_PAWN_TAKES = { new Position(1,  1, 0), new Position(-1,  1, 0) };
	private static Position [] BLACK_PAWN_MOVES = { new Position(0, -1, 0) } ;
	private static Position [] BLACK_PAWN_TAKES = { new Position(1, -1, 0), new Position(-1, -1, 0) };
	
	private static Position [] A_ROOK_PAWN_MOVES       = { new Position( 1,  0, 0) } ;
	private static Position [] WHITE_A_ROOK_PAWN_TAKES = { new Position( 1, -1, 0) } ;
	private static Position [] BLACK_A_ROOK_PAWN_TAKES = { new Position( 1,  1, 0) } ;
	
	private static Position [] F_ROOK_PAWN_MOVES       = { new Position(-1,  0, 0) } ;
	private static Position [] WHITE_F_ROOK_PAWN_TAKES = { new Position(-1, -1, 0) } ;
	private static Position [] BLACK_F_ROOK_PAWN_TAKES = { new Position(-1,  1, 0) } ;

	public Pawn(Color color) {
		super(color);
	}
	
	@Override
	public List<Move> generateMoves(Board board, char promote) {
		List<Move> moves = generateMoves (board, promote, 
				(color.equals(White) ? WHITE_PAWN_MOVES : BLACK_PAWN_MOVES),
				(color.equals(White) ? WHITE_PAWN_TAKES : BLACK_PAWN_TAKES));
		
		if (position.x == Board.file(Board.A)) {
			moves.addAll(generateMoves (board, promote, A_ROOK_PAWN_MOVES, 
				(color.equals(White) ? WHITE_A_ROOK_PAWN_TAKES : BLACK_A_ROOK_PAWN_TAKES)));
		}
		else if (position.x == Board.file(Board.F)) {
			moves.addAll(generateMoves (board, promote, F_ROOK_PAWN_MOVES, 
					(color.equals(White) ? WHITE_F_ROOK_PAWN_TAKES : BLACK_F_ROOK_PAWN_TAKES)));
		}
		
		return moves;
	}
}
