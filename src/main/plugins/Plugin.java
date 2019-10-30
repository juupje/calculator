package main.plugins;

public interface Plugin {
	public String getName();
	public void run();
	public void exit();
}