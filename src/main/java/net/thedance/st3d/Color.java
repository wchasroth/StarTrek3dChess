package net.thedance.st3d;

public enum Color {
	Black(8, 9, 6, -1, 8, '=',  2, 3), 
	White(1, 0, 2,  1, 0, '-', 10,11);
	
	private int  frontRank;
	private int  backRank;
	private int  startLevel;
	private int  pawnDirection;
	private int  abStartRank;
	private char borderColor;
	private int  promoteY1;
	private int  promoteY2;
	
	Color (int frontRank, int backRank, int startLevel, int pawnDirection, int abStartRank, char borderColor, int promoteY1, int promoteY2) {
		this.frontRank     = frontRank;
		this.backRank      = backRank;
		this.startLevel    = startLevel;
		this.pawnDirection = pawnDirection;
		this.abStartRank   = abStartRank;
		this.borderColor   = borderColor;
		this.promoteY1     = promoteY1;
		this.promoteY2     = promoteY2;
	}
	
	public int  getFrontRank()     { return frontRank; }
	public int  getBackRank()      { return backRank; }
	public int  getStartLevel()    { return startLevel; }
	public int  getPawnDirection() { return pawnDirection; }
	public int  getAbStartRank()   { return abStartRank; }
	public char getBorder()        { return borderColor; }
	
	public Color getOpposite() {
		return (this.equals(Black) ? White : Black);
	}
	
	public boolean isPromotedAt (int y) {
		return (y == promoteY1 || y == promoteY2);
	}
}
