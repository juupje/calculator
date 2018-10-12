package helpers;

import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;
import main.Calculator;
import main.Interpreter;
import main.Parser;

public class Timer {

	public static TimerResult time(String s) {
		String[] args = Parser.getArguments(s);
		if (args.length > 2)
			throw new IllegalArgumentException("Expected 1 or 2 arguments, got " + args.length);
		int repeats = Setting.getInt(Setting.DEF_TIMER_RUNS);
		if (args.length == 2)
			try {
				repeats = (int) Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Expected 2nd argument to be an integer, got " + args[1]);
			}
		long time = 0;
		try {
			Calculator.ioHandler.disable();
			long startTime = System.nanoTime();
			for (int i = 0; i < repeats; i++) {
				Interpreter.Interpret(args[0]);
			}
			time = System.nanoTime() - startTime;
		} catch (UnexpectedCharacterException | InvalidFunctionException | TreeException e) {
			e.printStackTrace();
		} finally {
			Calculator.ioHandler.enable();
		}
		return new TimerResult(time, repeats, args[0]);
	}

	public static class TimerResult {
		double time;
		int repeats;
		double avg;
		String command;

		public TimerResult(long time, int repeats, String command) {
			this.time = time*1e-9; //convert from ns to s
			this.repeats = repeats;
			this.avg = this.time / repeats;
			this.command = command;
		}

		@Override
		public String toString() {
			return "Timer results for command: " + command + System.lineSeparator() + "  loops: " + repeats
					+ System.lineSeparator() + "  total time: " + time + System.lineSeparator() + "  avg time: " + avg;
		}

	}
}
