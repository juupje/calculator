package com.github.juupje.calculator.algorithms.algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.algorithms.algebra.range.*;
import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.EinSumException;
import com.github.juupje.calculator.helpers.exceptions.IndexException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.helpers.exceptions.TreeException;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.main.Variable;
import com.github.juupje.calculator.main.Variables;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MIndexedObject;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;
import com.github.juupje.calculator.tree.DFSTask;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;

public class EinSum extends Algorithm {

	String sum;
	ArrayList<Index> freeIndices;
	Set<String> dummyIndices;
	Set<String> indexNames;
	Shape shape;
	DFSTask indexVariablesToInternal = new DFSTask() {
		@SuppressWarnings("unchecked")
		@Override
		public void accept(Node<?> n) {
			if (n.parent != null && n == n.parent.right() && n.parent.getData().equals(Operator.ELEMENT)) {
				// this is the left node of an index variable
				if (n.getData() instanceof Variable) {
					changeNode((Node<Variable>) n);
				} else if (n.getData() instanceof MVector) {
					for (MathObject mo : ((MVector) n.getData()).elements()) {
						// if the element is an expression with only a single node containing a variable
						if (mo instanceof MExpression) {
							MExpression expr = (MExpression) mo;
							if (!expr.getTree().getRoot().isInternal()
									&& expr.getTree().getRoot().getData() instanceof Variable) {
								changeNode((Node<Variable>) expr.getTree().getRoot());
							}
						}
					}
				}
			}
		}

		void changeNode(Node<Variable> n) {
			if (indexNames.contains(n.getData().getName()))
				n.setData(new Variable("$" + n.getData().getName()));
		}
	};

	@Override
	public MathObject execute(String... args) {
		prepare(args);
		return execute();
	}

	public MathObject einsumTerm(Tree subtree) {
		return null;
	}

	@Override
	public MIndexedObject execute() {
		if (!prepared)
			return null;
		// collect all indices (both free and dummy)
		indexNames = freeIndices.stream().map(index -> index.getName()).collect(Collectors.toSet());
		indexNames.addAll(dummyIndices);
		// build the tree from the sum string
		EinSumTree tree = new EinSumTree(
				new Parser(sum, indexNames.stream().collect(Collectors.toMap(Function.identity(), s -> MReal.class)))
						.getTree());
		tree.DFS(indexVariablesToInternal); // prepends a '$' to all index names
		freeIndices.forEach(index -> index.setName("$" + index.getName()));
		dummyIndices = dummyIndices.stream().map(s -> "$" + s).collect(Collectors.toSet());

		// this is where the fun starts
		/*
		 * The einsum can be computed by contracting each dummy index pair. So, for each
		 * node in the graph containing the multiply operator, we list the indices on
		 * the left and on the right side. Then, we sum over each index which is on both
		 * sides. All other indices which are present only on the right or left should
		 * one of the free indices.
		 */
		tree.expand();
		int size = 1;
		for (Index index : freeIndices)
			size *= index.getRange().length();
		MIndexedObject obj = new MIndexedObject(shape);

		IndexIterator indit = new IndexIterator(freeIndices);
		if (size > 1) {
			while (true) {
				// calculate the sum (with einstein summation) for each combination of index
				// values
				obj.set(tree.evaluateTree(), indit.getIndices());
				if (!indit.next())
					break;
			}
		} else {
			obj.set(tree.evaluateTree(), 0);
		}
		// Remove the internal variables
		for (Index index : freeIndices)
			Variables.remove(index.getName());
		return obj;
	}

	public void resetIndices() {
		for (Index index : freeIndices)
			index.getRange().reset();
	}

	public Set<Variable> getIndices(EinSumNode<Operator> n) {
		if (!n.data.equals(Operator.ELEMENT))
			return null;
		return null;
	}

	private boolean isAddition(Object op) {
		if (op instanceof Operator)
			return op == Operator.ADD || op == Operator.SUBTRACT;
		return false;
	}

	@Override
	protected MathObject execute(MathObject... args) {
		throw new RuntimeException("This shouldn't actually happen... please report this bug to the developer.");
	}

	@Override
	public Shape shape(Shape... shapes) {
		return null;
	}

