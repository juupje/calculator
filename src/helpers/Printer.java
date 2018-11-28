package helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;

import com.sun.scenario.Settings;

import algorithms.Functions.Function;
import graph.Graph;
import main.Calculator;
import main.Operator;
import main.Variables;
import mathobjects.MConst;
import mathobjects.MExpression;
import mathobjects.MFunction;
import mathobjects.MMatrix;
import mathobjects.MScalar;
import mathobjects.MVector;
import mathobjects.MathObject;
import tree.Node;
import tree.Tree;

public class Printer {

	private static String lastLatexFileName = null;
	private static String lastTextFileName = null;
	private static PrintWriter writer;
	
	/**
	 * The latex preamble used for writing stuff to LaTeX files:
	 * <pre>{@code
	 * \documentclass[preview]{standalone}
	 * \ usepackage{amsmath}
	 * \author{Calculator - By Joep Geuskens}
	 * \begin{document}
	 * </pre>
	 */
	private static final String latexPreamble = "\\documentclass[preview]{standalone}\n" + "\\usepackage{amsmath}\n"
			+ "\\author{Calculator - By Joep Geuskens}\n" + "\\begin{document}";
	private static final String latexEnd = "\\end{document}";
	
	/**
	 * Recursively prints the given node and its children to the Dot file which is
	 * currently being written. The printed result be like this:
	 * 
	 * node[label="node.toString()"]; node->left [print left] node->right [print
	 * right]
	 * 
	 * @param node
	 *            the {@link Node} to be printed.
	 */
	private static void printNodeDot(Node<?> node) {
		writer.println(node.hashCode() + "[label=\"" + node.toString() + "\"];");
		if (node.left() != null) {
			writer.println(node.hashCode() + " -> " + node.left().hashCode());
			printNodeDot(node.left());
		}
		if (node.right() != null) {
			writer.println(node.hashCode() + " -> " + node.right().hashCode());
			printNodeDot(node.right());
		}
	}

