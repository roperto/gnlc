package net.geral.slotcar.lapcounter.gui.cfgwz;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.InvalidParameterException;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.core.Logger;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.AutoDetect;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.Completed;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.ConflictChecker;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.LaneConfiguration;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.LaneConfigurationTest;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.LanesQuantity;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.MainPowerTest;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.RaceLights;
import net.geral.slotcar.lapcounter.gui.cfgwz.cards.Welcome;

public class ConfigurationWizard extends JPanel implements ActionListener {
	private static final long			serialVersionUID			= 1L;
	
	private final Dimension				size						= new Dimension(400, 300);
	private final CardLayout			cardLayout					= new CardLayout();
	private final JPanel				cardPanel					= new JPanel(cardLayout);
	private final JButton				btnBack						= new JButton("Back");
	private final JButton				btnNext						= new JButton();
	private final JButton				btnCancel					= new JButton("Cancel");
	
	private final HashMap<String, Card>	cards						= new HashMap<String, Card>();
	public final Welcome				cardWelcome					= new Welcome(this);
	public final AutoDetect				cardAutoDetect				= new AutoDetect(this);
	public final MainPowerTest			cardMainPowerTest			= new MainPowerTest(this);
	public final RaceLights				cardRaceLights				= new RaceLights(this);
	public final LanesQuantity			cardLanesQuantity			= new LanesQuantity(this);
	public final LaneConfiguration		cardLaneConfiguration		= new LaneConfiguration(this);
	public final ConflictChecker		cardConflictChecker			= new ConflictChecker(this);
	public final LaneConfigurationTest	cardLaneConfigurationTest	= new LaneConfigurationTest(this);
	public final Completed				cardCompleted				= new Completed(this);
	
	public final Kernel					kernel;
	
	private final boolean				firstTimeConfig;
	private final JLabel				lblTitle					= new JLabel("[title]");
	
	private Card						currentCard					= null;
	private boolean						wasPaused					= false;
	private final JPanel				panelCenter					= new JPanel();
	
	public ConfigurationWizard(final Kernel k, final boolean first) {
		setBackground(Color.BLACK);
		kernel = k;
		firstTimeConfig = first;
		
		// set size, really!
		panelCenter.setSize(size);
		panelCenter.setMinimumSize(size);
		panelCenter.setMaximumSize(size);
		panelCenter.setPreferredSize(size);
		
		initComponents();
		cardWelcome.activate();
	}
	
	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getActionCommand().equals("cancel")) {
			cancel();
			return;
		}
		
		if (e.getActionCommand().equals("next")) {
			currentCard.next();
			return;
		}
		
		if (e.getActionCommand().equals("back")) {
			currentCard.back();
			return;
		}
		
		Logger.log("No Action (" + e.getActionCommand() + ") @ " + getClass().getName() + ": " + e.toString());
	}
	
	private void cancel() {
		if (firstTimeConfig) {
			final int r = JOptionPane.showConfirmDialog(this, "Exit the program?", "Cancel Configuration", JOptionPane.YES_NO_OPTION);
			if (r == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
		else {
			finish();
		}
	}
	
	public void finish() {
		kernel.window.applyConfiguration();
		kernel.window.showLanes();
		kernel.getController().setPaused(wasPaused);
	}
	
	private void initComponents() {
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[] {1.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[] {1.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		final GridBagConstraints gbc_panelCenter = new GridBagConstraints();
		gbc_panelCenter.fill = GridBagConstraints.BOTH;
		gbc_panelCenter.insets = new Insets(0, 0, 5, 5);
		gbc_panelCenter.gridx = 1;
		gbc_panelCenter.gridy = 1;
		add(panelCenter, gbc_panelCenter);
		panelCenter.setLayout(new BorderLayout(0, 0));
		panelCenter.add(cardPanel, BorderLayout.CENTER);
		panelCenter.add(lblTitle, BorderLayout.NORTH);
		lblTitle.setOpaque(true);
		lblTitle.setForeground(Color.WHITE);
		lblTitle.setBackground(Color.BLACK);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
		
		// buttons
		final JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panelCenter.add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setBackground(Color.BLACK);
		btnBack.setPreferredSize(new Dimension(80, 23));
		btnBack.setActionCommand("back");
		btnNext.setPreferredSize(new Dimension(80, 23));
		btnNext.setActionCommand("next");
		btnCancel.setPreferredSize(new Dimension(80, 23));
		btnCancel.setActionCommand("cancel");
		buttonPane.add(btnBack);
		buttonPane.add(btnNext);
		final JPanel panel = new JPanel();
		panel.setBackground(Color.BLACK);
		panel.setPreferredSize(new Dimension(30, 23));
		buttonPane.add(panel);
		buttonPane.add(btnCancel);
		btnBack.addActionListener(this);
		btnNext.addActionListener(this);
		btnCancel.addActionListener(this);
	}
	
	public void open() {
		wasPaused = kernel.getController().isPaused();
		kernel.getController().setPaused(true);
		kernel.communication.close();
		kernel.window.show(this);
	}
	
	public void registerCard(final Card c) {
		// called automatically by the constructor of 'Card'
		cards.put(c.id, c);
		cardPanel.add(c, c.id);
	}
	
	public void setCard(final String card) {
		final Card toShow = cards.get(card);
		if (toShow == null) { throw new InvalidParameterException("Invalid card: " + card); }
		if (currentCard != toShow) {
			if (currentCard != null) {
				currentCard.deactivated();
			}
			currentCard = toShow;
			cardLayout.show(cardPanel, card);
			updateVisuals();
			currentCard.activated();
		}
	}
	
	public void updateVisuals() {
		lblTitle.setText(currentCard.getTitle());
		btnBack.setEnabled(currentCard.enableBack());
		btnNext.setEnabled(currentCard.enableNext());
		btnNext.setText(currentCard.isFinish() ? "Finish" : "Next");
	}
}