	public void prepare(String[] args) {
		if (args.length == 0)
			throw new IllegalArgumentException("Got no arguments.");
		final Pattern pat1 = Pattern.compile("^(\\w+)=(\\[(\\d+(?:,|\\]$))+)");
		final Pattern pat2 = Pattern.compile("^(\\w+)=(\\d+):(\\d+)$");
		final Pattern pat3 = Pattern.compile("\\[(?:(?:\\w+|:),)*(?:[a-zA-Z:])\\]");
		sum = args[0];
		freeIndices = new ArrayList<Index>();
		for (int i = 1; i < args.length; i++) {
			Matcher match = pat2.matcher(args[i]);
			if (match.find()) {
				String name = match.group(1);
				freeIndices.add(new Index(name,
						new SimpleRange(Integer.valueOf(match.group(2)), Integer.valueOf(match.group(3)))));
				continue;
			}

			match = pat1.matcher(args[i]);
			if (match.find()) {
				String name = match.group(1);
				String group2 = match.group(2);
				String[] svalues = group2.substring(1, group2.length() - 1).split(",");
				int[] values = new int[svalues.length];
				for (int j = 0; j < svalues.length; j++)
					values[j] = Integer.valueOf(svalues[j]);
				freeIndices.add(new Index(name, new ArrayRange(values)));
				continue;
			}
			throw new IllegalArgumentException("Argument '" + args[i] + "' could not be parsed.");
		}

		shape = computeShape(freeIndices);

		Set<String> freeIndices = this.freeIndices.stream().map(index -> index.getName()).collect(Collectors.toSet());
		dummyIndices = new HashSet<>();
		Matcher match = pat3.matcher(args[0]);
		while (match.find()) {
			String group = match.group();
			group = group.substring(1, group.length() - 1);
			String[] elements = group.split(",\\s*");
			for (String element : elements) {
				if (!element.equals(":") && !freeIndices.contains(element))
					dummyIndices.add(element);
			}
		}
		prepared = true;
	}

	private Shape computeShape(ArrayList<Index> freeIndices) {
		int[] shape = new int[freeIndices.size()];
		for (int i = 0; i < shape.length; i++) {
			shape[i] = freeIndices.get(i).getRange().length();
		}
		return new Shape(shape);
	}

	class EinSumTree extends Tree {
		EinSumTree(Tree tree) {
			root = (EinSumNode<?>) tree.copy(new Function<Node<?>, EinSumNode<?>>() {
				@Override
				public EinSumNode<?> apply(Node<?> n) {
					return new EinSumNode<Object>(n.data);
				}
			}).getRoot();
		}

		void replaceIndex(Node<?> n, String indexName, int indexValue) {
			if (n.getData().equals(Operator.ELEMENT)) {
				if (n.right().getData() instanceof Variable) {
					Variable var = (Variable) n.right().getData();
					if (var.getName().equals(indexName))
						n.right().replace(new Node<MReal>(new MReal(indexValue)));
				} else if (n.right().getData() instanceof MVector) {
					MVector vec = (MVector) n.right().getData();
					for (int i = 0; i < vec.size(); i++) {
						if (vec.get(i) instanceof MExpression) {
							Node<?> root = ((MExpression) vec.get(i)).getTree().getRoot();
							if (!root.isInternal() && root.getData() instanceof Variable) {
								Variable var = (Variable) root.getData();
								if (var.getName().equals(indexName))
									vec.set(i, indexValue);
							}
						} else if (!(vec.get(i) instanceof MReal)) {
							throw new IndexException("Cannot process index " + vec.get(i).toString());
						}
					}
				}
			}
			if (n.left() != null)
				replaceIndex(n.left(), indexName, indexValue);
			if (n.right() != null)
				replaceIndex(n.right(), indexName, indexValue);
		}

		Node<?> copyWithIndex(EinSumNode<?> n, String indexName, int indexValue) {
			Node<?> copy = n.copy();
			replaceIndex(copy, indexName, indexValue);
			return copy;
		}

		EinSumNode<?> expand(EinSumNode<?> n, Index index) {
			int size = index.getRange().length() - 1;
			EinSumNode<?>[] plus = new EinSumNode[size];
			for (int i = 0; i < size; i++) {
				plus[i] = new EinSumNode<Operator>(Operator.ADD);
				plus[i].left(copyWithIndex(n, index.getName(), index.getRange().next()));
				if (i > 0)
					plus[i - 1].right(plus[i]);
			}
			plus[plus.length - 1].right(copyWithIndex(n, index.getName(), index.getRange().next()));
			if (n != root)
				n.replace(plus[0]);
			else
				root = plus[0];
			return plus[0];
		}

