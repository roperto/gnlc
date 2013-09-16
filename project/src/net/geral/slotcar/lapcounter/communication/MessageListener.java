package net.geral.slotcar.lapcounter.communication;

import java.util.EventListener;

public interface MessageListener extends EventListener {
	
	void onError(MessageEvent evt);
	
	void onVersion(MessageEvent evt);
	
}
