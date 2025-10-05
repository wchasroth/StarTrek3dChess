package com.caucus.st3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.caucus.st3d.pieces.AttackBoard;
import com.caucus.st3d.pieces.Piece;
import com.caucus.st3d.pieces.PieceFactory;
import com.caucus.st3d.pieces.Zen;

/**
 * The Board is the entire game state: the main board squares, the attack board squares,
 * the move number, and the black and white pieces.
 * 
 * The heart of the board is the rectangular prism of squares, in [x,y,z].   Only a subset
 * of this prism is actually used.  Squares that a piece can land on are marked "landable".
 * 
 * There is an "outer edge" that is 2 squares deep, all the around the surface of the prism -- 
 * these squares are always marked as not landable.  That way, the pieces do not need to know the 
 * board limits, they just know the directions they can move in -- if they 
 * move incrementally in a direction and hit a not-landable square, they stop looking in that direction.
 * 
 * The actual playable board is a subset of the prism, "in the middle".  The ranks and files
 * are thus measured in the absolute [x,y,z], e.g. file A is actually x=2 (since x=0 and x=1
 * are forever forbidden squares).  Similarly, level 1 is actually z=2.
 * 
 * @author Charles Roth  April 2012
 */
public class Board {
	public static final int RANK0  =   2;
	public static final int RANK1  =   3;
	public static final int RANK8  =  10;
	public static final int RANK9  =  11;
	public static final int LEVEL1 =   2;
	
	public static final int A = 2;
	public static final int B = 3;
	public static final int C = 4;
	public static final int D = 5;
	public static final int E = 6;
	public static final int F = 7;
	
	public static final boolean LANDABLE     = true;
	public static final boolean NOT_LANDABLE = false;
	
	protected int xSize;
	protected int ySize;
	protected int zSize;
	
	protected int moveNumber;
	protected Move lastMoveMade;
	protected RuleSet ruleSet;
	
	private Map<Color, List<Piece>> pieces  = new HashMap<Color, List<Piece>>();
	protected Square [][][]     squares;
	
	// Empty board.
	public Board(RuleSet ruleSet) {
		this.ruleSet = ruleSet;
		initializeEmptyContainingRectangularPrism();
		initializeMainBoards();
		moveNumber = 0;
		lastMoveMade = new Move(new Zen(Color.White), new Position(0, 0, 0), null);
		pieces.put(Color.White, new ArrayList<Piece>());
		pieces.put(Color.Black, new ArrayList<Piece>());
	}
	
	// Board created from a description of the board state.  Mostly used for unit-tests,
	// but also for "normal" starting position of the board.
	public Board (String [] boardState) {
		initializeEmptyContainingRectangularPrism();
		initializeMainBoards();
		pieces.put(Color.White, new ArrayList<Piece>());
		pieces.put(Color.Black, new ArrayList<Piece>());
		
//		this.ruleSet = boardState[0];
		this.ruleSet = RuleSet.valueOf(boardState[0]);
		PieceFactory factory = new PieceFactory (ruleSet);
		moveNumber = NumberUtils.toInt(boardState[1]);
		for (int i=2;   i<boardState.length;   ++i) {
			Piece piece = factory.makePiece(boardState[i]);
			if (piece instanceof AttackBoard)  placeAttackBoard( (AttackBoard) piece);   // probably can merge into one
			else                               placePiece      (               piece);
		}
		
		lastMoveMade = new Move(new Zen(Color.White), new Position(0, 0, 0), null);
	}
	
	public static int file(int letter) {
		return letter;
	}
	
	public static int file(char letter) {
		letter = Character.toLowerCase(letter);
		return A + (letter - 'a');
	}
	
	public static int rank(int num) {
		return RANK0 + num;
	}
	
	public static int rank(char num) {
		return RANK0 + (num - '0');
	}
	
	public static int level (int num) {
		return 1+ num;
	}
	
	public static int level (String num) {
		return 1 + NumberUtils.toInt(StringUtils.strip(num, "()"));
	}
	
	public Piece placePiece (Piece piece, Position position) {
		piece.setPosition(position);
		placePiece(piece);
		return piece;
	}
	
	public void placePiece (Piece piece) {
		squares[piece.getPosition().x][piece.getPosition().y][piece.getPosition().z].setPiece(piece);
		pieces.get(piece.getColor()).add(piece);
	}
	
	public AttackBoard placeAttackBoard (AttackBoard ab, Position position) {
		ab.setPosition(position);
		placeAttackBoard(ab);
		return ab;
	}
	
