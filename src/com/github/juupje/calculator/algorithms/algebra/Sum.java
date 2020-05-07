package com.github.juupje.calculator.algorithms.algebra;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.helpers.exceptions.IndexException;
import com.github.juupje.calculator.helpers.exceptions.InvalidOperationException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MSequence;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

public class Sum extends Algorithm {

	MSequence sequence;
	int begin, end;
	
	public Sum() {}
	
	public Sum(MSequence seq) {
		sequence = seq;
		begin = sequence.getBegin();
		end = sequence.getEnd();
	}
	
	public Sum(MSequence seq, int begin, int end) {
		sequence = seq;
		if(begin<seq.getBegin() || end>seq.getEnd())
			throw new IndexException("Index range [" + begin + ", " + end + "] does not fit the sequence's range: [" + sequence.getBegin() + ", " + sequence.getEnd() + "]");
		this.begin = begin;
		this.end = end;
	}
	
	@Override
	public MathObject execute() {
		MathObject first = sequence.get(begin).copy();
		try {
			if(first instanceof MScalar) {
				MScalar sum = (MScalar) first;
				for(int i = begin+1; i <= end; i++)
					sum.add((MScalar) sequence.get(i));
				return sum;
			} else if(first instanceof MVector) {
				MVector sum = (MVector) first;
				for(int i = begin+1; i <= end; i++)
					sum.add((MVector) sequence.get(i));
				return sum;
			} else if(first instanceof MMatrix) {
				MMatrix sum = (MMatrix) first;
				for(int i = begin+1; i <= end; i++)
					sum.add((MMatrix) sequence.get(i));
				return sum;
			} else
				throw new IllegalArgumentException("The sequence's items appear to be neither scalar, vector or matrix valued...");
		} catch(ClassCastException e) {
			throw new InvalidOperationException("The sequence's items appear to be of different types, which cannot be summed up.");
		}
	}

	@Override
	protected MathObject execute(MathObject... args) {
		prepare(args);
		return execute();
	}

	@Override
	protected void prepare(MathObject[] args) {
		if(args.length==1 || args.length==3) {
			if(args[0] instanceof MSequence)
				sequence = (MSequence) args[0];
			else
				throw new IllegalArgumentException("First argument should be a sequence, got " + argTypeToString(args[0]));
			if(args.length==3) {
				if(args[1] instanceof MReal && args[2] instanceof MReal && ((MReal) args[1]).isPosInteger() && ((MReal) args[2]).isPosInteger()) {
					begin = (int) ((MReal) args[1]).getValue();
					end = (int) ((MReal) args[2]).getValue();
					if(begin >= end && end > 0)
						throw new IllegalArgumentException("Begin index cannot be larger than or equal to the end index: " + begin +">=" + end);
					if(begin<sequence.getBegin() || (sequence.getEnd() > 0 && end>sequence.getEnd()))
						throw new IndexException("Index range [" + begin + ", " + end + "] does not fit the sequence's range: [" + sequence.getBegin() + ", " + (sequence.getEnd()>0 ? sequence.getEnd() : "infinity") + "]");
				} else
					throw new IllegalArgumentException("Expected arguments 2 and 3 to be positive integers, got " + args[1].toString() + " and" + args[2].toString());
			} else {
				begin = sequence.getBegin();
				end = sequence.getEnd();
				if(end <= 0 || end == Integer.MAX_VALUE)
					throw new IllegalArgumentException("The given sequence is infinitely long, cannot calculate infinite summations.");
			}
			prepared = true;
		} else
			throw new IllegalArgumentException("Expected 1 or 3 arguments, got " + args.length);
	}
	
	@Override
	public Shape shape(Shape... shapes) {
		if(shapes.length==1 || shapes.length==3) {
			if(shapes.length==3) {
				if(!shapes[1].isScalar() || !shapes[2].isScalar())
					throw new ShapeException("Second and third argument are expected to be scalars");
			}
			int[] seqshape = shapes[0].asArray();
			if(seqshape.length==1) return new Shape();
			int[] shape = new int[seqshape.length-1];
			for(int i = 0; i <shape.length; i++)
				shape[i] = seqshape[i+1];
			return new Shape(shape);
		}
		return null;
	}
}