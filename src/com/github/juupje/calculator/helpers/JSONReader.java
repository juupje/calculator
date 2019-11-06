package com.github.juupje.calculator.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

import com.github.juupje.calculator.main.Calculator;

public class JSONReader {	
	public static JSONObject parse(String path) throws IOException {
		InputStream in = Calculator.class.getResourceAsStream(path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuffer buf = new StringBuffer();
		String s;
		if(in != null) {
			while((s = reader.readLine()) != null) {
				buf.append(s + "\n");
			}
		}
		in.close();
		reader.close();
		return new JSONObject(buf.toString());
	}
}
