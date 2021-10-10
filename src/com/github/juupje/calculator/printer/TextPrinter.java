package com.github.juupje.calculator.printer;

import com.github.juupje.calculator.algorithms.functions.Function;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.mathobjects.MConst;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MIndexedObject;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MRecSequence;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MSequence;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;
import com.github.juupje.calculator.settings.Settings;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;

public class TextPrinter {
	
	private static final String newLine = System.lineSeparator();
	
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
	
	/**
	 * Converts the given <tt>MVector</tt> to a text format and returns it as a
	 * <tt>String</tt>.
	 * 
	 * @param mo the {@link MVector} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	public static void toText(StringBuilder sb, MVector mo) {
		toText(sb, mo.elements());
		if(!mo.isTransposed())
			 //non-transposed vectors are column vectors, however the vector is printed as a row
			sb.append("'");
	}
	
	/**
	 * Converts the given <tt>MMatrix</tt> to a text format and returns it as a
	 * <tt>String</tt>.
	 * 
	 * @param mo the {@link MMatrix} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	public static void toText(StringBuilder sb, MMatrix mo) {
		toText(sb, mo.elements());
	}

	/**
	 * Converts the given <tt>MIndexedObject</tt> to a text format and returns it as a
	 * <tt>String</tt>.
	 * 
	 * @param mo the {@link MIndexedObject} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
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
				toMatrixString(sb, mo.elements(), i*rows*cols, rows, cols);
				sb.append(",\n");
			}
			sb.append("]");
		} else {
			sb.append("Shape ").append(shape.toString()).append("\n[").append(Tools.join(", ",(Object[]) mo.elements())).append("]");
		}
	}
	
	/**
	 * Converts the given <tt>MSequence</tt> to a text format and returns it as a
	 * <tt>String</tt>.
	 * 
	 * @param mo the {@link MSequence} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	public static void toText(StringBuilder sb, MSequence mo) {
		sb.append("{");
		toText(sb, mo.getFunction());
		sb.append(" | ").append(mo.getIndexName()).append("=").append(mo.getBegin())
			.append("...").append(mo.getEnd()>= 0 ? mo.getEnd() : "infinity").append("}");
	}
	
	/**
	 * Converts the given <tt>MRecSequence</tt> to a text format and returns it as a
	 * <tt>String</tt>.
	 * 
	 * @param mo the {@link MRecSequence} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	public static void toText(StringBuilder sb, MRecSequence mo) {
		sb.append("r{[").append(mo.getIndexName()).append("]=").append(toText(mo.getFunction()).replace("_", ""));
		//we can replace _ as it is used for the sequence itself: the variable name contains only a-zA-Z
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
					s[i][j] = Printer.numToString(m[i+rstart][j+cstart]);
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
					sb.append(Printer.numToString(m[i][j])).append(", ");
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
	 * <tt>String</tt>. This is done by simply calling {@link TextPrinter#toText(StringBuilder, MathObject)}
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
		else if (mo instanceof MScalar)
			sb.append(Printer.numToString((MScalar) mo));
		else if(mo instanceof MRecSequence)
			toText(sb, (MRecSequence) mo);
		else if(mo instanceof MSequence)
			toText(sb, (MSequence) mo);
		else
			throw new IllegalArgumentException("Can't export " + Tools.type(mo) + " to text");
	}
}
