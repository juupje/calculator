package com.github.juupje.calculator.algorithms;

import java.util.Objects;

import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

public class MatrixGenerator extends Algorithm {
	
	enum Type {
		ID, ZEROS, ONES, FULL
	}
	
	Type type;
	
	public MatrixGenerator(Type type) {
		Objects.requireNonNull(type);
		this.type = type;
	}
	
	@Override
	public MathObject execute() {
		return null;
	}

	@Override
	protected MathObject execute(MathObject... args) {
		if(type == Type.ID) {
			if(args.length==1) {
				if(args[0] instanceof MReal && ((MReal) args[0]).isInteger()) {
					int n = (int) ((MReal) args[0]).getValue();
					return MMatrix.identity(n);
				} else
					throw new IllegalArgumentException("Expected first argument to be an integer, got " + args[0].toString());
			} else
				throw new IllegalArgumentException("id expected 1 argument, got " + args.length);
		}
		
		if(!((args.length==3 && type == Type.FULL) || 
				((args.length==2 || args.length==1) && type != Type.FULL)))
			throw new IllegalArgumentException(type.name().toLowerCase() + " expected " + (type==Type.FULL ? "3" : "2 or 1") + " arguments, got " + args.length);
		
		if(args[0] instanceof MReal && ((MReal) args[0]).isPosInteger() 
				&& (args.length==1 || args[1] instanceof MReal && ((MReal) args[1]) instanceof MReal)) {
			int rows = (int) ((MReal) args[0]).getValue();
			if(type == Type.FULL) {
				if(!args[2].isNumeric())
					throw new IllegalArgumentException("Cannot fill a matrix with non-numeric value " + args[2].toString());
				if(!((MReal) args[1]).isPosInteger())
					throw new IllegalArgumentException("Expected second argument to be a positive integer, got " + args[1]);
				int cols = (int) ((MReal) args[1]).getValue();
				
				if(rows<=0 || cols<=0)
					throw new ShapeException("Matrix size cannot be <=0, got shape (" + rows + ", " + cols + ")");
				
				MathObject[][] matrix = new MathObject[rows][cols];
				for(int i = 0; i < rows; i++)
					for(int j = 0; j < cols; j++)
						matrix[i][j] = args[2].copy();
				return new MMatrix(matrix);
			} else {
				int cols = rows;
				if(args.length==2)
					cols = (int) ((MReal) args[1]).getValue();
				if(rows<=0 || cols<=0)
					throw new ShapeException("Matrix size cannot be <=0, got shape (" + rows + ", " + cols + ")");
				
				MathObject[][] matrix = new MathObject[rows][cols];
				int k = type==Type.ONES ? 1 : 0;
				for(int i = 0; i < rows; i++)
					for(int j = 0; j < cols; j++)
						matrix[i][j] = new MReal(k);
				return new MMatrix(matrix);
			}
		} else
			throw new IllegalArgumentException("Expected " + (args.length==1 ? " 1 real positive integer" : (args.length==3 ? " first 2 arguments to be " : "") + " 2 real positive integers") + ", got " + argTypesToString(args));
	}

	@Override
	public Shape shape(Shape... shapes) {
		return null;
	}

}
