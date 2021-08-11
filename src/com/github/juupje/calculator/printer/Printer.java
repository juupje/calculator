package com.github.juupje.calculator.printer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.main.Variable;
import com.github.juupje.calculator.main.Variables;
import com.github.juupje.calculator.mathobjects.MComplex;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MFraction;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MRealError;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.settings.Settings;

public class Printer {
	static final Locale locale = Locale.ROOT;
	private static String lastLatexFileName = null;
	private static String lastTextFileName = null;
	static final String newLine = System.lineSeparator();

	/**
	 * Evaluates the argument given and acts accordingly. <tt>args</tt> will be
	 * split around commas to find the parameters passed the method. The parameter
	 * length can be:
	 * <ul>
	 * <li>1, the name of variable to be printed.</li>
	 * <li>2, the name of the variable to be printed, and the name of the file it
	 * will be printed to.</li>
	 * </ul>
	 * {@link #toLatex(MathObject)} is used to convert the given variable to a text
	 * format.
	 * 
	 * @param args
	 * @see #toLatex(MathObject)
	 * @see #printLatex(String, String)
	 */
	public static void latex(String args) {
		String[] params = args.split(",");
		MathObject mo;
		if (Variables.exists(params[0]))
			mo = Variables.get(params[0]);
		else
			mo = new MExpression(params[0]);

		if (params.length == 2)
			lastLatexFileName = params[1].trim();
		else if (lastLatexFileName == null)
			lastLatexFileName = Calculator.ioHandler.findAvailableName("output_latex", "tex");
		printLatex(LaTeXPrinter.toLatex(mo), lastLatexFileName);
	}

	/**
	 * This prints the variable given in the parameters in a Dot binary tree graph.
	 * Evaluates the argument given and acts accordingly. <tt>args</tt> will be
	 * split around commas to find the parameters passed the method. The parameter
	 * length can be:
	 * <ul>
	 * <li>1, the name of variable to be printed.</li>
	 * <li>2, the name of the variable to be printed, and the name of the file it
	 * will be printed to.</li>
	 * </ul>
	 * 
	 * @param args
	 */
	public static void dot(String args) {
		String[] params = args.split(",");
		for(int i = 0; i < params.length; i++)
			params[i] = params[i].trim();
		MathObject mo;
		String name;
		if (params.length == 2)
			name = params[1];
		else
			name = Calculator.ioHandler.findAvailableName("output_dot", "dot");

		if (params[0].equals("dependencies")) {
			DotPrinter.printDot(Calculator.dependencyGraph, name);
			return;
		}

		if (Variables.exists(params[0]))
			mo = Variables.get(params[0]);
		else
			mo = new MExpression(params[0]);

		if (mo instanceof MExpression)
			DotPrinter.printDot((MExpression) mo, name);
		else
			throw new IllegalArgumentException("Can't export " + Tools.type(mo) + " in a Dot format.");
	}

	/**
	 * Evaluates the argument given and acts accordingly. <tt>args</tt> will be
	 * split around commas to find the parameters passed the method. The parameter
	 * length can be:
	 * <ul>
	 * <li>1, the name of variable to be printed.</li>
	 * <li>2, the name of the variable to be printed, and the name of the file it
	 * will be printed to.</li>
	 * </ul>
	 * {@link #toText(MathObject)} is used to convert the given variable to a text
	 * format.
	 * 
	 * @param args
	 * @see #toText(MathObject)
	 * @see #printText(String, String)
	 */
	public static void text(String args) {
		String[] params = args.split(",");
		MathObject mo;
		if (Variables.exists(params[0]))
			mo = Variables.get(params[0]);
		else
			mo = new MExpression(params[0]);

		if (params.length == 2)
			lastTextFileName = params[1].trim();
		else if (lastTextFileName == null)
			lastTextFileName = Calculator.ioHandler.findAvailableName("output_text", "txt");
		try {
			Calculator.ioHandler.writeToFile(lastTextFileName, TextPrinter.toText(mo), false);
		} catch(IOException e) {
			Calculator.errorHandler.handle("Failed to write to file", e);
		}
	}
	
