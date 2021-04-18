package com.github.juupje.calculator.commands;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.github.juupje.calculator.algorithms.linalg.MatrixToolkit;
import com.github.juupje.calculator.helpers.Helper;
import com.github.juupje.calculator.helpers.Timer;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.CircularDefinitionException;
import com.github.juupje.calculator.helpers.exceptions.InvalidFunctionException;
import com.github.juupje.calculator.helpers.exceptions.PluginException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Interpreter;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.main.Variables;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.printer.Printer;
import com.github.juupje.calculator.printer.TextPrinter;
import com.github.juupje.calculator.settings.Settings;

public enum Commands {
	
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
				Interpreter.execute(new File(Calculator.ioHandler.getDefaultPath() + args));
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
				Calculator.ioHandler.out(TextPrinter.toText(mo));
		}
	}), RESET(new Command() {
		@Override
		public void process(String args) {
			if(args == null || args.length()==0)
				Calculator.reset();
			else
				Calculator.ioHandler.err("reset expected no arguments");
		}
	}),	SETTING(new Command() {
		@Override
		public void process(String args) {
			Settings.processCommand(args);
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
				String maskString = mtk.maskAsString(mtk.classify());
				Calculator.ioHandler
						.out(Tools.type(mo) + "\nThis matrix is" + (maskString.length()>0 ? maskString : " no special type"));
			} else
				Calculator.ioHandler.out(Tools.type(mo));
		}
	});

	Command command;

	Commands(Command command) {
		this.command = command;
	}

	public void process(String args) {
		command.process(args);
	}
	
	public static final Map<String, Command> commands = new HashMap<>();
	static {
		for(Commands c : Commands.values())
			commands.put(c.toString().toLowerCase(), c.command);
	}
	
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
		Command old = commands.put(name, com);
		if(old != null) {
			commands.put(name, old);
			throw new PluginException("Could not assign command '" + name + "' as it already exists.");
		}
	}
	
	public static boolean isCommand(String name) {
		return commands.containsKey(name);
	}
}
