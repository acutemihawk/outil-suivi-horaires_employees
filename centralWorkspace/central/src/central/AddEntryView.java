package central;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddEntryView {

	public static void display(ManagerController mc, Worker w) {
		JPanel panel = new JPanel(new GridLayout(0, 1));

		JTextField nameField = new JTextField();
		panel.add(new JLabel("Entry (Format : YYYY-MM-DD/HH:mm/workerID) :"));
		panel.add(nameField);

		int result = JOptionPane.showConfirmDialog(null, panel, "Add entry",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			try {
				mc.parseEmulatorInput(nameField.getText());
				mc.getManagerView().updateInfos(w.getId_Worker());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
