package net.geral.slotcar.lapcounter.core;

import java.security.InvalidParameterException;
import net.geral.slotcar.lapcounter.core.styles.FreeRun;
import net.geral.slotcar.lapcounter.gui.cfgwz.ConfigurationWizard;
import net.geral.slotcar.lapcounter.gui.pilots.PilotRegistration;
import net.geral.slotcar.lapcounter.structs.Commands;

public class Commander {
	private static boolean toBoolean(final Object o) {
		if (o != null) {
			if (o instanceof Boolean) { return ((Boolean)o).booleanValue(); }
			
			if (o instanceof String) {
				String s = (String)o;
				s = s.toUpperCase();
				
				if (s.equals("YES")) { return true; }
				if (s.equals("TRUE")) { return true; }
				if (s.equals("ON")) { return true; }
				
				if (s.equals("NO")) { return false; }
				if (s.equals("FALSE")) { return false; }
				if (s.equals("OFF")) { return false; }
			}
		}
		
		Logger.error(new Exception("toBoolean: '" + o + "' is not valid. FALSE assumed."));
		
		return false;
	}
	
	private final Kernel	kernel;
	
	public Commander(final Kernel k) {
		kernel = k;
	}
	
	private boolean configure(String param) {
		param = param.toUpperCase();
		
		if ("HARDWARE".equals(param)) {
			(new ConfigurationWizard(kernel, false)).open();
			return true;
		}
		
		if ("PILOTS".equals(param)) {
			(new PilotRegistration(kernel)).open();
			return true;
		}
		
		return false;
	}
	
	private boolean displayColumns(final int n) {
		kernel.config.DisplayColumns = n;
		kernel.window.applyConfiguration();
		return true;
	}
	
	public boolean execute(final Commands c) {
		return execute(c, null);
	}
	
	public boolean execute(final Commands cmd, final Object param) {
		Logger.log("EXECUTE: " + cmd.toString() + " (" + param + ")");
		try {
			switch (cmd) {
				case EXIT:
					return exit(toBoolean(param));
				case FULLSCREEN:
					return fullscreen(toBoolean(param));
				case DISPLAY_COLUMNS:
					return displayColumns(toInt(param));
				case RESTART:
					return restart();
				case CONFIGURE:
					return configure(param.toString());
				case PAUSE:
					return pause(toBoolean(param));
				case START:
					return start(param.toString());
				default:
					Logger.error(new InvalidParameterException("Command not implemented: " + cmd.toString()));
					return false;
			}
		}
		catch (final Exception e) {
			Logger.error(e, "Command: " + cmd + ", Param: " + param);
			return false;
		}
	}
	
	public boolean execute(final String fullCommand) {
		if (fullCommand.indexOf(' ') == -1) { return execute(fullCommand, null); }
		
		final String[] parts = fullCommand.split(" ", 2);
		return execute(parts[0], parts[1]);
	}
	
	public boolean execute(String cmd, final Object param) {
		cmd = cmd.toUpperCase();
		
		if (cmd.equals("EXIT")) { return execute(Commands.EXIT, param); }
		if (cmd.equals("FULLSCREEN")) { return execute(Commands.FULLSCREEN, param); }
		if (cmd.equals("DISPLAY_COLUMNS")) { return execute(Commands.DISPLAY_COLUMNS, param); }
		if (cmd.equals("RESTART")) { return execute(Commands.RESTART, param); }
		if (cmd.equals("CONFIGURE")) { return execute(Commands.CONFIGURE, param); }
		if (cmd.equals("PAUSE")) { return execute(Commands.PAUSE, param); }
		if (cmd.equals("START")) { return execute(Commands.START, param); }
		
		Logger.error(new InvalidParameterException("Invalid command: " + cmd));
		return false;
	}
	
	private boolean exit(final boolean saveConfig) {
		Logger.log("Exiting (command), saveConfig = " + saveConfig);
		try {
			if (saveConfig) {
				kernel.config.save();
			}
		}
		finally {
			System.exit(0);
		}
		return true;
	}
	
	private boolean fullscreen(final boolean yn) {
		kernel.config.FullScreen = yn;
		kernel.window.applyConfiguration();
		return true;
	}
	
	private boolean pause(final boolean yn) {
		kernel.getController().setPaused(yn);
		return true;
	}
	
	private boolean restart() {
		kernel.getController().restart();
		return true;
	}
	
	private boolean start(final String param) {
		String type;
		String file = null;
		if (param.indexOf(' ') == -1) {
			type = param.toUpperCase();
		}
		else {
			final String[] s = param.split(" ", 2);
			type = s[0].toUpperCase();
			file = s[1];
		}
		
		if ("FREERUN".equals(type)) { return start(new FreeRun(kernel)); }
		
		Logger.error("Invalid type (" + type + "): " + param);
		return false;
	}
	
	private boolean start(final StyleController style) {
		kernel.setController(style);
		return true;
	}
	
	private int toInt(final Object o) {
		if (o != null) {
			if (o instanceof Integer) { return ((Integer)o).intValue(); }
			
			if (o instanceof String) { return Integer.parseInt((String)o); }
		}
		
		Logger.error(new Exception("toInt: '" + o + "' is not valid. ZERO assumed."));
		
		return 0;
	}
}
