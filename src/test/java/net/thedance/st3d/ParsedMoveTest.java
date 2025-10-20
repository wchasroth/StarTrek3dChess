package net.thedance.st3d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ParsedMoveTest {
	
	@Test
	public void shouldDetectInvalidMove_withNoEndingSquare() {
		ParsedMove move = new ParsedMove("Pb2");
		assertFalse (move.isValid());
	}
	
	@Test
	public void shouldDetectInvalidMove_withBadPieceLetter() {
		ParsedMove move = new ParsedMove("Xb2-b4");
		assertFalse (move.isValid());
	}
	
	@Test
	public void shouldParseNormalPawnMove() {
		ParsedMove move = new ParsedMove("Pb2(2)-b4(4)");
		assertTrue   (move.isValid());
		assertFalse  (move.isCapture());
		assertEquals ('p', move.getPieceLetter());
		assertEquals ( 0,  move.getCapturedLetter());
		assertEquals ( 0,  move.getPromotedLetter());
		assertEquals ("b2(2)", move.getStartingLabel());
		assertEquals ("b4(4)", move.getEndingLabel());
	}
	
	@Test
	public void shouldParseKnightMove_withoutStartingLabel() {
		ParsedMove move = new ParsedMove("N-b4(4)");
		assertTrue   (move.isValid());
		assertFalse  (move.isCapture());
		assertEquals ('n', move.getPieceLetter());
		assertEquals ( 0,  move.getPromotedLetter());
		assertEquals ("",  move.getStartingLabel());
		assertEquals ("b4(4)", move.getEndingLabel());
	}
	
	@Test
	public void shouldParseQueenCapturingPawn_withoutEndingLabel() {
		ParsedMove move = new ParsedMove("Qe6(6)xP");
		assertTrue   (move.isValid());
		assertTrue   (move.isCapture());
		assertEquals ('q', move.getPieceLetter());
		assertEquals ('p', move.getCapturedLetter());
		assertEquals ( 0,  move.getPromotedLetter());
		assertEquals ("e6(6)", move.getStartingLabel());
		assertEquals ("",      move.getEndingLabel());
	}
	
	@Test
	public void shouldParseRookCapturingBishop_withoutStartingLabel() {
		ParsedMove move = new ParsedMove("Rxbe8(7)");
		assertTrue   (move.isValid());
		assertTrue   (move.isCapture());
		assertEquals ('r', move.getPieceLetter());
		assertEquals ('b', move.getCapturedLetter());
		assertEquals ( 0,  move.getPromotedLetter());
		assertEquals ("",      move.getStartingLabel());
		assertEquals ("e8(7)", move.getEndingLabel());
	}
	
	@Test
	public void shouldParseNormalPawnPromotion() {
		ParsedMove move = new ParsedMove("P-e8(6)=Q");
		assertTrue   (move.isValid());
		assertFalse  (move.isCapture());
		assertEquals ('p', move.getPieceLetter());
		assertEquals ( 0 , move.getCapturedLetter());
		assertEquals ('q', move.getPromotedLetter());
		assertEquals ("",  move.getStartingLabel());
		assertEquals ("e8(6)", move.getEndingLabel());
	}
	
	@Test
	public void shouldParsePawnPromotionOnAttackBoardMove() {
		ParsedMove move = new ParsedMove("KL-e8(6)=Q");
		assertEquals ('q', move.getPromotedLetter());
	}
	
}
