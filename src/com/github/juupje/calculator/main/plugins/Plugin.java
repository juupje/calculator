package com.github.juupje.calculator.main.plugins;

public interface Plugin {
	public String getName();
	public void run();
	public void exit();
	public int version();
}