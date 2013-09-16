package net.geral.slotcar.lapcounter.communication;

import java.util.EventObject;

public class LapEvent extends EventObject {
	private static final long	serialVersionUID	= 1L;
	
	public final int			SensorIndex;
	public final int			LaneNumber;
	public final double			BlockSeconds;
	public final double			LapSeconds;
	
	public LapEvent(Communication source, int sensorIndex, int laneNumber, double blockSeconds, double lapSeconds) {
		super(source);
		SensorIndex = sensorIndex;
		LaneNumber = laneNumber;
		BlockSeconds = blockSeconds;
		LapSeconds = lapSeconds;
	}
	
	public String toString() {
		return getClass().getName() + "[SensorIndex=" + SensorIndex + ";LaneNumber=" + LaneNumber + ";Block=" + BlockSeconds + ";Time=" + LapSeconds + "]";
	}
}
