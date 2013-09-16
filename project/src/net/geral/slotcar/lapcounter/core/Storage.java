package net.geral.slotcar.lapcounter.core;

import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.InvalidParameterException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Storage {
	private static class FileChooser extends JFileChooser {
		private static final long	serialVersionUID	= 1L;
		private final boolean		loading;
		private final String		extension;
		
		public FileChooser(final Component parent, final String description, final Object type, final boolean load) {
			super(DEFAULT_PATH);
			loading = load;
			extension = getExtension(type);
			setDialogTitle(String.format("%s %s...", load ? "Load" : "Save", description));
			setFileFilter(new FileNameExtensionFilter(String.format("%s File", description), extension));
		}
		
		@Override
		public void approveSelection() {
			if (checkFile()) super.approveSelection();
		}
		
		private boolean checkFile() {
			final File f = getSelectedFile();
			if ((f == null) || (f.getName().length() == 0)) {
				JOptionPane.showMessageDialog(this, "Invalid file name.", "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			return loading ? checkFileLoad() : checkFileSave();
		}
		
		private boolean checkFileLoad() {
			final File f = getSelectedFile();
			
			try {
				final FileReader fr = new FileReader(f);
				fr.close();
			}
			catch (final IOException e) {
				JOptionPane.showMessageDialog(this, "Error reading file:\n" + f.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			return true;
		}
		
		private boolean checkFileSave() {
			File f = getSelectedFile();
			String name = f.getName();
			if (!name.endsWith("." + extension)) name += "." + extension;
			f = new File(f.getParentFile(), name);
			final boolean exists = f.exists();
			
			// saving, check file
			if (exists) {
				final int r = JOptionPane.showConfirmDialog(this, "File already exists.\nOverwrite?", "Overwrite Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (r != JOptionPane.YES_OPTION) return false;
			}
			
			try {
				final FileWriter fw = new FileWriter(f, true);
				fw.close();
				if (!exists) f.delete(); // if did not exist before, delete it
			}
			catch (final IOException e) {
				JOptionPane.showMessageDialog(this, "Error writing file:\n" + f.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			// no problems, save (using the new name, in case it changed)
			setSelectedFile(f);
			return true;
		}
	}
	
	public static final String	DEFAULT_PATH	= "save";
	public static final String	DEFAULT_FILE	= "autosave";
	
	public static File choosefile(final Component parent, final String description, final Object type, final boolean load) {
		final FileChooser fc = new FileChooser(parent, description, type, load);
		
		final int r = load ? fc.showOpenDialog(parent) : fc.showSaveDialog(parent);
		if (r != FileChooser.APPROVE_OPTION) return null;
		return fc.getSelectedFile();
	}
	
	public static File defaultFile(final Object o) {
		final String name = DEFAULT_FILE + "." + getExtension(o);
		return new File(DEFAULT_PATH, name);
	}
	
	public static String getExtension(final Object o) {
		return o.getClass().getSimpleName().toLowerCase();
	}
	
	public static void load(final Object target) {
		// load only if file exists
		final File f = defaultFile(target);
		if (f.isFile()) {
			load(target, f);
		}
		else {
			Logger.log("Cannot load (not found): " + f.getAbsolutePath());
		}
	}
	
	public static void load(final Object target, final File f) {
		Logger.log("Loading " + f.getAbsolutePath());
		
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(f));
			
			String l;
			while ((l = r.readLine()) != null) {
				try {
					loadValue(target, l);
				}
				catch (final Exception e) {
					Logger.error(e);
				}
			}
		}
		catch (final FileNotFoundException e) {
			Logger.error(e);
		}
		catch (final IOException e) {
			Logger.error(e);
		}
		finally {
			try {
				if (r != null) r.close();
			}
			catch (final Exception e) {};
		}
	}
	
	private static boolean loadPrimitive(final Object t, final Field f, final String value) throws IllegalArgumentException, IllegalAccessException {
		final String s = f.getType().getName();
		
		if (s.equals("boolean")) { return loadValue(t, f, Boolean.valueOf(value)); }
		if (s.equals("int")) { return loadValue(t, f, Integer.valueOf(value)); }
		
		Logger.error("Cannot load primitive " + s + ": " + value);
		return false;
	}
	
	private static boolean loadString(final Object t, final Field f, final String value) throws IllegalArgumentException, IllegalAccessException {
		f.set(t, value);
		return true;
	}
	
	private static boolean loadValue(final Color[] o, final int index, final String value) {
		o[index] = parseColor(value);
		return true;
	}
	
	private static boolean loadValue(final int[] o, final int index, final String value) {
		o[index] = Integer.parseInt(value);
		return true;
	}
	
	private static boolean loadValue(final Object t, final Field f, final Boolean b) throws IllegalArgumentException, IllegalAccessException {
		f.set(t, b);
		return true;
	}
	
	private static boolean loadValue(final Object t, final Field f, final Integer i) throws IllegalArgumentException, IllegalAccessException {
		f.set(t, i);
		return true;
	}
	
	private static boolean loadValue(final Object t, final String line) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		final String[] pair = line.split("=", 2);
		if (pair.length != 2) {
			Logger.error("Error loading configuration @ " + line);
			return false;
		}
		
		// not array
		if (pair[0].indexOf(':') == -1) { return loadValue(t, pair[0], pair[1]); }
		
		// array
		final String[] apair = pair[0].split(":", 2);
		if (apair.length != 2) {
			Logger.error("Error loading array configuration @ " + line);
			return false;
		}
		return loadValue(t, apair[0], Integer.parseInt(apair[1]), pair[1]);
	}
	
	private static boolean loadValue(final Object t, final String name, final int index, final String value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		final Field f = t.getClass().getField(name);
		final Class<?> c = f.getType();
		final Object o = f.get(t);
		
		if (!c.isArray()) {
			Logger.error("Not array: " + c);
		}
		
		if (o instanceof Color[]) { return loadValue((Color[])o, index, value); }
		if (o instanceof int[]) { return loadValue((int[])o, index, value); }
		
		Logger.error("Cannot load: " + name + " (" + f.getType() + ")");
		return false;
	}
	
	private static boolean loadValue(final Object t, final String name, final String value) throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
		Field f = null;
		try {
			f = t.getClass().getField(name);
		}
		catch (final NoSuchFieldException e) {
			Logger.log("No such field from configuration: " + name + "=" + value + "; skipping...");
			return false;
		}
		
		final Class<?> c = f.getType();
		
		if (c.isPrimitive()) { return loadPrimitive(t, f, value); }
		if (c == String.class) { return loadString(t, f, value); }
		
		Logger.error("Cannot load: " + name + " (" + f.getType() + ")");
		
		return false;
	}
	
	public static boolean makeDirectory(final File dir) {
		if (!dir.exists()) dir.mkdirs();
		
		if (dir.isDirectory()) return true;
		
		Logger.error("Invalid directory: " + dir.getAbsolutePath());
		return false;
	}
	
	private static Color parseColor(final String value) {
		final String[] ps = value.split(";");
		if (ps.length != 3) { throw new InvalidParameterException("Invalid color: " + value); }
		
		final int r = Integer.parseInt(ps[0]);
		final int g = Integer.parseInt(ps[1]);
		final int b = Integer.parseInt(ps[2]);
		return new Color(r, g, b);
	}
	
	public static void save(final Object source) {
		// try to create dirs/files as needed
		final File f = defaultFile(source);
		save(source, f);
	}
	
	private static boolean save(final Object source, final BufferedWriter w, final Field f) {
		try {
			final Class<?> c = f.getType();
			final Object o = f.get(source);
			final String n = f.getName();
			
			if (c.isPrimitive()) { return saveValue(w, n, String.valueOf(o)); }
			if (o == null) { return saveValue(w, n, "null"); }
			if (o instanceof String) { return saveValue(w, n, (String)o); }
			if (o instanceof Color[]) { return saveValue(w, n, (Color[])o); }
			if (o instanceof int[]) { return saveValue(w, n, (int[])o); }
			
			Logger.error("Cannot save field: " + f + " (" + o + ")");
		}
		catch (final IllegalArgumentException e) {
			Logger.log(e);
		}
		catch (final IllegalAccessException e) {
			Logger.log(e);
		}
		return false;
	}
	
	public static void save(final Object source, final File file) {
		try {
			if (!makeDirectory(file.getParentFile())) return;
			
			final BufferedWriter w = new BufferedWriter(new FileWriter(file, false));
			for (final Field f : source.getClass().getDeclaredFields()) {
				final int mod = f.getModifiers();
				// save public non-static fields
				if (Modifier.isPublic(mod) && (!Modifier.isStatic(mod))) {
					save(source, w, f);
				}
			}
			w.close();
		}
		catch (final IOException e) {
			Logger.error(e);
		}
		
	}
	
	private static boolean saveValue(final BufferedWriter w, final String n, final Color color) {
		return saveValue(w, n, color.getRed() + ";" + color.getGreen() + ";" + color.getBlue());
	}
	
	private static boolean saveValue(final BufferedWriter w, final String n, final Color[] o) {
		for (int i = 0; i < o.length; i++) {
			if (!saveValue(w, n + ":" + i, o[i])) { return false; }
		}
		return true;
	}
	
	private static boolean saveValue(final BufferedWriter w, final String n, final int[] o) {
		for (int i = 0; i < o.length; i++) {
			if (!saveValue(w, n + ":" + i, String.valueOf(o[i]))) { return false; }
		}
		return true;
	}
	
	private static boolean saveValue(final BufferedWriter w, final String name, final String value) {
		try {
			final String s = String.format("%s=%s%n", name, value);
			w.write(s);
			return true;
		}
		catch (final IOException e) {
			Logger.log(e);
			return false;
		}
	}
	
	// hide constructor
	private Storage() {}
}
