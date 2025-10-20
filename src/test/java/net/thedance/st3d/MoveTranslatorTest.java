package net.thedance.st3d;

import static net.thedance.st3d.Position.IS_AB;
import static net.thedance.st3d.Position.NOT_AB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.thedance.st3d.pieces.Pawn;
import net.thedance.st3d.pieces.Piece;

public class MoveTranslatorTest {
	private MoveTranslator translator;
	private List<Move> moves;
	private Board testBoard;
	
	@Before
	public void setup() {
		translator = new MoveTranslator();
		testBoard = new Board(TEST_BOARD);
	}
	
	@Test
	public void shouldHaveEightWhitePawns_onOpeningBoard() {
		Board board = new Board(Board.makeStartingBoardState(RuleSet.Default));
		List<Piece> whitePieces = board.getPieces(Color.White);
		assertEquals (18, whitePieces.size());
		
		List<Piece> whitePawns = translator.getPiecesMatching('p', whitePieces);
		assertEquals (8, whitePawns.size());
		for (Piece pawn: whitePawns) {
			assertEquals (Pawn.class,  pawn.getClass());
			assertEquals (Color.White, pawn.getColor());
		}
	}
	
	@Test
	public void shouldReturnNull_whenNoPosition() {
		List<Position> positions = translator.parsePosition("", new Board(RuleSet.Default));
		assertNull (positions);
	}
	
	@Test
	public void shouldReturnProperInternalPosition_whenGivenEnglishNotationForSquare() {
		List<Position> positions = translator.parsePosition("b4(4)", new Board(RuleSet.Default));
		assertEquals (1, positions.size());
		Position position = positions.get(0);
		assertEquals (3, position.x);
		assertEquals (6, position.y);
		assertEquals (5, position.z);
	}
	
	private String[] TEST_BOARD = { 
		"Default", 
		"0", 
		"Rd1(2)",
		"Pb2(2)",
		"nd5(4)",
		"pc3(2)",
		"Pe5(6)",
		"pe6(6)",
		"Pd7(6)",
	};
	
	@Test
	public void shouldTranslateNormalPawnMove() {
		moves = translator.translate("P-b3(2)", testBoard);
		assertEquals (1, moves.size());
		assertEquals (0, moves.get(0).getPromoted());
	}
	
	@Test
	public void shouldTranslatePawnTakingFirstMoveOption() {
		moves = translator.translate("Pb2(2)-b4(2)", testBoard);
		assertEquals (1, moves.size());
		assertEquals (MoveType.PawnOption, moves.get(0).getMoveType());
	}
	
	@Test
	public void shouldTranslatePawnTakingFirstMoveOptionAcrossMultipleBoards() {
		moves = translator.translate("Pb2(2)-b4", testBoard);
		assertEquals (2, moves.size());
		assertEquals ("b4(2)", moves.get(0).englishNotation(NOT_AB));
		assertEquals ("b4(4)", moves.get(1).englishNotation(NOT_AB));
		assertEquals (MoveType.PawnOption, moves.get(0).getMoveType());
		assertEquals (MoveType.PawnOption, moves.get(1).getMoveType());
	}
	
	@Test
	public void shouldTranslateAmbiguousPawnMove() {
		moves = translator.translate("Pb2(2)-", testBoard);
		assertEquals (4, moves.size());  // 2 moves on (2), 2 moves on (4)
	}
	
	@Test
	public void shouldFailOnBackwardsPawnMove() {
		moves = translator.translate("Pb2(2)-b1(2)", testBoard);
		assertEquals (0, moves.size());
	}
		
	@Test
	public void shouldFailOnDiagonalRookMove() {
		moves = translator.translate("Rd2(2)-e3(2)", testBoard);
		assertEquals (0, moves.size());
	}
	
	@Test
	public void shouldDetectUniqueKnightCaptureByRook() {
		moves = translator.translate("RxN", testBoard);
		assertEquals (1, moves.size());
	}
	
	@Test
	public void shouldTranslateFullDescriptionOfKnightCaptureByRook() {
		moves = translator.translate("Rd1(2)xNd5(4)", testBoard);
		assertEquals (1, moves.size());
	}
	
	@Test
	public void shouldDetectUniquePawnCaptureByPawn() {
		moves = translator.translate("P*P", testBoard);
		assertEquals (1, moves.size());
		TestUtils.printBoard(testBoard);
	}
	
