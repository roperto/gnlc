package net.geral.slotcar.lapcounter.gui.cfgwz.cards;

import net.geral.slotcar.lapcounter.gui.cfgwz.Card;
import net.geral.slotcar.lapcounter.gui.cfgwz.ConfigurationWizard;
import net.geral.slotcar.lapcounter.structs.RaceLightState;
import java.awt.BorderLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.JToggleButton;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;

public class RaceLights extends Card implements ActionListener {
	private static final long	serialVersionUID	= 1L;
	
	private JToggleButton		tglbtnOff			= new JToggleButton();
	private JToggleButton		tglbtnReady			= new JToggleButton();
	private JToggleButton		tglbtnSet			= new JToggleButton();
	private JToggleButton		tglbtnGo			= new JToggleButton();
	private JCheckBox			cbInvertLogic		= new JCheckBox("Invert Logic");
	
	public RaceLights(ConfigurationWizard w) {
		super(w);
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		JTextPane txtrPleaseCheckIf = new JTextPane();
		scrollPane.setViewportView(txtrPleaseCheckIf);
		txtrPleaseCheckIf.setEditable(false);
		txtrPleaseCheckIf.setText("Please, check if the race lights are working properly by selecting each of its state.\n\nIf not, try inverting the logic (checkbox below).\n\nIn case you are not using race lights, just ignore this step.");
		
		JPanel panelControls = new JPanel();
		add(panelControls, BorderLayout.SOUTH);
		panelControls.setLayout(new BorderLayout(0, 0));
		
		JPanel panelButtons = new JPanel();
		panelControls.add(panelButtons, BorderLayout.CENTER);
		panelButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panelCenterButtons = new JPanel();
		panelButtons.add(panelCenterButtons);
		panelCenterButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		tglbtnOff.setIcon(new ImageIcon(RaceLights.class.getResource("/images/lights/off.png")));
		tglbtnOff.addActionListener(this);
		panelCenterButtons.add(tglbtnOff);
		
		tglbtnReady.setIcon(new ImageIcon(RaceLights.class.getResource("/images/lights/ready.png")));
		tglbtnReady.addActionListener(this);
		panelCenterButtons.add(tglbtnReady);
		
		tglbtnSet.setIcon(new ImageIcon(RaceLights.class.getResource("/images/lights/set.png")));
		tglbtnSet.addActionListener(this);
		panelCenterButtons.add(tglbtnSet);
		
		tglbtnGo.setIcon(new ImageIcon(RaceLights.class.getResource("/images/lights/go.png")));
		tglbtnGo.addActionListener(this);
		panelCenterButtons.add(tglbtnGo);
		
		ButtonGroup group = new ButtonGroup();
		group.add(tglbtnOff);
		group.add(tglbtnReady);
		group.add(tglbtnSet);
		group.add(tglbtnGo);
		
		panelControls.add(cbInvertLogic, BorderLayout.SOUTH);
		cbInvertLogic.setHorizontalAlignment(SwingConstants.CENTER);
		cbInvertLogic.addActionListener(this);
	}
	
	@Override
	public void back() {
		wizard.cardMainPowerTest.activate();
	}
	
	@Override
	public void next() {
		wizard.cardLanesQuantity.activate();
	}
	
	@Override
	public void activated() {
		cbInvertLogic.setSelected(wizard.kernel.config.InvertRaceLightsLogic);
		tglbtnOff.setSelected(true);
		wizard.kernel.communication.setLightState(RaceLightState.Off);
	}
	
	@Override
	public void deactivated() {}
	
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
		return "Race Lights";
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		updateLight();
	}
	
	private void updateLight() {
		// set config
		wizard.kernel.config.InvertRaceLightsLogic = cbInvertLogic.isSelected();
		
		// set state
		wizard.kernel.communication.setLightState(getLightState());
	}
	
	private RaceLightState getLightState() {
		if (tglbtnOff.isSelected()) return RaceLightState.Off;
		if (tglbtnReady.isSelected()) return RaceLightState.Ready;
		if (tglbtnSet.isSelected()) return RaceLightState.Set;
		if (tglbtnGo.isSelected()) return RaceLightState.Go;
		
		// nothing selected? select OFF and try again
		tglbtnOff.setSelected(true);
		return getLightState();
	}
}
