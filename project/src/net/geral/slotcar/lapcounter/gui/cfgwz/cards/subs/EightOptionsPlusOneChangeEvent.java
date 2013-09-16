package net.geral.slotcar.lapcounter.gui.cfgwz.cards.subs;

import java.util.EventObject;

public class EightOptionsPlusOneChangeEvent extends EventObject {
	private static final long	serialVersionUID	= 1L;
	
	public final int			oldValue;
	public final int			newValue;
	
	public EightOptionsPlusOneChangeEvent(EightOptionsPlusOnePanel source, int oldValue, int newValue) {
		super(source);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public String toString() {
		return getClass().getName() + "[src=" + getSource() + ";old=" + oldValue + ";new=" + newValue + "]";
	}
}
