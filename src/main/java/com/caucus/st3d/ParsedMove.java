package com.caucus.st3d;

import org.apache.commons.lang.StringUtils;

public class ParsedMove {
	private boolean valid          = false;
	private char    pieceLetter    = 0;
	private String  startingLabel  = "";
	private boolean isCapture      = false;
	private char    capturedLetter = 0;
	private String  endingLabel    = "";
	private char    promotedLetter = 0;
	private boolean enPassant      = false;
	
	public ParsedMove(String moveText) {
		moveText = moveText.trim().toLowerCase();
		pieceLetter = Character.toLowerCase(moveText.charAt(0));
		if ("prnbkql".indexOf(pieceLetter) < 0)  return;
		
		
		if (moveText.endsWith("ep")) {
			moveText = moveText.replaceFirst("ep$", "").trim();
			enPassant = true;
		}
		
		if (moveText.endsWith("e.p.")) {
			moveText = moveText.replaceFirst("e\\.p\\.", "").trim();
			enPassant = true;
		}
		
		int separatorPos = StringUtils.indexOfAny(moveText, "*x-");
		if (separatorPos < 0)                   return;
		startingLabel = moveText.substring(1, separatorPos);
		
		endingLabel = moveText.substring(separatorPos+1);
		int promotionPos = endingLabel.indexOf('=');
		if (promotionPos > 0) {
			promotedLetter = endingLabel.charAt(promotionPos+1);
			endingLabel    = endingLabel.substring(0, promotionPos);
		}
		
		if (moveText.charAt(separatorPos) != '-') {
			isCapture = true;
			capturedLetter = endingLabel.charAt(0);
			endingLabel = endingLabel.substring(1);
		}
		
//		if (startingLabel.length() > 0  &&  ! startingLabel.matches(".*\\([0-9]\\)"))  return;
//		if (endingLabel.length()   > 0  &&  ! endingLabel.matches  (".*\\([0-9]\\)"))  return;
		
		valid = true;
	}
	
	public boolean isValid()            { return valid; }
	public boolean isCapture()          { return isCapture; }
	public boolean isEnPassant()        { return enPassant; }
	
	public char    getPieceLetter()     { return pieceLetter; }
	public char    getCapturedLetter()  { return capturedLetter; }
	public char    getPromotedLetter()  { return promotedLetter; }
	
	public String  getStartingLabel()   { return startingLabel; }
	public String  getEndingLabel()     { return endingLabel; }
	
	
	
}
