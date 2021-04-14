package com.github.juupje.calculator.main;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.juupje.calculator.helpers.exceptions.CircularDefinitionException;
import com.github.juupje.calculator.helpers.exceptions.IndexException;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MSequence;
import com.github.juupje.calculator.mathobjects.MathObject;
public final class Variables {
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
	
	/**
	 * Stores a variable with name {@code key} and value {@code value} as a key-value pair.
	 * Additionally, if the value is an expression or sequence, a check is made to see if this
	 * value is dependend on its own value, using {@code Calculator.dependencyGraph.isCyclic()}
	 * 
	 * @param key the name of the variable
	 * @param value the value of the variable
	 * @throws CircularDefinitionException if this variable depends on itself
	 */
	public static void set(String key, MathObject value) {
		key = key.trim();
		Variable toCheckCyclicity = null;
		MathObject old = null;
		if(value instanceof MExpression) {
			old = get(key);
			toCheckCyclicity = new Variable(key);
			Calculator.dependencyGraph.setConnections(toCheckCyclicity, ((MExpression) value).getDependencies());
		} else if(value instanceof MSequence) {
			old = get(key);
			toCheckCyclicity = new Variable(key);
			Calculator.dependencyGraph.setConnections(toCheckCyclicity, ((MSequence) value).getFunction().getDependencies());			
		}
		//Check if the new variable results in a cyclic definition
		if(toCheckCyclicity != null) {
			if(Calculator.dependencyGraph.isCyclic()) {
				//Yep, it is cyclic: revert the dependencyGraph
				if(old==null || !(old instanceof MSequence || old instanceof MExpression))
					Calculator.dependencyGraph.remove(new Variable(key));
				else
					Calculator.dependencyGraph.setConnections(new Variable(key),
							(old instanceof MSequence ? ((MSequence)old).getFunction() : (MExpression) old).getDependencies());
				throw new CircularDefinitionException(value);
			}
		}
		//Save te variable to the map
		vars.put(key, value);
		//Call the change listeners of the objects which depend on this variable
		Calculator.dependencyGraph.onValueChanged(new Variable(key));
	}
	
	public static boolean exists(String str) {
		return vars.containsKey(str);
	}

	public static void remove(String string) {
		vars.remove(string);
	}
	
	/**
	 * Remove all variables and the entire answer history.
	 * Should only be called from {@link Calculator#reset()}.
	 */
	public static void reset() {
		vars.clear();
		ans.clear();
	}
	
	public static int ans(MathObject mo) {
		ans.add(mo);
		return ans.size()-1;
	}
	
	public static MathObject ans() {
		if(ans.size()==0) return null;
		return ans.get(ans.size()-1);
	}
	
	public static MathObject ans(int i) {
		try {
		if(i<=0)
			return ans.get(ans.size()+i-1);
		return ans.get(i-1);
		} catch(IndexOutOfBoundsException e) {
			throw new IndexException("Answer " + (i>=0 ? i : ans.size()+i-1) + " does not exist.");
		}
	}
	
	public static int getAnswerCount() {
		return ans.size();
	}

	public static HashMap<String, MathObject> getAll() {
		return vars;
	}
}