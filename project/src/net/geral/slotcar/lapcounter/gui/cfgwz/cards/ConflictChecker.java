package net.geral.slotcar.lapcounter.gui.cfgwz.cards;

import net.geral.slotcar.lapcounter.gui.cfgwz.Card;
import net.geral.slotcar.lapcounter.gui.cfgwz.ConfigurationWizard;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.subs.ConflictCheckerRow;
import net.geral.slotcar.lapcounter.structs.Configuration;
import javax.swing.JScrollPane;

public class ConflictChecker extends Card {
	private static final long		serialVersionUID	= 1L;
	
	private static final int		LANES				= Configuration.MAX_LANES;
	
	private ConflictCheckerRow[]	panels				= new ConflictCheckerRow[LANES];
	
	public ConflictChecker(ConfigurationWizard w) {
		super(w);
		setLayout(new BorderLayout(0, 0));
		
		JPanel panelInfo = new JPanel();
		panelInfo.setBorder(new EmptyBorder(5, 0, 5, 0));
		add(panelInfo, BorderLayout.NORTH);
		panelInfo.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblInfo1 = new JLabel("Relays and sensors cannot be shared between different lanes.");
		lblInfo1.setFont(getFont().deriveFont(Font.ITALIC));
		lblInfo1.setHorizontalAlignment(SwingConstants.CENTER);
		panelInfo.add(lblInfo1);
		
		JLabel lblInfo2 = new JLabel("Please, resolve any conflicts before continuing.");
		lblInfo2.setFont(getFont().deriveFont(Font.ITALIC));
		lblInfo2.setHorizontalAlignment(SwingConstants.CENTER);
		panelInfo.add(lblInfo2);
		
		JPanel panelCenter = new JPanel();
		add(panelCenter, BorderLayout.CENTER);
		GridBagLayout gbl_panelCenter = new GridBagLayout();
		gbl_panelCenter.columnWidths = new int[] {0, 0, 0, 0};
		gbl_panelCenter.rowHeights = new int[] {0, 0, 0, 0};
		gbl_panelCenter.columnWeights = new double[] {1.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panelCenter.rowWeights = new double[] {1.0, 0.0, 1.0, Double.MIN_VALUE};
		panelCenter.setLayout(gbl_panelCenter);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 1;
		panelCenter.add(scrollPane, gbc_scrollPane);
		
		JPanel panelTable = new JPanel();
		scrollPane.setViewportView(panelTable);
		panelTable.setLayout(new GridLayout(0, 1, 0, 0));
		
		panelTable.add(new ConflictCheckerRow(0, wizard.cardLaneConfiguration));
		for (int i = 0; i < LANES; i++) {
			panels[i] = new ConflictCheckerRow(i + 1, wizard.cardLaneConfiguration);
			panelTable.add(panels[i]);
		}
	}
	
	@Override
	public void back() {
		wizard.cardLaneConfiguration.setLane(wizard.kernel.config.LanesQuantity);
		wizard.cardLaneConfiguration.activate();
	}
	
	@Override
	public void next() {}
	
	@Override
	public void activated() {
		Configuration cfg = wizard.kernel.config;
		boolean anyConflict = false;
		
		for (int i = 0; i < LANES; i++) {
			// conflict
			boolean cr = (cfg.countRelayUse(cfg.LaneRelay[i]) > 1); // relay
			boolean cs = (cfg.countSensorUse(cfg.LaneSensor[i]) > 1); // sensor
			if (cr || cs) anyConflict = true;
			// visibility
			boolean v = (i < cfg.LanesQuantity); // with only < because is i=index
			// update
			panels[i].setInfo(v, cr, cs, cfg.LaneRelay[i], cfg.LaneSensor[i]);
		}
		
		// if no conflicts, move on
		if (!anyConflict) wizard.cardLaneConfigurationTest.activate();
	}
	
	@Override
	public void deactivated() {}
	
	@Override
	public boolean isFinish() {
		return false;
	}
	
	@Override
	public boolean enableNext() {
		return false;
	}
	
	@Override
	public boolean enableBack() {
		return true;
	}
	
	@Override
	public String getTitle() {
		return "Configuration Conflict";
	}
}
