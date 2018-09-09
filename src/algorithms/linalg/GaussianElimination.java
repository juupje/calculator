package algorithms.linalg;

import helpers.Dimension;
import mathobjects.MMatrix;

public class GaussianElimination implements LinearAlgebra {

	Dimension dim;
	MatrixToolkit mtk;
	
	public GaussianElimination(MMatrix m, MMatrix b) {
		mtk = new MatrixToolkit(m.augment(b));
		mtk.setAugmCols(b.dim().cols());
		dim = new Dimension(mtk.rows, mtk.cols);
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
	public MMatrix execute() {
		gauss();
		return new MMatrix(mtk.matrix);
	}
}
