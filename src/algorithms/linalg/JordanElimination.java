package algorithms.linalg;

import mathobjects.MMatrix;

public class JordanElimination extends GaussianElimination {

	public JordanElimination(MMatrix m, MMatrix b) {
		super(m, b);
	}
	
	protected void jordan() {
		gauss();
		for(int j = mtk.cols - mtk.augmcols-1; j>0; j--) {
			for(int i = j-1; i>=0; i--) {
				mtk.addToRow(i, j, -mtk.matrix[i][j]);
				mtk.matrix[i][j] = 0;
			}
		}
	}
	
	@Override
	public MMatrix execute() {
		jordan();
		return new MMatrix(mtk.matrix);
	}

}
