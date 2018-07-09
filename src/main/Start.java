package main;

import mathobjects.MScalar;
import mathobjects.MVector;
import mathobjects.MathObject;

public class Start {

	public static void main(String[] args) {
		MScalar s2 = new MScalar(4);
		MVector v2 = new MVector(new double[] {1,2,3});
		MScalar s1 = new MScalar(3);
		MVector v1 = new MVector(new MathObject[] {s1, s2,s1});
		
		System.out.println("BEFORE:");
		System.out.println(v1);
		System.out.println(v2);
		System.out.println(s1);
		System.out.println(s2);
		System.out.println("OPERATIONS:");
		System.out.println(Operator.ADD.evaluate(v1, v2));
		MVector v3 = (MVector) v2.copy();
		v3.set(2, 10);
		System.out.println(Operator.ADD.evaluate(s1, s2));
		//System.out.println(Operator.ADD.evaluate(v1, s1));
		System.out.println("AFTER:");
		System.out.println(v1);
		System.out.println(v2);
		System.out.println(s1);
		System.out.println(s2);
	}

}
