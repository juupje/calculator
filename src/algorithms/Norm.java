package algorithms;

import mathobjects.MScalar;
import mathobjects.MVector;

public class Norm {
	
	public static MScalar eucl(MVector v) {
		double d = 0;
		for(int i = 0; i < v.size(); i++) {
			double d1 = ((MScalar) v.get(i).evaluate()).getValue();
			d += d1*d1;
		}
		return new MScalar(Math.sqrt(d));
	}
}
