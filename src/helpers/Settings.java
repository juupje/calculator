package helpers;

import java.util.ArrayList;

public class Settings {
	public static final short NORMAL = 1;
	public static final short ENG = 2;
	public static final short SCI = 3;
	public static short NOTATION = NORMAL;
	public static short PRECISSION = 3;
	public static ArrayList<String> arguments = new ArrayList<>();
	
	public static void setArgument(String[] args) {
		for(String s : args)
			arguments.add(s);
	}
	
	public static boolean existsArgument(String name) {
		return arguments.contains(name);
	} 
	
	public static String getNextArgument(String name) {
		return arguments.get(arguments.indexOf(name) + 1);
	}
	
	/**
	 * @param i the index of the argument to be returned
	 * @return the argument associated with the index.
	 */
	public static String getArgument(int i) {
		return arguments.get(i);
	}
	
}
