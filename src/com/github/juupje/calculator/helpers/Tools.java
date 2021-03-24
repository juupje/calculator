package com.github.juupje.calculator.helpers;

import java.util.List;
import java.util.Set;

import com.github.juupje.calculator.algorithms.Algorithms;
import com.github.juupje.calculator.algorithms.Functions;
import com.github.juupje.calculator.commands.Commands;
import com.github.juupje.calculator.helpers.exceptions.SyntaxException;
import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.main.Parser;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MathObject;

public class Tools {

	public static String join(String s, Object... list) {
		if(list.length==0) return "";
		StringBuilder builder = new StringBuilder();
		for(Object obj : list)
			builder.append(obj).append(s);
		return builder.substring(0, builder.length()-s.length());
	}
	
	public static String join(String s, List<? extends Object> set) {
		if(set.size()==0) return "";
		StringBuilder builder = new StringBuilder();
		for(Object obj : set)
			builder.append(obj.toString()).append(s);
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
	
	/**
	 * Finds the index of the next closing bracket at the same level, that is: all pairs of open+close bracket in between are ignored.
	 * For example, if we start at index 5 (or 4) in {@code "-(2+(2*(4+[1,1]*[3,4]))+3"}, the index 21 will be returned.
	 * If the start index points to the character of the opening bracket, the found closing bracket will be checked to correspond to the opening bracket.
	 * When that is not the case, an {@link UnexpectedCharacterException} is thrown.
	 * @param s the String in which the next closing bracket should be sought.
	 * @param begin the index at which the search starts
	 * @return the index of the closing bracket.
	 * @throws UnexpectedCharacterException when the closing bracket does not correspond to the opening bracket (if there is one).
	 */
	public static int findEndOfBrackets(String s, int begin) throws UnexpectedCharacterException {
		int pos = begin;
		char ch = s.charAt(begin);
		int count=0;
		char openBr = 0;
		if(ch == '(' || ch == '{' || ch == '[')
			openBr = ch;
		else if(ch == ')' || ch == '}' || ch == ']')
			return pos;
		while(count >= 0) {
			pos += 1;
			if(pos >= s.length())
				throw new UnexpectedCharacterException("Reached end while searching for end of brackets.");
			ch = s.charAt(pos);
			count += (ch == '(' || ch == '{' || ch == '[' ? 1 : (ch == ')' || ch == '}' || ch == ']' ? -1 : 0));
		}
		if(openBr != 0) {//check if the found closing bracket matches the opening bracket.
			if((openBr == '(' && ch == ')') || (openBr == '[' && ch == ']') || (openBr=='{' && ch=='}'))
				return pos;
			else
				throw new UnexpectedCharacterException("Mismatched closing bracket: " + ch);
		}
		return pos;
	}
	
	public static String type(Object obj) {
		String type = obj.getClass().getSimpleName();
		if(type.length()!=0)
			return type;
		else {
			type = obj.getClass().getName();
			return type.substring(type.lastIndexOf(".")+1);
		}
	}
	
	public static boolean checkNameValidity(String name) {
		return checkNameValidity(name, false);
	}
	
	public static boolean checkNameValidity(String name, boolean internal) {
		if(((name.startsWith("_") || name.startsWith("$")) && !internal) ||  Character.isDigit(name.codePointAt(0))) return false;
		if(name.equals("pi") || name.equals("e") || name.equals("i") || Functions.isFunction(name) || Algorithms.isAlgorithm(name) || Commands.isCommand(name)) return false;
		for(int i = 0; i < name.length(); i++) {
			int c = name.codePointAt(i);
			if(!((c>='0' && c<='9') || (c>='A' && c<='Z') || (c>='a' && c<='z') || c=='_'))
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
	
	public static int extractInteger(String s) {
		int i;
		try {
			//find the index
			i = Integer.parseInt(s);
		} catch(NumberFormatException e) {
			MathObject mo = new Parser(s).evaluate();
			if(mo instanceof MReal && ((MReal) mo).isPosInteger())
				i = (int) ((MReal) mo).getValue();
			else
				throw new UnexpectedCharacterException(mo.toString() + " is not a valid integer.");
		}
		return i;
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