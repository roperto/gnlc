package net.geral.slotcar.lapcounter.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.core.Logger;
import net.geral.slotcar.lapcounter.structs.Configuration;

public class MainPopupMenu extends JPopupMenu implements ActionListener, PopupMenuListener {
	private static final long	serialVersionUID	= 1L;
	
	private static final int	MAX_COLUMNS			= Configuration.MAX_LANES;
	
	private static void setSelected(final JRadioButtonMenuItem[] items, final int index) {
		if ((index < 0) || (index >= items.length)) {
			Logger.log(new IndexOutOfBoundsException("Invalid index: " + index));
		}
		items[index].setSelected(true);
	}
	
	private final Kernel					kernel;
	private final JCheckBoxMenuItem			pause				= createCheckboxItem("Pause", "PAUSE", true);
	private final JCheckBoxMenuItem			display_fullscreen	= createCheckboxItem("Fullscreen Mode", "FULLSCREEN", false);
	
	private final JRadioButtonMenuItem[]	display_columns		= new JRadioButtonMenuItem[MAX_COLUMNS];
	
	public MainPopupMenu(final Kernel k) {
		if (k == null) { throw new NullPointerException("Kernel cannot be null."); }
		kernel = k;
		
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		add(pause);
		add(createMenuItem("Restart", "RESTART"));
		addSeparator();
		add(createMenuItem("Start", "START FREERUN"));
		addSeparator();
		add(createConfigure());
		addSeparator();
		add(createMenuItem("Exit", "EXIT YES"));
		
		addPopupMenuListener(this);
	}
	
	@Override
	public void actionPerformed(final ActionEvent e) {
		final Object src = e.getSource();
		
		if (src instanceof JCheckBoxMenuItem) {
			kernel.commander.execute(e.getActionCommand(), ((JCheckBoxMenuItem)src).isSelected() ? "on" : "off");
			return;
		}
		
		if (src instanceof JMenuItem) {
			kernel.commander.execute(e.getActionCommand());
			return;
		}
		
		Logger.error("Invalid action source: " + src);
	}
	
	private JCheckBoxMenuItem createCheckboxItem(final String label, final String action, final boolean selected) {
		final JCheckBoxMenuItem i = new JCheckBoxMenuItem(label, selected);
		i.setActionCommand(action);
		i.addActionListener(this);
		return i;
	}
	
	private JMenu createConfigure() {
		final JMenu m = new JMenu("Configure");
		m.add(createMenuItem("Hardware...", "CONFIGURE HARDWARE"));
		m.add(createMenuItem("Pilots...", "CONFIGURE PILOTS"));
		m.addSeparator();
		m.add(createConfigureDisplay());
		return m;
	}
	
	private JMenu createConfigureDisplay() {
		final JMenu m = new JMenu("Display");
		m.add(display_fullscreen);
		m.addSeparator();
		m.add(createDisplayColumns());
		return m;
	}
	
	private JMenu createDisplayColumns() {
		final JMenu m = new JMenu("Columns");
		final ButtonGroup grp = new ButtonGroup();
		for (int i = 0; i < MAX_COLUMNS; i++) {
			display_columns[i] = new JRadioButtonMenuItem(String.valueOf(i + 1), i == 0);
			display_columns[i].setActionCommand("DISPLAY_COLUMNS " + (i + 1));
			display_columns[i].addActionListener(this);
			grp.add(display_columns[i]);
			m.add(display_columns[i]);
		}
		return m;
	}
	
	private JMenuItem createMenuItem(final String label, final String action) {
		final JMenuItem i = new JMenuItem(label);
		i.setActionCommand(action);
		i.addActionListener(this);
		return i;
	}
	
	@Override
	public void popupMenuCanceled(final PopupMenuEvent e) {}
	
	@Override
	public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {}
	
	@Override
	public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
		pause.setSelected(kernel.getController().isPaused());
		display_fullscreen.setSelected(kernel.window.isFullscreen());
		setSelected(display_columns, kernel.config.DisplayColumns - 1); // use function to avoid out-of-range values
	}
}
