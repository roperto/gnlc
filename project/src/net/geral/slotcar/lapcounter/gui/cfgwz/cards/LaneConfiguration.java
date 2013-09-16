package net.geral.slotcar.lapcounter.gui.cfgwz.cards;

import net.geral.slotcar.lapcounter.communication.Communication;
import net.geral.slotcar.lapcounter.communication.LapEvent;
import net.geral.slotcar.lapcounter.communication.LapListener;
import net.geral.slotcar.lapcounter.core.Logger;
import net.geral.slotcar.lapcounter.gui.cfgwz.Card;
import net.geral.slotcar.lapcounter.gui.cfgwz.ConfigurationWizard;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.subs.EightOptionsPlusOneChangeEvent;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.subs.EightOptionsPlusNoneOneListener;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.subs.EightOptionsPlusOnePanel;
import net.geral.slotcar.lapcounter.structs.Configuration;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.border.LineBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LaneConfiguration extends Card implements EightOptionsPlusNoneOneListener, LapListener, ActionListener {
	private static final long				serialVersionUID	= 1L;
	
	private final JLabel					lblColorExample		= new JLabel(" Lane Color Example");
	private final EightOptionsPlusOnePanel	relay8cb			= new EightOptionsPlusOnePanel();
	private final EightOptionsPlusOnePanel	sensor8cb			= new EightOptionsPlusOnePanel();
	
	private int								laneNumber			= 1;
	
	public LaneConfiguration(ConfigurationWizard w) {
		super(w);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {100, 0, 80, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 10, 0, 0, 0, 10, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[] {1.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblColor = new JLabel("Color:");
		GridBagConstraints gbc_lblColor = new GridBagConstraints();
		gbc_lblColor.fill = GridBagConstraints.BOTH;
		gbc_lblColor.insets = new Insets(0, 0, 5, 5);
		gbc_lblColor.gridx = 0;
		gbc_lblColor.gridy = 1;
		add(lblColor, gbc_lblColor);
		lblColor.setFont(new Font("SansSerif", Font.BOLD, 11));
		
		JLabel lblColorInfo = new JLabel("Choose a color to represent this lane.");
		GridBagConstraints gbc_lblColorInfo = new GridBagConstraints();
		gbc_lblColorInfo.gridwidth = 2;
		gbc_lblColorInfo.fill = GridBagConstraints.BOTH;
		gbc_lblColorInfo.insets = new Insets(0, 0, 5, 0);
		gbc_lblColorInfo.gridx = 1;
		gbc_lblColorInfo.gridy = 1;
		add(lblColorInfo, gbc_lblColorInfo);
		lblColorInfo.setHorizontalAlignment(SwingConstants.TRAILING);
		lblColorInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
		
		lblColorExample.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblColorExample.setOpaque(true);
		lblColorExample.setBackground(Color.WHITE);
		lblColorExample.setFont(new Font("Monospaced", Font.BOLD, 12));
		GridBagConstraints gbc_lblColorExample = new GridBagConstraints();
		gbc_lblColorExample.fill = GridBagConstraints.BOTH;
		gbc_lblColorExample.gridwidth = 2;
		gbc_lblColorExample.insets = new Insets(0, 0, 5, 5);
		gbc_lblColorExample.gridx = 0;
		gbc_lblColorExample.gridy = 2;
		add(lblColorExample, gbc_lblColorExample);
		
		JButton btnChoose = new JButton("choose");
		btnChoose.addActionListener(this);
		GridBagConstraints gbc_btnChoose = new GridBagConstraints();
		gbc_btnChoose.fill = GridBagConstraints.BOTH;
		gbc_btnChoose.insets = new Insets(0, 0, 5, 0);
		gbc_btnChoose.gridx = 2;
		gbc_btnChoose.gridy = 2;
		add(btnChoose, gbc_btnChoose);
		
		JLabel lblRelay = new JLabel("Power Relay:");
		lblRelay.setFont(new Font("SansSerif", Font.BOLD, 11));
		GridBagConstraints gbc_lblRelay = new GridBagConstraints();
		gbc_lblRelay.fill = GridBagConstraints.BOTH;
		gbc_lblRelay.insets = new Insets(0, 0, 5, 5);
		gbc_lblRelay.gridx = 0;
		gbc_lblRelay.gridy = 4;
		add(lblRelay, gbc_lblRelay);
		
		JLabel lblRelayInfo = new JLabel("Choose the correct relay that turns on the power for this lane.");
		lblRelayInfo.setHorizontalAlignment(SwingConstants.TRAILING);
		lblRelayInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
		GridBagConstraints gbc_lblRelayInfo = new GridBagConstraints();
		gbc_lblRelayInfo.fill = GridBagConstraints.BOTH;
		gbc_lblRelayInfo.gridwidth = 2;
		gbc_lblRelayInfo.insets = new Insets(0, 0, 5, 0);
		gbc_lblRelayInfo.gridx = 1;
		gbc_lblRelayInfo.gridy = 4;
		add(lblRelayInfo, gbc_lblRelayInfo);
		
		GridBagConstraints gbc_relay8cb = new GridBagConstraints();
		gbc_relay8cb.gridwidth = 3;
		gbc_relay8cb.insets = new Insets(0, 0, 5, 0);
		gbc_relay8cb.fill = GridBagConstraints.BOTH;
		gbc_relay8cb.gridx = 0;
		gbc_relay8cb.gridy = 5;
		add(relay8cb, gbc_relay8cb);
		
		JLabel lblPoweralert = new JLabel("If the power seems to be off, try selecting another relay.");
		GridBagConstraints gbc_lblPoweralert = new GridBagConstraints();
		gbc_lblPoweralert.gridwidth = 3;
		gbc_lblPoweralert.insets = new Insets(0, 0, 5, 5);
		gbc_lblPoweralert.gridx = 0;
		gbc_lblPoweralert.gridy = 6;
		add(lblPoweralert, gbc_lblPoweralert);
		
		JLabel lblSensor = new JLabel("Sensor:");
		lblSensor.setFont(new Font("SansSerif", Font.BOLD, 11));
		GridBagConstraints gbc_lblSensor = new GridBagConstraints();
		gbc_lblSensor.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblSensor.insets = new Insets(0, 0, 5, 5);
		gbc_lblSensor.gridx = 0;
		gbc_lblSensor.gridy = 8;
		add(lblSensor, gbc_lblSensor);
		
		JLabel lblSensorInfo = new JLabel("Run a car through the sensors to auto-detect this lane.");
		lblSensorInfo.setHorizontalAlignment(SwingConstants.TRAILING);
		lblSensorInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
		GridBagConstraints gbc_lblSensorInfo = new GridBagConstraints();
		gbc_lblSensorInfo.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblSensorInfo.gridwidth = 2;
		gbc_lblSensorInfo.insets = new Insets(0, 0, 5, 0);
		gbc_lblSensorInfo.gridx = 1;
		gbc_lblSensorInfo.gridy = 8;
		add(lblSensorInfo, gbc_lblSensorInfo);
		GridBagConstraints gbc_sensor8cb = new GridBagConstraints();
		gbc_sensor8cb.gridwidth = 3;
		gbc_sensor8cb.insets = new Insets(0, 0, 5, 0);
		gbc_sensor8cb.fill = GridBagConstraints.BOTH;
		gbc_sensor8cb.gridx = 0;
		gbc_sensor8cb.gridy = 9;
		add(sensor8cb, gbc_sensor8cb);
		
		// events
		relay8cb.addChangeListener(this);
		sensor8cb.addChangeListener(this);
	}
	
	@Override
	public void back() {
		if (laneNumber <= 1) {
			wizard.cardLanesQuantity.activate();
		}
		else {
			setLane(laneNumber - 1);
		}
	}
	
	@Override
	public void next() {
		if (laneNumber >= wizard.kernel.config.LanesQuantity) {
			wizard.cardConflictChecker.activate();
		}
		else {
			setLane(laneNumber + 1);
		}
	}
	
	@Override
	public void activated() {
		wizard.kernel.communication.addLapListener(this);
		setLane(laneNumber);
	}
	
	@Override
	public void deactivated() {
		wizard.kernel.communication.removeLapListener(this);
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
		return "Lane #" + laneNumber;
	}
	
	public void setLane(int lane) {
		laneNumber = lane;
		loadConfiguration();
		updateRelay();
		wizard.updateVisuals();
	}
	
	private void loadConfiguration() {
		Configuration c = wizard.kernel.config;
		
		lblColorExample.setBackground(c.LaneColor[laneNumber - 1]);
		relay8cb.set(c.LaneRelay[laneNumber - 1]);
		sensor8cb.set(c.LaneSensor[laneNumber - 1]);
	}
	
	private void updateRelay() {
		int relay = wizard.kernel.config.LaneRelay[laneNumber - 1];
		Communication c = wizard.kernel.communication;
		if (relay == 0) {
			c.setTrackPower(true);
		}
		else {
			c.setTrackPower(false);
			c.setLanePower(relay - 1, true);
		}
	}
	
	@Override
	public void changed(EightOptionsPlusOneChangeEvent e) {
		if (e.getSource() == relay8cb) relayChanged(e.newValue);
		else if (e.getSource() == sensor8cb) sensorChanged(e.newValue);
	}
	
	private void sensorChanged(int newValue) {
		wizard.kernel.config.LaneSensor[laneNumber - 1] = newValue;
		Logger.log("Lane #" + laneNumber + " set to sensor #" + newValue);
	}
	
	private void relayChanged(int newValue) {
		wizard.kernel.config.LaneRelay[laneNumber - 1] = newValue;
		Logger.log("Lane #" + laneNumber + " set to relay #" + newValue);
		updateRelay();
	}
	
	@Override
	public void onLap(LapEvent evt) {
		int sensor = evt.SensorIndex + 1;
		sensor8cb.set(sensor);
		sensorChanged(sensor);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Color c = JColorChooser.showDialog(this, "Select Lane Color...", wizard.kernel.config.LaneColor[laneNumber - 1]);
		if (c != null) {
			wizard.kernel.config.LaneColor[laneNumber - 1] = c;
			lblColorExample.setBackground(c);
		}
	}
}
