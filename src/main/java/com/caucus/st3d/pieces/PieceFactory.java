package com.caucus.st3d.pieces;

import static com.caucus.st3d.Board.A;
import static com.caucus.st3d.Board.file;
import static com.caucus.st3d.Board.level;
import static com.caucus.st3d.Board.rank;

import org.apache.commons.lang.math.NumberUtils;

import com.caucus.st3d.Color;
import com.caucus.st3d.Position;
import com.caucus.st3d.RuleSet;

public class PieceFactory {
	private RuleSet ruleSet;
	
	public PieceFactory (RuleSet ruleSet) {
		this.ruleSet = ruleSet;
	}
	
//	public Piece makePiece (String line) {
//		line = line.trim();
//		char letter = line.charAt(0);
//		Color color = (Character.isUpperCase(letter) ? Color.White : Color.Black);
//		Piece piece = null;
//		boolean upright = true;
//		switch (Character.toLowerCase(letter)) {
//			case 'b': piece = new Bishop(color);        break;
//			case 'r': piece = new Rook  (color);        break;
//			case 'n': piece = new Knight(color);        break;
//			case 'q': piece = new Queen (color);        break;
//			case 'k': piece = new King  (color);        break;
//			case 'p': piece = new Pawn  (color);        break;
//			case 'l': 
//				AttackBoard ab = new AttackBoard(color);   
//				if (line.length() >= 8  &&  line.charAt(7) == 'i')  upright = false;
//				piece = ab;
//				break;
//		}
//		
//		line = line.toLowerCase();
//		int x = file (A + line.charAt(1) - 'a');
//		int y = rank (NumberUtils.toInt(line.substring(2,3)));
//		int z = level(NumberUtils.toInt(line.substring(4,5)));
//		piece.setPosition(new Position(x, y, z, upright));
//		
//		if (line.indexOf("/m") > 0) {
//			piece.moved = true;
//		}
//		
//		return piece;
//	}
	
	public Piece makePiece (String line) {
		line = line.trim();
		char letter = line.charAt(0);
		Color color = (Character.isUpperCase(letter) ? Color.White : Color.Black);
		boolean upright = true;
		if (line.length() >= 8  &&  line.charAt(7) == 'i')  upright = false;
		
		line = line.toLowerCase();
		int x = file (A + line.charAt(1) - 'a');
		int y = rank (NumberUtils.toInt(line.substring(2,3)));
		int z = level(NumberUtils.toInt(line.substring(4,5)));
		
		Piece piece = makePiece (letter, color, x, y, z, upright);
		
		if (line.indexOf("/m") > 0) {
			piece.moved = true;
		}
		
		return piece;
	}
	
	public Piece makePiece (char pieceLetter, Color color, int x, int y, int z, boolean upright) {
		Piece piece = null;
		switch (Character.toLowerCase(pieceLetter)) {
			case 'b': piece = new Bishop(color);        break;
			case 'r': piece = new Rook  (color);        break;
			case 'n': piece = new Knight(color);        break;
			case 'q': piece = new Queen (color);        break;
			case 'k': piece = new King  (color);        break;
			
			case 'p': 
				if (ruleSet == RuleSet.Roth)
					piece = new RothPawn  (color);
				else
					piece = new Pawn (color);
				break;
				
			case 'l': 
				AttackBoard ab = new AttackBoard(color);   
				piece = ab;
				break;
		}
		
		piece.setPosition(new Position(x, y, z, upright));
		return piece;
	}
	
//	"Bd1(2)",
//	"Ne1(2)",

}
