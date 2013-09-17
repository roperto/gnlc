package net.geral.slotcar.lapcounter.communication.serial;

import gnu.io.CommPortIdentifier;
import java.util.Enumeration;
import java.util.Vector;
import net.geral.slotcar.lapcounter.communication.CommunicationDetector;
import net.geral.slotcar.lapcounter.communication.CommunicationDetectorListener;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.core.Logger;
import net.geral.slotcar.lapcounter.structs.Configuration;

public class SerialDetector extends Thread implements CommunicationDetector {
	public final int							DETECTOR_TIMEOUT_MS	= 10000;

	private final Configuration					config;
	private final CommunicationDetectorListener	listener;

	private SerialDetectorState					state				= SerialDetectorState.READY;
	private boolean								running				= false;
	private Vector<String>						ports				= null;
	private SerialDriver						comm				= null;
	private long								timer				= 0L;

	public SerialDetector(Kernel k, CommunicationDetectorListener l) {
		if (k == null) throw new NullPointerException("Kernel cannot be null.");
		if (l == null) throw new NullPointerException("Listener cannot be null.");
		config = k.config;
		if (k.communication instanceof SerialDriver) comm = (SerialDriver) k.communication;
		else throw new RuntimeException("Invalid driver used for detection.");
		listener = l;
	}

	@Override
	public void abort() {
		running = false;
	}

	private boolean changeState(SerialDetectorState newState) {
		if (state == newState) return false;
		state = newState;
		return false;
	}

	private boolean checkPort() {
		// any port to check?
		if (ports.size() == 0) {
			listener.println();
			listener.println("Cannot find Geral.NET Lap Counter Controller!");
			return changeState(SerialDetectorState.NOT_FOUND);
		}

		// get port and remove from list

		config.Port = ports.firstElement();
		ports.remove(0);
		listener.print("Checking port '" + config.Port + "'... ");

		// try opening
		comm.open();

		// wait for result
		timer = System.currentTimeMillis() + DETECTOR_TIMEOUT_MS;
		return changeState(SerialDetectorState.WAITING_PORT_CHECK);
	}

	private boolean listPorts() {
		listener.print("Detecting available serial ports... ");
		updatePorts();
		listener.println(ports.size() + " found.");
		return changeState(SerialDetectorState.CHECK_PORT);
	}

	private boolean loop() {
		switch (state) {
			case READY:
				throw new RuntimeException("Ready to start. Did you call 'run' instead of 'loop'?");
			case NOT_FOUND:
			case FOUND:
				config.AutoDetectHangedLastTime = false;
				listener.completed(state == SerialDetectorState.FOUND);
				running = false;
				return false;
			case STARTED:
				return changeState(SerialDetectorState.LIST_PORTS);
			case LIST_PORTS:
				return listPorts();
			case CHECK_PORT:
				return checkPort();
			case WAITING_PORT_CHECK:
				return waitingPortCheck();
			default:
				Logger.error("Invalid state: " + state.toString());
				return true;
		}
	}

	@Override
	public void run() {
		while (running) {
			if (loop()) yield();
		}
	}

	@Override
	public void start() {
		if (state != SerialDetectorState.READY) throw new RuntimeException("Detector not read: " + state.toString());
		state = SerialDetectorState.STARTED;
		running = true;
		super.start();
	}

	private void updatePorts() {
		ports = new Vector<String>();
		Enumeration<?> en = CommPortIdentifier.getPortIdentifiers();

		while (en.hasMoreElements()) {
			CommPortIdentifier id = (CommPortIdentifier) en.nextElement();
			if (id.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				Logger.log("Port " + id.getName() + " found.");
				ports.add(id.getName());
			}
		}
	}

	private boolean waitingPortCheck() {
		// open?
		if (comm.getDriverState() == DriverState.OPEN) {
			listener.println("found!");
			return changeState(SerialDetectorState.FOUND);
		}

		// error?
		if (comm.getDriverState() == DriverState.CLOSED) {
			// get result and stop driver
			DriverError error = comm.getError();

			listener.println(error.title + ".");
			return changeState(SerialDetectorState.CHECK_PORT); // check next
		}

		// timeout
		if (System.currentTimeMillis() > timer) {
			listener.println("no response.");
			comm.close();
			return changeState(SerialDetectorState.CHECK_PORT);
		}

		// waiting
		return true;
	}
}
