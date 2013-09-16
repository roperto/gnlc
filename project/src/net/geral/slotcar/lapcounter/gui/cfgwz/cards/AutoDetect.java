package net.geral.slotcar.lapcounter.gui.cfgwz.cards;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import net.geral.slotcar.lapcounter.communication.CommunicationDetector;
import net.geral.slotcar.lapcounter.communication.CommunicationDetectorListener;
import net.geral.slotcar.lapcounter.gui.cfgwz.Card;
import net.geral.slotcar.lapcounter.gui.cfgwz.ConfigurationWizard;

public class AutoDetect extends Card implements CommunicationDetectorListener {
	private static final long		serialVersionUID	= 1L;

	public final JTextArea			txtrStatus			= new JTextArea();

	private CommunicationDetector	detector			= null;
	private StringBuffer			status				= new StringBuffer();
	private Card					nextCard			= null;

	public AutoDetect(ConfigurationWizard configurationWizard) {
		super(configurationWizard);

		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, BorderLayout.CENTER);
		txtrStatus.setFont(new Font("Monospaced", Font.PLAIN, 12));
		scrollPane.setViewportView(txtrStatus);

		txtrStatus.setEditable(false);
	}

	@Override
	public void activated() {
		detector = wizard.kernel.communication.createDetector(wizard.kernel, this);
		status = new StringBuffer();
		txtrStatus.setText(status.toString());
		wizard.kernel.communication.close();
		detector.start();
	}

	@Override
	public void back() {
		wizard.cardWelcome.activate();
	}

	@Override
	public void completed(boolean found) {
		if (found) {
			nextCard = wizard.cardMainPowerTest;
			println();
			println();
			println("Detection Complete! Please click 'Next' to continue.");
		}
		else {
			println();
			println();
			println("Detection Failed! Please click 'Back' to start over.");
			nextCard = null;
		}
		wizard.updateVisuals();
	}

	@Override
	public void deactivated() {
		if (detector != null) detector.abort();
		detector = null;
	}

	@Override
	public boolean enableBack() {
		return true;
	}

	@Override
	public boolean enableNext() {
		return (nextCard != null);
	}

	@Override
	public String getTitle() {
		return "Detecting Hardware...";
	}

	@Override
	public boolean isFinish() {
		return false;
	}

	@Override
	public void next() {
		if (nextCard != null) nextCard.activate();
	}

	@Override
	public void print(String s) {
		status.append(s);
		txtrStatus.setText(status.toString());
	}

	@Override
	public void println() {
		status.append("\n");
		txtrStatus.setText(status.toString());
		txtrStatus.setCaretPosition(status.length());
	}

	@Override
	public void println(String s) {
		status.append(s);
		println();
	}
}
