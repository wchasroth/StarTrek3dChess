package com.caucus.st3d;

import static com.caucus.st3d.Board.B;
import static com.caucus.st3d.Board.D;
import static com.caucus.st3d.Board.file;
import static com.caucus.st3d.Board.level;
import static com.caucus.st3d.Board.rank;
import static com.caucus.st3d.Position.NOT_AB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.caucus.st3d.pieces.BasePawn;
import com.caucus.st3d.pieces.Pawn;
import com.caucus.st3d.pieces.Piece;

public class BoardTest {
	
	private MoveTranslator translator;
	
	@Before
	public void setup() {
		translator = new MoveTranslator();
	}
	
	@Test
	public void shouldCreateBoardWithCorrectSize() {
		Board board = new Board(RuleSet.Default);
		assertEquals (10, board.xSize);
		assertEquals (14, board.ySize);
		assertEquals (11, board.zSize);
	}
	
	@Test
	public void shouldCreateBoardWith48LandableMainBoardSquares() {
		Board board = new Board(RuleSet.Default);
		int landableCount = 0;
		for (int x=0;   x<board.xSize;  ++x) {
			for (int y=0;   y<board.ySize;   ++y) {
				for (int z=0;   z<board.zSize;   ++z)  {
					if (board.squares[x][y][z].isLandable())  ++landableCount;
				}
			}
		}
		assertEquals (48, landableCount);
	}
	
	
	private static final String[] BOARD_1PAWN_3MOVES = { 
		"Default", 
		"0", 
		"La0(2)/i", 
		"Pa1(1)",
	};
	
	@Test
	public void shouldMoveAttackBoardToA2Level4Inverted() {
		Board testBoard = new Board(BOARD_1PAWN_3MOVES);
		
		List<Piece> abs = translator.getPiecesMatching('l', testBoard.getPieces(Color.White));
		assertEquals (1, abs.size());
		List<Move> moves = abs.get(0).generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (3, moves.size());
		
		Move qLa24 = TestUtils.extractMoveThatMatches(moves, "a2(4)i", Position.IS_AB);
		assertNotNull (qLa24);
		testBoard.makeMove(qLa24);
		assertEquals ("Wl:a2(4)i", abs.get(0).toString());
	}
	
	@Test
	public void shouldMakeBoardStateArrayFromBoard() {
		Board testBoard = new Board(BOARD_1PAWN_3MOVES);
		String [] state = testBoard.makeBoardState();
		assertEquals ("Default", state[0]);
		assertEquals ("0",       state[1]);
		assertEquals ("la0(2)i", state[2]);
		assertEquals ("Pa1(1)",  state[3]);
	}
	
	private static final String [] BOARD_PILOT_PAWN = {
		"Default",
		"0",
		"La0(2)",
		"Pb1(3)",
	};
	
	@Test
	public void shouldMovePilotingPawnOnAttackBoard() {
		Board testBoard = new Board(BOARD_PILOT_PAWN);
		TestUtils.printBoard(testBoard);
		
		List<Piece> abs = translator.getPiecesMatching('l', testBoard.getPieces(Color.White));
		List<Move> moves = abs.get(0).generateMoves(testBoard, Piece.NO_PROMOTE);
		assertEquals (3, moves.size());
		
		Move qLa24 = TestUtils.extractMoveThatMatches(moves, "a2(4)", Position.IS_AB);
		testBoard.makeMove(qLa24);
		
		List<Piece> pawns = translator.getPiecesMatching('p', testBoard.getPieces(Color.White));
		assertEquals (1, pawns.size());
		assertTrue (pawns.get(0).getMoved());
		assertEquals ("b3(5)", pawns.get(0).getPosition().englishNotation(NOT_AB));
	}
	
	private static final String [] BOARD_KING_IN_CHECK = {
		"Default",
		"0",
		"Ke1(2)",
		"pe4(4)",
		"qb4(2)",
	};
	
	@Test
	public void shouldDetectWhenKingIsInCheck() {
		Board testBoard = new Board(BOARD_KING_IN_CHECK);
		TestUtils.printBoard(testBoard);
		assertTrue (testBoard.kingInCheck(Color.White));
		
		Pawn pawn = new Pawn(Color.White);
		testBoard.placePiece(pawn, new Position(file(D), rank(2), level(2)));
		TestUtils.printBoard(testBoard);
		assertFalse (testBoard.kingInCheck(Color.White));
	}
	
	private static final String [] BOARD_PAWN_ABOUT_TO_PROMOTE = {
		"Default",
		"0",
		"Pb7(6)"
	};
	
