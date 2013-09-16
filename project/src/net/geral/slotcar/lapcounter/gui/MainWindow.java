package net.geral.slotcar.lapcounter.gui;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.core.Logger;
import net.geral.slotcar.lapcounter.structs.Commands;
import net.geral.slotcar.lapcounter.structs.Configuration;

public class MainWindow extends JFrame implements WindowListener {
	private static final long		serialVersionUID	= 1L;
	
	private static final Dimension	MINIMUM_SIZE		= new Dimension(600, 400);
	
	public final LanesPanel			lanesPanel;
	
	private final Kernel			kernel;
	
	public MainWindow(final Kernel k) {
		super("Lap Counter - Geral.NET");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/images/bigicons/Crystal_Clear_app_ktron.png")));
		if (k == null) throw new NullPointerException("Kernel cannot be null.");
		kernel = k;
		
		lanesPanel = new LanesPanel(kernel);
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(MINIMUM_SIZE);
		setMinimumSize(MINIMUM_SIZE);
		setLocationRelativeTo(null);
		addWindowListener(this);
		
		showLanes();
	}
	
	public void applyConfiguration() {
		Logger.log("Applying new window configuration...");
		final Configuration c = kernel.config;
		setFullscreen(c.FullScreen);
		lanesPanel.applyConfiguration();
	}
	
	public boolean isFullscreen() {
		return isUndecorated();
	}
	
	public void setFullscreen(final boolean yn) {
		// check if needs to change
		if (yn == isFullscreen()) return;
		
		// set
		setVisible(false);
		dispose();
		setUndecorated(yn);
		final GraphicsDevice d = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		d.setFullScreenWindow(yn ? this : null);
		if (!yn) setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void show(final JPanel panel) {
		setContentPane(panel);
		validate();
	}
	
	public void showLanes() {
		setContentPane(lanesPanel);
		lanesPanel.applyConfiguration();
	}
	
	@Override
	public void windowActivated(final WindowEvent e) {}
	
	@Override
	public void windowClosed(final WindowEvent e) {}
	
	@Override
	public void windowClosing(final WindowEvent e) {
		kernel.commander.execute(Commands.EXIT, true);
		System.exit(0); // should not be called ('exit' should exit first)
	}
	
	@Override
	public void windowDeactivated(final WindowEvent e) {}
	
	@Override
	public void windowDeiconified(final WindowEvent e) {}
	
	@Override
	public void windowIconified(final WindowEvent e) {}
	
	@Override
	public void windowOpened(final WindowEvent e) {}
}