	/**
	 * Prints the given <tt>MExpression</tt> in a Dot format to a file with the given name. The
	 * method uses {@link #printNodeDot(Node)} to print the nodes.
	 * 
	 * @param tree
	 *            the {@link Tree} to be printed.
	 * @param name
	 *            the name of the Dot file (excluding the .dot extension).
	 * @see #printDot(Node)
	 */
	public static void printDot(MExpression expr, String name) {
		try {
			writer = new PrintWriter(name + ".dot", "UTF-8");
			Tree tree = expr.getTree();
			writer.println("digraph tree {");
			writer.println("node [fontname=\"Arial\"];");
			if (tree.root == null)
				writer.println("");
			else if (!tree.root.isInternal())
				writer.println(tree.root.toString());
			else
				printNodeDot(tree.root);
			writer.println("labelloc=\"t\"\nlabel=\"" + name + "\"\n}");
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public static void printDot(Graph<?> g, String name) {
		try {
			writer = new PrintWriter(name + ".dot", "UTF-8");
			writer.println("digraph dependencies {");
			for(Graph<?>.Node n : g.getNodes()) {
				writer.println(n.hashCode() + "[label=\"" + n.toString() + "\"];");
				for(Graph<?>.Edge e : n.getEdges())
					writer.println(n.hashCode() + "->" + e.getB().hashCode());
			}
			writer.println("}");
			writer.close();
		} catch(FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Recursively converts the given node and its children to a LaTeX format which is
	 * then returned as a <tt>String</tt>.	 * 
	 * @param node
	 *            the {@link Node} to be converted to LaTeX.
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 */
	public static String printNodeLatex(Node<?> n) {
		String s = "";
		if (n.data instanceof MConst) {
			String name = ((MConst) n.data).name();
			switch (name) {
			case "PI":
				s += "\\pi ";
				break;
			default:
				s += name;
			}
		} else if (n.data instanceof Operator) {
			Operator op = (Operator) n.data;
			switch (op) {
			case ADD:
			case SUBTRACT:
				s += printNodeLatex(n.left()) + (op == Operator.ADD ? "+" : "-") + printNodeLatex(n.right());
				break;
			case MULTIPLY:
				String s2 = printNodeLatex(n.right());
				boolean brackets = s2.contains("-") || s2.contains("+");
				if (brackets)
					s2 = "(" + s2 + ")";
				if (n.left().isNumeric() && !Character.isDigit(s2.charAt(0))) // there needn't be a multiplication dot
																				// between a number and a variable.
					s += printNodeLatex(n.left()) + s2;
				else
					s += printNodeLatex(n.left()) + "\\cdot " + s2;
				break;
			case DIVIDE:
				s += "\\frac{" + printNodeLatex(n.left()) + "}{" + printNodeLatex(n.right()) + "}";
				break;
			case POWER:
				if (n.left().isInternal())
					s += "(" + printNodeLatex(n.left()) + ")";
				else
					s += printNodeLatex(n.left());
				s += "^{" + printNodeLatex(n.right()) + "}";
				break;
			case MOD:
				s += printNodeLatex(n.left()) + "\\bmod " + printNodeLatex(n.right());
				break;
			case NEGATE:
				s += "-" + printNodeLatex(n.left());
				break;
			case INVERT:
				s += "\\frac{1}{" + printNodeLatex(n.left()) + "}";
				break;
			case ELEMENT:
				s += printNodeLatex(n.left()) + "_{" + printNodeLatex(n.right()) + "}"; 
				break;
			default:
				break;
			}
		} else if (n.data instanceof Function) {
			String s2 = printNodeLatex(n.left());
			String name = ((Function) n.data).name();
			s += "\\" + name + "{" + (s2.length() > 2 && !name.equals("sqrt") ? "(" + s2 + ")" : s2) + "}";
		} else if(n.data instanceof MathObject)
			s += toLatex((MathObject) n.data);
		else
			s += n.toString();
		return s;

	}

	/**
	 * Converts the given <tt>MExpression</tt> to a LaTeX representation and returns it as a <tt>String</tt>. The
	 * method uses {@link #printNodeLatex(Node)} to print the nodes.
	 * 
	 * @param expr the {@link MExpression} to be converted to LaTeX.
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 * @see #printNodeLatex(Node)
	 */
	public static String toLatex(MExpression expr) {
		Tree tree = expr.getTree();
		if (tree.root.isInternal())
			return printNodeLatex(tree.root);
		else
			return tree.root.toString();
	}
	
	/**
	 * Converts the given <tt>MVector</tt> to a LaTeX representation and returns it as a <tt>String</tt>, using the <tt>pmatrix</tt> environment.
	 * @param v the {@link MVector} to be converted to LaTeX.	 
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 */
	public static String toLatex(MVector v) {
		String latex = "\\begin{pmatrix}";
		String s = v.isTransposed() ? "&" : "\\\\";
		for(MathObject mo : v.elements())
			latex += toLatex(mo) + s;
		return latex.substring(0, latex.length()-s.length()) + "\\end{pmatrix}";
	}
	
	/**
	 * Converts the given <tt>MMatrix</tt> to a LaTeX representation and returns it as a <tt>String</tt>, using the <tt>pmatrix</tt> environment.
	 * @param m the {@link MMatrix} to be converted to LaTeX.
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 */
	public static String toLatex(MMatrix m) {
		String latex = "\\begin{pmatrix}" + System.lineSeparator();
		for(MathObject[] row : m.elements()) {
			for(MathObject mo : row)
				latex += toLatex(mo) + " & ";
			latex = latex.substring(0, latex.length() - 2) + "\\\\"; //cut off the last "& " and add line separator.
		}
		return latex + System.lineSeparator() + "\\end{pmatrix}";
	}
	
	/**
	 * Calls the <tt>toLatex()</tt> method which corresponds with the type of <tt>mo</tt>
	 * @param mo
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 */
	public static String toLatex(MathObject mo) {
		if(mo instanceof MVector)
			return toLatex((MVector) mo);
		else if(mo instanceof MMatrix)
			return toLatex((MMatrix) mo);
		else if(mo instanceof MExpression)
			return toLatex((MExpression) mo);
		else if(mo instanceof MScalar)
			return mo.toString();
		else 
			throw new IllegalArgumentException("Can't export " + mo.getClass() + " to LaTex");
	}
	
	/**
	 * Recursively converts the given {@link Node} and its children to a text representation.
	 * 
	 * @param node
	 *            the {@link Node} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	private static String nodeToText(Node<?> n) {
		String s = "";
		if (n.data instanceof MConst)
			s += ((MConst) n.data).name();
		else if (n.data instanceof Operator) {
			Operator op = (Operator) n.data;
			switch (op) {
			case ADD:
			case SUBTRACT:
				s += nodeToText(n.left()) + (op == Operator.ADD ? "+" : "-") + nodeToText(n.right());
				break;
			case MULTIPLY:
				String s2 = nodeToText(n.right());
				boolean brackets = s2.contains("-") || s2.contains("+");
				if (brackets)
					s2 = "(" + s2 + ")";
				if (n.left().isNumeric() && !Character.isDigit(s2.charAt(0))) // there needn't be a multiplication dot
																			  // between a number and a variable.
					s += nodeToText(n.left()) + s2;
				else
					s += nodeToText(n.left()) + "*" + s2;
				break;
			case DIVIDE:
			case POWER:
				String right = nodeToText(n.right());
				String left = nodeToText(n.left());
				if (n.right().right() != null) //Brackets are needed
					right = "(" + right + ")";
				if(n.left().right() != null)
					left = "(" + left + ")";
				s += left + (op == Operator.POWER ? "^" : "/") + right;
				break;
			case MOD:
				s += "mod[" + nodeToText(n.left()) + ", " + nodeToText(n.right()) + "]";
				break;
			case NEGATE:
				s += "-" + nodeToText(n.left());
				break;
			case INVERT:
				s += "1/" + (n.left().isInternal() ? "(" + nodeToText(n.left()) + ")" : nodeToText(n.left()));
				break;
			case ELEMENT:
				s += nodeToText(n.left()) + "[" + nodeToText(n.right()) + "]";
				break;
			default:
				break;
			}
		} else if (n.data instanceof Function)
			s += ((Function) n.data).name() + "(" + nodeToText(n.left()) + ")";
		else
			s += n.toString();
		return s;
	}
	
	/**
	 * Converts the given <tt>MExpression</tt> to a text format and returns it as a <tt>String</tt>. This is done using {@link #nodeToText(Node)}.
	 * @param mo the {@link MExpression} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	public static String toText(MExpression mo) {
		Tree tree = mo.getTree();
		if(tree.root.isInternal())
			return nodeToText(tree.root);
		else
			return tree.root.toString();
	}
	
	/**
	 * Converts the given <tt>MathObject</tt> to a text format and returns it as a <tt>String</tt>. This is done by simply calling {@code mo.toString()}.
	 * Note that {@link MExpression#toString()} and {@link MFunction#toString()} use {@link #toText(MExpression)}.
	 * @param mo the {@link MathObject} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	public static String toText(MathObject mo) {
		return mo.toString();
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
	 * {@link #toLatex(MathObject)} is used to convert the given variable to a text format.
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
		else if(lastLatexFileName  == null)
			lastLatexFileName = findAvailableName("output_latex", "tex");
		printLatex(toLatex(mo), lastLatexFileName);
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
	 * @param args
	 */
	public static void dot(String args) {
		String[] params = args.split(",");
		MathObject mo;
		String name;
		if(params.length == 2)
			name = params[1];
		else
			name = findAvailableName("output_dot", "dot");

		if(params[0].equals("dependencies")) {
			printDot(Calculator.dependencyGraph, name);
			return;
		}
		
		if(Variables.exists(params[0]))
			mo = Variables.get(params[0]);
		else
			mo = new MExpression(params[0]);
		
		if(mo instanceof MExpression)
			printDot((MExpression) mo, name);
		else
			throw new IllegalArgumentException("Can't export " + mo.getClass() + " in a Dot format.");
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
	 * {@link #toText(MathObject)} is used to convert the given variable to a text format.
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
		else if(lastTextFileName == null)
			lastTextFileName = findAvailableName("output_text", "txt");
		printText(toText(mo), lastTextFileName);
	}
	
	/**
	 * Prints the given String (which should contain a LaTeX expression) to a file with the given name.
	 * The document will  have the following structure: <br/>
	 * a preamble (see {@link #latexPreamble}) <br/>
	 * <tt>\begin{equation*}</tt><br/>
	 * <tt>str</tt><br/>
	 * <tt>\end{equation*}</tt><br/>
	 * a ending to the document (see {@link #latexEnd})<br/>
	 * If a file with the given name already exists, the String will be inserted after the last <tt>\end{equation*}</tt>.
	 * @param str the {@code String} to be printed.
	 * @param name the name of the file to which <tt>str</tt> will be printed to.
	 */
	private static void printLatex(String str, String name) {
		try {
			String n = System.lineSeparator();
			File f = new File(name + ".tex");
			if(f.exists()) {
				String s = new String(Files.readAllBytes(f.toPath()));
				writer = new PrintWriter(name + ".tex", "UTF-8");
				int index = s.indexOf(latexEnd);
				writer.println(s.substring(0, index == -1 ? 0 : index));
				writer.println(name.replace("_", " \\_") + ":" + n + "\\begin{equation*}" + n
						+ str + n + "\\end{equation*}" + n + latexEnd);
			} else {
				writer = new PrintWriter(f);
				writer.println(latexPreamble + n
					+ name.replace("_", "\\_") + ":\n\\begin{equation*}" + n
					+ str + n
					+ "\\end{equation*}" + n
					+ latexEnd);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Prints the given String to a file with the given name.
	 * If a file with the given name already exists, the String will be appended.
	 * @param str the {@code String} to be printed.
	 * @param fileName the name of the file to which <tt>str</tt> will be printed to.
	 */
	private static void printText(String str, String fileName) {
		try {
			File f = new File(fileName + ".tex");
			if(f.exists())
				Files.write(f.toPath(), (System.lineSeparator() + str).getBytes(), StandardOpenOption.APPEND);
			else
				Files.write(f.toPath(), str.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts the double value of the {@link MScalar} to a String.
	 * @param scalar the {@code MScalar} to be converted to a String.
	 * @return the created String.
	 */
	public static String numToString(MScalar scalar) {
		return numToString(scalar.getValue());
	}
	
	/**
	 * Converts the double to a String. Using the number representation settings in {@link Settings}.
	 * @param double the value to be converted.
	 * @return the created String.
	 */
	public static String numToString(double num) {
		String s = "";
		if ((Math.abs(num) >= 0.01 && Math.abs(num) < 1000 && Setting.getInt(Setting.NOTATION) == Setting.ENG) || num == 0 || Setting.getInt(Setting.NOTATION) == Setting.NORMAL || (Setting.getInt(Setting.NOTATION) == Setting.SCI && Math.abs(num) < 10 && Math.abs(num) >= 1)) {
			s = String.format("%." + Setting.getInt(Setting.PRECISION) + "f", num);
			while (s.endsWith("0"))
				s = s.substring(0, s.length() - 1);
			if (s.endsWith("."))
				s = s.substring(0, s.length() - 1);
		} else {
			String format = "0.";
			for (int i = 0; i < Setting.getInt(Setting.PRECISION); i++) format += "#";
			format += "E0";
			s = new DecimalFormat(format).format(num);
		}
		return s;
	}
	
	/**
	 * Finds a numeric suffix <tt>num>0</tt> (an integer bigger than 0) such that a file with the name{@code name + num + "." + ext} does not exist.
	 * @param name the name for which the suffix will be sought.
	 * @param ext the file extension to the filename.
	 * @return the found filename.
	 */
	public static String findAvailableName(String name, String ext) {
		int num = 0;
		File file;
		do {
			file = new File(name + num++ + "." + ext);
		} while (file.exists());
		return file.getName().substring(0, file.getName().lastIndexOf("."));
	}
}