	@Test
	public void shouldPromoteViaNormalPawnMove() {
		Board testBoard = new Board(BOARD_PAWN_ABOUT_TO_PROMOTE);
		List<Piece> pawns = translator.getPiecesMatching('p', testBoard.getPieces(Color.White));
		assertEquals (1, pawns.size());
		assertTrue (pawns.get(0) instanceof BasePawn);
		Pawn pawn = (Pawn) pawns.get(0);
		
		Move move = new Move(pawn, new Position(file(B), rank(8), level(6)), null, 'q');
		testBoard.makeMove(move);
		TestUtils.printBoard(testBoard);
		assertTrue (testBoard.getSquare(move).getPiece().isA('q'));
		assertEquals (1, testBoard.getPieces(Color.White).size());
	}
	
	private static final String [] BOARD_CAPTURE_ON_ATTACK_BOARD = {
		"Default",
		"1",
		"La0(2)",
		"Le0(2)",
		"Pb1(3)",
		"rb3(2)",
	};
	
	@Test
	public void shouldChangeColorOfAttackBoard_whenSoloPieceOnItIsCaptured() {
		Board testBoard = new Board(BOARD_CAPTURE_ON_ATTACK_BOARD);
		MoveTranslator translator = new MoveTranslator();
		List<Move> moves = translator.translate("rxp", testBoard);
		assertEquals (1, moves.size());
		testBoard.makeMove(moves.get(0));
		TestUtils.printBoard(testBoard);
		
		List<Piece> whitePieces = testBoard.getPieces(Color.White);
		assertEquals (1, whitePieces.size());
		List<Piece> blackPieces = testBoard.getPieces(Color.Black);
		assertEquals (2, blackPieces.size());
		
	}
	
	private static final String [] BOARD_EN_PASSANT = {
		"Default",
		"0",
		"Pb2(2)",
		"pc4(4)",
	};
	
	@Test
	public void shouldGenerateEnPassantCapture() {
		Board testBoard = prepareBoard_withWhitePawnTwoSquareMoveJustMade();
		List<Move> enPassants = generateBlackPawnEnPassantCapture(testBoard, 3);
		assertEquals (1, enPassants.size());
		Move actualMove = enPassants.get(0);
		assertEquals ("Bp:c4(4)-b4(2)   Bp:c4(4)-b3(2)", actualMove.toString().trim());
	}
	
	@Test
	public void shouldTranslateEnPassantCapture() {
		Board testBoard = prepareBoard_withWhitePawnTwoSquareMoveJustMade();
		TestUtils.printBoard(testBoard);
		List<Move> moves = translator.translate("pxpep", testBoard);
		assertEquals ("Bp:c4(4)-b4(2)   Bp:c4(4)-b3(2)", moves.get(0).toString());
		assertEquals (MoveType.EnPassant, moves.get(0).getMoveType());
	}
	
	@Test
	public void shouldMakeEnPassantCaptureOnBoard() {
		Board testBoard = prepareBoard_withWhitePawnTwoSquareMoveJustMade();
		List<Move> enPassants = generateBlackPawnEnPassantCapture(testBoard, 3);
		
		testBoard.makeMove(enPassants.get(0));
		assertEquals (0, testBoard.getPieces(Color.White).size());
		List<Piece> blackPieces = testBoard.getPieces(Color.Black);
		assertEquals (1, blackPieces.size());
		assertEquals ("b3(2)", blackPieces.get(0).getPosition().englishNotation(false));
	}
	
	private Board prepareBoard_withWhitePawnTwoSquareMoveJustMade() {
		Board testBoard = new Board (BOARD_EN_PASSANT);
		MoveTranslator translator = new MoveTranslator();
		List<Move> moves = translator.translate("P-b4(2)", testBoard);
		assertEquals (1, moves.size());
		testBoard.makeMove(moves.get(0));
		return testBoard;
	}
	
	private List<Move> generateBlackPawnEnPassantCapture (Board board, int numberOfExpectedBlackPawnMoves) {
		List<Move> resultingMoves = new ArrayList<Move>();
		
		BasePawn blackPawn = (BasePawn) board.getPieces(Color.Black, 'p').get(0);
		blackPawn.setMoved(true);
		
		List<Move> blackPawnMoves = blackPawn.generateMoves(board, (char) 0);
		assertEquals (numberOfExpectedBlackPawnMoves, blackPawnMoves.size());
		
		for (Move move: blackPawnMoves)  {
			if (move.getSecondary() != null)   resultingMoves.add(move);
		}
		return resultingMoves;
	}
}
