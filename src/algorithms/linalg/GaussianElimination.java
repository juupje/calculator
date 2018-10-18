package algorithms.linalg;

import algorithms.Algorithm;
import helpers.Dimension;
import mathobjects.MMatrix;
import mathobjects.MVector;
import mathobjects.MathObject;

public class GaussianElimination extends Algorithm {

	Dimension dim;
	MatrixToolkit mtk;
	
	public GaussianElimination() {}
	
	public GaussianElimination(MMatrix m, MMatrix b) {
		mtk = new MatrixToolkit(m.augment(b));
		mtk.setAugmCols(b.dim().cols());
		dim = new Dimension(mtk.rows, mtk.cols);
		prepared = true;
	}
	
	@Override
	public MMatrix execute() {
		if(!prepared) return null;
		gauss();
		return new MMatrix(mtk.matrix);
	}
	
	protected void gauss() {
		mtk.reorder(mtk.rows);
		for(int i = 0; i < dim.rows(); i++) {
			mtk.multiplyRow(i, 1.0/mtk.matrix[i][i]);
			for(int row = i+1; row < dim.rows(); row++) {
				if(mtk.matrix[row][i] != 0) {
					mtk.addToRow(row, i, -mtk.matrix[row][i]);
					mtk.matrix[row][i] = 0;
				}
			}
			mtk.reorder(mtk.rows);
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
			mtk = new MatrixToolkit(((MMatrix) args[0]).augment((MMatrix) args[1]));
			mtk.setAugmCols(((MMatrix) args[1]).dim().cols());
		} else if(args.length == 2 && args[0] instanceof MMatrix && args[1] instanceof MVector) {
			mtk = new MatrixToolkit(((MMatrix) args[0]).augment((MVector) args[1]));
			mtk.setAugmCols(1);
		} else if(args.length == 1 && args[0] instanceof MMatrix)
			mtk = new MatrixToolkit((MMatrix) args[0]); //no need to copy as the toolkit only works with the double values.
		else
			throw new IllegalArgumentException("Arguments " + argTypesToString(args) + " not applicable for Gaussian Elimination, see help for correct use.");
		dim = new Dimension(mtk.rows, mtk.cols);
	}
}