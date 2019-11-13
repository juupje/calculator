package com.github.juupje.calculator.helpers;

import com.github.juupje.calculator.helpers.exceptions.CircularDefinitionException;
import com.github.juupje.calculator.helpers.exceptions.InvalidFunctionException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Interpreter;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.settings.Settings;

public class Timer {

	public static TimerResult time(String s) {
		String[] args = Parser.getArguments(s);
		if (args.length > 2)
			throw new IllegalArgumentException("Expected 1 or 2 arguments, got " + args.length);
		int repeats = Settings.getInt(Settings.TIMER_DEF_RUNS);
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
				Interpreter.interpret(args[0]);
			}
			time = System.nanoTime() - startTime;
		} catch (UnexpectedCharacterException | InvalidFunctionException | TreeException | CircularDefinitionException | ShapeException e) {
			Calculator.errorHandler.handle(e);
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
					+ System.lineSeparator() + "  total time: " + time + "s" + System.lineSeparator() + "  avg time: " + avg + "s";
		}
	}
}