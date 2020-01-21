package com.github.juupje.calculator.mathobjects;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.Shape;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.main.Variable;
import com.github.juupje.calculator.tree.DFSTask;
import com.github.juupje.calculator.tree.Node;

public class MRecSequence extends MSequence {
	
	private LinkedList<MScalar> items;
	private Shape shape;
	private int initialParameterCount = 0;
	
	public MRecSequence(String index, MFunction func, LinkedList<MScalar> startParams, int initialParameterCount) {
		super(index, 0, Integer.MAX_VALUE, func);
		this.items = startParams;
		shape = new Shape(Integer.MAX_VALUE);
		this.initialParameterCount = initialParameterCount;
	}
	
	public MRecSequence(String index, MFunction func, LinkedList<MScalar> startParams) {
		this(index, func, startParams, startParams.size());
	}

	static final Pattern pat = Pattern.compile("\\[(.+?)\\]");
	public static MRecSequence parseRecursive(String expr) {
		//definition using recursion: r{[n]=expr([n-k]...[n-1]), *,*} where * represents the values of [1]...[k]
		if(expr.startsWith("{")) {
			if(expr.endsWith("}"))
				expr = expr.substring(1,expr.length()-1);
			else {
				String msg = "Unbalanced brackets in ";
				throw new UnexpectedCharacterException(msg + expr, msg.length());
			}
			String[] args = Parser.getArguments(expr);
			//first argument should have the form [n]=...
			if(args[0].matches("\\[[a-zA-z]+\\]=.+")) {
				String indexName = Tools.extractFirst(args[0], '[', ']');
				String s = "";
				int index = 0;
				args[0] = args[0].substring(args[0].indexOf('=')+1);
				Matcher match = pat.matcher(args[0]);
				while(match.find()) {
					s += args[0].substring(index, match.start()) + "_" + match.group();
					index = match.end();
				}
				s += args[0].substring(index);
				//parse s as a function with variable _
				MFunction func = MFunction.create(new String[] {"_[" + Integer.MAX_VALUE + "]", indexName}, s, false, MFunction.FLAG_INTERNAL);
				
				LinkedList<MScalar> startParams = new LinkedList<>();
				for(int i = 1; i < args.length; i++) {
					MathObject obj = new Parser(args[i]).evaluate();
					if(obj instanceof MScalar)
						startParams.add((MScalar) obj);
					else
						throw new ShapeException("Expected a scalar value, got " + obj.toString() + " with shape " + obj.shape());
				}
				
				MRecSequence seq = new MRecSequence(indexName, func, startParams);
				func.getTree().DFS(new DFSTask() {
					@Override
					public void accept(Node<?> n) {
						if(n.data instanceof Variable && ((Variable) n.data).getName().equals("_")) {
							Node<?> sibling = n.parent.right;
							if(!(sibling.data.equals(Operator.SUBTRACT) && sibling.left.data.equals(new Variable(indexName))
										&& sibling.right.data instanceof MReal && ((MReal) sibling.right.data).isInteger()))
								throw new UnexpectedCharacterException("Expected index to match '" + indexName + "-*', but got " + Printer.nodeToText(sibling));
						}
					}
				});
				if(!func.shape().isScalar())
					throw new ShapeException("Recursive sequence needs to be of scalar shape, got " + func.shape());
				return seq;
			} else {
				throw new UnexpectedCharacterException("Expected first part of recursive sequence to match '[*]=', got" + args[0]);
			}
		} else
			throw new UnexpectedCharacterException("Expected { at start of sequence definition, got " + expr);
	}
	
	public int getInitalParameterCount() {
		return initialParameterCount;
	}
	
	@Override
	public Shape shape() {
		return shape;
	}
	
	@Override
	public String toString() {
		return Printer.toText(this);
	}

	@Override
	public MathObject get(int index) {
		if(items.size()>index)
			return items.get(index);
		MathObject temp = func.paramMap.get(indexName);
		MScalar result = (MScalar) func.evaluateAt(this, new MReal(index));
		func.paramMap.put(indexName, temp);
		items.add(result);
		return result;
	}

	@Override
	public MathObject copy() {
		return new MRecSequence(indexName, func.copy(), items);
	}
}