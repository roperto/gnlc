package net.geral.slotcar.lapcounter.gui.cfgwz;

import javax.swing.JPanel;

public abstract class Card extends JPanel {
	private static final long			serialVersionUID	= 1L;
	
	private static int					lastId				= 0;
	
	public final String					id;
	public final ConfigurationWizard	wizard;
	
	public Card(ConfigurationWizard w) {
		lastId++;
		id = getClass().getSimpleName() + ":" + lastId;
		wizard = w;
		// register self
		if (w != null) wizard.registerCard(this);
	}
	
	public abstract void back();
	
	public abstract void next();
	
	public abstract void activated();
	
	public abstract void deactivated();
	
	public abstract boolean isFinish();
	
	public abstract boolean enableNext();
	
	public abstract boolean enableBack();
	
	public void activate() {
		wizard.setCard(id);
	}
	
	public abstract String getTitle();
}
