package algorithms.linalg;

import algorithms.Algorithm;
import helpers.Shape;
import helpers.Tools;
import helpers.exceptions.ShapeException;
import mathobjects.MMatrix;
import mathobjects.MReal;
import mathobjects.MVector;
import mathobjects.MathObject;

public class GaussianElimination extends Algorithm {

	Shape shape;
	MatrixToolkit<?> mtk;
	
	public GaussianElimination() {}
	
	public GaussianElimination(MMatrix m, MMatrix b) {
		if(m.shape().rows() != b.shape().rows())
			throw new ShapeException("Augmented matrix needs to have the same amount of rows as the matrix, got " + m.shape() + " and " + b.shape());
		mtk = MatrixToolkit.getToolkit(m.augment(b));
		mtk.setAugmCols(b.shape().cols());
		shape = new Shape(mtk.rows, mtk.cols);
		prepared = true;
	}
	
	public GaussianElimination(MMatrix m) {
		mtk = MatrixToolkit.getToolkit(m);
		shape = m.shape();
		prepared = true;
	}
	
	@Override
	public MMatrix execute() {
		if(!prepared) return null;
		gauss();
		return mtk.toMMatrix();
	}
	
	@SuppressWarnings("unlikely-arg-type")
	protected void gauss() {
		mtk.reorder(mtk.rows);
		if(mtk.isReal()) {
			DoubleMatrixToolkit tk = (DoubleMatrixToolkit) mtk;
			for(int i = 0; i < shape.rows(); i++) {
				if(tk.matrix[i][i]!=0 && tk.matrix[i][i] != 1)
					tk.multiplyRow(i, 1.0/tk.matrix[i][i]);
				for(int row = i+1; row < shape.rows(); row++) {
					if(tk.matrix[row][i] != 0) {
						tk.addToRow(row, i, -tk.matrix[row][i]);
						tk.matrix[row][i] = 0d;
					}
				}
				tk.reorder(tk.rows);
			}
		} else {
			ScalarMatrixToolkit tk = (ScalarMatrixToolkit) mtk;
			for(int i = 0; i < shape.rows(); i++) {
				if(!tk.matrix[i][i].equals(0) && !tk.matrix[i][i].equals(1))
					tk.multiplyRow(i, new MReal(1.0).divide(tk.matrix[i][i]));
				for(int row = i+1; row < shape.rows(); row++) {
					if(!tk.matrix[row][i].equals(0)) {
						tk.addToRow(row, i, tk.matrix[row][i].copy().negate());
						tk.matrix[row][i] = new MReal(0);
					}
				}
				tk.reorder(tk.rows);
			}
		}
	}
	
	@Override
	public MMatrix execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	public void prepare(MathObject[] args) {
		super.prepare(args);
		if(args.length == 2 && args[0] instanceof MMatrix && args[1] instanceof MMatrix) {
			mtk = MatrixToolkit.getToolkit(((MMatrix) args[0]).augment((MMatrix) args[1]));
			mtk.setAugmCols(((MMatrix) args[1]).shape().cols());
		} else if(args.length == 2 && args[0] instanceof MMatrix && args[1] instanceof MVector) {
			mtk = MatrixToolkit.getToolkit(((MMatrix) args[0]).augment((MVector) args[1]));
			mtk.setAugmCols(1);
		} else if(args.length == 1 && args[0] instanceof MMatrix)
			mtk = MatrixToolkit.getToolkit((MMatrix) args[0]); //no need to copy as the toolkit only works with the double values.
		else
			throw new IllegalArgumentException("Arguments " + argTypesToString(args) + " not applicable for Gaussian Elimination, see help for correct use.");
		shape = new Shape(mtk.rows, mtk.cols);
	}
	
	@Override
	public Shape shape(Shape... shapes)  {
		if(shapes.length==0)
			throw new IllegalArgumentException("No arguments found.");
		if(shapes[0].dim()!=2)
			throw new ShapeException("First argument of this algorithm needs to have a shape of dimension 2, got " + shapes[0]);
		if(shapes.length==1)
			return shapes[0];
		else if(shapes.length==2)
			return new Shape(shapes[0].get(0), shapes[0].get(1) + (shapes[1].dim()==1 ? 1 : shapes[1].get(1)));
		throw new IllegalArgumentException("Algorithm not defined for shapes " + Tools.join(", ", (Object[]) shapes));
	}
}