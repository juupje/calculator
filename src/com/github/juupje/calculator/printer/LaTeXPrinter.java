package com.github.juupje.calculator.printer;

import com.github.juupje.calculator.algorithms.Functions.Function;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.mathobjects.MComplex;
import com.github.juupje.calculator.mathobjects.MConst;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MFraction;
import com.github.juupje.calculator.mathobjects.MIndexedObject;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MRecSequence;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MSequence;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.settings.Settings;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;

public class LaTeXPrinter {
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
	static final String latexPreamble = "\\documentclass[preview]{standalone}\n\\usepackage{amsmath}\n\\usepackage{amssymb}"
			+ "\\author{Calculator - By Joep Geuskens}\n\\newcommand{\\func}[1]{\\mathrm{#1}}\n\\begin{document}";
	static final String latexEnd = "\\end{document}";

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
				return Printer.numToString(((MComplex) s).getR()) + "\\cdot e^{" + Printer.numToString(((MComplex) s).arg()) + "i}";
			else {
				String stra = numToLatex(s.real());
				String strb = numToLatex(s.imag());
				if(stra.equals("0") && strb.equals("0"))
					return "0";
				return (!stra.equals("0") ? stra : "") + (!strb.equals("0") ? (strb.startsWith("-") ? "" : (stra.equals("0") ? "" : "+")) + 
						(strb.equals("1") ? "" : (strb.equals("-1") ? "-" : strb)) + "i" : "");
			}
		} else if(s.isFraction()) {
			MFraction frac = (MFraction) s;
			return "\\frac{"+frac.getNominator()+"}{"+frac.getDenominator()+"}";
		} else if(s.hasError()) {
			//TODO: implement a conversion to LaTeX string
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
		String str = Printer.numToString(d);
		int index = str.indexOf("e");
		if(index == -1)
			return str;
		String exp = str.substring(index+1);
		return str.substring(0, index)+"\\cdot10^{"+(exp.charAt(0)=='+' ? exp.substring(1) : exp)+"}";
	}
}
