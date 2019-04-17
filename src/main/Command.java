package main;

import java.io.File;

import helpers.Helper;
import helpers.Printer;
import helpers.Setting;
import helpers.Timer;
import helpers.exceptions.CircularDefinitionException;
import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.ShapeException;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;
import mathobjects.MathObject;

public enum Command {

	DEL {
		@Override
		public void process(String args) {
			DELETE.process(args);
		}
	},
	DELETE {
		@Override
		public void process(String args) {
			for (String name : Parser.getArguments(args))
				Variables.remove(name);
		}
	},
	DOT {
		@Override
		public void process(String args) {
			Printer.dot(args);
		}
	},
	EXECUTE {
		@Override
		public void process(String args) {
			try {
				Interpreter.execute(new File(args));
			} catch (ShapeException | UnexpectedCharacterException | InvalidFunctionException | TreeException
					| CircularDefinitionException e) {
				Calculator.errorHandler.handle(e);
			}
		}
	},
	HELP {
		@Override
		public void process(String args) {
			Helper.printHelp(args);
		}
	},
	LATEX {
		@Override
		public void process(String args) {
			Printer.latex(args);
		}
	},
	LIST {
		@Override
		public void process(String args) {
			Helper.printList(args);
		}
	},
	PRINT {
		@Override
		public void process(String args) {
			MathObject mo = Variables.get(args);
			if (mo == null)
				Calculator.ioHandler.err("There exists no variable with that name.");
			else
				Calculator.ioHandler.out(Printer.toText(mo));
		}
	},
	SETTING {
		@Override
		public void process(String args) {
			Setting.processCommand(args);
		}
	},
	SHAPE {
		@Override
		public void process(String args) {
			Calculator.ioHandler.out(Variables.get(args).shape());
		}
	},
	TIME {
		@Override
		public void process(String args) {
			Calculator.ioHandler.out(Timer.time(args).toString());
		}
	},
	TYPE {
		@Override
		public void process(String args) {
			Calculator.ioHandler.out(Variables.get(args).getClass().getName());
		}
	};

	public abstract void process(String args);
	
	public static Command findCommand(String s) {
		int index = s.indexOf('(');
		if(index==-1)
			return null;
		String command = s.substring(0, index);
		try {
			return valueOf(command.toUpperCase());
		} catch(IllegalArgumentException e) {
			return null;
		}
	}
	
}
