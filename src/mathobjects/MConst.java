package mathobjects;

public enum MConst {
	pi(new MReal(Math.PI)),
	e(new MReal(Math.E)),
	i(new MComplex(0,1)),
	_c(new MReal(299792458)), //m/s - speed of light in vacuum
	_e(new MReal(1.60217662)), //C - electron charge
	_mu0(new MReal(1.2566370614359173e-06)), //N/A^2 - magnetic permeability of vacuum
	_epsilon0(new MReal(8.854187817620389e-12)), //F/m - dielectic permittivity of vacuum
	_h(new MReal(6.62607004e-34)), //Js - Plank's constant
	_hbar(new MReal(1.0545718001391127e-34)), //Js - Reduced Plank's constant  hbar=h/2pi
	_G(new MReal(6.67408e-11)), //m^3/(kg s^2) - Gravitational constant
	_g(new MReal(9.80665)), //N/kg - standard gravitational acceleration on earth
	_R(new MReal(8.3144598)), //J/(K mol) - molar gas constant
	_alpha(new MReal(0.0072973525664)), //fine structure constant
	_NA(new MReal(6.022140857e+23)), //Avogadro's number
	_k(new MReal(1.38064852e-23)), //J/K - Boltzmann constant
	_sigma(new MReal(5.670367e-8)), //W/(m^2K^4) - Stefan-Boltzmann constant
	_wien(new MReal(0.0028977729)), //m*K - Wien's displacement constant
	_rydberg(new MReal(10973731.568508)), //1/m - Rydberg constant
	_m_e(new MReal(9.10938356e-31)), //kg - electron mass
	_m_p(new MReal(1.672621898e-27)), //kg - proton mass
	_m_n(new MReal(1.674927471e-27)), //kg - neutron mass
	_u(new MReal(1.66053904e-27)); //kg - atomic mass unit
	
	MScalar value;
	MConst(MScalar value) {
		this.value = value;
	}
	
	public static boolean isConstant(String s) {
		try {get(s);}catch(IllegalArgumentException e) {return false;}return true;
	}
	
	public static MConst get(String name) {
		return valueOf(name);
	}
	
	public String toString() {
		if(name().startsWith("_"))
			return name().substring(1);
		return name();
	}
	
	public MScalar evaluate() {
		return value.copy();
	}
}
