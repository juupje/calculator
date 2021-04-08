package com.github.juupje.calculator.helpers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import com.github.juupje.calculator.algorithms.Functions.Function;
import com.github.juupje.calculator.graph.Graph;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.main.Variable;
import com.github.juupje.calculator.main.Variables;
import com.github.juupje.calculator.mathobjects.MComplex;
import com.github.juupje.calculator.mathobjects.MConst;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MFunction;
import com.github.juupje.calculator.mathobjects.MIndexedObject;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MRecSequence;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MSequence;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MVectorFunction;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;
import com.github.juupje.calculator.settings.Settings;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;

public class Printer {
	private static final Locale locale = Locale.ROOT;
	private static String lastLatexFileName = null;
	private static String lastTextFileName = null;
	private static final String newLine = System.lineSeparator();
	/**
	 * The latex preamble used for writing stuff to LaTeX files:
	 * 
	 * <pre>
	 * {@code
	 * \documentclass[preview]{standalone}
	 * \'usepackage{amsmath}
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
	private static void printNodeDot(StringBuilder sb, Node<?> node, boolean connectParent) {
		if(node.data instanceof MVectorFunction) {
			MVectorFunction v = (MVectorFunction) node.data;
			String name = "";
			for(int i = 0; i < v.size(); i++) {
				name += i + ", ";
				sb.append(node.hashCode()).append(" -> ").append(i).append(newLine);
				sb.append(i).append(" -> ").append(v.get(i).getTree().getRoot().hashCode()).append(newLine);
				printNodeDot(sb, v.get(i).getTree().getRoot(), connectParent);
			}
			sb.append(node.hashCode()).append("[label=\"(").append(name.substring(0, name.length()-2)).append(")\"];").append(newLine);			
			return;
		}
		
		sb.append(node.hashCode()).append("[label=<").append(node.toHTMLLabel()).append(">, tooltip=\"")
			.append(Tools.type(node)).append("<").append(Tools.type(node.data)).append(">").append("\"];").append(newLine);
		
		if (node.left() != null) {
			sb.append(node.hashCode()).append(" -> ").append(node.left().hashCode()).append(";").append(newLine);
			printNodeDot(sb,node.left(), connectParent);
		}
		if (node.right() != null) {
			sb.append(node.hashCode()).append(" -> ").append(node.right().hashCode()).append(";").append(newLine);
			printNodeDot(sb, node.right(), connectParent);
		}
		if(node.parent != null && connectParent)
			sb.append(node.hashCode()).append(" -> ").append(node.parent.hashCode()).append(" [style=dashed];").append(newLine);
		
	}

	/**
	 * Prints the given <tt>Tree</tt> in a Dot format to a file with the
	 * given name. The method uses {@link #printNodeDot(Node)} to print the nodes.
	 * 
	 * @param tree the {@link Tree} to be printed.
	 * @param name the name of the Dot file (excluding the .dot extension).
	 * @see #printDot(Node)
	 */
	public static void printDot(Tree tree, String name) {
		printDot(tree, name, false);
	}
	
	/**
	 * Prints the given <tt>Tree</tt> in a Dot format to a file with the
	 * given name. The method uses {@link #printNodeDot(Node)} to print the nodes.
	 * 
	 * @param tree the {@link Tree} to be printed.
	 * @param name the name of the Dot file (excluding the .dot extension).
	 * @param connectParent draws connections from nodes to their parents
	 * @see #printDot(Node)
	 */
	public static void printDot(Tree tree, String name, boolean connectParent) {
			StringBuilder sb = new StringBuilder();
			sb.append("digraph tree {"+newLine).append("node [fontname=\"Arial\"];"+newLine);
			if (tree.root == null)
				sb.append(newLine);
			else if (!tree.root.isInternal() && !(tree.root.data instanceof MVectorFunction))
				sb.append(tree.root.toString()).append(newLine);
			else
				printNodeDot(sb, tree.root, connectParent);
			sb.append("labelloc=\"t\"\nlabel=\"").append(name).append("\"\n}").append(newLine);
		try {
			Calculator.ioHandler.writeToFile(name + ".dot", sb, false);
		} catch (IOException e) {
			Calculator.errorHandler.handle(e);
		}
	}
	
