package algorithms.algebra;

import tree.DFSTask;
import tree.Node;

public class Simplifier {

	public void simplify(Node<?> n) {
		DFSTask dfs = new DFSTask() {
			@Override
			public void accept(Node<?> n) {
				
			}
		};
		/**
		 * Idea:
		 * Simplify functions in 4 steps:
		 * 1: remove all unnecessary operations (adding/subtracting 0, multiplying/dividing by 1 or 0, raising to power 0 or 1 etc.)
		 * 2: reorder the tree in such a way that numbers are moved to right and variables to the left
		 * 3: process all operations containing scalars only.
		 */
	}
}
