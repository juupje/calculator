package main;

import mathobjects.MathObject;

public enum Operator {
	ADD {
		@Override
		public MathObject evaluate(MathObject a, MathObject b) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	
	SUBSTACT {
		@Override
		public MathObject evaluate(MathObject a, MathObject b) {
			return null;
		}
	};
	
	public abstract MathObject evaluate(MathObject a, MathObject b);
}
