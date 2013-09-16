package net.geral.slotcar.lapcounter.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.structs.Configuration;

public class LanesPanel extends JPanel implements MouseListener, ComponentListener {
	private static final long	serialVersionUID	= 1L;
	
	private static final int	EXTRA_WIDTH			= 10;
	private static final int	BASE_FONT_SIZE		= 20;
	private static final Font	BASE_FONT			= new Font("Monospaced", Font.BOLD, BASE_FONT_SIZE);
	private static final int	LANES				= Configuration.MAX_LANES;
	
	private final JLabel		lblHeader			= new JLabel("Geral.NET Lap Counter");
	private final JLabel[]		lanes				= new JLabel[LANES];
	private final MainPopupMenu	menu;
	
	private final Kernel		kernel;
	
	public LanesPanel(final Kernel k) {
		if (k == null) throw new NullPointerException("Kernel cannot be null.");
		kernel = k;
		
		menu = new MainPopupMenu(kernel);
		
		setLayout(null);
		setBackground(Color.BLACK);
		
		initComponents();
		addComponentListener(this);
		addMouseListener(this);
	}
	
	private void adjustSize(final JLabel l) {
		final int fw = l.getFontMetrics(BASE_FONT).stringWidth(l.getText());
		final int cw = l.getWidth();
		final double ratio = (double)cw / (double)fw;
		
		float newSize = (int)(BASE_FONT_SIZE * ratio) - EXTRA_WIDTH;
		newSize = Math.min(newSize, l.getHeight());
		
		final Font newFont = BASE_FONT.deriveFont(newSize);
		l.setFont(newFont);
	}
	
	public void applyConfiguration() {
		final Configuration c = kernel.config;
		if (c == null) return;
		
		// update visibility & color
		for (int i = 0; i < LANES; i++) {
			lanes[i].setVisible(i < c.LanesQuantity);
			lanes[i].setBackground(c.LaneColor[i]);
		}
		
		resized();
	}
	
	@Override
	public void componentHidden(final ComponentEvent e) {}
	
	@Override
	public void componentMoved(final ComponentEvent e) {}
	
	@Override
	public void componentResized(final ComponentEvent e) {
		resized();
	}
	
	@Override
	public void componentShown(final ComponentEvent e) {}
	
	public String get(final int lane) {
		final JLabel l = (lane == 0) ? lblHeader : lanes[lane - 1];
		return l.getText();
	}
	
	private void initComponents() {
		lblHeader.setForeground(Color.WHITE);
		lblHeader.setBackground(Color.BLACK);
		lblHeader.setOpaque(true);
		lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
		lblHeader.setFont(BASE_FONT);
		add(lblHeader);
		
		final Border b = BorderFactory.createLineBorder(Color.BLACK, 1);
		for (int i = 0; i < LANES; i++) {
			lanes[i] = new JLabel("Lane " + (i + 1) + "");
			lanes[i].setOpaque(true);
			lanes[i].setBackground(Color.WHITE);
			lanes[i].setFont(BASE_FONT);
			lanes[i].setHorizontalAlignment(SwingConstants.CENTER);
			lanes[i].setVerticalAlignment(SwingConstants.CENTER);
			lanes[i].setBorder(b);
			add(lanes[i]);
		}
	}
	
	private boolean maybeShowPopup(final MouseEvent e) {
		if (!e.isPopupTrigger()) return false;
		
		menu.show(e.getComponent(), e.getX(), e.getY());
		return true;
	}
	
	@Override
	public void mouseClicked(final MouseEvent e) {}
	
	@Override
	public void mouseEntered(final MouseEvent e) {}
	
	@Override
	public void mouseExited(final MouseEvent e) {}
	
	@Override
	public void mousePressed(final MouseEvent e) {
		if (maybeShowPopup(e)) return;
	}
	
	@Override
	public void mouseReleased(final MouseEvent e) {
		if (maybeShowPopup(e)) return;
	}
	
	private void resized() {
		final Configuration c = kernel.config;
		
		// check width
		final int cols = Math.min(c.DisplayColumns, c.LanesQuantity);
		final int availWidth = getWidth();
		final int width = availWidth / cols;
		final int extraWidth = availWidth - (cols * width); // integer-difference
		
		// check height
		final int rows = (int)Math.ceil((double)c.LanesQuantity / (double)cols) + 1; // +1 = info panel
		final int availHeight = getHeight();
		final int height = availHeight / rows;
		final int extraHeight = availHeight - (rows * height); // integer-difference
		
		// resize components
		int y = extraHeight;
		int x = 0;
		lblHeader.setBounds(0, 0, availWidth, height + y);
		for (int i = 0; i < LANES; i++) {
			if ((i % cols) == 0) {
				x = extraWidth;
				y += height;
			}
			else {
				x += width;
			}
			lanes[i].setBounds(x, y, width, height);
		}
		
		// resize fonts
		adjustSize(lblHeader);
		for (int i = 0; i < LANES; i++) {
			adjustSize(lanes[i]);
		}
	}
	
	public void set(final int lane, final String s) {
		final JLabel l = (lane == 0) ? lblHeader : lanes[lane - 1];
		final int length = l.getText().length();
		l.setText(s);
		if (s.length() != length) adjustSize(l);
	}
	
	public void setElapsed(final double elapsed) {
		set(0, Util.secs2h_m_s_1ms(elapsed));
	}
}