	public void placeAttackBoard (AttackBoard ab) {
		squares[ab.getPosition().x][ab.getPosition().y][ab.getPosition().z].setPiece(ab);
		setAttackBoardSquaresFor(ab, LANDABLE);
		pieces.get(ab.getColor()).add(ab);
	}
	
	private void setAttackBoardSquaresFor (AttackBoard ab, boolean landable) {
		int z = ab.getPosition().z + ab.getDeltaZ();
		for (int deltaX: arrayOf(0, 1)) {

			for (int deltaY: arrayOf(0, 1)) {
				squares[ab.getPosition().x + deltaX][ab.getPosition().y + deltaY][z].setLandable(landable);
			}
		}
	}
	
	public static int [] arrayOf (int ... numbers) {
		return numbers;
	}
	
	public Square getSquare (Position position) {
		return squares[position.x][position.y][position.z];
	}
	
	public Square getSquare (int x, int y, int z) {
		return squares[x][y][z];
	}
	
	public Move getLastMoveMade() {
		return lastMoveMade;
	}
	
	public List<Piece> getPieces(Color color) {
		return pieces.get(color);
	}
	
	public List<Piece> getPieces(Color color, char pieceLetter) {
		List<Piece> results = new ArrayList<Piece>();
		for (Piece piece: pieces.get(color)) {
			if (Character.toLowerCase(piece.toChar()) == pieceLetter)
				results.add(piece);
		}
		return results;
	}
	
	public int getMoveNumber() {
		return moveNumber;
	}
	
//====================================================================================
	private void initializeEmptyContainingRectangularPrism() {
		xSize = file(F)  - file(A)  + 1 + 2 + 2;
		ySize = rank(9)  - rank(0)  + 1 + 2 + 2;
		zSize = level(7) - level(1) + 1 + 2 + 2;
		
		squares = new Square [xSize][ySize][zSize];
		for (int x=0;   x<xSize;  ++x) {
			for (int y=0;   y<ySize;   ++y) {
				for (int z=0;   z<zSize;   ++z)
					squares[x][y][z] = new Square();
			}
		}
	}
	
	private void initializeMainBoards() {
		for (int x=file(B);   x<=file(E);   ++x) {
			for (int y=rank(1);   y<=rank(4);   ++y)  squares[x][y][level(2)].setLandable(true);
			for (int y=rank(3);   y<=rank(6);   ++y)  squares[x][y][level(4)].setLandable(true);
			for (int y=rank(5);   y<=rank(8);   ++y)  squares[x][y][level(6)].setLandable(true);
		}
	}
	
	private static final String[] STARTING_BOARD = { 
		"", 
		"0",        // move number
		"La0(2)/u", 
		"Le0(2)/u", 
		"Ra0(3)", 
		"Qb0(3)", 
		"Ke0(3)",
		"Rf0(3)",
		"Nb1(2)",
		"Bc1(2)",
		"Bd1(2)",
		"Ne1(2)",
		"Pa1(3)", "Pb1(3)", "Pe1(3)", "Pf1(3)",
		"Pb2(2)", "Pc2(2)", "Pd2(2)", "Pe2(2)",
		
		"la8(6)/u", 
		"le8(6)/u", 
		"ra9(7)", 
		"qb9(7)", 
		"ke9(7)",
		"rf9(7)",
		"nb8(6)",
		"bc8(6)",
		"bd8(6)",
		"ne8(6)",
		"pa8(7)", "pb8(7)", "pe8(7)", "pf8(7)",
		"pb7(6)", "pc7(6)", "pd7(6)", "pe7(6)",
	};
	
	public static String [] makeStartingBoardState (RuleSet ruleSet) {
		String [] startingBoardState = STARTING_BOARD.clone();
		startingBoardState[0] = ruleSet.toString();
		return startingBoardState;
	}
	
//	public void makeMove (Move move) {
//		Piece piece1   = move.getPiece();
//		
//		Move secondary = move.getSecondary();
//		if (secondary != null) {
//			Piece piece2 = secondary.getPiece();
//			
//			clearPiece(piece1);
//			clearPiece(piece2);
//			
//			placePiece(move);
//			placePiece(secondary);
//			piece2.setMoved(true);
//		}
//		
//		else {
//			clearPiece(piece1);
//			Piece capturedPiece = (piece1 instanceof AttackBoard ? null : getSquare(move).getPiece());
//			if (capturedPiece != null) {
//				Color capturedColor = capturedPiece.getColor();
//				List<Piece> opponentPieces = pieces.get(capturedColor);
//				
//				AttackBoard capturedAb = getAttackBoardVulnerableToCapture(capturedPiece);
//				if (capturedAb != null) switchColorOfCapturedAttackBoard (capturedAb);
//				
//				opponentPieces.remove(capturedPiece);
//			}
//			placePiece(move);
//		}
//		
//		// en passant
//		
//		piece1.setMoved(true);
//		lastMoveMade = move;
//		++moveNumber;
//	}
	
