package net.thedance.st3d;

/**
 * Absolute x, y, z position in Board (*not* rank/file/level) coordinates.
 */

public class Position {
	public static final boolean IS_AB  = true;
	public static final boolean NOT_AB = false;
	
	public int     x;
	public int     y;
	public int     z;
	public boolean upright;
	
	public Position (int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.upright = true;
	}
	
	public Position (int x, int y, int z, boolean upright) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.upright = upright;
	}
	
	public Position (Position original) {
		this.x = original.x;
		this.y = original.y;
		this.z = original.z;
		this.upright = original.upright;
	}
	
	public String toString() {
		return "[" + x + "][" + y + "][" + z + "]";
	}
	
	public void increment(Position vector) {
		this.x += vector.x;
		this.y += vector.y;
		this.z += vector.z;
	}
	
	public boolean equals (Position other) {
		return (this.x == other.x  &&  this.y == other.y  &&  this.z == other.z);
	}
	
	public boolean equalsAB (Position other) {
		return (this.x == other.x  &&  this.y == other.y  &&  this.z == other.z  &&  this.upright == other.upright);
	}
	
	@Override
	public boolean equals (Object otherObj) {
		if (! (otherObj instanceof Position))  return false;
		return equals ((Position) otherObj);
	}
	
	private static final int HASH_PRIME = 31;
	@Override
	public int hashCode() {
		return ((this.x * HASH_PRIME) + this.y * HASH_PRIME) + this.z;
	}
	
	public String englishNotation(boolean showInverted) {
		String file = "abcdef".substring(x-2, x-1);
		String rank = "" + (y-2);
		String level = "(" + (z-1) + ")";
		String result = file + rank + level;
		if (! upright && showInverted) result += "i";
		return result;
	}
	
}
