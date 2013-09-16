package net.geral.slotcar.lapcounter.core;

import net.geral.slotcar.lapcounter.communication.LapEvent;
import net.geral.slotcar.lapcounter.structs.Configuration;

public abstract class StyleController {
	protected final int		MAX_LANES			= Configuration.MAX_LANES;
	protected final int		PAUSE_BLINK_TIME	= 1000;
	protected final Kernel	kernel;
	
	protected boolean		paused				= false;
	
	private long			millisStart			= System.currentTimeMillis();
	private String			headerBeforePause	= "";
	private long			pausedStart			= millisStart;
	private long			pausedFor			= 0L;
	
	public StyleController(Kernel k) {
		kernel = k;
	}
	
	public final boolean loop() {
		// check paused
		if (paused) {
			boolean showPause = (((System.currentTimeMillis() - pausedStart) / PAUSE_BLINK_TIME) % 2 == 0);
			kernel.window.lanesPanel.set(0, showPause ? "PAUSED" : headerBeforePause);
			return false;
		}
		
		// check communication
		if (!kernel.communication.open()) return false;
		
		// continue
		long now = System.currentTimeMillis();
		double total = (now - millisStart - pausedFor) / 1000.0;
		
		return loop(total);
	}
	
	protected abstract boolean loop(double elapsed);
	
	public void restart() {
		paused = false;
		pausedFor = 0L;
		millisStart = System.currentTimeMillis();
	}
	
	public void setPaused(boolean yn) {
		if (paused == yn) return;
		
		kernel.communication.setPowerPause(yn);
		
		long now = System.currentTimeMillis();
		
		if (yn) {
			headerBeforePause = kernel.window.lanesPanel.get(0);
			pausedStart = now;
		}
		else {
			kernel.window.lanesPanel.set(0, headerBeforePause);
			headerBeforePause = "";
			long thisPause = now - pausedStart;
			pausedFor += thisPause;
			unpaused(thisPause / 1000.0);
		}
		
		paused = yn;
	}
	
	protected abstract void unpaused(double secondsPaused);
	
	public abstract void lap(LapEvent evt);
	
	public boolean isPaused() {
		return paused;
	}
	
	public abstract void stop();
}