		void expand(EinSumNode<?> n) {
			HashMap<Index, Integer> rightIndices = null, leftIndices = null;
			if (n.left() != null) {
				expand((EinSumNode<?>) n.left());
				leftIndices = ((EinSumNode<?>) n.left()).getIndices();
			}
			if (n.right() != null) {
				expand((EinSumNode<?>) n.right());
				rightIndices = ((EinSumNode<?>) n.right()).getIndices();
			}
			/*
			 * First handle three possibilities 1. The node is an element operator -> add
			 * new indices to the index map 2. The node is an add or subtract operator ->
			 * make sure the left and right child have the same index map, then copy one of
			 * them 3. The node is neither of the above -> add the left and right maps
			 * together
			 * 
			 * Then check whether the node's parent is an addition. If so, the node has to
			 * be the root node of a term. If the node's index map contains indices which
			 * appeared twice, expand the node into a series of additions, each with a
			 * different value for that index. Then remove that index from the map. If the
			 * node has any indices left in its map, check whether they all appear less than
			 * once.
			 * 
			 * Lastly, if the node is the root node, it should contain no more non-free
			 * indices.
			 */
			if (n.getData().equals(Operator.ELEMENT)) {
				if (n.left() == null || n.right() == null)
					throw new TreeException("Element node with less than two child nodes");

				// The indices of the element operator should be a single node
				if (n.right().isInternal())
					throw new EinSumException("The indices of '" + Printer.nodeToText(n) + "' are not independent");

				n.addIndices(leftIndices);
				// We need to find the shape of the left child
				Shape leftShape = null;
				try {
					leftShape = getShape(n.left());
				} catch (TreeException | ShapeException e) {
					throw new EinSumException("Cannot determine the shape of '" + Printer.nodeToText(n.left()) + "'");
				}
				if (leftShape.isScalar())
					throw new EinSumException("Object '" + Printer.nodeToText(n.left()) + "' not indexable");

				// Now we find the indices added to this node by the right child (which contains
				// the indices of the left child)
				// The right child can only be a vector containing the indices or a variable
				// with the index
				// If that index is a variable (so no numerical value) and present in the dummy
				// variable list, we add it to this node
				if (n.right().getData() instanceof Variable) {
					// There is only a single index, stored in a Variable
					Variable var = (Variable) n.right().getData();
					if (dummyIndices.contains(var.getName()))
						// Get the name from the variable and the range from the shape
						n.addIndex(new Index(var.getName(), new SimpleRange(leftShape.get(0) - 1)));

				} else if (n.right().getData() instanceof MVector) {
					MVector vec = (MVector) n.right().getData();
					if (vec.size() != leftShape.size())
						throw new IndexException("Index dimension mismatch. Object dim: " + leftShape.size()
								+ ", index dim: " + vec.size());
					for (int i = 0; i < vec.size(); i++) {
						MathObject obj = vec.get(i);
						if(obj instanceof MReal)
							continue;
						if (!(obj instanceof MExpression))
							throw new EinSumException("Cannot process index '" + obj.toString() + "'");
						Node<?> root = ((MExpression) obj).getTree().getRoot();
						if (!root.isInternal() && root.getData() instanceof Variable) {
							// Check if the index stores in this variable is a dummy index
							Variable var = (Variable) root.getData();
							if (dummyIndices.contains(var.getName()))
								// Get the name from the variable and the range from the shape
								n.addIndex(new Index(var.getName(), new SimpleRange(leftShape.get(i) - 1)));
						}
					}
				}
			} else if (isAddition(n.data)) {
				// If this node is an addition, the left and right indices need to be the same
				if (rightIndices.size() == leftIndices.size()) {
					for (Entry<Index, Integer> entry : leftIndices.entrySet()) {
						if (!rightIndices.containsKey(entry.getKey())
								|| rightIndices.get(entry.getKey()) != entry.getValue())
							throw new EinSumException(
									"Index mismatch between terms: " + entry.getKey().getName().replace("$", ""));
					}
				} else
					throw new EinSumException("Index mismatch between terms");
				n.setIndices(leftIndices);
			} else {
				// Combine the indices on the right with those on the left
				n.addIndices(leftIndices);
				n.addIndices(rightIndices);
			}

			if (n.parent != null) {
				if (isAddition(n.parent.data)) {
					// sum over dummy indices which appeared twice
					if(n.getIndices()!= null) {
						for (Iterator<Entry<Index, Integer>> iter = n.getIndices().entrySet().iterator(); iter.hasNext();) {
							Entry<Index, Integer> entry = iter.next();
							if (entry.getValue() == 2) {
								// sum over this index
								EinSumNode<?> copy = expand(n, entry.getKey());
								iter.remove();
								copy.setIndices(n.getIndices());
							} else if (entry.getValue() > 2)
								// no indices should appear more than twice
								throw new EinSumException(entry.getKey().toString() + " occurs " + entry.getValue()
										+ " times in term '" + Printer.nodeToText(n) + "'");
						}
					}
				}
			} else {
				// this node is the root
				// we can only sum over it if it is any operator except for an addition
				if (n.getData() instanceof Operator && !isAddition(n.getData())) {
					// sum over all the double dummy indices
					if (n.getIndices() != null) {
						for (Iterator<Entry<Index, Integer>> iter = n.getIndices().entrySet().iterator(); iter.hasNext();) {
							Entry<Index, Integer> entry = iter.next();
							if (entry.getValue() == 2) {
								// sum over this index
								EinSumNode<?> copy = expand(n, entry.getKey());
								iter.remove();
								copy.setIndices(n.getIndices());
							} else if (entry.getValue() > 2)
								// no indices should appear more than twice
								throw new EinSumException(entry.getKey().toString() + " occurs " + entry.getValue()
										+ " times in term '" + Printer.nodeToText(n) + "'");
						}
						// throw an error if any index remains.
						if (n.getIndices().size() != 0) {
							List<String> unmatched = n.getIndices().keySet().stream().map(index -> index.getName()).collect(Collectors.toList());
							if (unmatched.size() == 1)
								throw new EinSumException("Unmatched dummy index " + unmatched.get(0).replace("$", ""));
							throw new EinSumException(
									"Unmatched dummy indices " + Tools.join(", ", unmatched).replace("$", ""));
						}
					}
				}
			}
		}

