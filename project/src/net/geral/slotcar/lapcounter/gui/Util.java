package net.geral.slotcar.lapcounter.gui;

import java.awt.Color;

public class Util {
	public static Color makeDarker(final Color color, final double factor) {
		return new Color(Math.max((int)(color.getRed() * factor), 0),
				Math.max((int)(color.getGreen() * factor), 0),
				Math.max((int)(color.getBlue() * factor), 0));
	}
	
	public static String makeString(final int laps, final double last, final double best) {
		final String format = "LAP %3d  %6.3f  (BEST %6.3f)";
		if (laps < 0) return String.format(format, 0, 0.0, 0.0).replace('0', '-');
		if (laps == 0) return String.format(format, 0, 9.999, 9.999).replace('9', '-');
		return String.format(format, laps, last, best);
	}
	
	public static String secs2h_m_s_1ms(final double elapsed) {
		int s = (int)elapsed;
		final int ms = (int)((elapsed - s) * 10);
		int m = s / 60;
		final int h = m / 60;
		
		s %= 60;
		m %= 60;
		
		return String.format("%02d:%02d:%02d.%d", h, m, s, ms);
	}
	
	private Util() {}
}
