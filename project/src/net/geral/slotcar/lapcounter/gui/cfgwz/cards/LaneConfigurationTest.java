package net.geral.slotcar.lapcounter.gui.cfgwz.cards;

import javax.swing.JPanel;
import net.geral.slotcar.lapcounter.communication.LapEvent;
import net.geral.slotcar.lapcounter.communication.LapListener;
import net.geral.slotcar.lapcounter.gui.cfgwz.Card;
import net.geral.slotcar.lapcounter.gui.cfgwz.ConfigurationWizard;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.subs.EightOptionsPlusNoneOneListener;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.subs.EightOptionsPlusOneChangeEvent;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.subs.EightOptionsPlusOnePanel;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.subs.MiniLaneLapInfo;
import net.geral.slotcar.lapcounter.structs.Configuration;

public class LaneConfigurationTest extends Card implements LapListener, EightOptionsPlusNoneOneListener {
	private static final long				serialVersionUID	= 1L;
	private static final int				LANES				= Configuration.MAX_LANES;
	
	private final EightOptionsPlusOnePanel	poweredLane			= new EightOptionsPlusOnePanel();
	
	private final MiniLaneLapInfo[]			lanes				= new MiniLaneLapInfo[LANES];
	
	public LaneConfigurationTest(ConfigurationWizard w) {
		super(w);
		setLayout(new BorderLayout(0, 0));
		
		JPanel panelInfo = new JPanel();
		add(panelInfo, BorderLayout.SOUTH);
		panelInfo.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblPleaseTestThe = new JLabel("Please, test the configuration before continuing.");
		lblPleaseTestThe.setBorder(new EmptyBorder(5, 0, 0, 0));
		lblPleaseTestThe.setFont(new Font("SansSerif", Font.ITALIC, 11));
		lblPleaseTestThe.setHorizontalAlignment(SwingConstants.CENTER);
		panelInfo.add(lblPleaseTestThe);
		
		JLabel lblIfThePower = new JLabel("If the power or sensor is incorrectly assigned, please go back to fix it.");
		lblIfThePower.setBorder(new EmptyBorder(0, 0, 5, 0));
		lblIfThePower.setFont(new Font("SansSerif", Font.ITALIC, 11));
		lblIfThePower.setHorizontalAlignment(SwingConstants.CENTER);
		panelInfo.add(lblIfThePower);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {0, 0, 0, 0};
		gbl_panel.rowHeights = new int[] {0, 0, 0, 10, 0, 0, 0};
		gbl_panel.columnWeights = new double[] {1.0, 1.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[] {1.0, 0.0, 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblPower = new JLabel("Powered Lane:");
		GridBagConstraints gbc_lblPower = new GridBagConstraints();
		gbc_lblPower.fill = GridBagConstraints.BOTH;
		gbc_lblPower.insets = new Insets(0, 0, 5, 5);
		gbc_lblPower.gridx = 1;
		gbc_lblPower.gridy = 1;
		panel.add(lblPower, gbc_lblPower);
		
		GridBagConstraints gbc_poweredLane = new GridBagConstraints();
		gbc_poweredLane.insets = new Insets(0, 0, 5, 5);
		gbc_poweredLane.fill = GridBagConstraints.BOTH;
		gbc_poweredLane.gridx = 1;
		gbc_poweredLane.gridy = 2;
		poweredLane.setNoneLabel("All");
		poweredLane.addChangeListener(this);
		panel.add(poweredLane, gbc_poweredLane);
		
		JPanel panelLanes = new JPanel();
		GridBagConstraints gbc_panelLanes = new GridBagConstraints();
		gbc_panelLanes.insets = new Insets(0, 0, 5, 5);
		gbc_panelLanes.fill = GridBagConstraints.BOTH;
		gbc_panelLanes.gridx = 1;
		gbc_panelLanes.gridy = 4;
		panel.add(panelLanes, gbc_panelLanes);
		panelLanes.setLayout(new GridLayout(0, 2, 0, 0));
		
		MiniLaneLapInfo lane1 = new MiniLaneLapInfo();
		panelLanes.add(lane1);
		
		MiniLaneLapInfo lane2 = new MiniLaneLapInfo();
		panelLanes.add(lane2);
		
		MiniLaneLapInfo lane3 = new MiniLaneLapInfo();
		panelLanes.add(lane3);
		
		MiniLaneLapInfo lane4 = new MiniLaneLapInfo();
		panelLanes.add(lane4);
		
		MiniLaneLapInfo lane5 = new MiniLaneLapInfo();
		panelLanes.add(lane5);
		
		MiniLaneLapInfo lane6 = new MiniLaneLapInfo();
		panelLanes.add(lane6);
		
		MiniLaneLapInfo lane7 = new MiniLaneLapInfo();
		panelLanes.add(lane7);
		
		MiniLaneLapInfo lane8 = new MiniLaneLapInfo();
		panelLanes.add(lane8);
		
		// make array
		lanes[0] = lane1;
		lanes[1] = lane2;
		lanes[2] = lane3;
		lanes[3] = lane4;
		lanes[4] = lane5;
		lanes[5] = lane6;
		lanes[6] = lane7;
		lanes[7] = lane8;
	}
	
	@Override
	public void back() {
		wizard.cardLaneConfiguration.setLane(wizard.kernel.config.LanesQuantity);
		wizard.cardLaneConfiguration.activate();
	}
	
	@Override
	public void next() {
		wizard.cardCompleted.activate();
	}
	
	@Override
	public void activated() {
		Configuration cfg = wizard.kernel.config;
		// load config
		for (int i = 0; i < LANES; i++) {
			lanes[i].setVisible(i < cfg.LanesQuantity);
			lanes[i].setLane(cfg.LaneColor[i], i + 1);
		}
		// set default to all and ammount
		poweredLane.set(0);
		poweredLane.setAmmount(cfg.LanesQuantity);
		// set power
		setPower();
		// listen for laps
		wizard.kernel.communication.addLapListener(this);
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
		return "Lane Configuration Test";
	}
	
	@Override
	public void onLap(LapEvent evt) {
		int lane = evt.LaneNumber - 1;// convert lane number to index
		for (int i = 0; i < LANES; i++) {
			if (i == lane) lanes[i].setInfo(evt.LapSeconds, evt.BlockSeconds);
			else lanes[i].clearInfo();
		}
	}
	
	@Override
	public void changed(EightOptionsPlusOneChangeEvent event) {
		setPower();
	}
	
	private void setPower() {
		int p = poweredLane.getSelected();
		
		if (p == 0) wizard.kernel.communication.setTrackPower(true);
		else {
			wizard.kernel.communication.setTrackPower(false);
			wizard.kernel.communication.setLanePower(p - 1, true);
		}
	}
}
