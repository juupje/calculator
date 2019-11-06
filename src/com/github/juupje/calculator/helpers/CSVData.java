package com.github.juupje.calculator.helpers;

public class CSVData<T> {
	String[] columnNames;
	
	T[][] data;
	int rows, cols;
	public CSVData() {
		data = null;
		rows = cols = 0;
	}
	
	public CSVData(T[][] data) {
		this.data = data;
	}
	
	public T get(int row, int col) {
		if(row < 0 || col < 0 || row>=rows || col>=cols)
			throw new ArrayIndexOutOfBoundsException("CSV data has " + rows + " rows and " + cols + " columns, trying to access index " + row + " , " + col);
		return data[row][col];
	}
	
	public T[] row(int row) {
		if(row < 0 ||row>=rows)
			throw new ArrayIndexOutOfBoundsException("CSV data has " + rows + " rows, trying to access index " + row);
		return data[row];
	}
	
	@SuppressWarnings("unchecked")
	public T[] col(int col) {
		if(col < 0 || col>=cols)
			throw new ArrayIndexOutOfBoundsException("CSV data has " + cols + " columns, trying to access index " + col);
		Object[] result = new Object[data.length];
		for(int i = 0; i < data.length; i++)
			result[i] = data[i][col];
		return (T[]) result;
	} 
	
	public int rows() {
		return rows;
	}
	
	public int cols() {
		return cols;
	}
}
