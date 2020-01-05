package com.github.juupje.calculator.settings;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.github.juupje.calculator.main.Calculator;

public class SettingsHandler {

	private Preferences pref;
	public SettingsHandler() {
		init();
	}
	
	public void init() {
		pref = Preferences.userRoot().node("/preferences");		
	}
	
	public boolean exists(String key) {
		try {
			return pref.nodeExists(key);
		} catch (BackingStoreException e) {
			Calculator.errorHandler.handle(e);
			return false;
		}
	}
	
	public void set(Setting setting, Object value) {
		if (setting.getType().equals(Integer.class))
			pref.putInt(setting.toString(), (Integer) value);
		else if (setting.getType().equals(Double.class))
			pref.putDouble(setting.toString(), (Double) value);
		else if (setting.getType().equals(String.class))
			pref.put(setting.toString(), (String) value);
		else if (setting.getType().equals(Boolean.class))
			pref.putBoolean(setting.toString(), (Boolean) value);
	}
	
	public void set(String key, Object value) {
		if (value instanceof Integer)
			pref.putInt(key, (Integer) value);
		else if (value instanceof Double)
			pref.putDouble(key, (Double) value);
		else if (value instanceof String)
			pref.put(key, (String) value);
		else if (value instanceof Boolean)
			pref.putBoolean(key, (Boolean) value);
	}
	
	public int getInt(String key, int def) {
		return pref.getInt(key, def);
	}
	
	public boolean getBoolean(String key, boolean def) {
		return pref.getBoolean(key, def);
	}
	
	public String getString(String key, String def) {
		return pref.get(key, def);
	}
	
	public double getDouble(String key, double def) {
		return pref.getDouble(key, def);
	}
}
