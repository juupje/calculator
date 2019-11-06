package com.github.juupje.calculator.main;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.github.juupje.calculator.helpers.exceptions.UnexpectedCharacterException;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;

public class VectorParser extends Parser {
	ArrayList<MathObject> v;
	
	public VectorParser(String s) {
		super(s);
		expr = expr.substring(1, expr.length()-1);
		expr = expr.replace(" ",  "");
		v = new ArrayList<>();
	}
	
	private boolean exprContainsInVector(int c) {
		int index = -1;
		while((index = expr.indexOf((char) c, index+1))!=-1) {
			int indexleft, indexright;
			indexright = indexleft = index;
			while(indexleft >= 0 && !"[]".contains("" + expr.charAt(indexleft))) indexleft--;
			while(indexright < expr.length() && !"[]".contains("" + expr.charAt(indexright))) indexright++;
			if((indexleft >= 0 && expr.charAt(indexleft) == '[') && (indexright < expr.length() && expr.charAt(indexright) == ']'))
				return true;
		}
		return false;
	}
	
	private boolean exprContainsNotInVector(int c) {
		return !exprContainsInVector(c) && expr.contains("" + (char) c);
	}
	
	@SuppressWarnings("serial")
	public MathObject parse() throws UnexpectedCharacterException {
		nextChar();
		if(exprContainsNotInVector(';')) { //trying to create a matrix.
			ArrayList<String> rows = toElements(expr, ';');
			if(rows.size()==2 && rows.get(1).equals("")) {
				return new MMatrix(new ArrayList<MVector>() {{ add((MVector) new VectorParser("[" + rows.get(0) + "]").parse());}});
			}
			return new MMatrix(rows.stream().map(s -> {
				try {
					return (MVector) new VectorParser("[" + s + "]").parse();
				} catch (UnexpectedCharacterException e) {
					Calculator.errorHandler.handle(e);
					return null;
				}
			}).collect(Collectors.toList()));
		} else {
			ArrayList<String> elements = toElements(expr, ',');
			MathObject[] v = new MathObject[elements.size()];
			for(int i = 0; i < elements.size(); i++) {
				try {
					v[i] = new MReal(Double.parseDouble(elements.get(i)));
				} catch(NumberFormatException e) {
					MExpression me =  new MExpression(elements.get(i));
					if(!me.getTree().getRoot().isInternal() && me.getTree().getRoot().getData() instanceof MathObject)
						v[i] = (MathObject) me.getTree().getRoot().data;
					else v[i] = me;
				}
			}
			return new MVector(v);
		}
	}

	private ArrayList<String> toElements(String s, char separator) {
		int position = 0;
		int brcount = 0;
		ArrayList<String> elements = new ArrayList<String>();
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			brcount += (c == '[' ? 1 : (c == ']' ? -1 : 0));
			if(brcount == 0 && c == separator) {
				elements.add(s.substring(position, i));
				position = i + 1;
			}
		}
		elements.add(s.substring(position, s.length()));
		return elements;
	}
}
