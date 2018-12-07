package algorithms;

import mathobjects.MReal;
import mathobjects.MScalar;
import mathobjects.MVector;

public class Norm {
	
	public static MReal eucl(MVector v) {
		double d = 0;
		for(int i = 0; i < v.size(); i++) {
			double d1 = ((MScalar) v.get(i).evaluate()).abs();
			d += d1*d1;
		}
		return new MReal(Math.sqrt(d));
	}
}