	/**
	 * Prints the given <tt>MExpression</tt> in a Dot format to a file with the
	 * given name. The method uses {@link #printNodeDot(Node)} to print the nodes.
	 * 
	 * @param expr the {@link MExpression} to be printed.
	 * @param name the name of the Dot file (excluding the .dot extension).
	 * @see #printDot(Node)
	 */
	public static void printDot(MExpression expr, String name) {
		printDot(expr.getTree(), name);
	}

	public static void printDot(Graph<?> g, String name) {
			StringBuilder sb = new StringBuilder();
			sb.append("digraph dependencies {").append(newLine);
			for (Graph<?>.Node n : g.getNodes()) {
				sb.append(n.hashCode()).append("[label=\"").append(n.toString()).append("\"];").append(newLine);
				for (Graph<?>.Edge e : n.getEdges())
					if(e.getA().equals(n))
						sb.append(e.getA().hashCode()).append("->").append(e.getB().hashCode()).append(newLine);
			}
			sb.append("}").append(newLine);
		try {
			Calculator.ioHandler.writeToFile(name+".dot", sb, false);
		} catch (IOException e) {
			Calculator.errorHandler.handle(e);
		}
	}

	private static String printNodeLatex(Node<?> n) {
		StringBuilder sb = new StringBuilder();
		printNodeLatex(sb, n);
		return sb.toString();
	}
	
	/**
	 * Recursively converts the given node and its children to a LaTeX format which
	 * is then returned as a <tt>String</tt>. *
	 * 
	 * @param node the {@link Node} to be converted to LaTeX.
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 */
	private static void printNodeLatex(StringBuilder sb, Node<?> n) {
		if (n.data instanceof MConst) {
			toLatex(sb,(MConst)n.data);
		} else if (n.data instanceof Operator) {
			Operator op = (Operator) n.data;
			switch (op) {
			case ADD:
			case SUBTRACT:
				printNodeLatex(sb, n.left());
				sb.append((op == Operator.ADD ? "+" : "-"));
				printNodeLatex(sb, n.right());
				break;
			case MULTIPLY:
				printNodeLatex(sb, n.left());
				String s2 = printNodeLatex(n.right()); //because this cannot be appended directly
				boolean brackets = n.right().data.equals(Operator.ADD) || n.right().data.equals(Operator.SUBTRACT);//s2.contains("-") || s2.contains("+");
				if (brackets)
					s2 = "\\left(" + s2 + "\\right)";
				if (n.left().isNumeric() && !Character.isDigit(s2.charAt(0)))
					// there needn't be a multiplication dot between a number and a variable.
					sb.append(s2);
				else
					sb.append("\\cdot ").append(s2);
				break;
			case DIVIDE:
				sb.append("\\frac{");
				printNodeLatex(sb, n.left());
				sb.append("}{");
				printNodeLatex(sb, n.right());
				sb.append("}");
				break;
			case POWER:
				if (n.left().isInternal()) {
					sb.append("\\left(");
					printNodeLatex(sb, n.left());
					sb.append("\\right)");
				} else
					printNodeLatex(sb, n.left());
				sb.append("^{");
				printNodeLatex(sb, n.right());
				sb.append("}");
				break;
			case MOD:
				printNodeLatex(sb, n.left());
				sb.append("\\bmod ");
				printNodeLatex(sb, n.right());
				break;
			case NEGATE:
				sb.append("-");
				printNodeLatex(sb, n.left());
				break;
			case INVERT:
				if(n.left().isInternal()) {
					sb.append("\\left(");
					printNodeLatex(sb, n.left());
					sb.append("\\right)");
				} else
					printNodeLatex(sb, n.left());
				sb.append("^{-1}");
				break;
			case ELEMENT:
				printNodeLatex(sb, n.left());
				sb.append("_{");
				printNodeLatex(sb, n.right());
				sb.append("}");
				break;
			case CONJUGATE:
				printNodeLatex(sb, n.left());
				sb.append("^*");
			default:
				break;
			}
		} else if (n.data instanceof Function) {
			switch ((Function) n.data) {
			case SQRT:
				sb.append("\\sqrt{");
				printNodeLatex(sb, n.left());
				sb.append("}");
				break;
			case ABS:
				sb.append("\\left|");
				printNodeLatex(sb, n.left());
				sb.append("\\right|");
				break;
			case SIN:
			case COS:
			case TAN:
			case LN:
			case LOG:
				sb.append("\\").append(n.data.toString()).append("\\left(");
				printNodeLatex(sb, n.left());
				sb.append("\\right)");
				break;
			case ASIN:
			case ACOS:
			case ATAN:
				sb.append("\\arc").append(n.data.toString().substring(1)).append("\\left(");
				printNodeLatex(sb, n.left());
				sb.append("\\right)");
				break;
			default:
				sb.append("\\operatorname{").append(n.data.toString()).append("}{\\left(");
				printNodeLatex(sb, n.left());
				sb.append("\\right)}");
			}
		} else if (n.data instanceof MathObject)
			toLatex(sb, (MathObject) n.data);
		else
			sb.append(n.toString());
	}