	public static void export(String arg) {
		String[] args = Parser.getArguments(arg);
		boolean temp = Settings.getBool(Settings.MULTILINE_MATRIX);
		Settings.set(Settings.MULTILINE_MATRIX, false);
		try {
			File f;
			if(args.length==1 && args[0].length() != 0) {
				f = Calculator.ioHandler.writeToFile(args[0] + ".cal", ExportPrinter.exportAll(), false);
			} if(args.length==0 || (args.length==1 && args[0].length() == 0)) {
				String name = Calculator.ioHandler.findAvailableName("export", "cal");
				f = Calculator.ioHandler.writeToFile(name + ".cal", ExportPrinter.exportAll(), false);
			} else {
				String filename = args[args.length-1];
				ArrayList<Variable> toBeExported = new ArrayList<>(args.length-1);
				for(int i = 0; i < args.length-1; i++) {
					if(!Variables.exists(args[i]))
						Calculator.ioHandler.err("Variable with name '" + args[i] + "' was not defined.");
					else
						toBeExported.add(new Variable(args[i]));
				}
				if(toBeExported.size()==0) return;
				StringBuilder export = ExportPrinter.exportSafe(toBeExported);
				f = Calculator.ioHandler.writeToFile(filename+".cal", export, false);
			}
			if(f != null)
				Calculator.ioHandler.out("Saved export file to: " + f.getAbsolutePath());
			else
				Calculator.ioHandler.out("Export file could not be saved");
		} catch(Exception e) {
			Calculator.errorHandler.handle(e);
		} finally {
			Settings.set(Settings.MULTILINE_MATRIX, temp);
		}
	}

	/**
	 * Prints the given String (which should contain a LaTeX expression) to a file
	 * with the given name. The document will have the following structure: <br/>
	 * a preamble (see {@link #latexPreamble}) <br/>
	 * <tt>\begin{equation*}</tt><br/>
	 * <tt>str</tt><br/>
	 * <tt>\end{equation*}</tt><br/>
	 * a ending to the document (see {@link #latexEnd})<br/>
	 * If a file with the given name already exists, the String will be inserted
	 * after the last <tt>\end{equation*}</tt>.
	 * 
	 * @param str  the {@code String} to be printed.
	 * @param name the name of the file to which <tt>str</tt> will be printed to.
	 */
	private static void printLatex(String str, String name) {
		try {
			File f = Calculator.ioHandler.getFile(name, "tex");
			StringBuilder sb = new StringBuilder();
			if (f.exists()) {
				String s = Calculator.ioHandler.readFileToString(f);
				int index = s.indexOf(LaTeXPrinter.latexEnd);
				sb.append(s.substring(0, index == -1 ? 0 : index)+newLine);
				sb.append(name.replace("_", " \\_")).append(":").append(newLine)
					.append("\\begin{equation*}").append(newLine).append(str).append(newLine)
					.append("\\end{equation*}").append(newLine).append(LaTeXPrinter.latexEnd);
			} else {
				sb.append(LaTeXPrinter.latexPreamble).append(newLine).append(name.replace("_", "\\_"))
					.append(":\n\\begin{equation*}").append(newLine).append(str).append(newLine)
					.append("\\end{equation*}").append(newLine).append(LaTeXPrinter.latexEnd);
			}
			Calculator.ioHandler.writeToFile(f, sb, false);
		} catch (IOException e) {
			Calculator.errorHandler.handle(e);
		}
	}
	
