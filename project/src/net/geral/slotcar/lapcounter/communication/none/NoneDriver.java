package net.geral.slotcar.lapcounter.communication.none;

import net.geral.slotcar.lapcounter.communication.Communication;
import net.geral.slotcar.lapcounter.communication.CommunicationDetector;
import net.geral.slotcar.lapcounter.communication.CommunicationDetectorListener;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.core.Logger;
import net.geral.slotcar.lapcounter.structs.RaceLightState;

public class NoneDriver extends Communication {
	private boolean	open	= false;

	public NoneDriver(Kernel k) {
		super(k);
	}

	@Override
	public void close() {
		open = false;
	}

	@Override
	public CommunicationDetector createDetector(Kernel k, CommunicationDetectorListener l) {
		return new NoneDriverDetector(k, l);
	}

	@Override
	public boolean open() {
		return open;
	}

	@Override
	public void run() {}

	@Override
	public void setLanePower(int laneIndex, boolean yn) {
		Logger.log("NoneComm: setLanePower(" + laneIndex + "," + yn + ")");
	}

	@Override
	public void setLightState(RaceLightState state) {
		Logger.log("NoneComm: setLightState(" + state + ")");
	}

	@Override
	public void setPowerPause(boolean yn) {
		Logger.log("NoneComm: setPowerPause(" + yn + ")");
	}

	@Override
	public void setTrackPower(boolean yn) {
		Logger.log("NoneComm: setTrackPower(" + yn + ")");
	}

	@Override
	public void start() {
		open = true;
		super.start();
	}
}
