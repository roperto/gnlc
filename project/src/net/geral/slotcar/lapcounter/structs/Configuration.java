package net.geral.slotcar.lapcounter.structs;

import java.awt.Color;
import net.geral.slotcar.lapcounter.core.Storage;

public class Configuration {
	// constants
	public static final int	MAX_LANES					= 8;
	
	// finals (arrays itself cannot be changed, its contents can)
	public final Color[]	LaneColor					= new Color[MAX_LANES];
	public final int[]		LaneRelay					= new int[MAX_LANES];
	public final int[]		LaneSensor					= new int[MAX_LANES];
	
	// public, changes allowed
	public boolean			InvertRaceLightsLogic		= false;
	public boolean			NewConfiguration			= true;
	public boolean			AutoDetectPort				= true;
	public boolean			AutoDetectHangedLastTime	= false;
	public String			Port						= null;
	public int				LanesQuantity				= 2;
	public boolean			FullScreen					= false;
	public int				DisplayColumns				= 1;
	
	// constructor - array defaults
	public Configuration() {
		for (int i = 0; i < MAX_LANES; i++) {
			LaneColor[i] = getDefaultColor(i);
			LaneRelay[i] = i + 1;
			LaneSensor[i] = i + 1;
		}
	}
	
	public int countRelayUse(final int relay) {
		// if relay is zero (none), do not count
		if (relay == 0) { return 0; }
		// count
		int c = 0;
		for (int i = 0; i < LanesQuantity; i++) {
			if (LaneRelay[i] == relay) {
				c++;
			}
		}
		return c;
	}
	
	public int countSensorUse(final int sensorNumber) {
		// if relay is zero (none), do not count
		if (sensorNumber == 0) { return 0; }
		// count
		int c = 0;
		for (int i = 0; i < LanesQuantity; i++) {
			if (LaneSensor[i] == sensorNumber) {
				c++;
			}
		}
		return c;
	}
	
	// static methods
	public Color getDefaultColor(final int lane) {
		switch (lane) {
			case 0:
				return new Color(153, 255, 153);
			case 1:
				return new Color(255, 255, 153);
			case 2:
				return new Color(204, 204, 255);
			case 3:
				return new Color(255, 204, 204);
			case 4:
				return new Color(255, 204, 102);
			case 5:
				return new Color(255, 176, 255);
			case 6:
				return new Color(204, 255, 255);
			default:
				return new Color(237, 236, 236);
		}
	}
	
	public int getLaneForSensor(final int sensorNumber) {
		// no sensor, no lane
		if (sensorNumber == 0) { return 0; }
		// find it
		for (int i = 0; i < MAX_LANES; i++) {
			if (LaneSensor[i] == sensorNumber) { return i + 1; }
		}
		// not found
		return 0;
	}
	
	public void load() {
		Storage.load(this);
	}
	
	public void save() {
		Storage.save(this);
	}
}
