package shattered;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import org.jetbrains.annotations.NotNull;

final class CrashWindow {

	public static void create(@NotNull final String reason) {
		final String message =
				"An error has occurred that prevented " + Shattered.NAME + " from starting.\n" +
						"To prevent data corruption, " + Shattered.NAME + " will now exit!\n" +
						"Reason: " + reason;
		final JOptionPane pane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
		final JDialog dialog = pane.createDialog(Shattered.NAME + " - Error");
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				Runtime.getRuntime().halt(-1);
			}
		});
		pane.addPropertyChangeListener(event -> Runtime.getRuntime().halt(-1));
		dialog.setVisible(true);
	}
}