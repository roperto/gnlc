package net.geral.slotcar.lapcounter.communication.none;

import javax.swing.SwingUtilities;
import net.geral.slotcar.lapcounter.communication.CommunicationDetector;
import net.geral.slotcar.lapcounter.communication.CommunicationDetectorListener;
import net.geral.slotcar.lapcounter.core.Kernel;

public class NoneDriverDetector implements CommunicationDetector, Runnable {
	private final CommunicationDetectorListener	listener;

	public NoneDriverDetector(Kernel k, CommunicationDetectorListener l) {
		listener = l;
	}

	@Override
	public void abort() {}

	@Override
	public void run() {
		listener.completed(true);
	}

	@Override
	public void start() {
		SwingUtilities.invokeLater(this);
	}
}
