package com.caucus.st3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.caucus.st3d.pieces.AttackBoard;
import com.caucus.st3d.pieces.Piece;
import static com.caucus.st3d.Board.*;

public class MoveTranslator {
	
	public List<Move> translate (String moveText, Board board) {
		Color color = (board.getMoveNumber() % 2 == 0 ? Color.White : Color.Black);
		
		moveText = moveText.toLowerCase().trim();
		if (moveText.equals("o-o"))   return castleMoves (board, color, MoveType.KingSideCastle);
		if (moveText.equals("o-o-o")) return castleMoves (board, color, MoveType.QueenSideCastle);
		
		if (moveText.startsWith("ql")  ||  moveText.startsWith("kl") ) {
			return attackBoardMoves (moveText.substring(1), board, color);
		}
		if (moveText.startsWith("l")) {
			return attackBoardMoves (moveText, board, color);
		}
		
		ParsedMove parsedMove = new ParsedMove(moveText);
		if (! parsedMove.isValid())  return null;
		
		List<Move> results = new ArrayList<Move>();
		List<Position> startPositions = parsePosition(parsedMove.getStartingLabel(), board);
		List<Position> endPositions   = parsePosition(parsedMove.getEndingLabel(), board);
		
		List<Piece> pieces = getPiecesMatching (parsedMove.getPieceLetter(), board.getPieces(color));
		for (Piece piece: pieces) {
			if (startPositions == null  ||  startPositions.contains(piece.getPosition())) {
				
				List<Move> moves = piece.generateMoves(board, Piece.NO_PROMOTE);
				for (Move move: moves) {
					if (endPositions == null  || endPositions.contains(move)) {
						move.setPromoted(parsedMove.getPromotedLetter());
						Piece pieceAtEnd = board.getSquare(move).getPiece();
						
						if (parsedMove.isCapture()) {
							if (pieceAtEnd != null  &&  pieceAtEnd.isA(parsedMove.getCapturedLetter())) {
								results.add(move);
							}
						}
						else if (pieceAtEnd == null) {
							results.add(move);
						}
					}
				}
			}
		}
			
		return results;
	}

	protected List<Move> castleMoves(Board board, Color color, MoveType castlingType) {
		List<Piece> kings = getPiecesMatching('k', board.getPieces(color));
		if (kings == null  ||  kings.size() != 1)  return null;
		
		Piece king = kings.get(0);
		List<Move> castleMoves = new ArrayList<Move>();
		for (Move move: king.generateMoves(board, Piece.NO_PROMOTE)) {
			if (move.getMoveType() == castlingType) 
				castleMoves.add(move);
		}
		
		return castleMoves;
	}
	
	public List<Piece> getPiecesMatching (char pieceLetter, List<Piece> pieces) {
		List<Piece> results = new ArrayList<Piece>();
		for (Piece piece: pieces) {
			if (Character.toLowerCase(piece.toChar()) == pieceLetter)  results.add(piece);
		}
		
		return results;
	}
	
	protected List<Position> parsePosition (String positionText, Board board) {
		if (StringUtils.isEmpty(positionText))  return null;
		
		List<Position> results = new ArrayList<Position>();
		int x = file(positionText.charAt(0));
		int y = rank(positionText.charAt(1));
		
		if (positionText.length() > 2) {
			results.add(new Position(x, y, level(positionText.substring(2))));
		}
		else {
			for (int z = level(1);   z<=level(7);  ++z) {
				if (board.getSquare(x, y, z).isLandable())
					results.add(new Position(x, y, z));
			}
		}
		return results;
	}
	
	protected List<Move> attackBoardMoves(String moveText, Board board, Color color)  {
		ParsedMove parsedMove = new ParsedMove(moveText);
		if (! parsedMove.isValid())  return null;
		
		List<Move> results = new ArrayList<Move>();
		
		Position startPosition = parseAttackBoardPosition(parsedMove.getStartingLabel());
		Position endPosition   = parseAttackBoardPosition(parsedMove.getEndingLabel());
		
		List<Piece> pieces = getPiecesMatching (parsedMove.getPieceLetter(), board.getPieces(color));
		for (Piece piece : pieces) {
			if (piece instanceof AttackBoard) {  // Should always be true, but just in case...
				AttackBoard ab = (AttackBoard) piece;
				if (startPosition == null || startPosition.equalsAB(piece.getPosition())) {

					List<Move> moves = ab.generateMoves(board, parsedMove.getPromotedLetter());
					for (Move move : moves) {
						if (endPosition == null || endPosition.equalsAB(move)) {
							results.add(move);
						}
					}
				}
			}
		}
		
		return results;
	}
	
	protected Position parseAttackBoardPosition (String positionText) {
		if (StringUtils.isEmpty(positionText))  return null;
		if (positionText.length() < 5)          return null;  // Must be xx(n)
		
		boolean inverted = positionText.trim().endsWith("i");
		positionText = positionText.substring(0,5);
		
		Position result = abPositionFromEnglish.get(positionText);
		if (result != null) {
			result = new Position(result);
			result.upright = ! inverted;
		}
		return result;
	}
	
	private static Map<String,Position> abPositionFromEnglish;
	static {
		abPositionFromEnglish = new HashMap<String,Position>();
		abPositionFromEnglish.put("b1(2)", new Position(file(A), rank(0), level(2)));
		abPositionFromEnglish.put("b3(4)", new Position(file(A), rank(2), level(4)));
		abPositionFromEnglish.put("b4(2)", new Position(file(A), rank(4), level(2)));
		abPositionFromEnglish.put("b5(6)", new Position(file(A), rank(4), level(6)));
		abPositionFromEnglish.put("b6(4)", new Position(file(A), rank(6), level(4)));
		abPositionFromEnglish.put("b8(6)", new Position(file(A), rank(8), level(6)));
		
		abPositionFromEnglish.put("e1(2)", new Position(file(E), rank(0), level(2)));
		abPositionFromEnglish.put("e3(4)", new Position(file(E), rank(2), level(4)));
		abPositionFromEnglish.put("e4(2)", new Position(file(E), rank(4), level(2)));
		abPositionFromEnglish.put("e5(6)", new Position(file(E), rank(4), level(6)));
		abPositionFromEnglish.put("e6(4)", new Position(file(E), rank(6), level(4)));
		abPositionFromEnglish.put("e8(6)", new Position(file(E), rank(8), level(6)));
	}
	
	
	@SuppressWarnings("unused")
	private void printPositions (String text, List<Position> positions) {
		if (positions == null)  return;
		for (Position position: positions) {
			System.out.println (text + ": " + position.englishNotation(Position.NOT_AB));
		}
	}
	
	
	// Pb2(2)-b4(2)
	// O-O
	// Rc4(4)xQc5(6)
	// Pd7(6)-d8(6)=Q
	// QLb1(2)-b3(4)
	

}
