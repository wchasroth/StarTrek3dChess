package net.thedance.st3d;

import static net.thedance.st3d.Board.arrayOf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class CommandLineInterpreter {
	public static String VERSION_NUMBER = "2012-09-22";

	private InputReader reader;
	private RuleSet     ruleSet;
	private Board       board;
	private MoveTranslator translator;
	
	public CommandLineInterpreter (InputReader reader, RuleSet ruleSet) {
		this.reader = reader;
		this.ruleSet = ruleSet;
		translator = new MoveTranslator();
	}
		
	public void readAndExecuteCommands() {
		String[] startingBoardState = Board.makeStartingBoardState(ruleSet);
		board = new Board(startingBoardState);
		List<String> moveList = new ArrayList<String>();

		printBoard(board);
		System.out.println(VERSION_NUMBER);
		System.out.println("Type 'help' for help.");
		showPrompt(board);
		String line;
		while ((line = reader.readLine()) != null) {

			if (line.trim().length() == 0) {
				showPrompt(board);
				continue;
			}

			if (line.toLowerCase().equals("quit"))  return;

			if (line.toLowerCase().equals("help")) {
				System.out.println("Commands:\n   Help\n   Load filename\n   Save filename\n   Quit\n   (chess moves)\n");
				showPrompt(board);
				continue;
			}

			if (line.toLowerCase().startsWith("save")) {
				saveGame(line, moveList);
				showPrompt(board);
				continue;
			}

			if (line.toLowerCase().startsWith("load")) {
				moveList = loadGame(line);
				if (moveList == null) {
					moveList = new ArrayList<String>();
					System.out.println("No such file!");
				}
				board = playGame(moveList, startingBoardState);
				printBoard(board);
				showPrompt(board);
				continue;
			}
			
			if (line.toLowerCase().equals("bs")) {  // board state
				String [] state = board.makeBoardState();
				for (String stateText: state) System.out.println (stateText);
				showPrompt(board);
				continue;
			}

			if (line.toLowerCase().equals("undo")) {
				moveList.remove(moveList.size() - 1);
				board = playGame(moveList, startingBoardState);
				printBoard(board);
				showPrompt(board);
				continue;
			}

			if (line.toLowerCase().startsWith("list")) {
				Color color = (board.getMoveNumber() % 2 == 0 ? Color.White : Color.Black);
				for (String moveText : listMovesFor(line.substring(5).trim(), board, color)) {
					System.out.println("   " + moveText);
				}
				showPrompt(board);
				continue;
			}

			line = line.replaceAll(" ", "");
			List<Move> possibleMoves = translator.translate(line, board);

			if (possibleMoves == null) {
				System.out.println("Bad syntax.");
			} else if (possibleMoves.size() == 0) {
				System.out.println("No such move.");
			} else if (possibleMoves.size() > 1) {
				System.out.println("Move is ambiguous.");
			} else {
				moveList.add(line);
				board.makeMove(possibleMoves.get(0));
				Color lastMoved = board.getLastColorMoved();

				if (board.kingInCheck(lastMoved)) {
					moveList.remove(moveList.size() - 1);
					board = playGame(moveList, startingBoardState);
					printBoard(board);
					System.out.println("Illegal move, leaves king in check.");
					showPrompt(board);
					continue;
				}
			}
			printBoard(board);
			showPrompt(board);
		}
	}
	
	protected List<String> listMovesFor (String pieceAndSquare, Board board, Color color) {
		List<String> results = new ArrayList<String>();
		MoveTranslator translator = new MoveTranslator();
		String moveText = pieceAndSquare + "-";
		List<Move> moves = translator.translate(moveText, board);
		
		for (Move move: moves) {
			results.add(moveText + move.englishNotation(Position.NOT_AB));
		}
		return results;
	}
	
	private Board playGame (List<String> moveList, String [] fromBoardState) {
		Board board = new Board(fromBoardState);
		MoveTranslator translator = new MoveTranslator();
		for (String moveText: moveList) {
			List<Move> possibleMoves = translator.translate(moveText, board);
			if (possibleMoves == null  ||  possibleMoves.size() != 1)  return null;
			board.makeMove(possibleMoves.get(0));
		}
		
		return board;
	}
	
	private void showPrompt(Board board) {
		int number = 1 + (board.getMoveNumber() / 2);
		Color color = board.getNextColorToMove();
		System.out.print (number + ". " + color.toString() + "> ");
	}
		
//	private static void showMoves (Piece piece, Board board) {
//		List<Move> moves = piece.generateMoves(board, Piece.NO_PROMOTE);
//		for (Move move: moves) {
//			board.getSquare(move).setMarker( move.getMoveType() == MoveType.Normal ? '$' : '!');
//		}
//	}
	
	private void printBoard(Board board) {
		for (int level: arrayOf(6, 4, 2)) {
			LevelPrinter printer = new LevelPrinter();
			printer.fillFrom(board, level);
			printer.print();
			System.out.println();
		}
	}

	private boolean saveGame (String command, List<String> moveList) {
		File saveFile = extractFileFromCommand(command);
		if (saveFile == null)  return false;
		try {
			FileUtils.writeLines(saveFile, formatCurrentGame(moveList));
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private File extractFileFromCommand (String command) {
		int blankPos = command.indexOf(' ');
		if (blankPos < 0)  return null;
		
		File file = new File(command.substring(blankPos+1).trim());
		return file;
	}
	
	private List<String> loadGame(String command) {
		File loadFile = extractFileFromCommand(command);
		if (loadFile == null)  return null;
		
		List<String> gameLines;
		try {
			gameLines = FileUtils.readLines(loadFile);
		} catch (IOException e) {
			return null;
		}
		
		List<String> moveList = new ArrayList<String>();
		for (String line: gameLines) {
			String [] parts = StringUtils.split(line);
			moveList.add(parts[1]);
			if (parts.length == 3)
				moveList.add(parts[2]);
		}
		
		return moveList;
	}
	
	private List<String> formatCurrentGame(List<String> moveList) {
		List<String> lines = new ArrayList<String>();
		int number = 0;
		for (int i=0;   i<moveList.size();   ++i) {
			++number;
			String blacksMove = (i+1 < moveList.size() ? moveList.get(i+1) : "");
			lines.add(String.format("%3d. %-20s %s", number, moveList.get(i), blacksMove));
			++i;
		}
		return lines;
	}

}
