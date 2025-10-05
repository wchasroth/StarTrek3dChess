package com.caucus.st3d;


public class St3DMain {
	public static void main (String [] args) {
		RuleSet ruleSet = RuleSet.Default;
		
		if (args.length > 0) {
			if (args[0].equals("--help") || args[0].equals("-h")) {
				System.err.println ("ST3D move interpreter version " + CommandLineInterpreter.VERSION_NUMBER);
				System.err.println ("   Command line options:");
				System.err.println ("      --help");
				System.err.println ("      --roth   (disable sideways motion of rook pawns)");
				System.exit(0);
			}
			
			if (args[0].equals("--roth"))  ruleSet = RuleSet.Roth;
		}
		
		InputReader reader = new InputReader();
		CommandLineInterpreter interpreter = new CommandLineInterpreter(reader, ruleSet);
		
		interpreter.readAndExecuteCommands();
	}
}
