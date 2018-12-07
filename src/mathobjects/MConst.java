package mathobjects;

public enum MConst {
	PI(new MReal(Math.PI)),
	E(new MReal(Math.E)),
	i(new MComplex(0,1));
	
	MScalar value;
	MConst(MScalar value) {
		this.value = value;
	}
	
	public static boolean isConstant(String s) {
		try {get(s.toUpperCase());}catch(IllegalArgumentException e) {return false;}return true;
	}
	
	public static MConst get(String name) {
		return valueOf(name.toUpperCase());
	}
	
	public MScalar evaluate() {
		return value;
	}
}
