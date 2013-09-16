package net.geral.slotcar.lapcounter.communication;

import java.util.EventObject;

public class MessageEvent extends EventObject {
	private static final long	serialVersionUID	= 1L;
	
	public final String			Message;
	
	public MessageEvent(Communication source, String message) {
		super(source);
		Message = message;
	}
	
	public String toString() {
		return getClass().getName() + "[" + Message + "]";
	}
}
