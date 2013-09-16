package net.geral.slotcar.lapcounter.structs;

import net.geral.slotcar.lapcounter.core.SoundController;

public enum Sounds {
	ProgramStart,
	Lap,
	PilotBestLap,
	RaceBestLap,
	Countdown,
	ReadySet,
	// end of enum
	;
	
	private SoundController	controller;
	
	private Sounds() {
		controller = new SoundController(toString());
	}
	
	public void play() {
		(new Thread(controller)).start();
	}
}
