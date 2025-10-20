package net.thedance.st3d;

public enum MoveType {
	KingSideCastle(true),
	QueenSideCastle(true),
	PawnOption(false),
	EnPassant(false),
	Normal(false);
	
	private boolean isCastle;
	
	MoveType (boolean isCastle) {
		this.isCastle = isCastle;
	}
	
	public boolean isCastle() {
		return this.isCastle;
	}
	
}
