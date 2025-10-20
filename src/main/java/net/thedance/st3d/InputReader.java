package net.thedance.st3d;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class InputReader {
	private BufferedReader reader;
	
	public InputReader() {
		reader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public String readLine() {
		String result = null;
		try {
			result = reader.readLine();
		}
		catch (Exception e) { }
		return result;
	}
}
