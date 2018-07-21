package main;
import java.util.HashMap;

import mathobjects.MathObject;
public class Variables {
	private static HashMap<String, MathObject> vars = new HashMap<String, MathObject>();
	
	public static MathObject get(String key) {
		return vars.get(key);
	}
	
	public static void set(String key, MathObject value) {
		vars.put(key, value);
	}
	
	public static boolean exists(String str) {
		return vars.containsKey(str);
	}
}