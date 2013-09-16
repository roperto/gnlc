package net.geral.slotcar.lapcounter.gui.cfgwz.cards;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JTextPane;
import javax.swing.JRadioButton;
import net.geral.slotcar.lapcounter.core.Logger;
import net.geral.slotcar.lapcounter.gui.cfgwz.Card;
import net.geral.slotcar.lapcounter.gui.cfgwz.ConfigurationWizard;
import javax.swing.JScrollPane;

public class MainPowerTest extends Card implements ActionListener {
	private static final long	serialVersionUID	= 1L;
	
	private final JRadioButton	rdbtnPowerOn		= new JRadioButton("Power On");
	private final JRadioButton	rdbtnPowerOff		= new JRadioButton("Power Off");
	
	private boolean				firstTime			= true;
	
	public MainPowerTest(ConfigurationWizard w) {
		super(w);
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		
		rdbtnPowerOn.setSelected(true);
		rdbtnPowerOn.addActionListener(this);
		panel.add(rdbtnPowerOn);
		
		rdbtnPowerOff.addActionListener(this);
		panel.add(rdbtnPowerOff);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnPowerOn);
		group.add(rdbtnPowerOff);
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		JTextPane textPane = new JTextPane();
		textPane.setText("Please, try turning the power off and on using the buttons below.\n\nIf all lanes are powered off and on properly, please click 'Next' to continue.\n\nIf you have other problems, please check your power cables, USB connection or click 'Back' to start over.\n\n\nRemember: the power's default is to be on, it should work even when your computer turned off.");
		textPane.setEditable(false);
		scrollPane.setViewportView(textPane);
	}
	
	@Override
	public void back() {
		if (wizard.cardWelcome.isAutoDetect()) wizard.cardWelcome.activate();
		// else wizard.cardManual.activate(); //TODO
	}
	
	@Override
	public void next() {
		wizard.cardRaceLights.activate();
	}
	
	@Override
	public void activated() {
		if (firstTime) wizard.kernel.communication.setTrackPower(true);
		firstTime = false;
	}
	
	@Override
	public void deactivated() {
		rdbtnPowerOn.setSelected(true);
		wizard.kernel.communication.setTrackPower(true);
	}
	
	@Override
	public boolean isFinish() {
		return false;
	}
	
	@Override
	public boolean enableNext() {
		return true;
	}
	
	@Override
	public boolean enableBack() {
		return true;
	}
	
	@Override
	public String getTitle() {
		return "Main Power Test";
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if (src == rdbtnPowerOn) {
			wizard.kernel.communication.setTrackPower(true);
			wizard.updateVisuals();
			return;
		}
		
		if (src == rdbtnPowerOff) {
			wizard.kernel.communication.setTrackPower(false);
			wizard.updateVisuals();
			return;
		}
		
		Logger.log(e.toString());
	}
}
