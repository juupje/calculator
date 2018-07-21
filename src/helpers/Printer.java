package helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import algorithms.Functions.Function;
import main.Operator;
import main.Variables;
import mathobjects.MConst;
import mathobjects.MExpression;
import mathobjects.MMatrix;
import mathobjects.MScalar;
import mathobjects.MVector;
import mathobjects.MathObject;
import tree.Node;
import tree.Tree;

public class Printer {

	private static PrintWriter writer;
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

	/**
	 * Recursively prints the given node and its children to the LaTeX file which is
	 * currently being written. The printed result be like this:
	 * 
	 * @param node
	 *            the {@link Node} to be printed.
	 */
	public static String printNodeLatex(Node<?> n) {
		String s = "";
		if (n.data instanceof MConst) {
			String name = ((MConst) n.data).name();
			switch (name) {
			case "PI":
				s += "\\pi";
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
					s += printNodeLatex(n.left()) + "\\cdot" + s2;
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
				s += printNodeLatex(n.left()) + "\\bmod" + printNodeLatex(n.right());
				break;
			case NEGATE:
				s += "-" + printNodeLatex(n.left());
				break;
			case INVERT:
				s += "\\frac{1}{" + printNodeLatex(n.left()) + "}";
				break;
			}
		} else if (n.data instanceof Function) {
			String s2 = printNodeLatex(n.left());
			String name = ((Function) n.data).name();
			s += "\\" + name + "{" + (s2.length() > 2 && !name.equals("sqrt") ? "(" + s2 + ")" : s2) + "}";
		} else
			s += n.toString();
		return s;

	}

	/**
	 * Prints the given <tt>MExpression</tt> in a LaTeX format to a file with the given name. The
	 * method uses {@link #printNodeLatex(Node)} to print the nodes.
	 * 
	 * @param tree
	 *            the {@link Tree} to be printed.
	 * @param name
	 *            the name of the LaTeX file (excluding the .tex extension).
	 * @see #printLatex(Node)
	 */
	public static String toLatex(MExpression expr) {
		Tree tree = expr.getTree();
		if (tree.root.isInternal())
			return printNodeLatex(tree.root);
		else
			return tree.root.toString();
	}
	
	/**
	 * Prints the given <tt>MVector</tt> in a LaTeX format to a file with the given name.	 * 
	 * @param v
	 *            the {@link MVector} to be printed.
	 * @param name
	 *            the name of the LaTeX file (excluding the .tex extension).
	 */
	public static String toLatex(MVector v) {
		String latex = "\\begin{pmatrix}";
		for(MathObject mo : v.elements())
			latex += toLatex(mo) + "\\\\";
		return latex + "\\end{pmatrix}";
	}
	
	/**
	 * Prints the given <tt>MVector</tt> in a LaTeX format to a file with the given name.	 * 
	 * @param v
	 *            the {@link MVector} to be printed.
	 * @param name
	 *            the name of the LaTeX file (excluding the .tex extension).
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
	 * Calls the <tt>toLatex</tt> method which corresponds with the type of <tt>mo</tt>
	 * @param mo
	 * @return
	 */
	public static String toLatex(MathObject mo) {
		System.out.println(mo.toString() + " : " + mo.getClass());
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
	public static void latex(String args) {
		String[] params = args.split(",");
		MathObject mo;
		String name;
		if (Variables.exists(params[0]))
			mo = Variables.get(params[0]);
		else
			mo = new MExpression(params[0]);

		if (params.length == 2)
			name = params[1].trim();
		else
			name = findAvailableName("output_latex", "tex");
		printLatexEq(toLatex(mo), name);
	}
	
	private static void printLatexEq(String eq, String name) {
		try {
			writer = new PrintWriter(name + ".tex", "UTF-8");
			String n = System.lineSeparator();
			writer.println(latexPreamble + n
					+ name.replace("_", "\\_") + ":\n\\begin{equation*}" + n
					+ eq + n
					+ "\\end{equation*}" + n
					+ latexEnd);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
		if(Variables.exists(params[0]))
			mo = Variables.get(params[0]);
		else
			mo = new MExpression(params[0]);
		
		if(params.length == 2)
			name = params[1];
		else
			name = findAvailableName("output_dot", "dot");
		if(mo instanceof MExpression)
			printDot((MExpression) mo, name);
		else
			throw new IllegalArgumentException("Can't export " + mo.getClass() + " in a Dot format.");
	}
	
	public static String findAvailableName(String name, String ext) {
		int num = 0;
		File file;
		do {
			file = new File(name + num++ + "." + ext);
		} while (file.exists());
		return file.getName().substring(0, file.getName().lastIndexOf("."));
	}
}