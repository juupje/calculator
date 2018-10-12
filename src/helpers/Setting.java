package helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.json.JSONObject;

import main.Calculator;
import main.Parser;

public enum Setting {

	NOTATION(Integer.class), PRECISION(Integer.class), DEF_INT_STEPS(Integer.class), DEF_TIMER_RUNS(Integer.class);

	// Constants
	public static final short NORMAL = 1;
	public static final short ENG = 2;
	public static final short SCI = 3;

	public static HashMap<Setting, Object> map = new HashMap<Setting, Object>();
	private static Preferences pref = Preferences.userRoot().node("/preferences");

	// Runtime arguments
	public static ArrayList<String> arguments = new ArrayList<>();

	Class<?> type;

	Setting(Class<?> c) {
		type = c;
	}

	public Class<?> getType() {
		return type;
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
			map.put(setting, value);
			if (setting.getType().equals(Integer.class))
				pref.putInt(setting.toString(), (Integer) value);
			else if (setting.getType().equals(Double.class))
				pref.putDouble(setting.toString(), (Double) value);
			else if (setting.getType().equals(String.class))
				pref.put(setting.toString(), (String) value);
			else if (setting.getType().equals(Boolean.class))
				pref.putBoolean(setting.toString(), (Boolean) value);
			try {
				pref.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
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
		Setting setting = null;
		try {
			setting = valueOf(s.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("There is no setting with name '" + s + "'.");
		}
		if (setting != null)
			set(setting, o);
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
		System.out.println("Loading preferences...");
		if (pref.getBoolean("firstTime", true)) {
			pref.putBoolean("firstTime", false);
			System.out.println("Couldn't find settings, using defaults.");
			resetVariables();
			return;
		}
		for (Setting s : values()) {
			if (s.getType().equals(Integer.class)) {
				set(s, pref.getInt(s.toString(), getInt(s)));
			} else if (s.getType().equals(Double.class))
				set(s, pref.getDouble(s.toString(), getDouble(s)));
			else if (s.getType().equals(String.class))
				set(s, pref.get(s.toString(), getString(s)));
			else if (s.getType().equals(Boolean.class))
				set(s, pref.getBoolean(s.toString(), getBool(s)));
		}
		Calculator.ioHandler.out("Settings were (re)loaded.");
	}

	public static void resetVariables() {
		// Read settings from default JSON file
		try {
			File file = new File(Calculator.class.getResource("/files/defaultsettings.json").toURI());
			FileInputStream in = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			in.read(data);
			in.close();
			String s = new String(data, "UTF-8");
			JSONObject json = new JSONObject(s);
			for (Setting setting : values())
				set(setting, json.get(setting.toString().toLowerCase()));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static void setArgument(String[] args) {
		for (String s : args)
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

	public static void processCommand(String s) {
		String[] args = Parser.getArguments(s);

		if (args.length == 1) {
			if (args[0].equals("reset") && Calculator.ioHandler
					.askYesNo("Are you sure you want to reset all settings to their default values?"))
				resetVariables();
			else {
				try {
					Calculator.ioHandler.out("Setting " + args[0].toUpperCase() + " is set to "
							+ map.get(valueOf(args[0].toUpperCase())));
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
}