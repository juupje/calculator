package algorithms.linalg;

import helpers.Dimension;

import mathobjects.MMatrix;
import mathobjects.MVector;
import mathobjects.MathObject;

public class JordanElimination extends GaussianElimination {

	public JordanElimination() {}
	
	public JordanElimination(MMatrix m, MMatrix b) {
		super(m, b);
	}
	
	@Override
	public MMatrix execute() {
		gauss();//First execute the Gaussian elimination
		for(int j = mtk.cols - mtk.augmcols-1; j>0; j--) {
			for(int i = j-1; i>=0; i--) {
				mtk.addToRow(i, j, -mtk.matrix[i][j]);
				mtk.matrix[i][j] = 0;
			}
		}
		return new MMatrix(mtk.matrix);
	}
	
	@Override
	public MMatrix execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	public void prepare(MathObject[] args) {
		mtk = null;
		if (args.length == 2)
			if (args[0] instanceof MMatrix) {
				if (args[1] instanceof MMatrix) {
					mtk = new MatrixToolkit(((MMatrix) args[0]).augment((MMatrix) args[1]));
					mtk.setAugmCols(((MMatrix) args[1]).dim().cols());
				} else if (args[1] instanceof MVector) {
					mtk = new MatrixToolkit(((MMatrix) args[0]).augment((MVector) args[1]));
					mtk.setAugmCols(1);
				}
			}
		if (mtk == null)
			throw new IllegalArgumentException("Arguments " + argTypesToString(args)
					+ " not applicable for Gaussian Elimination, see help for correct use.");
		else
			dim = new Dimension(mtk.rows, mtk.cols);
	}
}
