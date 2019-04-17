package helpers;

import java.util.Set;

public class Tools {

	public static String join(String s, Object... list) {
		String result = "";
		for(Object obj : list)
			result += obj.toString() + s;
		return result.substring(0, result.length()-s.length());
	}
	
	public static String join(String s, Set<? extends Object> set) {
		String result = "";
		for(Object obj : set)
			result += obj.toString() + s;
		return result.substring(0, result.length()-s.length());
	}
	
	public static String join(String s, int[] list) {
		String result = "";
		for(int i : list)
			result += i + s;
		return result.substring(0, result.length()-s.length());
	}
	
	public static String join(String s, double[] list) {
		String result = "";
		for(double i : list)
			result += i + s;
		return result.substring(0, result.length()-s.length());
	}
	
	public static String join(String s, String... list) {
		String result = "";
		for(String str : list)
			result += str + s;
		return result.substring(0, result.length()-s.length());
	}
	
	public static double fact(int n) {
		if(n<0)
			throw new IllegalArgumentException("Factorial is only defined for positive integers, got " + n);
		double f = 1;
		for(int i = 2; i <= n; i++)
			f*=i;
		return f;
	}
	
	public static double remainder(double a, double b) {
		return a - b*Math.floor(a/b);
	}
	
	/**
	 * Reduces a number to fit in the interval [a,b) (including a, excluding b)
	 * @param num the number to be reduced
	 * @param a the lower bound of the interval (included)
	 * @param b the upper bound of the interval (excluded)
	 * @return the equivalent of num inside the interval [a,b)
	 */
	public static double reduce(double num, double a, double b) {
		if(a>b) {
			double temp = a;
			a = b;
			b = temp;
		} else if(a==b)
			return a;
		if(num>=a && num<b)
			return num;
		if(num==b)
			return a;
		double interval = b-a;
		return num-Math.floor(num/interval)*interval+a;
	}
}