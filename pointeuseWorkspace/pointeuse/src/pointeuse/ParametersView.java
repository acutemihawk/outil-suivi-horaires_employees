package pointeuse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 * @author User
 * @brief Pane that controls the emulator parameters
 */
public class ParametersView extends JPanel implements ActionListener {

	private JTextField portField;
	private JTextField addressField;
	private EmulatorController emc;
	
	private JButton changePortButton;
	private JButton changeAddressButton;
	
	/**
	 * @brief constructor for the class
	 * @param emc
	 */
	public ParametersView(EmulatorController emc) {
		this.emc = emc;
		
		//Main panel for the port input
		JPanel portPane = new JPanel();
		BoxLayout portLayout = new BoxLayout(portPane, BoxLayout.X_AXIS);
		portPane.setLayout(portLayout);
		
		portPane.add(new JLabel("Port : "));
		
		//Port panel textfield
		portField = new JTextField();
		portField.setMaximumSize(new Dimension(150, 25));
		portField.addActionListener(this);
		portField.setText(String.valueOf(emc.getServerPort()));
		portPane.add(portField);

		changePortButton = new JButton("Change");
		changePortButton.addActionListener(this);
		portPane.add(changePortButton);
		
		JPanel addressPane = new JPanel();
		BoxLayout IPLayout = new BoxLayout(addressPane, BoxLayout.X_AXIS);
		addressPane.setLayout(IPLayout);
		
		addressPane.add(new JLabel("IP : "));
		
		addressField = new JTextField();
		addressField.setMaximumSize(new Dimension(150, 25));
		addressField.addActionListener(this);
		addressField.setText(String.valueOf(emc.getServerAddress()));
		addressPane.add(addressField);

		changeAddressButton = new JButton("Change");
		changeAddressButton.addActionListener(this);
		addressPane.add(changeAddressButton);
		
		this.setLayout(new GridLayout(7, 0));
		this.add(portPane);
		this.add(addressPane);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == changePortButton) {
			int port = Integer.parseInt(portField.getText());
			if(port < 65535 && port > 0) {
				emc.changeServerPort(port);
				System.out.println("Port changed to : " +port +System.lineSeparator());
			}
			else {
				textError();
			}
		}
		if(e.getSource() == changeAddressButton) {
			String address = addressField.getText();
			emc.changeServerAddress(address);
			System.out.println("Address changed to : " +address +System.lineSeparator());
		}
	}
	
	/**
	 * @brief highligh the text area when there is an error
	 */
	public void textError() {
		portField.setBorder(new LineBorder(Color.RED, 2));

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				portField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
			}
		}, 2000);
	}
}

