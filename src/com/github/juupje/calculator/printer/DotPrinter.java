package com.github.juupje.calculator.printer;

import java.io.IOException;

import com.github.juupje.calculator.graph.Graph;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MVectorFunction;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;

public class DotPrinter {

	private static final String newLine = System.lineSeparator();
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
				sb.append(n.hashCode()).append("[label=<").append(n.toString())
				.append("<br/><font point-size=\"10\">").append(n.start).append(" : ").append(n.finish).append("</font>>];").append(newLine);
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
}
