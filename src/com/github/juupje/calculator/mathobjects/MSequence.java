package com.github.juupje.calculator.mathobjects;

import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.IndexException;
import com.github.juupje.calculator.helpers.exceptions.InvalidOperationException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Parser;

public class MSequence implements MathObject {
	
	String indexName = "";
	int begin = 1;
	int end;
	MFunction func;
	MReal index;
	Shape shape;
	
	public MSequence(String index, int begin, int end, MFunction func) {
		this.indexName = index;
		this.begin = begin;
		this.end = end;
		this.func = func;
		this.index = new MReal(begin);
		Shape fshape = func.shape();
		if(fshape.isScalar())
			shape = new Shape(end-begin);
		else
			shape = new Shape(end-begin, fshape);
	}
	
	public MSequence(String index, int begin, MFunction func) {
		this(index, begin, -1, func);
	}
	
	public MathObject get(int index) {
		this.index = new MReal(index);
		if(end<0) {
			if(index>=begin)
				return func.evaluateAt(this.index);
		} else {
			if(index>=begin && index<=end)
				return func.evaluateAt(this.index);
		}
		throw new IndexException(String.valueOf(index), String.valueOf(begin), (String)(end>0 ? end : "infinity"));
	}
	
	public MFunction getFunction() {
		return func;
	}
	
	public int getBegin() {
		return begin;
	}
	
	public int getEnd() {
		return end;
	}
	
	public String getIndexName() { 
		return indexName;
	}

	/**
	 * Negates the defining function of the sequence. This is equivalent to negating every element in this sequence.
	 * @see MFunction#negate();
	 * @return this
	 */
	@Override
	public MSequence negate() {
		func.negate();
		return this;
	}

	/**
	 * this method throws and {@link InvalidOperationException} because inverting a sequence is not defined.
	 */
	@Override
	public MathObject invert() {
		throw new InvalidOperationException("The inverse of a sequence is not properly defined and hence not implemented.");
	}

	/**
	 * Returns a copy of this sequence.
	 * @see MFunction#copy()
	 * @return a new sequence with the same index name, begin, end and a copy of the defining function.
	 */
	@Override
	public MathObject copy() {
		return new MSequence(indexName, begin, end, func.copy());
	}

	/**
	 * Evaluates the defining function and returns a copy with that function.
	 * @see MFunction#evaluate()
	 */
	@Override
	public MathObject evaluate() {
		return new MSequence(indexName, begin, end, func.evaluate());
	}

	/**
	 * Returns the shape of the defining function.
	 * @return the result of {@link MFunction#shape}
	 */
	@Override
	public Shape shape() {
		return shape;
	}

	@Override
	public boolean isNumeric() {
		return false;
	}
	
	public static MSequence parse(String expr, boolean defined) {
		if(expr.startsWith("r{"))
			return MRecSequence.parseRecursive(expr.substring(1));
		//definition: {i=1:n, i/n}
		else if(expr.startsWith("{")) {
			if(expr.endsWith("}"))
				expr = expr.substring(1,expr.length()-1);
			else {
				String msg = "Unbalanced brackets in ";
				throw new UnexpectedCharacterException(msg + expr, msg.length());
			}
			String[] args = Parser.getArguments(expr);
			if(!args[0].matches("[a-zA-Z]+=\\d*:\\d*"))
				throw new UnexpectedCharacterException("Could not parse index argument, see help(sequences)");
	
			int eqIndex = args[0].indexOf('=');
			int colonIndex = args[0].indexOf(':');
			int begin = (colonIndex-eqIndex>1 ? Integer.valueOf(args[0].substring(eqIndex+1, colonIndex)) : 1);
			int end = (colonIndex != args[0].length()-1 ? Integer.valueOf(args[0].substring(colonIndex+1)) : -1);
			String index = args[0].substring(0,eqIndex);
			if(!Tools.checkNameValidity(index))
				throw new UnexpectedCharacterException(index + " is not a valid index" + (index.equals("i") ? " (i is reserved for the imaginary unit)." : "."));
			MFunction func = MFunction.create(new String[] {index}, args[1], defined);
			return new MSequence(index, begin, end, func);
		}
		throw new UnexpectedCharacterException("Expected { at start of sequence definition, got " + expr);
	}
	
	@Override
	public String toString() {
		return Printer.toText(this);
	}
}