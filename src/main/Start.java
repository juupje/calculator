package main;

import java.io.File;
import java.util.Scanner;

import helpers.Interpreter;

public class Start {

	public static void main(String[] args) {
		if(args != null && args.length > 0) {
			if(args[0].equals("run"))
				Interpreter.execute(new File(args[1]));
		}
		Scanner scan = new Scanner(System.in);
		String line = "";
		do {
			System.out.print(">>> ");
			line = scan.nextLine().trim();
			if(line.equals("quit") || line.equals("exit")) break;
			Interpreter.Interpret(line);
		} while(true);
		scan.close();
	}

}
