package net.geral.slotcar.lapcounter.core.styles;

import net.geral.slotcar.lapcounter.communication.LapEvent;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.core.StyleController;
import net.geral.slotcar.lapcounter.gui.Util;
import net.geral.slotcar.lapcounter.structs.Sounds;

public class FreeRun extends StyleController {
	protected int[]		lapCount		= new int[MAX_LANES];
	protected double[]	pausedTime		= new double[MAX_LANES];
	protected double[]	lastLap			= new double[MAX_LANES];
	protected double[]	bestLap			= new double[MAX_LANES];
	protected double	bestLapTotal	= Double.MAX_VALUE;
	
	public FreeRun(Kernel k) {
		super(k);
		restart();
	}
	
	@Override
	protected boolean loop(double elapsed) {
		kernel.window.lanesPanel.setElapsed(elapsed);
		return false;
	}
	
	@Override
	public void lap(LapEvent evt) {
		// check valid lane
		if ((evt.LaneNumber == 0) || (evt.LaneNumber > kernel.config.LanesQuantity)) return;
		
		// count
		int i = evt.LaneNumber - 1;
		lapCount[i]++;
		lastLap[i] = evt.LapSeconds - pausedTime[i];
		pausedTime[i] = 0.0;
		
		if (lapCount[i] > 0) {
			if (lastLap[i] < bestLap[i]) {
				bestLap[i] = lastLap[i];
				if (bestLap[i] < bestLapTotal) {
					bestLapTotal = bestLap[i];
					Sounds.RaceBestLap.play();
				}
				else {
					Sounds.PilotBestLap.play();
				}
			}
			else {
				Sounds.Lap.play();
			}
		}
		else {
			Sounds.Lap.play();
		}
		
		// display
		updateLaneInfo(evt.LaneNumber);
	}
	
	private void updateLaneInfo(int laneNumber) {
		int i = laneNumber - 1;
		kernel.window.lanesPanel.set(laneNumber, Util.makeString(lapCount[i], lastLap[i], bestLap[i]));
	}
	
	public void restart() {
		super.restart();
		for (int i = 0; i < MAX_LANES; i++) {
			lapCount[i] = -1;
			pausedTime[i] = 0.0;
			lastLap[i] = Double.MAX_VALUE;
			bestLap[i] = Double.MAX_VALUE;
			updateLaneInfo(i + 1);
		}
	}
	
	@Override
	protected void unpaused(double secondsPaused) {
		for (int i = 0; i < MAX_LANES; i++) {
			pausedTime[i] += secondsPaused;
		}
	}
	
	@Override
	public void stop() {}
}
