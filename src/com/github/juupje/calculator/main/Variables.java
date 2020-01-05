package com.github.juupje.calculator.main;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MSequence;
import com.github.juupje.calculator.mathobjects.MathObject;
public class Variables {
	private static HashMap<String, MathObject> vars = new HashMap<String, MathObject>();
	private static ArrayList<MathObject> ans = new ArrayList<>();
	
	/**
	 * Returns the variable with the given name from the map, or {@code null} if no mapping with such name exists.
	 * @param key the name of the variable to be returned.
	 * @return the variable associated with the given name.
	 * @see HashMap#get(Object)
	 */
	public static MathObject get(String key) {
		return vars.get(key);
	}
	
	public static void set(String key, MathObject value) {
		if(value instanceof MExpression) {
			Calculator.dependencyGraph.setConnections(new Variable(key), ((MExpression) value).getDependencies());
		} else if(value instanceof MSequence)
			Calculator.dependencyGraph.setConnections(new Variable(key), ((MSequence) value).getFunction().getDependencies());			
		vars.put(key.trim(), value);
		Calculator.dependencyGraph.onValueChanged(new Variable(key));
	}
	
	public static boolean exists(String str) {
		return vars.containsKey(str);
	}

	public static void remove(String string) {
		vars.remove(string);
	}
	
	public static void ans(MathObject mo) {
		ans.add(mo);
	}
	
	public static MathObject ans() {
		if(ans.size()==0) return null;
		return ans.get(ans.size()-1);
	}
	
	public static MathObject ans(int i) {
		if(i<=0)
			return ans.get(ans.size()+i-1);
		return ans.get(i-1);
	}

	public static HashMap<String, MathObject> getAll() {
		return vars;
	}
}