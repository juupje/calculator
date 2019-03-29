package main;
import java.util.HashMap;

import mathobjects.MExpression;
import mathobjects.MathObject;
public class Variables {
	private static HashMap<String, MathObject> vars = new HashMap<String, MathObject>();
	
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
		}
		vars.put(key, value);
	}
	
	public static boolean exists(String str) {
		return vars.containsKey(str);
	}

	public static void remove(String string) {
		vars.remove(string);
	}
	
	public static void ans(MathObject mo) {
		vars.put("ans", mo);
	}
	
	public static MathObject ans() {
		return get("ans");
	}

	public static HashMap<String, MathObject> getAll() {
		return vars;
	}
}