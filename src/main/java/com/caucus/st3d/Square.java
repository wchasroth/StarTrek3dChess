package com.caucus.st3d;

import com.caucus.st3d.pieces.AttackBoard;
import com.caucus.st3d.pieces.Piece;

public class Square {
	protected Piece       piece;
	protected boolean     landable;
	protected AttackBoard upAb;
	protected AttackBoard downAb;
	protected char        marker;
	
	public Square() {
		this.piece    = null;
		this.landable = false;
		this.marker   = 0;
	}
	
	public void setLandable(boolean landable) {
		this.landable = landable;
	}
	
	public boolean isLandable() {
		return landable;
	}
	
	public void setPiece (Piece piece) {
		if (piece instanceof AttackBoard) {
			AttackBoard ab = (AttackBoard) piece;
			if (ab.isUpright())  upAb   = ab;
			else                 downAb = ab;
		}
		else {
			this.piece = piece;
		}
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public void clearPiece(Piece piece) {
		if (piece != null  &&  piece instanceof AttackBoard) {
			AttackBoard ab = (AttackBoard) piece;
			if (ab.isUpright())  upAb   = null;
			else                 downAb = null;
		}
		else {
			if (this.piece == piece)
				this.piece = null;
		}
	}
	
	public void setMarker(char mark) {
		this.marker = mark;
	}
	
	public char getMarker() {
		return this.marker;
	}
	
	public AttackBoard getAttackBoard (boolean upright) {
 		return (upright ? upAb : downAb);
	}
	
	public String toString() {
		return "P=" + (piece != null ? piece.toChar() : "") + ", landable=" + landable + ", "
				+ (upAb != null ? "U" : "") + (downAb != null ? "D" : "");
	}
	
	
}