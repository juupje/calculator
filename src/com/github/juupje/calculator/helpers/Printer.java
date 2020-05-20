package com.github.juupje.calculator.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.github.juupje.calculator.algorithms.Functions.Function;
import com.github.juupje.calculator.graph.Graph;
import com.github.juupje.calculator.helpers.exceptions.UndefinedException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.main.Variable;
import com.github.juupje.calculator.main.Variables;
import com.github.juupje.calculator.mathobjects.MComplex;
import com.github.juupje.calculator.mathobjects.MConst;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MFunction;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MRecSequence;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MSequence;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MVectorFunction;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.settings.Settings;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;

public class Printer {

	private static String lastLatexFileName = null;
	private static String lastTextFileName = null;
	private static PrintWriter writer;

	/**
	 * The latex preamble used for writing stuff to LaTeX files:
	 * 
	 * <pre>
	 * {@code
	 * \documentclass[preview]{standalone}
	 * \ usepackage{amsmath}
	 * \author{Calculator - By Joep Geuskens}
	 * \begin{document}
	 * </pre>
	 */
	private static final String latexPreamble = "\\documentclass[preview]{standalone}\n\\usepackage{amsmath}\n\\usepackage{amssymb}"
			+ "\\author{Calculator - By Joep Geuskens}\n\\newcommand{\\func}[1]{\\mathrm{#1}}\n\\begin{document}";
	private static final String latexEnd = "\\end{document}";

	/**
	 * Recursively prints the given node and its children to the Dot file which is
	 * currently being written. The printed result be like this:
	 * 
	 * node[label="node.toString()"]; node->left [print left] node->right [print
	 * right]
	 * 
	 * @param node the {@link Node} to be printed.
	 */
	private static void printNodeDot(Node<?> node) {
		if(node.data instanceof MVectorFunction) {
			MVectorFunction v = (MVectorFunction) node.data;
			String name = "";
			for(int i = 0; i < v.size(); i++) {
				name += i + ", ";
				writer.println(node.hashCode() + " -> " + i);
				writer.println(i + " -> " + v.get(i).getTree().getRoot().hashCode());
				printNodeDot(v.get(i).getTree().getRoot());
			}
			writer.println(node.hashCode() + "[label=\"(" + name.substring(0, name.length()-2) + ")\"];");			
			return;
		}
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
	 * Prints the given <tt>MExpression</tt> in a Dot format to a file with the
	 * given name. The method uses {@link #printNodeDot(Node)} to print the nodes.
	 * 
	 * @param tree the {@link Tree} to be printed.
	 * @param name the name of the Dot file (excluding the .dot extension).
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
			else if (!tree.root.isInternal() && !(tree.root.data instanceof MVectorFunction))
				writer.println(tree.root.toString());
			else
				printNodeDot(tree.root);
			writer.println("labelloc=\"t\"\nlabel=\"" + name + "\"\n}");
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			Calculator.errorHandler.handle(e);
		}
	}

	public static void printDot(Graph<?> g, String name) {
		try {
			writer = new PrintWriter(name + ".dot", "UTF-8");
			writer.println("digraph dependencies {");
			for (Graph<?>.Node n : g.getNodes()) {
				writer.println(n.hashCode() + "[label=\"" + n.toString() + "\"];");
				for (Graph<?>.Edge e : n.getEdges())
					if(e.getA().equals(n))
						writer.println(e.getA().hashCode() + "->" + e.getB().hashCode());
			}
			writer.println("}");
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			Calculator.errorHandler.handle(e);
		}
	}

