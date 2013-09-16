package net.geral.slotcar.lapcounter.communication;

import javax.swing.event.EventListenerList;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.structs.RaceLightState;

public abstract class Communication implements Runnable {
	// protected fields
	protected Thread				thread				= new Thread(this);
	protected final Kernel			kernel;

	// private fields
	private final EventListenerList	messageListeners	= new EventListenerList();
	private final EventListenerList	lapListeners		= new EventListenerList();

	// constructor
	public Communication(Kernel k) {
		if (k == null) throw new NullPointerException("Kernel cannot be null.");
		kernel = k;
		start();
	}

	public void addLapListener(LapListener l) {
		lapListeners.add(LapListener.class, l);
	}

	public void addMessageListener(MessageListener l) {
		messageListeners.add(MessageListener.class, l);
	}

	public abstract void close();

	public abstract CommunicationDetector createDetector(Kernel k, CommunicationDetectorListener l);

	protected void fireErrorMessageEvent(String message) {
		// adapted from:
		// http://download.oracle.com/javase/1.4.2/docs/api/javax/swing/event/EventListenerList.html
		MessageEvent evt = null;
		// Guaranteed to return a non-null array
		Object[] listeners = messageListeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == MessageListener.class) {
				if (evt == null) evt = new MessageEvent(this, message);
				((MessageListener) listeners[i + 1]).onError(evt);
			}
		}
	}

	protected void fireLapEvent(int sensorIndex, double blockSeconds, double lapSeconds) {
		// adapted from:
		// http://download.oracle.com/javase/1.4.2/docs/api/javax/swing/event/EventListenerList.html
		LapEvent evt = null;
		// Guaranteed to return a non-null array
		Object[] listeners = lapListeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == LapListener.class) {
				if (evt == null) {
					final int laneNumber = kernel.config.getLaneForSensor(sensorIndex + 1);
					evt = new LapEvent(this, sensorIndex, laneNumber, blockSeconds, lapSeconds);
				}
				((LapListener) listeners[i + 1]).onLap(evt);
			}
		}
	}

	protected void fireVersionMessageEvent(String message) {
		// adapted from:
		// http://download.oracle.com/javase/1.4.2/docs/api/javax/swing/event/EventListenerList.html
		MessageEvent evt = null;
		// Guaranteed to return a non-null array
		Object[] listeners = messageListeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == MessageListener.class) {
				if (evt == null) evt = new MessageEvent(this, message);
				((MessageListener) listeners[i + 1]).onVersion(evt);
			}
		}
	}

	// abstract methods
	public abstract boolean open();

	public void removeLapListener(LapListener l) {
		lapListeners.remove(LapListener.class, l);
	}

	public void removeMessageListener(MessageListener l) {
		messageListeners.remove(MessageListener.class, l);
	}

	public abstract void setLanePower(int laneIndex, boolean yn);

	public abstract void setLightState(RaceLightState state);

	public abstract void setPowerPause(boolean yn);

	public abstract void setTrackPower(boolean yn);

	// implemented methods
	public void start() {
		thread.start();
	}
}
