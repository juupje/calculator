package main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import algorithms.linalg.MatrixToolkit;
import helpers.Helper;
import helpers.Printer;
import helpers.Setting;
import helpers.Timer;
import helpers.exceptions.CircularDefinitionException;
import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.ShapeException;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;
import mathobjects.MMatrix;
import mathobjects.MathObject;

public abstract class Command {

	enum Commands {
		DEL(new Command() {
			@Override
			public void process(String args) {
				DELETE.process(args);
			}
		}), DELETE(new Command() {
			@Override
			public void process(String args) {
				for (String name : Parser.getArguments(args))
					Variables.remove(name);
			}
		}), DOT(new Command() {
			@Override
			public void process(String args) {
				Printer.dot(args);
			}
		}), EXECUTE(new Command() {
			@Override
			public void process(String args) {
				try {
					Interpreter.execute(new File(args));
				} catch (ShapeException | UnexpectedCharacterException | InvalidFunctionException | TreeException
						| CircularDefinitionException e) {
					Calculator.errorHandler.handle(e);
				}
			}
		}), EXPORT(new Command() {
			@Override
			public void process(String args) {
				Printer.export(args);
			}
		}), HELP(new Command() {
			@Override
			public void process(String args) {
				Helper.printHelp(args);
			}
		}), LATEX(new Command() {
			@Override
			public void process(String args) {
				Printer.latex(args);
			}
		}), LIST(new Command() {
			@Override
			public void process(String args) {
				Helper.printList(args);
			}
		}), PRINT(new Command() {
			@Override
			public void process(String args) {
				MathObject mo = Variables.get(args);
				if (mo == null)
					Calculator.ioHandler.err("There exists no variable with that name.");
				else
					Calculator.ioHandler.out(Printer.toText(mo));
			}
		}), SETTING(new Command() {
			@Override
			public void process(String args) {
				Setting.processCommand(args);
			}
		}), SHAPE(new Command() {
			@Override
			public void process(String args) {
				Calculator.ioHandler.out(Variables.get(args).shape());
			}
		}), TIME(new Command() {
			@Override
			public void process(String args) {
				Calculator.ioHandler.out(Timer.time(args).toString());
			}
		}), TYPE(new Command() {
			@Override
			public void process(String args) {
				MathObject mo = Variables.get(args);
				if (mo == null)
					throw new IllegalArgumentException("There exists no variable with the name " + args);
				if (mo instanceof MMatrix) {
					MatrixToolkit<?> mtk = MatrixToolkit.getToolkit((MMatrix) mo);
					Calculator.ioHandler
							.out(mo.getClass().getSimpleName() + "\nThis matrix is" + mtk.maskAsString(mtk.classify()));
				} else
					Calculator.ioHandler.out(mo.getClass().getSimpleName());
			}
		});

		Command command;

		Commands(Command command) {
			this.command = command;
		}

		public void process(String args) {
			command.process(args);
		}
	}

	public static final Map<String, Command> commands = new HashMap<>();
	static {
		for(Commands c : Commands.values())
			commands.put(c.toString().toLowerCase(), c.command);
	}

	public abstract void process(String args);

	public static Command findCommand(String s) {
		int index = s.indexOf('(');
		if (index == -1)
			return null;
		String command = s.substring(0, index);
		try {
			return commands.get(command.toLowerCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	public static void insertCommand(String name, Command com) {
		commands.put(name, com);
	}
}