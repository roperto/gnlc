package net.geral.slotcar.lapcounter.gui.cfgwz.cards;

import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import javax.swing.JCheckBox;
import net.geral.slotcar.lapcounter.gui.cfgwz.Card;
import net.geral.slotcar.lapcounter.gui.cfgwz.ConfigurationWizard;
import javax.swing.JScrollPane;

public class Welcome extends Card {
	private static final long		serialVersionUID	= 1L;
	
	public static final JCheckBox	chckbxAutoDetect	= new JCheckBox("Auto-detect configuration.");
	
	public Welcome(ConfigurationWizard configurationWizard) {
		super(configurationWizard);
		
		setLayout(new BorderLayout(0, 0));
		chckbxAutoDetect.setHorizontalAlignment(SwingConstants.CENTER);
		
		chckbxAutoDetect.setSelected(true);
		add(chckbxAutoDetect, BorderLayout.SOUTH);
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		JTextPane txtpnWelcomeToThe = new JTextPane();
		scrollPane.setViewportView(txtpnWelcomeToThe);
		txtpnWelcomeToThe.setText("Welcome to the Geral.NET Lap Counter Configuration Wizard!\r\n\r\nFollow the next steps to create a basic configuration for this software.");
		txtpnWelcomeToThe.setEditable(false);
	}
	
	@Override
	public void back() {}
	
	@Override
	public void next() {
		if (chckbxAutoDetect.isSelected()) {
			wizard.cardAutoDetect.activate();
		}
		else {
			// TODO
		}
	}
	
	@Override
	public void activated() {}
	
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
		return false;
	}
	
	@Override
	public String getTitle() {
		return "Geral.NET Lap Counter";
	}
	
	@Override
	public void deactivated() {}
	
	public boolean isAutoDetect() {
		return chckbxAutoDetect.isSelected();
	}
}
