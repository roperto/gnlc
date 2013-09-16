package net.geral.slotcar.lapcounter.gui.cfgwz.cards.subs;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

public class MiniLaneLapInfo extends JPanel {
	private static final long	serialVersionUID	= 1L;
	
	private static final String	FORMAT				= "Lane %d: %6.3f (%5.3f)";
	private static final String	CLEAR_FORMAT		= String.format("Lane %%d: %06.3f (%05.3f)", 0.0, 0.0).replace('0', '-');
	
	private final JLabel		label				= new JLabel("Lane X: X.XXX (X.XXX)");
	
	private String				clear				= "";
	private int					lane				= 0;
	
	public MiniLaneLapInfo() {
		setBorder(new EmptyBorder(1, 1, 1, 1));
		setLayout(new BorderLayout(0, 0));
		
		label.setBackground(Color.WHITE);
		label.setOpaque(true);
		label.setBorder(new LineBorder(new Color(0, 0, 0)));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Monospaced", Font.BOLD, 12));
		add(label, BorderLayout.CENTER);
		
	}
	
	public void setLane(Color bg, int number) {
		lane = number;
		clear = String.format(CLEAR_FORMAT, lane);
		label.setBackground(bg);
		clearInfo();
	}
	
	public void setInfo(double lapSeconds, double blockSeconds) {
		// clamp
		if (lapSeconds > 99.999) lapSeconds = 99.999;
		if (blockSeconds > 9.999) blockSeconds = 9.999;
		// set
		label.setText(String.format(FORMAT, lane, lapSeconds, blockSeconds));
	}
	
	public void clearInfo() {
		label.setText(clear);
	}
}
