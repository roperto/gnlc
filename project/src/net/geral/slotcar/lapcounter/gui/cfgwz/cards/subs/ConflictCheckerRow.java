package net.geral.slotcar.lapcounter.gui.cfgwz.cards.subs;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.border.Border;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.LaneConfiguration;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ConflictCheckerRow extends JPanel implements MouseListener {
	private static final long		serialVersionUID	= 1L;
	
	private static final int		LABEL_BORDER_WIDTH	= 1;
	private static final int		PANEL_BORDER_WIDTH	= 1;
	private static final Border		LABEL_BORDER		= BorderFactory.createEmptyBorder(LABEL_BORDER_WIDTH, LABEL_BORDER_WIDTH, LABEL_BORDER_WIDTH, LABEL_BORDER_WIDTH);
	private static final Border		BORDER_NONE			= BorderFactory.createEmptyBorder(PANEL_BORDER_WIDTH, PANEL_BORDER_WIDTH, PANEL_BORDER_WIDTH, PANEL_BORDER_WIDTH);
	private static final Border		BORDER_LINE			= BorderFactory.createLineBorder(Color.BLACK, PANEL_BORDER_WIDTH);
	private static final Color		CELL_OUTLINE		= new Color(255, 128, 128);
	private static final Color		ROW_OUTLINE			= new Color(255, 255, 153);
	
	private final JLabel			lblRelay;
	private final JLabel			lblSensor;
	private final JLabel			lblFix;
	
	private final int				lane;
	private final LaneConfiguration	target;
	
	public ConflictCheckerRow(int laneNumber, LaneConfiguration fixTarget) {
		lane = laneNumber;
		target = fixTarget;
		boolean title = (lane == 0);
		Font f = getFont();
		if (title) f = f.deriveFont(Font.BOLD);
		
		setLayout(new GridLayout(1, 0, 0, 0));
		setOpaque(false);
		setBackground(ROW_OUTLINE);
		
		JLabel lblName = new JLabel(title ? "  Lane #  " : "Lane " + lane);
		lblName.setFont(f);
		lblName.setBorder(LABEL_BORDER);
		lblName.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblName);
		
		lblRelay = new JLabel(title ? "  Relay  " : "n/a");
		lblRelay.setBorder(LABEL_BORDER);
		lblRelay.setHorizontalAlignment(SwingConstants.CENTER);
		lblRelay.setFont(f);
		lblRelay.setBackground(CELL_OUTLINE);
		add(lblRelay);
		
		lblSensor = new JLabel(title ? "  Sensor  " : "n/a");
		lblSensor.setBorder(LABEL_BORDER);
		lblSensor.setHorizontalAlignment(SwingConstants.CENTER);
		lblSensor.setFont(f);
		lblSensor.setBackground(CELL_OUTLINE);
		add(lblSensor);
		
		lblFix = new JLabel(title ? "  Fix  " : "");
		if (laneNumber != 0) {
			lblFix.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			lblFix.addMouseListener(this);
		}
		if (!title) lblFix.setIcon(new ImageIcon(ConflictCheckerRow.class.getResource("/images/icons/Crystal_Clear_action_configure.png")));
		if (title) lblFix.setBorder(LABEL_BORDER);
		lblFix.setHorizontalAlignment(SwingConstants.CENTER);
		lblFix.setFont(f);
		add(lblFix);
	}
	
	public void setInfo(boolean visible, boolean relayConflict, boolean sensorConflict, int relay, int sensor) {
		// set visibility
		setVisible(visible);
		if (!visible) return;
		// any conflict
		boolean c = (relayConflict || sensorConflict);
		lblFix.setVisible(c);
		// set text
		lblRelay.setText(relay == 0 ? "none" : String.valueOf(relay));
		lblSensor.setText(sensor == 0 ? "none" : String.valueOf(sensor));
		// set background and border
		setBorder(c ? BORDER_LINE : BORDER_NONE);
		setOpaque(c);
		// set cell background
		lblRelay.setOpaque(relayConflict);
		lblSensor.setOpaque(sensorConflict);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		target.setLane(lane);
		target.activate();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
}
