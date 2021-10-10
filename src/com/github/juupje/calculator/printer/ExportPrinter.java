package com.github.juupje.calculator.printer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import com.github.juupje.calculator.algorithms.functions.Function;
import com.github.juupje.calculator.graph.Graph;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.main.Variable;
import com.github.juupje.calculator.main.Variables;
import com.github.juupje.calculator.mathobjects.MConst;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MFunction;
import com.github.juupje.calculator.mathobjects.MIndexedObject;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MRecSequence;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MSequence;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MVectorFunction;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.tree.Node;

public class ExportPrinter {
	
	private static void export(StringBuilder sb, MathObject mo) {
		if(mo instanceof MScalar)
			sb.append(Printer.numToString((MScalar) mo).toUpperCase());
		else if(mo instanceof MVector) {
			MathObject[] v = ((MVector) mo).elements();
			sb.append('[');
			for(int i = 0; i < v.length; i++) {
				export(sb, v[i]);
				sb.append(',');
			}
			sb.setLength(sb.length()-1);
			sb.append(']');
			if(((MVector) mo).isTransposed())
				sb.append("'");
		} else if(mo instanceof MMatrix) {
			MathObject[][] m = ((MMatrix)mo).elements();
			sb.append('[');
			for(int i = 0; i < m.length; i++) {
				for(int j = 0; j < m[0].length; j++) {
					export(sb, m[i][j]);
					if(j < m[0].length-1)
						sb.append(',');
				}
				if(i < m.length-1)
					sb.append(';');
			}
			sb.append(']');
		} else if(mo instanceof MIndexedObject) {
			MathObject[] v = ((MIndexedObject) mo).elements();
			sb.append('[');
			for(int i = 0; i < v.length; i++) {
				export(sb, v[i]);
				sb.append(",");
			}
			sb.setLength(sb.length()-1);
			sb.append(']').append(((MIndexedObject) mo).shape());
		} else if(mo instanceof MRecSequence) {
			MRecSequence rseq = (MRecSequence) mo;
			sb.append("r{[").append(rseq.getIndexName()).append("]=");
			StringBuilder sb2 = new StringBuilder();
			export(sb2, rseq.getFunction());
			sb.append(sb2.toString().replace("_", ""));
			for(int i = 0; i < rseq.getInitalParameterCount(); i++) {
				sb.append(",");
				export(sb, rseq.get(i));
			}
			sb.append('}');
		} else if(mo instanceof MSequence) {
			MSequence seq = (MSequence) mo;
			sb.append("{").append(seq.getIndexName()).append('=').append(seq.getBegin()).append(':');
			if(seq.getEnd()>0 && seq.getEnd()!=Integer.MAX_VALUE)
				sb.append(seq.getEnd());
			sb.append(',');
			export(sb, seq.getFunction());
			sb.append('}');
		} else if(mo instanceof MExpression) {
			export(sb, ((MExpression) mo).getTree().getRoot());
		}
	}
	
	private static String export(Node<?> n) {
		StringBuilder sb = new StringBuilder();
		export(sb,n);
		return sb.toString();
	}
	
	/**
	 * Recursively converts the given {@link Node} and its children to a text representation that can be
	 * used as an import in the calculator. This method is very similar to {@link TextPrinter#nodeToText(StringBuilder, Node)}
	 * with only its recursive references changed.
	 * 
	 * @param node the {@link Node} to be converted to text.
	 * @return a <tt>String</tt> containing the argument in text format.
	 */
	private static void export(StringBuilder sb, Node<?> n) {
		if (n.data instanceof MConst)
			sb.append(((MConst) n.data).name());
		else if (n.data instanceof Operator) {
			Operator op = (Operator) n.data;
			switch (op) {
			case ADD:
			case SUBTRACT:
				export(sb, n.left());
				sb.append(op == Operator.ADD ? "+" : "-");
				export(sb, n.right());
				break;
			case MULTIPLY:
				String s1 = export(n.left());
				String s2 = export(n.right());
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
				String right = export(n.right());
				String left = export(n.left());
				if (n.right().right() != null) //Brackets are needed
					right = "(" + right + ")";
				if(n.left().right() != null)
					left = "(" + left + ")";
				sb.append(left).append(op == Operator.POWER ? "^" : "/").append(right);
				break;
			case MOD:
				sb.append("mod[");
				export(sb, n.left());
				sb.append(", ");
				export(sb, n.right());
				sb.append("]");
				break;
			case NEGATE:
				sb.append("-");
				export(sb, n.left());
				break;
			case INVERT:
				if(n.left().isInternal()) {
					sb.append("(");
					export(sb, n.left());
					sb.append(")");
				} else
					export(sb, n.left());
				sb.append("^-1");
				break;
			case ELEMENT:
				export(sb, n.left());
				sb.append("[");
				export(sb, n.right());
				sb.append("]");
				break;
			case CONJUGATE:
				if(n.left().isInternal()) {
					sb.append("&(");
					export(sb, n.left());
					sb.append(")");
				} else {
					sb.append("&");
					export(sb, n.left());
				}
				break;
			default:
				break;
			}
		} else if (n.data instanceof Function) {
			sb.append(n.data).append("(");
			export(sb, n.left());
			sb.append(")");
		} else if(n.data instanceof MathObject) {
			export(sb, ((MathObject) n.data));
		} else if(n.data instanceof Variable) {
			sb.append(((Variable) n.data).getName());
		}
	}
	
	/**
	 * Exports this variable in such a way that it can be used as an input again.
	 * It is the task of the code that calls this method to ensure that the order
	 * in which the variables are exported is such that respects their dependencies
	 * @param sb the StringBuilder to which the export string is appended
	 * @param var the variable to be exported
	 */
	public static void export(StringBuilder sb, Variable var) {
		MathObject mo = var.get();
		if(mo instanceof MFunction) {
			sb.append(var.getName()).append('(').append(Tools.join(",",((MFunction)mo).getParameters())).append(')');
			if(((MFunction) mo).isDefined()) sb.append(':');
		} else if(mo instanceof MVectorFunction) {
			sb.append(var.getName()).append('(').append(Tools.join(",",((MVectorFunction)mo).getParameters())).append(')');
			if(((MVectorFunction) mo).isDefined()) sb.append(':');
		} else if(mo instanceof MExpression)
			sb.append(var.getName()).append(":");
		else
			sb.append(var.getName());
		sb.append("=");
		export(sb, mo);
	}
	
	public static StringBuilder exportAll() {		
		StringBuilder export = new StringBuilder();
		HashSet<String> exported = new HashSet<String>();
		for(Entry<String, MathObject> entry : Variables.getAll().entrySet()) {
			if(!(entry.getValue() instanceof MExpression)) {
				export.append(entry.getKey()).append("=");
				export(export, entry.getValue());
				export.append(Printer.newLine);
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
	
	public static StringBuilder exportSafe(ArrayList<Variable> toBeExported) {
		StringBuilder export = new StringBuilder();
		HashSet<String> exported = new HashSet<String>();
		for(Iterator<Variable> iter = toBeExported.iterator(); iter.hasNext();) {
			Variable var = iter.next();
			if(!(var.get() instanceof MExpression)) {
				export(export, var);
				export.append(Printer.newLine);
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
		//Export this nodes value
		export(s, n.getData());
		s.append(Printer.newLine);
		exported.add(n.getData().getName()); //add it to the exported set
		return time;
	}
}
