package com.github.juupje.calculator.helpers;

import java.util.Set;

import com.github.juupje.calculator.algorithms.Algorithms;
import com.github.juupje.calculator.algorithms.Functions;
import com.github.juupje.calculator.helpers.exceptions.SyntaxException;

public class Tools {

	public static String join(String s, Object... list) {
		if(list.length==0) return "";
		StringBuilder builder = new StringBuilder();
		for(Object obj : list)
			builder.append(obj).append(s);
		return builder.substring(0, builder.length()-s.length());
	}
	
	public static String join(String s, Set<? extends Object> set) {
		if(set.size()==0) return "";
		StringBuilder builder = new StringBuilder();
		for(Object obj : set)
			builder.append(obj.toString()).append(s);
		return builder.substring(0, builder.length()-s.length());
	}
	
	public static String join(String s, int[] list) {
		if(list.length==0) return "";
		StringBuilder builder = new StringBuilder();
		for(int i : list)
			builder.append(i).append(s);
		return builder.substring(0, builder.length()-s.length());
	}
	
	public static String join(String s, double[] list) {
		if(list.length==0) return "";
		StringBuilder builder = new StringBuilder();
		for(double i : list)
			builder.append(i).append(s);
		return builder.substring(0, builder.length()-s.length());
	}
	
	public static String join(String s, String... list) {
		if(list.length==0) return "";
		StringBuilder builder = new StringBuilder();
		for(String str : list)
			builder.append(str).append(s);
		return builder.substring(0, builder.length()-s.length());
	}
	
	public static boolean insideBrackets(String s, int index) {
		int brBefore = bracketLevel(s, index);
		if(brBefore == 0) return false;
		int brAfter = bracketLevel(s.substring(index+1), s.length()-index-1);
		if(brAfter != 0) {
			if(brAfter + brBefore == 0)
				return true;
			else throw new SyntaxException("Inconsistent brackets in '" + s + "'");
		} else
			return false;
	}
	
	private static int bracketLevel(String s, int index) {
		int count = 0;
		for(int i = 0;i<index; i++) {
			char c = s.charAt(i);
			if(c=='(' || c == '[' || c == '{') {
				count++;
			} else if(c==')' || c == ']' || c=='}')
				count--;
		}
		return count;
	}
	
	public static boolean checkNameValidity(String name) {
		return checkNameValidity(name, false);
	}
	
	public static boolean checkNameValidity(String name, boolean internal) {
		if(((name.startsWith("_") || name.startsWith("$")) && !internal) ||  Character.isDigit(name.codePointAt(0))) return false;
		if(name.equals("pi") || name.equals("e") || name.equals("i") || Functions.isFunction(name) || Algorithms.isAlgorithm(name)) return false;
		for(int i = 0; i < name.length(); i++) {
			int c = name.codePointAt(i);
			if(c<65 && c>90 && c<97 && c>120 && c<945 && c>969 && c<913 && c>937 && c<30 && c<48 && c>57)
				return false;
		}
		return true;
	}
	
	public static String extractName(String s) {
		int index = 0;
		while(index!=-1) {
			index = s.indexOf("=", index);
			if (index > 0 && !insideBrackets(s, index)) {
				if(s.charAt(index-1)==':') index--;
				int brIndex = s.indexOf('(');
				if(brIndex>0 && brIndex<index) index = brIndex;
				return s.substring(0, index);
			}
		}
		return null;
	}
	
	public static String extractFirst(String s, String begin, String end) {
		int index = s.indexOf(begin)+begin.length();
		return s.substring(index, s.indexOf(end, index));
	}
	
	public static String extractFirst(String s, char begin, char end) {
		int index = s.indexOf(begin)+1;
		return s.substring(index, s.indexOf(end, index));
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
			return reduce(num, b, a);
		} else if(a==b)
			return a;
		if(num>=a && num<b)
			return num;
		if(num==b)
			return a;
		double interval = b-a;
		return num-Math.floor(num/interval)*interval+a;
	}
	
	public static double clamp(double val, double min, double max) {
		if(min==max) return min;
		if(val<min) return min;
		if(val>max) return max;
		return val;
	}
}