	/**
	 * Recursively converts the given node and its children to a LaTeX format which
	 * is then returned as a <tt>String</tt>. *
	 * 
	 * @param node the {@link Node} to be converted to LaTeX.
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 */
	public static String printNodeLatex(Node<?> n) {
		String s = "";
		if (n.data instanceof MConst) {
			s += toLatex((MConst)n.data);
		} else if (n.data instanceof Operator) {
			Operator op = (Operator) n.data;
			switch (op) {
			case ADD:
			case SUBTRACT:
				s += printNodeLatex(n.left()) + (op == Operator.ADD ? "+" : "-") + printNodeLatex(n.right());
				break;
			case MULTIPLY:
				String s2 = printNodeLatex(n.right());
				boolean brackets = n.right().data.equals(Operator.ADD) || n.right().data.equals(Operator.SUBTRACT);//s2.contains("-") || s2.contains("+");
				if (brackets)
					s2 = "\\left(" + s2 + "\\right)";
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
					s += "\\left(" + printNodeLatex(n.left()) + "\\right)";
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
				s += (n.left().isInternal() ? "\\left(" + printNodeLatex(n.left()) + "\\right)" : printNodeLatex(n.left())) + "^{-1}";
				break;
			case ELEMENT:
				s += printNodeLatex(n.left()) + "_{" + printNodeLatex(n.right()) + "}";
				break;
			case CONJUGATE:
				s += printNodeLatex(n.left()) + "^*";
			default:
				break;
			}
		} else if (n.data instanceof Function) {
			switch ((Function) n.data) {
			case SQRT:
				s += "\\sqrt{" + printNodeLatex(n.left()) + "}";
				break;
			case ABS:
				s += "\\left|" + printNodeLatex(n.left()) + "\\right|";
				break;
			case SIN:
			case COS:
			case TAN:
			case LN:
			case LOG:
				s += "\\" + n.data + "\\left(" + printNodeLatex(n.left()) + "\\right)";
				break;
			case ASIN:
			case ACOS:
			case ATAN:
				s += "\\arc" + n.data.toString().substring(1) + "\\left(" + printNodeLatex(n.left()) + "\\right)";
				break;
			default:
				s += "\\operatorname{" + n.data + "}{\\left(" + printNodeLatex(n.left()) + "\\right)}";
			}
		} else if (n.data instanceof MathObject)
			s += toLatex((MathObject) n.data);
		else
			s += n.toString();
		return s;

	}

	public static String toLatex(MConst c) {
		switch (c) {
		case pi:
			return "\\pi ";
		case _sigma:
			return"\\sigma ";
		case _alpha:
			return "\\alpha ";
		case _b_w:
			return"b_w";
		case _NA:
			return "N_A";
		case _epsilon0:
			return "\\epsilon_0";
		case _mu0:
			return "\\mu_0";
		case _hbar:
			return "\\hbar ";
		case _m_e:
		case _m_n:
		case _m_p:
			return c.name().toLowerCase().substring(1); 
		default:
			String name = c.name();
			if(name.startsWith("_"))
				name = name.substring(1);
			return name.toLowerCase();
		}
	}
	
	/**
	 * Converts the given <tt>MExpression</tt> to a LaTeX representation and returns
	 * it as a <tt>String</tt>. The method uses {@link #printNodeLatex(Node)} to
	 * print the nodes.
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
	 * Converts the given <tt>MVector</tt> to a LaTeX representation and returns it
	 * as a <tt>String</tt>, using the <tt>pmatrix</tt> environment.
	 * 
	 * @param v the {@link MVector} to be converted to LaTeX.
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 */
	public static String toLatex(MVector v) {
		StringBuilder latex = new StringBuilder("\\begin{pmatrix}");
		String s = v.isTransposed() ? "&" : "\\\\";
		for (MathObject mo : v.elements())
			latex.append(toLatex(mo)).append(s);
		latex.setLength(latex.length() - s.length());
		return latex.append("\\end{pmatrix}").toString();
	}