		/**
		 * Do a DFS while collecting the indices from the bottom up. If a node's parent
		 * is an addition or subtraction, check whether the subtree of which this node
		 * is the root contains a dummy index exactly twice. If it does, then replace
		 * this node by a series of additions each term in the addition is the exact
		 * same except for the value of the dummy index.
		 */
		private void expand() {
			expand((EinSumNode<?>) root);
		}
	}

	public class EinSumNode<T> extends Node<T> {

		HashMap<Index, Integer> indices;

		EinSumNode(T data) {
			super(data);
		}

		void setIndices(HashMap<Index, Integer> index) {
			indices = index;
		}

		public HashMap<Index, Integer> getIndices() {
			return indices;
		}

		void addIndices(Set<Index> index) {
			if (index == null)
				return;
			if (indices == null)
				indices = new HashMap<>();
			for (Index i : index)
				indices.put(i, indices.get(i) == null ? 1 : indices.get(i) + 1);
		}

		void addIndices(Map<Index, Integer> newIndices) {
			if (newIndices == null)
				return;
			if (indices == null)
				indices = new HashMap<>();
			for (Entry<Index, Integer> entry : newIndices.entrySet()) {
				if (indices.containsKey(entry.getKey())) {
					// check whether the ranges of the two indices is the same
					Index index = entry.getKey();
					for (Index i : indices.keySet()) {
						if (i.getName().equals(index.getName())) {
							if (i.getRange().equals(index.getRange()))
								indices.put(index, indices.get(index) + entry.getValue());
							else
								throw new IndexException(
										"Range for index " + i.getName().replace("$", "") + " inconsistent");
						}
					}
				} else
					indices.put(entry.getKey(), entry.getValue());
			}
		}

		void addIndex(Index index) {
			if (index == null)
				return;
			if (indices == null)
				indices = new HashMap<>();
			if (indices.containsKey(index)) {
				// check whether the ranges of the two indices is the same
				for (Index i : indices.keySet()) {
					if (i.getName().equals(index.getName())) {
						if (i.getRange().equals(index.getRange()))
							indices.put(index, indices.get(index) + 1);
						else
							throw new IndexException("Range for index " + index.getName().replace("$", "") + " inconsistent");
					}
				}
			} else
				indices.put(index, 1);
		}

		@Override
		public String toHTMLLabel() {
			if (indices == null || indices.size() == 0)
				return super.toHTMLLabel();
			String str = "";
			for (Entry<Index, Integer> entry : indices.entrySet())
				str += entry.getKey().getName() + ":" + entry.getValue() + ", ";
			return toString() + "<br/><font point-size=\"10\">" + hashCode() + "<br/>"
					+ str.substring(0, str.length() - 2) + "</font>";
		}
	}
}