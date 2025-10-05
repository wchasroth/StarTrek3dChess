package com.caucus.st3d;

import org.junit.Test;
import static com.caucus.st3d.Board.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PositionTest {
	
	@Test
	public void shouldRenderPositionInEnglishNotiation() {
		Position position = new Position(file(B), rank(2), level(3));
		assertEquals (3, position.x);
		assertEquals (4, position.y);
		assertEquals (4, position.z);
		
		assertTrue (position.englishNotation(Position.NOT_AB).startsWith("b2(3)"));
	}

}
