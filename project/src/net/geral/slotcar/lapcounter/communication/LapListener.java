package net.geral.slotcar.lapcounter.communication;

import java.util.EventListener;

public interface LapListener extends EventListener {
	
	void onLap(LapEvent evt);
	
}