	/**
	 * Converts the given <tt>MMatrix</tt> to a LaTeX representation and returns it
	 * as a <tt>String</tt>, using the <tt>pmatrix</tt> environment.
	 * 
	 * @param m the {@link MMatrix} to be converted to LaTeX.
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 */
	public static String toLatex(MMatrix m) {
		StringBuilder latex = new StringBuilder();
		latex.append("\\begin{pmatrix}").append(System.lineSeparator());
		for (MathObject[] row : m.elements()) {
			for (MathObject mo : row)
				latex.append(toLatex(mo)).append(" & ");
			//cut off the last "& " and add line separator.
			latex.setLength(latex.length() - 2);
			latex.append("\\\\");
		}
		return latex.append(System.lineSeparator()).append("\\end{pmatrix}").toString();
	}
	
	public static String toLatex(MSequence m) {
		String name = m.getIndexName()=="a" ? "b" : "a";
		String indexedName = name + "_" + m.getIndexName();
		StringBuilder latex = new StringBuilder();
		latex.append("(").append(indexedName).append(")_{").append(m.getIndexName()).append("\\in\\mathbb{N}}\\qquad ");
		if(m instanceof MRecSequence) {
			String fstring = toLatex(m.getFunction());
			latex.append(indexedName).append("=").append(fstring.replace("__", name + "_"));
			System.out.println(latex);
		} else
			latex.append(indexedName).append("=").append(toLatex(m.getFunction()));
		return latex.toString();
	}

	/**
	 * Calls the <tt>toLatex()</tt> method which corresponds with the type of
	 * <tt>mo</tt>
	 * 
	 * @param mo
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 */
	public static String toLatex(MathObject mo) {
		if (mo instanceof MVector)
			return toLatex((MVector) mo);
		else if (mo instanceof MMatrix)
			return toLatex((MMatrix) mo);
		else if (mo instanceof MExpression)
			return toLatex((MExpression) mo);
		else if (mo instanceof MReal)
			return mo.toString();
		else if (mo instanceof MComplex)
			return mo.toString().replace("(", "{").replace(")", "}");
		else if(mo instanceof MSequence)
			return toLatex((MSequence) mo);
		else
			throw new IllegalArgumentException("Can't export " + mo.getClass() + " to LaTex");
	}

