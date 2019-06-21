package algorithms.linalg;

import helpers.Shape;
import helpers.exceptions.InvalidFunctionException;
import helpers.exceptions.ShapeException;
import helpers.exceptions.TreeException;
import helpers.exceptions.UnexpectedCharacterException;
import main.Calculator;
import main.Operator;
import main.Parser;
import mathobjects.MMatrix;
import mathobjects.MReal;
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
		if(mtk.isReal()) {
			DoubleMatrixToolkit tk = (DoubleMatrixToolkit) mtk;
			for(int j = tk.cols - tk.augmcols-1; j>0; j--) {
				for(int i = j-1; i>=0; i--) {
					tk.addToRow(i, j, -tk.matrix[i][j]);
					tk.matrix[i][j] = 0d;
				}
			}
		} else {
			AlgebraicMatrixToolkit tk = (AlgebraicMatrixToolkit) mtk;
			for(int j = tk.cols - tk.augmcols-1; j>0; j--) {
				for(int i = j-1; i>=0; i--) {
					tk.addToRow(i, j, Operator.NEGATE.evaluate(tk.matrix[i][j]));
					tk.matrix[i][j] = new MReal(0);
				}
			}
		}
		return mtk.toMMatrix();
	}
	
	@Override
	public MathObject execute(String... args) {
		try {
			return execute(Parser.toMathObjects(args));
		} catch (ShapeException | UnexpectedCharacterException | InvalidFunctionException | TreeException e) {
			Calculator.errorHandler.handle(e);
			return MReal.NaN();
		}
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
					mtk = MatrixToolkit.getToolkit(((MMatrix) args[0]).augment((MMatrix) args[1]));
					mtk.setAugmCols(((MMatrix) args[1]).shape().cols());
				} else if (args[1] instanceof MVector) {
					mtk = MatrixToolkit.getToolkit(((MMatrix) args[0]).augment((MVector) args[1]));
					mtk.setAugmCols(1);
				}
			}
		if (mtk == null)
			throw new IllegalArgumentException("Arguments " + argTypesToString(args)
					+ " not applicable for Gaussian Elimination, see help for correct use.");
		else
			shape = new Shape(mtk.rows, mtk.cols);
	}
}