	public void makeMove (Move move) {
		makePartialMove (move);
		makePartialMove (move.getSecondary());
		
		lastMoveMade = move;
		++moveNumber;
	}
	
	protected void makePartialMove (Move move) {
		if (move == null)  return;
		
		Piece piece = move.getPiece();
		clearPiece(piece);
		Piece targetPiece = (piece instanceof AttackBoard ? null : getSquare(move).getPiece());
		
		if (targetPiece != null) {
			Color targetPieceColor = targetPiece.getColor();
			if (targetPieceColor != piece.getColor()) {
				List<Piece> opponentPieces = pieces.get(targetPieceColor);
				
				AttackBoard capturedAb = getAttackBoardVulnerableToCapture(targetPiece);
				if (capturedAb != null) switchColorOfCapturedAttackBoard (capturedAb);
				
				opponentPieces.remove(targetPiece);
			}
		}
		placePiece(move);
		piece.setMoved(true);
	}
	
	protected void switchColorOfCapturedAttackBoard (AttackBoard capturedAb) {
		Color oldColor = capturedAb.getColor();
		Color newColor = oldColor.getOpposite();
		pieces.get(oldColor).remove(capturedAb);
		capturedAb.setColor(newColor);
		pieces.get(newColor).add(capturedAb);
	}
	
	protected AttackBoard getAttackBoardVulnerableToCapture (Piece capturedPiece) {
		if (capturedPiece.getPosition().z %2 == 1)  return null;
		
		for (Piece piece: getPieces(capturedPiece.getColor(), 'l')) {
			AttackBoard ab = (AttackBoard) piece;
			
			List<Piece> carriesPieces = ab.piecesOnAttackBoard(this);
			if (carriesPieces.size() == 1  &&  carriesPieces.get(0).equals(capturedPiece))
				return ab;
		}

		return null;
	}
	
	public boolean kingInCheck (Color color) {
		List<Piece> kings = getPieces(color, 'k');
		Position kingPos = kings.get(0).getPosition();
		
		for (Piece piece: getPieces(color.getOpposite())) {
			for (Move move: piece.generateMoves(this, Piece.NO_PROMOTE)) {
				if (move.equals(kingPos))  return true;
				Move secondary = move.getSecondary();
				if (secondary != null  &&  secondary.equals(kingPos))  return true;
			}
		}
		return false;
	}
	
	private void placePiece (Move move) {
		Piece piece = move.getPiece();
		if (piece instanceof AttackBoard) {
			piece.setPosition(move);
			getSquare(move).setPiece(piece);
			setAttackBoardSquaresFor((AttackBoard) piece, LANDABLE);
		}
		else {
			if (move.getPromoted() != 0)   piece = promotePiece(move);
			piece.setPosition(move);
			getSquare(move).setPiece(piece);
		}
	}
	
	private Piece promotePiece (Move move) {
		Piece piece = move.getPiece();
		PieceFactory factory = new PieceFactory(ruleSet);
		Piece promotedPiece = factory.makePiece(move.getPromoted(), piece.getColor(), move.x, move.y, move.z, true);
		List<Piece> pieces = getPieces(piece.getColor());
		pieces.remove(piece);
		pieces.add(promotedPiece);
		return promotedPiece;
	}
	
	private void clearPiece (Piece piece) {
		if (piece instanceof AttackBoard) {
			setAttackBoardSquaresFor((AttackBoard) piece, NOT_LANDABLE);
		}
		getSquare(piece.getPosition()).clearPiece(piece);
	}
	
	public Color getNextColorToMove() {
		return (getMoveNumber() % 2 == 0 ? Color.White : Color.Black);
	}
	
	public Color getLastColorMoved() {
		return (getMoveNumber() % 2 == 0 ? Color.Black : Color.White);
	}
	
	public String [] makeBoardState() {
		ArrayList<String> state = new ArrayList<String>();
		state.add(ruleSet.toString());
		state.add("" + moveNumber);
		
		for (Color color: Color.values()) {
			for (Piece piece: pieces.get(color)) {
				state.add(piece.toChar() + piece.getPosition().englishNotation(true));
			}
		}
		
		return state.toArray(new String [0]);
	}
}