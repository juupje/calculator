package helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CSVHandler {
	
	public static final short TYPE_DOUBLE = 0;
	public static final short TYPE_INT = 1;
	public static final short TYPE_LONG = 3;
	public static final short TYPE_STRING = 4;
	
	public static CSVData<?> read(String path, short type) throws URISyntaxException {
		return read(new File(CSVHandler.class.getResource(path).toURI()), type);
	}
	
	public static CSVData<?> read(String path) throws URISyntaxException {
		return read(path, TYPE_DOUBLE);
	}
	
	public static CSVData<?> read(File file) {
		return read(file, TYPE_DOUBLE);
	}
	
	public static String[] readLine(String path, int n) throws URISyntaxException {
		return readLine(new File(CSVHandler.class.getResource(path).toURI()), n);
	}
	
	public static String[] readLine(File f, int n) {
		try(BufferedReader br = new BufferedReader(new FileReader(f))) {
			for(int i = 0; i < n; i++)
				br.readLine();
			return br.readLine().split(",");
		} catch(NullPointerException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void printLine(String path, int n, double[] data) throws URISyntaxException {
		Object[] array = new Object[data.length];
		for(int i = 0; i < data.length; i++)
			array[i] = data[i];
		printLine(new File(CSVHandler.class.getResource(path).toURI()), n, array);
	}
	
	public static void printLine(String path, int n, Object[] data) throws URISyntaxException {
		printLine(new File(CSVHandler.class.getResource(path).toURI()), n, data);
	}
	
	public static void printLine(File file, int n, Object[] data) {
		try(BufferedReader br = new BufferedReader(new FileReader(file))){
			String line;
			StringBuffer inputBuffer = new StringBuffer();
			int counter = 0;
			while((line = br.readLine())!=null) {
				if(counter==n)
					inputBuffer.append(Tools.join(", ", data) + "\n");
				else
					inputBuffer.append(line + "\n");
				counter++;
			}
			if(counter<=n) {
				for(int i = counter; i < n; i++)
					inputBuffer.append("\n");
				inputBuffer.append(Tools.join(", ", data));
			}
			String inputStr = inputBuffer.toString();
			FileOutputStream fileOut = new FileOutputStream(file.getPath());
			fileOut.write(inputStr.getBytes());
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static double[] readDoubleLine(String path, int n) throws URISyntaxException {
		return readDoubleLine(new File(CSVHandler.class.getResource(path).toURI()), n);
	}
	
	public static double[] readDoubleLine(File f, int n) {
		String[] s = readLine(f, n);
		if(s==null || s[0].equals("")) return null;
		double[] d = new double[s.length];
		for(int i = 0; i < s.length; i++)
			d[i] = Double.parseDouble(s[i]);
		return d;
	}
	
	public static CSVData<?> read(File file, short type) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		int rows = lines.size();
		int cols = lines.get(0).split(",").length;
		switch(type) {
		case TYPE_INT:
			return parse(lines, s -> {
				Integer[] d = new Integer[s.length];
				for(int i = 0; i < s.length; i++)
					d[i] = Integer.parseInt(s[i]);
				return d;
			}, new Integer[rows][cols]);
		case TYPE_LONG:
			return parse(lines, s -> {
				Long[] d = new Long[s.length];
				for(int i = 0; i < s.length; i++)
					d[i] = Long.parseLong(s[i]);
				return d;
			}, new Long[rows][cols]);
		case TYPE_STRING:
			return parse(lines, s -> s, new Double[rows][cols]);
		case TYPE_DOUBLE:
		default:
			return parse(lines, s -> {
				Double[] d = new Double[s.length];
				for(int i = 0; i < s.length; i++)
					d[i] = Double.parseDouble(s[i]);
				return d;
			}, new Double[rows][cols]);
		}
	}
	
	public static <T> CSVData<T> parse(List<String> lines, Function<String[], T[]> f, T[][] array) {
		return new CSVData<T>(lines.stream().map(line -> f.apply(line.split(","))).collect(Collectors.toList()).toArray(array));
	}
}