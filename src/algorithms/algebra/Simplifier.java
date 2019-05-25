package algorithms.algebra;

import main.Operator;
import mathobjects.MathObject;
import tree.DFSTask;
import tree.Node;

public class Simplifier {
	
	static DFSTask numericOperants = new DFSTask() {
		@Override
		public void accept(Node<?> n) {
			if(n.data instanceof Operator) {
				switch((Operator) n.data) {
				case ADD:
				case SUBTRACT:
				case DIVIDE:
				case MULTIPLY:
				case POWER:
				case MOD:
					if(n.left().isNumeric() && n.right.isNumeric())
						n.replace(new Node<MathObject>(((Operator) n.data).evaluate(n.left().asMathObject(), n.right().asMathObject())));
					break;
				case NEGATE:
				case INVERT:
					if(n.left().isNumeric())
						n.replace(new Node<MathObject>(((Operator) n.data).evaluate(n.left().asMathObject())));
					break;
				default:
					break;					
				}
			}
		}
	};

	public void simplify(Node<?> n) {

		/**
		 * Idea:
		 * Simplify functions in 4 steps:
		 * 1: process all operations containing scalars only.
		 * 2: remove all unnecessary operations (adding/subtracting 0, multiplying/dividing by 1 or 0, raising to power 0 or 1 etc.)
		 * 3: reorder the tree in such a way that numbers are moved to right and variables to the left
		 * 4: repeat step 1
		 */
	}
}
