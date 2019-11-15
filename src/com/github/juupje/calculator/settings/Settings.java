package com.github.juupje.calculator.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import com.github.juupje.calculator.helpers.JSONReader;
import com.github.juupje.calculator.main.Calculator;
import com.github.juupje.calculator.main.Parser;

public enum Settings {

	//Calculation stuff
	PRECISION(Integer.class),
	COMPLEX_ENABLED(Boolean.class),

	//Algoritm stuff
	TIMER_DEF_RUNS(Integer.class),
	INT_DEF_STEPS(Integer.class),
	ABC_SHOW_TEXT(Boolean.class),

	//Display stuff
	NOTATION(Integer.class),
	COMPLEX_IN_POLAR(Boolean.class),
	SHOW_STACKTRACE(Boolean.class),
	MULTILINE_MATRIX(Boolean.class);
	
	// Constants
	public static final short NORMAL = 1;
	public static final short ENG = 2;
	public static final short SCI = 3;
	
	public static HashMap<String, Setting> settings = new HashMap<String, Setting>();
	static {
		for(Settings s : Settings.values())
			settings.put(s.toString().toLowerCase(), s.getSetting());
	}

	public static HashMap<Setting, Object> map = new HashMap<Setting, Object>();

	// Runtime arguments
	public static ArrayList<String> arguments = new ArrayList<>();

	Setting setting;

	Settings(Class<?> c) {
		setting = new Setting(name(), c);
	}

	public Class<?> getType() {
		return setting.type;
	}
	
	public Setting getSetting() {
		return setting;
	}

	public static void set(Settings s, Object value) {
		set(s.getSetting(), value);
	}
	
	/**
	 * Sets the value of the given setting to given value.
	 * 
	 * @param setting the <tt>Setting</tt> whose value will be changed.
	 * @param value   the new value of <tt>setting</tt>.
	 */
	public static void set(Setting setting, Object value) {
		if (value == null)
			return;
		if (value.getClass().equals(setting.getType())) {
			Calculator.settingsHandler.set(setting, value);
			map.put(setting, value);
		} else
			throw new IllegalArgumentException("Setting " + setting.toString().toLowerCase() + " expects value of type "
					+ setting.getType() + ", got " + value.getClass());
	}

	/**
	 * Sets the value of the given setting to given value.
	 * 
	 * @param s the name of the <tt>Setting</tt> whose value will be changed.
	 * @param o the new value of that <tt>Setting</tt>.
	 */
	public static void set(String s, Object o) {
		Settings setting = null;
		try {
			setting = valueOf(s.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("There is no setting with name '" + s + "'.");
		}
		if (setting != null)
			set(setting, o);
	}
	
	public static int getInt(Settings setting) {
		return getInt(setting.getSetting());
	}

	public static double getDouble(Settings setting) {
		return getDouble(setting.getSetting());
	}

	public static boolean getBool(Settings setting) {
		return getBool(setting.getSetting());
	}

	public static String getString(Settings setting) {
		return getString(setting.getSetting());
	}
	
	public static int getInt(Setting setting) {
		Object obj = map.get(setting);
		return obj == null ? 0 : (Integer) obj;
	}

	public static double getDouble(Setting setting) {
		Object obj = map.get(setting);
		return obj == null ? 0 : (Double) obj;
	}

	public static boolean getBool(Setting setting) {
		Object obj = map.get(setting);
		return obj == null ? false : (Boolean) obj;
	}

	public static String getString(Setting setting) {
		Object obj = map.get(setting);
		return obj == null ? null : (String) obj;
	}

	public static void loadPrefs() {
		Calculator.ioHandler.out("Loading preferences...");
		SettingsHandler sh = Calculator.settingsHandler;
		if (sh.getBoolean("firstTime", true)) {
			sh.set("firstTime", false);
			Calculator.ioHandler.out("Couldn't find settings, using defaults.");
			resetVariables();
			return;
		}
		for (Settings s : values()) {
			if (s.getType().equals(Integer.class)) {
				set(s, sh.getInt(s.toString(), getInt(s)));
			} else if (s.getType().equals(Double.class))
				set(s, sh.getDouble(s.toString(), getDouble(s)));
			else if (s.getType().equals(String.class))
				set(s, sh.getString(s.toString(), getString(s)));
			else if (s.getType().equals(Boolean.class))
				set(s, sh.getBoolean(s.toString(), getBool(s)));
		}
		Calculator.ioHandler.out("Settings were (re)loaded.");
	}

	public static void resetVariables() {
		try {
			JSONObject json = JSONReader.parse("/files/defaultsettings.json");
			for (Settings setting : values())
				set(setting, json.get(setting.toString().toLowerCase()));
		} catch (IOException e) {
			Calculator.errorHandler.handle(e);
		}
	}

	public static void setArgument(String[] args) {
		for (String s : args)
			arguments.add(s);
	}
	
	public static String[] getArguments() {
		String[] args = new String[arguments.size()];
		return arguments.toArray(args);
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

	public static void processCommand(String s) {
		String[] args = Parser.getArguments(s);

		if (args.length == 1) {
			if (args[0].equals("reset") && Calculator.ioHandler
					.askYesNo("Are you sure you want to reset all settings to their default values?"))
				resetVariables();
			else {
				try {
					Calculator.ioHandler.out("Setting " + args[0].toUpperCase() + " is set to "
							+ map.get(settings.get(args[0].toUpperCase())));
				} catch (IllegalArgumentException e) {
					Calculator.ioHandler.err("No such setting exists.");
				}
			}
		} else if (args.length == 2) {
			if (args[1].equals("true") || args[1].equals("on"))
				set(args[0], true);
			else if (args[1].equals("false") || args[1].equals("off"))
				set(args[0], false);
			else {
				try {
					double num = Double.parseDouble(args[1]);
					if ((int) num - num == 0)
						set(args[0], new Integer((int) num));
					else
						set(args[0], num);
				} catch (NumberFormatException e) {
					set(args[0], args[1]);
				}
			}
		} else
			throw new IllegalArgumentException("settings expected 2 arguments, got " + args.length);
	}
	
	public static Setting insertSetting(String name, Object value) {
		Setting s = new Setting(name, value.getClass());
		if(settings.containsKey(name)) return settings.get(name);
		settings.put(name, s);
		set(s, value);
		return s;
	}
}