	/**
	 * Converts the value of the {@link MScalar} to a String. Takes into account the setting
	 * COMPLEX_IN_POLAR if the value is complex.
	 * 
	 * @param scalar the {@code MScalar} to be converted to a String.
	 * @return the created String.
	 */
	public static String numToString(MScalar scalar) {
		if(scalar.isNaN())
			return "NaN";
		if (scalar.isComplex()) {
			((MComplex) scalar).fixPhi();
			if (Settings.getBool(Settings.COMPLEX_IN_POLAR))
				return numToString(((MComplex) scalar).getR()) + "e^(" + numToString(((MComplex) scalar).arg()) + "i)";
			else {
				String stra = numToString(scalar.real());
				String strb = numToString(scalar.imag());
				if(stra.equals("0") && strb.equals("0"))
					return "0";
				return (!stra.equals("0") ? stra : "") + (!strb.equals("0") ? (strb.startsWith("-") ? "" : (stra.equals("0") ? "" : "+")) + 
						(strb.equals("1") ? "" : (strb.equals("-1") ? "-" : strb)) + "i" : "");
			}
		} else if(scalar.isFraction()) {
			MFraction frac = (MFraction) scalar;
			return frac.getNominator()+"//"+frac.getDenominator();
		} else if(scalar.hasError()) {
			double x = scalar.real();
			double sx = ((MRealError) scalar).err();
			int errorDigits = Settings.getInt(Settings.ERROR_SIGNIFICANCE);
			int a = 0, b = 0;
			if(sx==0) {
				//handle special case of no error separately
				if(x==0)
					return String.format(locale, "%."+errorDigits+"f ? %."+errorDigits+"f", 0, 0);
				int oomToShow = Settings.getInt(Settings.PRECISION);
				b = (int) Math.floor(Math.log10(Math.abs(x)));
				double oom = Math.pow(10, b);
				if(b!=0)
					return String.format(locale, "(%."+oomToShow+"f ? %."+oomToShow+"f)e%d", x/oom, sx/oom, b);
				return String.format(locale, "%."+oomToShow+"f ? %."+oomToShow+"f", x/oom, sx/oom);
			} else
				 a = (int) Math.floor(Math.log10(sx))-errorDigits+1;
			
			if(x==0) {
				//handle special case of x=0 separately
				if(sx>=1 && sx<10)
					return String.format(locale, "%."+(errorDigits-1)+"f ? %."+(errorDigits-1)+"f", 0, sx);
				a += errorDigits-1;
				return String.format(locale, "(%."+(errorDigits-1)+"f ? %."+(errorDigits-1)+"f)e%d", 0, sx/Math.pow(10, a), a);
			} else
				b = (int) Math.floor(Math.log10(Math.abs(x)));
			int oomToShow = b-a;
			if(a>=b) {
				//if the error is larger than the value
				oomToShow = 1; //one digit will be in front of the decimal in the error
				b = a;
			}
			double oom = Math.pow(10, b);
			if(b!=0)
				return String.format(locale, "(%."+oomToShow+"f?%."+oomToShow+"f)e%d", x/oom, sx/oom, b);
			return String.format(locale, "%."+oomToShow+"f?%."+oomToShow+"f", x/oom, sx/oom);
		}
		return numToString(scalar.real());
	}

	/**
	 * Converts the double to a String. Using the number representation settings in
	 * {@link Settings}.
	 * 
	 * @param double the value to be converted.
	 * @return the created String.
	 */
	public static String numToString(double num) {
		String s = "";
		if(num==0) return "0";
		switch(Settings.getInt(Settings.NOTATION)) {
		case Settings.NORMAL:
			if((Math.abs(num) >= 0.001 && Math.abs(num)<1000) || MReal.isInteger(num)) {
				s = String.format(locale, "%." + Settings.getInt(Settings.PRECISION) + "f", num);
				while (s.endsWith("0"))
					s = s.substring(0, s.length() - 1);
				if (s.endsWith("."))
					s = s.substring(0, s.length() - 1);
				break;
			}
		case Settings.SCI:
			s = String.format(locale, "%." + Settings.getInt(Settings.PRECISION) +"e",num);
			//Do some more formatting to remove '+' signs in the exponent as well as 
			//'0's at the beginning of the exponent
			int index = s.indexOf("e");
			if(index == -1) break;
			String exp = s.substring(index+2);
			String sign = s.charAt(index+1)=='-' ? "-" : "";
			int i = 0;
			for(; i < exp.length(); i++)
				if(exp.charAt(i)!='0')
					break;
			if(i == exp.length()) {
				s = s.substring(0, index); //drop the exponent and the 'e'
				break;
			}
			if(i>0) exp = exp.substring(i);
			s = s.substring(0, index+1) + sign + exp;
			break;
		case Settings.ENG:
		   // If the value is negative, make it positive so the log10 works
		   double posVal = (num<0) ? -num : num;
		   double log10 = Math.log10(posVal);
		   // Determine how many orders of 3 magnitudes the value is
		   int count = (int) Math.floor(log10/3);
		   // Scale the value into the range 1<=val<1000
		   num /= Math.pow(10, count * 3);
		   s = String.format(locale, "%." + Settings.getInt(Settings.PRECISION) + "fe%d", num, count * 3);
		   break;
		}
		if(s.equals("-0"))
			return "0";
		return s;
	}
}