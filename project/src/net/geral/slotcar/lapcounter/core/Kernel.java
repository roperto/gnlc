package net.geral.slotcar.lapcounter.core;

import net.geral.slotcar.lapcounter.communication.Communication;
import net.geral.slotcar.lapcounter.communication.LapEvent;
import net.geral.slotcar.lapcounter.communication.LapListener;
import net.geral.slotcar.lapcounter.communication.serial.SerialDriver;
import net.geral.slotcar.lapcounter.core.styles.FreeRun;
import net.geral.slotcar.lapcounter.gui.MainWindow;
import net.geral.slotcar.lapcounter.gui.cfgwz.ConfigurationWizard;
import net.geral.slotcar.lapcounter.structs.Configuration;
import net.geral.slotcar.lapcounter.structs.PilotsData;

public class Kernel extends Thread implements LapListener {
	public final MainWindow		window			= new MainWindow(this);
	public final Configuration	config			= new Configuration();
	public final Communication	communication	= new SerialDriver(this);
	public final Commander		commander		= new Commander(this);
	public final PilotsData		pilots			= new PilotsData();

	private boolean				running			= false;
	private StyleController		controller		= new FreeRun(this);

	public Kernel() {
		Logger.init();
		communication.addLapListener(this);
	}

	public StyleController getController() {
		return controller;
	}

	@Override
	public void onLap(final LapEvent evt) {
		controller.lap(evt);
	}

	@Override
	public void run() {
		while (running) {
			synchronized (controller) {
				if (controller.loop()) Logger.error(new RuntimeException("Loop returned true, error found!"));
			}
			yield();
		}
	}

	public void setController(final StyleController newController) {
		// wait current loop to finish
		synchronized (controller) {
			controller.stop();
			controller = newController;
		}
	}

	@Override
	public void start() {
		window.setVisible(true);
		config.load();
		pilots.load();

		if (config.NewConfiguration) {
			config.NewConfiguration = false;
			final ConfigurationWizard cw = new ConfigurationWizard(this, true);
			cw.open();
		}

		controller.restart();
		window.applyConfiguration();

		running = true;
		super.start();
	}
}
