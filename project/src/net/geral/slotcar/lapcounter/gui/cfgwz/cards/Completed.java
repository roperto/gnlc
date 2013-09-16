package net.geral.slotcar.lapcounter.gui.cfgwz.cards;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import net.geral.slotcar.lapcounter.gui.cfgwz.Card;
import net.geral.slotcar.lapcounter.gui.cfgwz.ConfigurationWizard;

public class Completed extends Card {
	private static final long	serialVersionUID	= 1L;
	
	public Completed(final ConfigurationWizard w) {
		super(w);
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[] {1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		final JLabel label1 = new JLabel("Congratulations!");
		label1.setFont(new Font("SansSerif", Font.BOLD, 15));
		final GridBagConstraints gbc_label1 = new GridBagConstraints();
		gbc_label1.insets = new Insets(0, 0, 5, 0);
		gbc_label1.gridx = 0;
		gbc_label1.gridy = 1;
		add(label1, gbc_label1);
		
		final JLabel label2 = new JLabel("You are now ready to race!");
		final GridBagConstraints gbc_label2 = new GridBagConstraints();
		gbc_label2.insets = new Insets(0, 0, 5, 0);
		gbc_label2.gridx = 0;
		gbc_label2.gridy = 3;
		add(label2, gbc_label2);
		
		final JLabel label3 = new JLabel("Click 'finish' to save the configuration.");
		final GridBagConstraints gbc_label3 = new GridBagConstraints();
		gbc_label3.insets = new Insets(0, 0, 5, 0);
		gbc_label3.gridx = 0;
		gbc_label3.gridy = 5;
		add(label3, gbc_label3);
	}
	
	@Override
	public void activated() {}
	
	@Override
	public void back() {
		wizard.cardLaneConfigurationTest.activate();
	}
	
	@Override
	public void deactivated() {}
	
	@Override
	public boolean enableBack() {
		return true;
	}
	
	@Override
	public boolean enableNext() {
		return true;
	}
	
	@Override
	public String getTitle() {
		return "Configuration Completed!";
	}
	
	@Override
	public boolean isFinish() {
		return true;
	}
	
	@Override
	public void next() {
		wizard.kernel.config.save();
		wizard.finish();
	}
}