	@Test
	public void shouldDetectInvalidPawnCapture() {
		moves = translator.translate("Pc3(2)*P", testBoard);
		assertEquals (0, moves.size());
	}
	
	@Test
	public void shouldDetectInvalidPawnCaptureOfSameColor() {
		moves = translator.translate("Pb2(2)*Pb3(2)", testBoard);
		assertEquals (0, moves.size());
		TestUtils.printBoard(testBoard);
	}
	
	@Test
	public void shouldFailAdvancingBlockedPawn() {
		moves = translator.translate("Pe5(6)-e6(6)", testBoard);
		assertEquals (0, moves.size());
	}
	
	@Test
	public void shouldMovePawnToLastRankAndPromote() {
		moves = translator.translate("Pd7(6)-d8(6)=Q", testBoard);
		assertEquals (1, moves.size());
		assertEquals ('q', moves.get(0).getPromoted());
	}
	
	@Test
	public void bigBoardTest() {
		testBoard = new Board(Board.makeStartingBoardState(RuleSet.Default));
		testBoard.moveNumber++;
		moves = translator.translate("P-", testBoard);
		assertEquals (16, moves.size());
		TestUtils.printBoard(testBoard);
	}
	
	private static final String[] ATTACK_BOARDS = { 
		"Default", 
		"0", 
		"Pb2(2)",
		"La0(2)/i", 
		"Le2(4)",
		"Le4(6)/i",
	};
	
	@Test
	public void shouldTranslateFullEnglish_forAttackBoardMove() {
		testBoard = new Board(ATTACK_BOARDS);
		TestUtils.printBoard(testBoard);
		moves = translator.translate("QL-b3(4)", testBoard);
		assertEquals (1, moves.size());
	}
	
	@Test
	public void shouldParseAttackBoardPositionCorrectly() {
		assertEquals ("a2(4)",  translator.parseAttackBoardPosition("b3(4)" ).englishNotation(IS_AB));
		assertEquals ("a2(4)i", translator.parseAttackBoardPosition("b3(4)i").englishNotation(IS_AB));
		assertEquals ("a4(2)",  translator.parseAttackBoardPosition("b4(2)" ).englishNotation(IS_AB));
		assertEquals ("a4(6)",  translator.parseAttackBoardPosition("b5(6)" ).englishNotation(IS_AB));
	}
	
	@Test
	public void shouldReturnNull_whenParsingIllegalAttackBoardPosition() {
		assertNull (translator.parseAttackBoardPosition("b4(4)"));
	}
	
	@Test
	public void shouldTranslateEnglishAttackBoardMoves() {
		testBoard = new Board(ATTACK_BOARDS);
		TestUtils.printBoard(testBoard);
		assertMoveTranslatesTo("Lb1(2)i-b1(2)", "Wl:a0(2)i-a0(2)", testBoard);
		assertMoveTranslatesTo("L-b1(2)",       "Wl:a0(2)i-a0(2)", testBoard);
		assertMoveTranslatesTo("L-b3(4)",       "Wl:a0(2)i-a2(4)", testBoard);
		
		assertMoveTranslatesTo("Le3(4)-e5(6)",  "Wl:e2(4)-e4(6)", testBoard);
	}
	
	private void assertMoveTranslatesTo (String english, String result, Board board) {
		List<Move> moves = translator.attackBoardMoves(english, board, Color.White);
		assertEquals (1, moves.size());
		assertEquals (result, moves.get(0).toString().trim());
	}
	
	@Test
	public void shouldNotAllowMovingAttackBoardOntoAnotherAttackBoard() {
		testBoard = new Board(ATTACK_BOARDS);
		List<Move> moves = translator.attackBoardMoves("Le3(4)-e5(6)i", testBoard, Color.White);
		assertEquals (0, moves.size());
	}
	
	private static final String[] ATTACK_BOARD_PROMOTES_PAWN = { 
		"Default", 
		"0", 
		"La6(4)", 
		"Pb7(5)",
	};
	
	@Test
	public void shouldTranslatePilotedAttackBoard_whenPawnPromotes() {
		testBoard = new Board(ATTACK_BOARD_PROMOTES_PAWN);
		List<Move> abMoves = translator.attackBoardMoves ("lb6(4)-b8(6)=q", testBoard, Color.White);
		assertTrue (abMoves.get(0).toString().startsWith("Wl:a6(4)-a8(6)"));
		Move secondary = abMoves.get(0).getSecondary();
		assertEquals ("WP:b7(5)-b9(7)=q", secondary.toString().trim());
	}
	
	
}