	/**
	 * Recursively converts the given {@link Node} and its children to a text
	 * representation.
	 * 
	 * @param node the {@link Node} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	public static String nodeToText(Node<?> n) {
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
				String s1 = nodeToText(n.left());
				String s2 = nodeToText(n.right());
				boolean brackets1 = n.left().data.equals(Operator.ADD) || n.left().data.equals(Operator.SUBTRACT);
				boolean brackets2 = n.right().data.equals(Operator.ADD) || n.right().data.equals(Operator.SUBTRACT);
				if (brackets2)
					s2 = "(" + s2 + ")";
				if(brackets1)
					s1 = "(" + s1 + ")";
				if (n.left().isNumeric() && s2.charAt(0)!='-' && !Character.isDigit(s2.charAt(0))) // there needn't be a multiplication dot
																			  // between a number and a variable.
					s += s1 + s2;
				else
					s += s1 + "*" + s2;
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
				s += (n.left().isInternal() ? "(" + nodeToText(n.left()) + ")" : nodeToText(n.left())) + "^-1";
				break;
			case ELEMENT:
				s += nodeToText(n.left()) + "[" + nodeToText(n.right()) + "]";
				break;
			case CONJUGATE:
				if(n.left().isInternal())
					s += "&(" +nodeToText(n.left()) + ")";
				else
					s += "&" + nodeToText(n.left());
				break;
			default:
				break;
			}
		} else if (n.data instanceof Function)
			s += n.data + "(" + nodeToText(n.left()) + ")";
		else
			s += n.toString();
		return s;
	}

	/**
	 * Converts the given <tt>MExpression</tt> to a text format and returns it as a
	 * <tt>String</tt>. This is done using {@link #nodeToText(Node)}.
	 * 
	 * @param mo the {@link MExpression} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	public static String toText(MExpression mo) {
		Tree tree = mo.getTree();
		if (tree.root.isInternal())
			return nodeToText(tree.root);
		else
			return tree.root.toString();
	}

	/**
	 * Converts the given <tt>MExpression</tt> to a text format and returns it as a
	 * <tt>String</tt>. This is done using {@link #nodeToText(Node)}.
	 * 
	 * @param mo the {@link MExpression} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	public static String toText(MSequence mo) {
		return "{" + toText(mo.getFunction()) + " | " + mo.getIndexName() + "=" + 
				mo.getBegin() + "..." + (mo.getEnd()>= 0 ? mo.getEnd() : "infinity") + "}";
	}
	
	public static String toText(MRecSequence mo) {
		StringBuilder builder = new StringBuilder();
		builder.append("r{[").append(mo.getIndexName()).append("]=").append(toText(mo.getFunction()).replace("_", ""));
		builder.append(" | ");
		for(int i = 0; i < mo.getInitalParameterCount(); i++) {
			if(i>0)
				builder.append(", "); 
			builder.append("[").append(i).append("]=").append(mo.get(i).toString());
		}
		builder.append("}");
		return builder.toString();
	}
	
	/**
	 * Converts the given <tt>MathObject</tt> to a text format and returns it as a
	 * <tt>String</tt>. This is done by simply calling {@code mo.toString()}. Note
	 * that {@link MExpression#toString()} and {@link MFunction#toString()} use
	 * {@link #toText(MExpression)}.
	 * 
	 * @param mo the {@link MathObject} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	public static String toText(MathObject mo) {
		return mo.toString();
	}
	
	public static String toText(MathObject[][] m) {
		return toText(m, 0, m.length-1, 0, m[0].length-1);
	}
	
	public static String toText(Double[][] m, int rstart, int rend, int cstart, int cend) {
		if(Settings.getBool(Settings.MULTILINE_MATRIX)) {
			int rows = rend-rstart+1;
			int cols = cend-cstart+1;
			String[][] s = new String[rows][cols];
			int[] colmax = new int[cols];
			for(int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++) {
					s[i][j] = numToString(m[i+rstart][j+cstart]);
					colmax[j] = Math.max(colmax[j], s[i][j].length()+2);
				}
			String str = "";
			for(int i = 0; i < rows; i++) {
				String row = "  ";
				for(int j = 0; j < cols; j++) {
					row += s[i][j];
					for(int k = s[i][j].length(); k < colmax[j]; k++)
						row += " ";
				}
				if(i==0)
					str = "/" + row + "\\\n";
				else if(i==rows-1)
					str += "\\" + row + "/";
				else
					str += "|" + row + "|\n";
			}
			return str;
		} else {
			String s = "[";
			for(int i = rstart; i <= rend; i++) {
				for (int j = cstart; j <= cend; j++)
					s += numToString(m[i][j]) + ", ";
				s = s.substring(0, s.length() - 2) + ";";
			}
			return s.substring(0, s.length() - 1) + "]";
		}
	}
	
	public static String toText(MathObject[][] m, int rstart, int rend, int cstart, int cend) {
		if(Settings.getBool(Settings.MULTILINE_MATRIX)) {
			int rows = rend-rstart+1;
			int cols = cend-cstart+1;
			String[][] s = new String[rows][cols];
			int[] colmax = new int[cols];
			for(int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++) {
					s[i][j] = m[i+rstart][j+cstart].toString();
					colmax[j] = Math.max(colmax[j], s[i][j].length()+2);
				}
			String str = "";
			for(int i = 0; i < rows; i++) {
				String row = "  ";
				for(int j = 0; j < cols; j++) {
					row += s[i][j];
					for(int k = s[i][j].length(); k < colmax[j]; k++)
						row += " ";
				}
				if(i==0)
					str = "/" + row + "\\\n";
				else if(i==rows-1)
					str += "\\" + row + "/";
				else
					str += "|" + row + "|\n";
			}
			return str;
		} else {
			String s = "[";
			for(int i = rstart; i <= rend; i++) {
				for (int j = cstart; j <= cend; j++)
					s += m[i][j].toString() + ", ";
				s = s.substring(0, s.length() - 2) + ";";
			}
			return s.substring(0, s.length() - 1) + "]";
		}
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
	 * 
	 * @param args
	 */
	public static void dot(String args) {
		String[] params = args.split(",");
		MathObject mo;
		String name;
		if (params.length == 2)
			name = params[1];
		else
			name = findAvailableName("output_dot", "dot");

		if (params[0].equals("dependencies")) {
			printDot(Calculator.dependencyGraph, name);
			return;
		}

		if (Variables.exists(params[0]))
			mo = Variables.get(params[0]);
		else
			mo = new MExpression(params[0]);

		if (mo instanceof MExpression)
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
			lastTextFileName = findAvailableName("output_text", "txt");
		printText(toText(mo), lastTextFileName, "tex");
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
			String n = System.lineSeparator();
			File f = new File(name + ".tex");
			if (f.exists()) {
				String s = new String(Files.readAllBytes(f.toPath()));
				writer = new PrintWriter(name + ".tex", "UTF-8");
				int index = s.indexOf(latexEnd);
				writer.println(s.substring(0, index == -1 ? 0 : index));
				writer.println(name.replace("_", " \\_") + ":" + n + "\\begin{equation*}" + n + str + n
						+ "\\end{equation*}" + n + latexEnd);
			} else {
				writer = new PrintWriter(f);
				writer.println(latexPreamble + n + name.replace("_", "\\_") + ":\n\\begin{equation*}" + n + str + n
						+ "\\end{equation*}" + n + latexEnd);
			}
			writer.close();
		} catch (IOException e) {
			Calculator.errorHandler.handle(e);
		}
	}

	/**
	 * Prints the given String to a file with the given name. If a file with the
	 * given name already exists, the String will be appended.
	 * 
	 * @param str      the {@code String} to be printed.
	 * @param fileName the name of the file to which <tt>str</tt> will be printed
	 *                 to.
	 * @param ext	   the extension of the file. 
	 */
	private static File printText(String str, String fileName, String ext) {
		try {
			File f = new File(fileName + "." + ext);
			if (f.exists())
				Files.write(f.toPath(), (System.lineSeparator() + str).getBytes(), StandardOpenOption.APPEND);
			else
				Files.write(f.toPath(), str.getBytes());
			return f;
		} catch (IOException | InvalidPathException e) {
			Calculator.errorHandler.handle(e);
			return null;
		}
	}

	public static void export(String arg) {
		String[] args = Parser.getArguments(arg);
		boolean temp = Settings.getBool(Settings.MULTILINE_MATRIX);
		Settings.set(Settings.MULTILINE_MATRIX, false);
		try {
			File f;
			if(args.length==1 && args[0].length() != 0) {
				f = printText(exportAll(), args[0], "cal");
			} if(args.length==0 || (args.length==1 && args[0].length() == 0)) {
				String name = findAvailableName("export", "cal");
				f = printText(exportAll(), name, "cal");
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
				
				StringBuilder export = new StringBuilder();
				HashSet<String> exported = new HashSet<String>();
				for(Iterator<Variable> iter = toBeExported.iterator(); iter.hasNext();) {
					Variable var = iter.next();
					if(!(var.get() instanceof MExpression)) {
						export.append(var.getName() + "=" + var.get().toString() + System.lineSeparator());
						iter.remove();
						exported.add(var.getName());
					}
				}
				if(toBeExported.size()>0) { //there are still expressions (which might depend on other variables) to be exported
					//reset the dfs
					Set<Graph<Variable>.Node> nodes = Calculator.dependencyGraph.getNodes();
					for(Graph<Variable>.Node n : nodes)
						n.start = n.finish = 0;
					int time = 0;
					for(Graph<Variable>.Node n : nodes)
						if(n.start==0 && toBeExported.contains(n.getData()))
							time = DFS(n, time+1, export, exported);
				}
				f = printText(export.toString(), filename, "cal");
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
	
	private static String exportAll() {		
		StringBuilder export = new StringBuilder();
		HashSet<String> exported = new HashSet<String>();
		for(Entry<String, MathObject> entry : Variables.getAll().entrySet()) {
			if(!(entry.getValue() instanceof MExpression)) {
				export.append(entry.getKey() + "=" + entry.getValue().toString() + System.lineSeparator());
				exported.add(entry.getKey());
			}
		}
		for(Graph<Variable>.Node n : Calculator.dependencyGraph.getNodes())
			n.start = n.finish = 0;
		int time = 0;
		for(Graph<Variable>.Node n : Calculator.dependencyGraph.getNodes())
			if(n.start==0)
				time = DFS(n, time+1, export, exported);
		return export.toString();
	}
	
	private static int DFS(Graph<Variable>.Node n, int time, StringBuilder s, HashSet<String> exported) {
		n.start = time;
		for(Graph<Variable>.Edge e : n.getEdges()) {
			if(e.getB().start == 0)
				time = DFS(e.getB(), time+1, s, exported);
		}
		n.finish = ++time;
		if(exported.contains(n.getData().getName())) // already exported so we can skip this one
			return time;
		if(n.getData().get() instanceof MFunction) {
			MFunction f = (MFunction) n.getData().get();
			s.append(n.getData().getName() + "(" + Tools.join(", ", f.getParameters()) + ")" + (f.isDefined() ? ":=" : "=") + ((MExpression) n.getData().get()).toString() + System.lineSeparator());
		} else if(n.getData().get() instanceof MExpression) {
			s.append(n.getData().getName() + ":=" + ((MExpression) n.getData().get()).toString() + System.lineSeparator());
		} else
			s.append(n.getData().getName() + "=" + n.getData().get().toString() + System.lineSeparator());
		exported.add(n.getData().getName());
		return time;
	}
	
	/**
	 * Converts the double value of the {@link MScalar} to a String.
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
			if(Math.abs(num) >= 0.001 && Math.abs(num)<100000) {
				s = String.format("%." + Settings.getInt(Settings.PRECISION) + "f", num);
				while (s.endsWith("0"))
					s = s.substring(0, s.length() - 1);
				if (s.endsWith("."))
					s = s.substring(0, s.length() - 1);
				break;
			}
		case Settings.SCI:
			String format = "0.";
			for (int i = 0; i < Settings.getInt(Settings.PRECISION); i++)
				format += "#";
			if(num<1 || num>=10)
				format += "E0";
			s = new DecimalFormat(format).format(num);
			break;
		case Settings.ENG:
			   // If the value is negative, make it positive so the log10 works
			   double posVal = (num<0) ? -num : num;
			   double log10 = Math.log10(posVal);
			   // Determine how many orders of 3 magnitudes the value is
			   int count = (int) Math.floor(log10/3);
			   // Scale the value into the range 1<=val<1000
			   num /= Math.pow(10, count * 3);
			   s = String.format("%." + Settings.getInt(Settings.PRECISION) + "fe%d", num, count * 3);
			break;
		}
		if(s.equals("-0"))
			return "0";
		return s;
	}

	/**
	 * Finds a numeric suffix <tt>num>0</tt> (an integer bigger than 0) such that a
	 * file with the name{@code name + num + "." + ext} does not exist.
	 * 
	 * @param name the name for which the suffix will be sought.
	 * @param ext  the file extension to the filename.
	 * @return the found filename.
	 */
	public static String findAvailableName(String name, String ext) {
		int num = 0;
		File file;
		if(name.length()==0 || !isPathValid(name + "." + ext)) {
			throw new UnexpectedCharacterException(name + "." + ext + " is not a valid filename.");
		}
		do {
			file = new File(name + num++ + "." + ext);
		} while (file.exists());
		return file.getName().substring(0, file.getName().lastIndexOf("."));
	}

	public static boolean isPathValid(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException ex) {
            return false;
        }
        return true;
    }
}