package net.geral.slotcar.lapcounter;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.core.Logger;

/**
 * Entry-Point class. Will set look and feel, create a kernel and start it.
 */
public class LapCounter {
	private static final Kernel kernel = new Kernel();

	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			Logger.error(e);
		}

		try {
			kernel.start();
		} catch (Exception e) {
			Logger.error(e);
			System.exit(1); // force program kill
		}
	}
}
