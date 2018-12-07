package helpers;

public class Tools {

	public static String join(String s, Object... list) {
		String result = "";
		for(Object obj : list)
			result += obj.toString() + s;
		return result.substring(0, result.length()-s.length());
	}
	
	public static String join(String s, int[] list) {
		String result = "";
		for(int i : list)
			result += i + s;
		return result.substring(0, result.length()-s.length());
	}
	
	public static String join(String s, String... list) {
		String result = "";
		for(String str : list)
			result += str + s;
		return result.substring(0, result.length()-s.length());
	}
	
	public static double remainder(double a, double b) {
		return a - b*Math.floor(a/b);
	}
	
	public static double reduce(double num, double a, double b) {
		if(a>b) {
			double temp = a;
			a = b;
			b = temp;
		} else if(a==b)
			return a;
		if(num>a && num<b)
			return num;
		double interval = b-a;
		return num-Math.floor(num/interval)*interval+a;
	}
}