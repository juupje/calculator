package com.github.juupje.calculator.helpers.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Interpreter;
import com.github.juupje.calculator.settings.Arguments;
import com.github.juupje.calculator.settings.Settings;

public class IOHandler {
	
	protected Scanner scan;
	private boolean reading;
	private boolean enabled = true;
	
	public void disable() {
		enabled = false;
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void err(String str) {
		if(enabled)
			System.err.println(str);
	}
	
	public void out(String str) {
		if(enabled)
			System.out.println(str);
	}
	
	public void out(Object obj) {
		if(enabled)
			out(obj.toString());
	}
	
	public void debug(String str) {
		if(enabled && (Arguments.getBool(Arguments.DEBUG) || Settings.getBool(Settings.DEBUG)))
			out("DEBUG: " + str);
	}
	
	public void outNoLine(String str) {
		if(enabled)
			System.out.print(str);
	}
	
	public void startConsoleInput() {
		reading = true;
		while(reading) {
			outNoLine(">>> ");
			String line = in();
			if(line.equals("quit") || line.equals("exit")) {
				Calculator.exit();
				break;
			}
			try {
				Interpreter.interpret(line);
			} catch (Exception e) {
				Calculator.errorHandler.handle(e);
			}
		}
		close();
	}
	
	public void stopConsoleInput() {
		reading = false;
	}
	
	public String in() {
		if(scan == null)
			scan = new Scanner(System.in);
		return scan.nextLine();
	}
	
	public void close() {
		scan.close();
		scan = null;
	}
	
	public boolean askYesNo(String question) {
		if(ask(question, new String[] {"yes", "y", "1"}, new String[] {"no", "n", "0"}).equals("yes"))
			return true;
		return false;
	}
	
	public String ask(String question, String[]... options) {
		if(options.length == 0) return null;
		String optionString = "";
		for(String[] option : options)
			optionString += option[0] + "/";
		out(question + " [" + optionString.substring(0, optionString.length()-1) + "]");
		String result = in();
		for(String[] option : options)
			for(String s : option)
				if(s.equals(result))
					return option[0];
		return options[0][0];
	}
	
	public static String ansi(String str, ANSITags tag) {
		if(Arguments.getBool(Arguments.ANSI_COLORS)==true)
			return tag.getString() + str + ANSITags.ANSI_RESET.getString();
		return str;
	}
	
	/**
	 * Prints the given String to a file.
	 * 
	 * @param f        the file to which the content will be printed.
	 * @param content  the content which will be printed to the file.
	 * @param append   if {@code true} the content will be appended to the end of the file. 
	 */
	public void writeToFile(File f, String content, boolean append) throws IOException {
		FileWriter writer = new FileWriter(f, append);
		writer.append(content);
		writer.flush();
		writer.close();
	}
	
	/**
	 * @see #writeToFile(File, String, boolean)
	 */
	public void writeToFile(File f, StringBuilder content, boolean append) throws IOException {
		writeToFile(f, content.toString(), append);
	}
	
	/**
	 * Prints the given String to a file with the given name.
	 * 
	 * @param path     the path of the file to which the content will be printed, relative to the default path.
	 * @param content  the content which will be printed to the file.
	 * @param append   if {@code true} the content will be appended to the end of the file. 
	 */
	public File writeToFile(String path, String content, boolean append) throws IOException {
		File f = new File(getDefaultPath() + path);
		writeToFile(f, content, append);
		return f;
	}
	
	/**
	 * @see #writeToFile(String, String, boolean)
	 */
	public File writeToFile(String path, StringBuilder content, boolean append) throws IOException {
		return writeToFile(path, content.toString(), append);
	}
	
	public String readFileToString(File f) throws IOException {
		return new String(Files.readAllBytes(f.toPath()));
	}
	
	public List<String> readFile(File f) throws IOException {
		return Files.readAllLines(f.toPath());
	}
	
	public File getFile(String name, String extension) {
		return new File(getDefaultPath()+name+"."+extension);
	}
	
	/**
	 * Finds a numeric suffix <tt>num>0</tt> (an integer bigger than 0) such that a
	 * file with the name{@code name + num + "." + ext} does not exist.
	 * 
	 * @param name the name for which the suffix will be sought.
	 * @param ext  the file extension to the filename.
	 * @return the found filename.
	 */
	public String findAvailableName(String name, String ext) {
		int num = 0;
		File file;
		if(name.length()==0 || !isPathValid(getDefaultPath() + name + "." + ext)) {
			throw new UnexpectedCharacterException(name + "." + ext + " is not a valid filename.");
		}
		do {
			file = new File(getDefaultPath() + name + num++ + "." + ext);
		} while (file.exists());
		return file.getName().substring(0, file.getName().lastIndexOf("."));
	}

	public boolean isPathValid(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException ex) {
            return false;
        }
        return true;
    }
	
	public String getDefaultPath() {
		if(Arguments.exists(Arguments.WORK_DIR))
			return Arguments.get(Arguments.WORK_DIR);
		return "";
	}
}