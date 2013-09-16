package net.geral.slotcar.lapcounter.core;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

public abstract class Logger {
	public static final String LOG_FILE = "last.log";

	private static PrintStream logFile;
	private static boolean showingError = false;

	public static void error(Exception e) {
		error(e, null);
	}

	public static synchronized void error(Exception e, String info) {
		if (showingError)
			return;
		showingError = true;

		e.printStackTrace();
		if (info != null)
			System.err.println("Details: " + info);

		synchronized (logFile) {
			e.printStackTrace(logFile);
			if (info != null)
				logFile.println("Details: " + info);
			logFile.flush();
		}

		String msg = e.getClass().getSimpleName()
				+ ": details saved to log.\n\nAbort program?";
		int r = JOptionPane.showConfirmDialog(null, msg, "Error",
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
		if (r == JOptionPane.YES_OPTION)
			System.exit(1);

		showingError = false;
	}

	public static void error(String message) {
		error(new Exception(message));
	}

	public static void init() {
		try {
			File f = new File(LOG_FILE);
			if (f.exists())
				f.delete();
			logFile = new PrintStream(LOG_FILE);
		} catch (Exception e) {
			String msg = "*** Cannot create log ***\n" + e.getMessage();
			System.err.println(msg);
			JOptionPane.showMessageDialog(null, msg, "Fatal Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	public static void log(Exception e) {
		log(e, null);
	}

	public static void log(Exception e, String info) {
		e.printStackTrace();
		if (info != null)
			System.err.println("Details: " + info);

		synchronized (logFile) {
			e.printStackTrace(logFile);
			if (info != null)
				logFile.println("Details: " + info);
			logFile.flush();
		}
	}

	public static void log(Object o) {
		Class<?> c = o.getClass();

		log("--- " + c.getName() + " ---");

		Field[] fs = c.getFields();
		for (Field f : fs) {
			try {
				log(f.toString() + " = " + f.get(o).toString());
			} catch (Exception e) {
				log(f.toString() + " {" + e.toString() + "}");
			}
		}

		Method[] ms = c.getMethods();
		for (Method m : ms) {
			log(m.toString());
		}

		log("--- " + c.getName() + " ---");
	}

	public static void log(String msg) {
		System.out.println(msg);
		synchronized (logFile) {
			logFile.println(msg);
			logFile.flush();
		}
	}
}
