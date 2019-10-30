package main.plugins;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

import main.Calculator;

public class PluginLoader {
	
	private static ArrayList<Plugin> loadedPlugins;
	
	public static void load() {
		File dir = new File(System.getProperty("user.dir")+ File.separator + "plugins");
		System.out.println(dir);
		if(!dir.exists() || !dir.isDirectory()) return;

        File[] flist = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {return file.getPath().toLowerCase().endsWith(".jar");}
        });
        URL[] urls = new URL[flist.length];
        for (int i = 0; i < flist.length; i++)
			try {
				Calculator.ioHandler.out("Loading " + flist[i].getName());
				urls[i] = flist[i].toURI().toURL();
			} catch (MalformedURLException e) {
				Calculator.errorHandler.handle("Couldn't load jar.", e);
			}
        URLClassLoader ucl = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        ServiceLoader<Plugin> sl = ServiceLoader.load(Plugin.class, ucl);
        
        loadedPlugins = new ArrayList<>();
        Iterator<Plugin> apit = sl.iterator();
        while (apit.hasNext()) {
        	Plugin plugin = apit.next();
            plugin.run();
            loadedPlugins.add(plugin);
            Calculator.ioHandler.out("Loaded plugin '" + plugin.getName() + "'");
        }
	}
	
	public static void exit() {
		for(Iterator<Plugin> iter = loadedPlugins.iterator(); iter.hasNext();) {
			iter.next().exit();
			iter.remove();
		}
	}
}