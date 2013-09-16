package net.geral.slotcar.lapcounter.gui.cfgwz.cards.subs;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.InvalidParameterException;
import javax.swing.ButtonGroup;
import javax.swing.event.EventListenerList;
import java.awt.FlowLayout;

public class EightOptionsPlusOnePanel extends JPanel implements ActionListener {
	private static final long		serialVersionUID		= 1L;
	
	public static final int			BUTTONS					= 9;							// 8 + none
																							
	private final ButtonGroup		buttonGroup				= new ButtonGroup();
	private final JRadioButton[]	buttons					= new JRadioButton[BUTTONS];
	private final EventListenerList	changeEventListenerList	= new EventListenerList();
	JRadioButton					rbNone					= new JRadioButton("None");
	
	private int						oldValue				= 0;
	
	public EightOptionsPlusOnePanel() {
		FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 8, 0);
		setLayout(flowLayout);
		
		JRadioButton rb1 = new JRadioButton("1");
		buttonGroup.add(rb1);
		add(rb1);
		
		JRadioButton rb2 = new JRadioButton("2");
		buttonGroup.add(rb2);
		add(rb2);
		
		JRadioButton rb3 = new JRadioButton("3");
		buttonGroup.add(rb3);
		add(rb3);
		
		JRadioButton rb4 = new JRadioButton("4");
		buttonGroup.add(rb4);
		add(rb4);
		
		JRadioButton rb5 = new JRadioButton("5");
		buttonGroup.add(rb5);
		add(rb5);
		
		JRadioButton rb6 = new JRadioButton("6");
		buttonGroup.add(rb6);
		add(rb6);
		
		JRadioButton rb7 = new JRadioButton("7");
		buttonGroup.add(rb7);
		add(rb7);
		
		JRadioButton rb8 = new JRadioButton("8");
		buttonGroup.add(rb8);
		add(rb8);
		
		buttonGroup.add(rbNone);
		rbNone.setSelected(true);
		add(rbNone);
		
		buttons[0] = rbNone;
		buttons[1] = rb1;
		buttons[2] = rb2;
		buttons[3] = rb3;
		buttons[4] = rb4;
		buttons[5] = rb5;
		buttons[6] = rb6;
		buttons[7] = rb7;
		buttons[8] = rb8;
		
		// add events
		for (int i = 0; i < BUTTONS; i++) {
			buttons[i].addActionListener(this);
		}
	}
	
	public void addChangeListener(EightOptionsPlusNoneOneListener l) {
		changeEventListenerList.add(EightOptionsPlusNoneOneListener.class, l);
	}
	
	public void removeChangeListener(EightOptionsPlusNoneOneListener l) {
		changeEventListenerList.remove(EightOptionsPlusNoneOneListener.class, l);
	}
	
	protected void fireChangeEvent(int value) {
		// adapted from: http://download.oracle.com/javase/1.4.2/docs/api/javax/swing/event/EventListenerList.html
		EightOptionsPlusOneChangeEvent event = new EightOptionsPlusOneChangeEvent(this, oldValue, value);
		// Guaranteed to return a non-null array
		Object[] listeners = changeEventListenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == EightOptionsPlusNoneOneListener.class) {
				((EightOptionsPlusNoneOneListener)listeners[i + 1]).changed(event);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int value = getSelected();
		if (oldValue != value) {
			fireChangeEvent(value);
			oldValue = value;
		}
	}
	
	public int getSelected() {
		// find selected
		for (int i = 0; i < BUTTONS; i++) {
			if (buttons[i].isSelected()) return i;
		}
		// none selected, set 0
		buttons[0].setSelected(true);
		return 0;
	}
	
	public void setNoneLabel(String text) {
		rbNone.setText(text);
	}
	
	public void set(int laneNumber) {
		// no change
		if (laneNumber == oldValue) return;
		// invalid number
		if ((laneNumber < 0) || (laneNumber >= BUTTONS)) throw new InvalidParameterException("Lane must be between 0 to " + (BUTTONS - 1) + ". Parameter=" + laneNumber);
		// change it!
		buttons[laneNumber].setSelected(true);
		// set old
		oldValue = laneNumber;
	}
	
	public void setAmmount(int a) {
		for (int i = 1; i < BUTTONS; i++) { // disconsider last button (none)
			buttons[i].setEnabled(i <= a);
		}
	}
}
