package net.geral.slotcar.lapcounter.communication;

public interface CommunicationDetectorListener {
	public void print(String s);
	
	public void println(String s);
	
	public void println();
	
	public void completed(boolean found);
}
