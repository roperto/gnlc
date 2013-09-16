package net.geral.slotcar.lapcounter.gui.cfgwz.cards;

import net.geral.slotcar.lapcounter.core.Logger;
import net.geral.slotcar.lapcounter.gui.cfgwz.ConfigurationWizard;
import net.geral.slotcar.lapcounter.structs.Configuration;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;

public class LanesQuantity extends net.geral.slotcar.lapcounter.gui.cfgwz.Card implements ActionListener {
	private static final long		serialVersionUID	= 1L;
	private static final int		MAX_LANES			= Configuration.MAX_LANES;
	
	private final ButtonGroup		buttonGroup			= new ButtonGroup();
	private final JToggleButton[]	toggleButtons		= new JToggleButton[MAX_LANES];
	
	public LanesQuantity(ConfigurationWizard w) {
		super(w);
		
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWidths = new int[] {0, 200, 0, 0};
		gbl.rowHeights = new int[] {0, 100, 0, 0};
		gbl.columnWeights = new double[] {1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl.rowWeights = new double[] {1.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gbl);
		
		JLabel lblHowManyLanes = new JLabel("How many lanes do you want to configure?");
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.anchor = GridBagConstraints.SOUTH;
		gbc1.gridwidth = 3;
		gbc1.insets = new Insets(0, 0, 5, 5);
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		add(lblHowManyLanes, gbc1);
		lblHowManyLanes.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panelButtons = new JPanel();
		GridBagConstraints gbc_panelInner = new GridBagConstraints();
		gbc_panelInner.fill = GridBagConstraints.BOTH;
		gbc_panelInner.insets = new Insets(0, 0, 5, 5);
		gbc_panelInner.gridx = 1;
		gbc_panelInner.gridy = 1;
		add(panelButtons, gbc_panelInner);
		panelButtons.setLayout(new GridLayout(0, 4, 0, 0));
		
		for (int i = 0; i < MAX_LANES; i++) {
			toggleButtons[i] = new JToggleButton(String.valueOf(i + 1));
			toggleButtons[i].addActionListener(this);
			buttonGroup.add(toggleButtons[i]);
			panelButtons.add(toggleButtons[i]);
		}
	}
	
	@Override
	public void back() {
		wizard.cardRaceLights.activate();
	}
	
	@Override
	public void next() {
		wizard.cardLaneConfiguration.setLane(1);
		wizard.cardLaneConfiguration.activate();
	}
	
	@Override
	public void activated() {
		getToggleButton(wizard.kernel.config.LanesQuantity).setSelected(true);
	}
	
	private JToggleButton getToggleButton(int qtty) {
		if (qtty < 1) qtty = 1;
		if (qtty > MAX_LANES) qtty = MAX_LANES;
		return toggleButtons[qtty - 1];
	}
	
	@Override
	public void deactivated() {
		wizard.kernel.config.LanesQuantity = getToggleButtonSelected();
		Logger.log("Number of lanes: " + wizard.kernel.config.LanesQuantity);
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
		return "How many lanes?";
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		wizard.kernel.config.LanesQuantity = getToggleButtonSelected();
	}
	
	private int getToggleButtonSelected() {
		// find
		for (int i = 0; i < MAX_LANES; i++) {
			if (toggleButtons[i].isSelected()) return i + 1;
		}
		// not found, set it
		toggleButtons[0].setSelected(true);
		return 1;
	}
}
