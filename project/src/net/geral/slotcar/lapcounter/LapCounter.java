package net.geral.slotcar.lapcounter;

import gnu.io.CommPortIdentifier;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import net.geral.slotcar.lapcounter.core.Kernel;
import net.geral.slotcar.lapcounter.core.Logger;

/**
 * Entry-Point class. Will set look and feel, check rxtx and create a kernel and start it.
 */
public class LapCounter {
	private static final Kernel	kernel	= new Kernel();

	private static void checkRXTX() {
		Path dir = Paths.get("").toAbsolutePath();
		Logger.log("Running from: " + dir.toString());

		Path libs = null;
		String os = System.getProperty("os.name");
		String version = System.getProperty("os.version");
		String arch = System.getProperty("os.arch");
		if (os.contains("Windows")) {
			String x = (arch.contains("64")) ? "64" : "32";
			libs = dir.resolve("rxtx-lib/windows/x" + x);
		}
		else {
			// please send me samples for other OS...
		}
		// apply lib path
		if (libs == null) {
			Logger.log("Libs not detected for os='" + os + "' version='" + version + "' arch='" + arch + "'");
		}
		else {
			try {
				Logger.log("Libs for os='" + os + "' version='" + version + "' arch='" + arch + "':" + libs.toString());
				// set libs
				System.setProperty("java.library.path", libs.toString());
				// refresh
				Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
				fieldSysPath.setAccessible(true);
				fieldSysPath.set(null, null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		// check lib working
		try {
			CommPortIdentifier.getPortIdentifiers();
		}
		catch (UnsatisfiedLinkError e) {
			int r = JOptionPane
					.showConfirmDialog(
							null,
							"Error loading library/dll.\nYou probably need to copy the proper file from rxtx-lib folder to this program's folder.\n\nFor further help, check http://www.geral.net/gnlc/",
							"Library Not Found", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			if (r != JOptionPane.OK_OPTION) System.exit(0);
		}
	}

	public static void main(String[] args) {
		checkRXTX(); // check rxtx functionality

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch (Exception e) {
			Logger.error(e);
		}

		try {
			kernel.start();
		}
		catch (Exception e) {
			Logger.error(e);
			System.exit(1); // force program kill
		}
	}
}