	private static void toLatex(StringBuilder sb, MConst c) {
		switch (c) {
		case pi:
			sb.append("\\pi ");
		case _sigma:
			sb.append("\\sigma ");
		case _alpha:
			sb.append("\\alpha ");
		case _b_w:
			sb.append("b_w");
		case _NA:
			sb.append("N_A");
		case _epsilon0:
			sb.append("\\epsilon_0");
		case _mu0:
			sb.append("\\mu_0");
		case _hbar:
			sb.append("\\hbar ");
		case _m_e:
		case _m_n:
		case _m_p:
			sb.append(c.name().toLowerCase().substring(1));
		default:
			String name = c.name();
			if(name.startsWith("_"))
				name = name.substring(1);
			sb.append(name.toLowerCase());
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
	private static void toLatex(StringBuilder sb, MExpression expr) {
		Tree tree = expr.getTree();
		if (tree.root.isInternal())
			printNodeLatex(sb, tree.root);
		else
			sb.append(tree.root.toString());
	}

	/**
	 * Converts the given <tt>MVector</tt> to a LaTeX representation and returns it
	 * as a <tt>String</tt>, using the <tt>pmatrix</tt> environment.
	 * 
	 * @param v the {@link MVector} to be converted to LaTeX.
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 */
	private static void toLatex(StringBuilder sb, MVector v) {
		sb.append("\\begin{pmatrix}");
		String s = v.isTransposed() ? "&" : "\\\\";
		for (MathObject mo : v.elements()) {
			toLatex(sb, mo);
			sb.append(s);
		}
		sb.setLength(sb.length() - s.length());
		sb.append("\\end{pmatrix}");
	}

	/**
	 * Converts the given <tt>MMatrix</tt> to a LaTeX representation and returns it
	 * as a <tt>String</tt>, using the <tt>pmatrix</tt> environment.
	 * 
	 * @param m the {@link MMatrix} to be converted to LaTeX.
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 */
	private static void toLatex(StringBuilder sb, MMatrix m) {
		sb.append("\\begin{pmatrix}").append(newLine);
		for (MathObject[] row : m.elements()) {
			for (MathObject mo : row) {
				toLatex(sb, mo);
				sb.append(" & ");
			}
			//cut off the last "& " and add line separator.
			sb.setLength(sb.length() - 2);
			sb.append("\\\\");
		}
		sb.append(newLine).append("\\end{pmatrix}");
	}
	
	private static void toLatex(StringBuilder sb, MIndexedObject m) {
		if(m.shape().dim()==2) {
			sb.append("\\begin{pmatrix}").append(newLine);
			MathObject[] values = m.elements();
			int cols = m.shape().cols();
			for (int i = 0; i < values.length; i++) {
				toLatex(sb, values[i]);
				if(i+1==values.length) continue;
				if((i+1)%cols==0)
					sb.append(" \\\\ ");
				else
					sb.append(" & ");
			}
			sb.append(newLine).append("\\end{pmatrix}");
		} else if(m.shape().dim()<=1) {
			sb.append("\\begin{pmatrix}").append(newLine);
			MathObject[] values = m.elements();
			for(int i = 0; i < values.length; i++) {
				toLatex(sb, values[i]);
				if(i != values.length-1)
					sb.append(" & ");
			}
			sb.append(newLine).append("\\end{pmatrix}");			
		} else {
			final String[] indices = {"alpha", "beta", "gamma", "delta", "rho", "sigma", "mu", "nu", "kappa", "lambda"};
			StringBuilder shape = new StringBuilder();
			sb.append("\\{a_{");
			int indexCount = m.shape().size();
			int toberepeated = (indexCount>2*indices.length ? indices.length : (indexCount<indices.length ? 0 : indexCount%indices.length));
			for(int i = 0; i < indexCount; i++) {
				int index = i%indices.length;
				sb.append("\\"+indices[index]);
				shape.append(m.shape().get(i));
				if(index<toberepeated)
					sb.append("_{"+((int) i/indices.length) + "}");
				if(i != indexCount-1) {
					shape.append("\\times");
				}
			}
			sb.append("}\\}\\quad(").append(shape).append(")");
		}
	}
	
	private static void toLatex(StringBuilder sb, MSequence m) {
		String name = m.getIndexName()=="a" ? "b" : "a";
		String indexedName = name + "_{" + m.getIndexName() + "}";
		sb.append("(").append(indexedName).append(")_{").append(m.getIndexName()).append("\\in\\mathbb{N}}\\qquad ");
		if(m instanceof MRecSequence) {
			String fstring = toLatex(m.getFunction());
			sb.append(indexedName).append("=").append(fstring.replace("__", name + "_"));
		} else {
			sb.append(indexedName).append("=");
			toLatex(sb, m.getFunction());
		}
	}

	/**
	 * Calls the <tt>toLatex()</tt> method which corresponds with the type of
	 * <tt>mo</tt>
	 * 
	 * @param mo The mathobject to be converted to LaTeX
	 * @return a <tt>String</tt> containing the argument in LaTeX format.
	 */
	private static void toLatex(StringBuilder sb, MathObject mo) {
		if (mo instanceof MVector)
			toLatex(sb, (MVector) mo);
		else if (mo instanceof MMatrix)
			toLatex(sb, (MMatrix) mo);
		else if (mo instanceof MIndexedObject) 
			toLatex(sb, (MIndexedObject) mo);
		else if (mo instanceof MExpression)
			toLatex(sb, (MExpression) mo);
		else if (mo instanceof MReal)
			sb.append(numToLatex((MReal) mo));
		else if (mo instanceof MComplex)
			sb.append(numToLatex((MComplex) mo));
		else if(mo instanceof MSequence)
			toLatex(sb, (MSequence) mo);
		else
			throw new IllegalArgumentException("Can't export " + Tools.type(mo) + " to LaTex");
	}
	
	/**
	 * Converts a mathobject to a LaTeX string of that object.
	 * The method uses {@link #toLatex(StringBuilder, MathObject)} for this task.
	 * 
	 * @param mo The mathobject to be converted to LaTeX
	 * @return a String containing the LaTeX code
	 */
	public static String toLatex(MathObject mo) {
		StringBuilder sb = new StringBuilder();
		toLatex(sb, mo);
		return sb.toString();
	}

	public static String nodeToText(Node<?> n) {
		StringBuilder sb = new StringBuilder();
		nodeToText(sb, n);
		return sb.toString();
	}
	
	/**
	 * Recursively converts the given {@link Node} and its children to a text
	 * representation.
	 * 
	 * @param node the {@link Node} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	private static void nodeToText(StringBuilder sb, Node<?> n) {
		if (n.data instanceof MConst)
			sb.append(((MConst) n.data).name());
		else if (n.data instanceof Operator) {
			Operator op = (Operator) n.data;
			switch (op) {
			case ADD:
			case SUBTRACT:
				nodeToText(sb, n.left());
				sb.append(op == Operator.ADD ? "+" : "-");
				nodeToText(sb, n.right());
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
				if (n.left().isNumeric() && s2.charAt(0)!='-' && !Character.isDigit(s2.charAt(0)))
					// there needn't be a multiplication dot between a number and a variable.
					sb.append(s1).append(s2);
				else
					sb.append(s1).append("*").append(s2);
				break;
			case DIVIDE:
			case POWER:
				String right = nodeToText(n.right());
				String left = nodeToText(n.left());
				if (n.right().right() != null) //Brackets are needed
					right = "(" + right + ")";
				if(n.left().right() != null)
					left = "(" + left + ")";
				sb.append(left).append(op == Operator.POWER ? "^" : "/").append(right);
				break;
			case MOD:
				sb.append("mod[");
				nodeToText(sb, n.left());
				sb.append(", ");
				nodeToText(sb, n.right());
				sb.append("]");
				break;
			case NEGATE:
				sb.append("-");
				nodeToText(sb, n.left());
				break;
			case INVERT:
				if(n.left().isInternal()) {
					sb.append("(");
					nodeToText(sb, n.left());
					sb.append(")");
				} else
					nodeToText(sb, n.left());
				sb.append("^-1");
				break;
			case ELEMENT:
				nodeToText(sb, n.left());
				sb.append("[");
				nodeToText(sb, n.right());
				sb.append("]");
				break;
			case CONJUGATE:
				if(n.left().isInternal()) {
					sb.append("&(");
					nodeToText(sb, n.left());
					sb.append(")");
				} else {
					sb.append("&");
					nodeToText(sb, n.left());
				}
				break;
			default:
				break;
			}
		} else if (n.data instanceof Function) {
			sb.append(n.data).append("(");
			nodeToText(sb, n.left());
			sb.append(")");
		} else
			sb.append(n.toString());
	}

	/**
	 * Converts the given <tt>MExpression</tt> to a text format and returns it as a
	 * <tt>String</tt>. This is done using {@link #nodeToText(Node)}.
	 * 
	 * @param mo the {@link MExpression} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	public static void toText(StringBuilder sb, MExpression mo) {
		Tree tree = mo.getTree();
		if (tree.root.isInternal())
			nodeToText(sb, tree.root);
		else
			sb.append(tree.root.toString());
	}
	
	public static void toText(StringBuilder sb, MVector mo) {
		toText(sb, mo.elements());
	}
	
	public static void toText(StringBuilder sb, MMatrix mo) {
		toText(sb, mo.elements());
	}

	public static void toText(StringBuilder sb, MIndexedObject mo) {
		Shape shape = mo.shape();
		if(shape.dim()==0 || shape.dim()==1)
			toText(sb, mo.elements());
		else if(shape.dim()==2)
			toMatrixString(sb, mo.elements(), 0, shape.rows(), shape.cols());
		else if(Settings.getBool(Settings.MULTILINE_MATRIX) && shape.dim()==3) {
			int k = shape.get(0);
			sb.append("[\n");
			int rows = shape.get(1);
			int cols = shape.get(2);
			for(int i = 0; i < k; i++) {
				toMatrixString(sb, mo.elements(), k*rows*cols, rows, cols);
				sb.append(",\n");
			}
			sb.append("]");
		} else {
			sb.append("Shape ").append(shape.toString()).append("\n[").append(Tools.join(", ",(Object[]) mo.elements())).append("]");
		}
	}
	
	/**
	 * Converts the given <tt>MExpression</tt> to a text format and returns it as a
	 * <tt>String</tt>. This is done using {@link #nodeToText(Node)}.
	 * 
	 * @param mo the {@link MExpression} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	public static void toText(StringBuilder sb, MSequence mo) {
		sb.append("{");
		toText(sb, mo.getFunction());
		sb.append(" | ").append(mo.getIndexName()).append("=").append(mo.getBegin())
			.append("...").append(mo.getEnd()>= 0 ? mo.getEnd() : "infinity").append("}");
	}
	
	public static void toText(StringBuilder sb, MRecSequence mo) {
		sb.append("r{[").append(mo.getIndexName()).append("]=").append(toText(mo.getFunction()).replace("_", ""));
		sb.append(" | ");
		for(int i = 0; i < mo.getInitalParameterCount(); i++) {
			if(i>0)
				sb.append(", "); 
			sb.append("[").append(i).append("]=").append(mo.get(i).toString());
		}
		sb.append("}");
	}	
	
	private static void toText(StringBuilder sb, MathObject[][] m) {
		toText(sb, m, 0, m.length-1, 0, m[0].length-1);
	}
	
	public static void toText(StringBuilder sb, Double[][] m, int rstart, int rend, int cstart, int cend) {
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
			for(int i = 0; i < rows; i++) {
				String row = "  ";
				for(int j = 0; j < cols; j++) {
					row += s[i][j];
					for(int k = s[i][j].length(); k < colmax[j]; k++)
						row += " ";
				}
				if(i==0)
					sb.append("/").append(row).append("\\").append(newLine);
				else if(i==rows-1)
					sb.append("\\").append(row).append("/");
				else
					sb.append("|").append(row).append("|").append(newLine);
			}
		} else {
			sb.append("[");
			for(int i = rstart; i <= rend; i++) {
				for (int j = cstart; j <= cend; j++)
					sb.append(numToString(m[i][j])).append(", ");
				sb.setLength(sb.length() - 2);
				sb.append(";");
			}
			sb.setLength(sb.length() - 1);
			sb.append("]");
		}
	}
	
	public static void toText(StringBuilder sb, MathObject[][] m, int rstart, int rend, int cstart, int cend) {
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
			for(int i = 0; i < rows; i++) {
				String row = "  ";
				for(int j = 0; j < cols; j++) {
					row += s[i][j];
					for(int k = s[i][j].length(); k < colmax[j]; k++)
						row += " ";
				}
				if(i==0)
					sb.append("/").append(row).append("\\");
				else if(i==rows-1)
					sb.append(newLine).append("\\").append(row).append("/");
				else
					sb.append(newLine).append("|").append(row).append("|");
			}
		} else {
			sb.append("[");
			for(int i = rstart; i <= rend; i++) {
				for (int j = cstart; j <= cend; j++)
					sb.append(toText(m[i][j])).append(", ");
				sb.setLength(sb.length() - 2);
				sb.append(";");
			}
			sb.setLength(sb.length() - 1);
			sb.append("]");
		}
	}
	
	private static void toMatrixString(StringBuilder sb, MathObject[] v, int start, int rows, int cols) {
		if(Settings.getBool(Settings.MULTILINE_MATRIX)) {
			String[][] s = new String[rows][cols];
			int[] colmax = new int[cols];
			for(int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++) {
					s[i][j] = v[i*cols+j+start].toString();
					colmax[j] = Math.max(colmax[j], s[i][j].length()+2);
				}
			for(int i = 0; i < rows; i++) {
				StringBuilder row = new StringBuilder("  ");
				for(int j = 0; j < cols; j++) {
					row.append(s[i][j]);
					for(int k = s[i][j].length(); k < colmax[j]; k++)
						row.append(" ");
				}
				if(i==0)
					sb.append("/").append(row).append("\\");
				else if(i==rows-1)
					sb.append(newLine).append("\\").append(row).append("/");
				else
					sb.append(newLine).append("|").append(row).append("|");
			}
		} else {
			sb.append("[");
			int len = cols*rows;
			for(int i = 0; i < len; i++) {
				toText(sb, v[start+i]);
				if(i!=len-1) {
					if((i+1)%cols==0)
						sb.append("; ");
					else 
						sb.append(", ");
				}
			}
			sb.append("]");
		}
	}
	
	private static void toText(StringBuilder sb, MathObject[] vector) {
		sb.append("(");
		for(int i = 0; i < vector.length; i++) {
			toText(sb, vector[i]);
			if(i!=vector.length-1)
				sb.append(", ");
			if(Settings.getBool(Settings.MULTILINE_MATRIX) && vector[i] instanceof MMatrix)
				sb.append(newLine);
		}
		sb.append(")");
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
		StringBuilder sb = new StringBuilder();
		toText(sb, mo);
		return sb.toString();
	}
	
	public static void toText(StringBuilder sb, MathObject mo) {
		if (mo instanceof MVector)
			toText(sb, (MVector) mo);
		else if (mo instanceof MMatrix)
			toText(sb, (MMatrix) mo);
		else if (mo instanceof MIndexedObject) 
			toText(sb, (MIndexedObject) mo);
		else if (mo instanceof MExpression)
			toText(sb, (MExpression) mo);
		else if (mo instanceof MReal)
			sb.append(numToString((MReal) mo));
		else if (mo instanceof MComplex)
			sb.append(numToString((MComplex) mo));
		else if(mo instanceof MRecSequence)
			toText(sb, (MRecSequence) mo);
		else if(mo instanceof MSequence)
			toLatex(sb, (MSequence) mo);
		else
			throw new IllegalArgumentException("Can't export " + Tools.type(mo) + " to text");
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
			lastLatexFileName = Calculator.ioHandler.findAvailableName("output_latex", "tex");
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
		for(int i = 0; i < params.length; i++)
			params[i] = params[i].trim();
		MathObject mo;
		String name;
		if (params.length == 2)
			name = params[1];
		else
			name = Calculator.ioHandler.findAvailableName("output_dot", "dot");

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
			Calculator.ioHandler.writeToFile(lastTextFileName, toText(mo), false);
		} catch(IOException e) {
			Calculator.errorHandler.handle("Failed to write to file", e);
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
				int index = s.indexOf(latexEnd);
				sb.append(s.substring(0, index == -1 ? 0 : index)+newLine);
				sb.append(name.replace("_", " \\_")).append(":").append(newLine)
					.append("\\begin{equation*}").append(newLine).append(str).append(newLine)
					.append("\\end{equation*}").append(newLine).append(latexEnd);
			} else {
				sb.append(latexPreamble).append(newLine).append(name.replace("_", "\\_"))
					.append(":\n\\begin{equation*}").append(newLine).append(str).append(newLine)
					.append("\\end{equation*}").append(newLine).append(latexEnd);
			}
			Calculator.ioHandler.writeToFile(f, sb, false);
		} catch (IOException e) {
			Calculator.errorHandler.handle(e);
		}
	}

	public static void export(String arg) {
		String[] args = Parser.getArguments(arg);
		boolean temp = Settings.getBool(Settings.MULTILINE_MATRIX);
		Settings.set(Settings.MULTILINE_MATRIX, false);
		try {
			File f;
			if(args.length==1 && args[0].length() != 0) {
				f = Calculator.ioHandler.writeToFile(args[0] + ".cal", exportAll(), false);
			} if(args.length==0 || (args.length==1 && args[0].length() == 0)) {
				String name = Calculator.ioHandler.findAvailableName("export", "cal");
				f = Calculator.ioHandler.writeToFile(name + ".cal", exportAll(), false);
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
	
	public static StringBuilder exportAll() {		
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
		return export;
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
	
	/**
	 * Converts the value of the {@link MScalar} to a LaTeX string. Takes into account the setting
	 * COMPLEX_IN_POLAR if the value is complex.
	 * 
	 * @param scalar the {@code MScalar} to be converted to LaTeX.
	 * @return the created String.
	 */
	public static String numToLatex(MScalar s) {
		if(s.isNaN())
			return "NaN";
		if (s.isComplex()) {
			((MComplex) s).fixPhi();
			if (Settings.getBool(Settings.COMPLEX_IN_POLAR))
				return numToString(((MComplex) s).getR()) + "\\cdot e^{" + numToString(((MComplex) s).arg()) + "i}";
			else {
				String stra = numToLatex(s.real());
				String strb = numToLatex(s.imag());
				if(stra.equals("0") && strb.equals("0"))
					return "0";
				return (!stra.equals("0") ? stra : "") + (!strb.equals("0") ? (strb.startsWith("-") ? "" : (stra.equals("0") ? "" : "+")) + 
						(strb.equals("1") ? "" : (strb.equals("-1") ? "-" : strb)) + "i" : "");
			}
		}
		return numToLatex(s.real());
	}
	
	/**
	 * Converts the double to a LaTeX string. Using the number representation settings in
	 * {@link Settings}. It uses the {@link #numToString(double)} method and replaces the exponential e{...}
	 * with \cdot10^{...}
	 * 
	 * @param double the value to be converted.
	 * @return the created String.
	 */
	public static String numToLatex(double d) {
		String str = numToString(d);
		int index = str.indexOf("e");
		if(index == -1)
			return str;
		String exp = str.substring(index+1);
		return str.substring(0, index)+"\\cdot10^{"+(exp.charAt(0)=='+' ? exp.substring(1) : exp)+"}";
	}
}