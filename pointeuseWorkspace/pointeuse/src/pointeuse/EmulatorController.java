package pointeuse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;

public class EmulatorController {

	private final String DATA_BACKUP_PATH = "src" + File.separator + "pointeuse" + File.separator + "assets"
			+ File.separator + "DATA_BACKUP.txt";

	private final String SETTINGS_BACKUP_PATH = "src" + File.separator + "pointeuse" + File.separator + "assets"
			+ File.separator + "config.txt";

	private final int SERVER_DEFAULT_PORT = 7700;
	private final String SERVER_DEFAULT_ADDRESS = "127.0.0.1";

	private int serverPort;
	private String serverAddress;
	private EmulatorView view;
	private TimeManagerController time;

	public EmulatorController() {
		setupSettings();
		view = new EmulatorView(this);
		time = new TimeManagerController(view);
		Thread thread = new Thread(time);
		thread.start();
	}

	public TimeManagerController getTimeManager() {
		return time;
	}

	public void sendId(int id) {

		if (!time.getTodayWeekDay().equals("Sunday") && !time.getTodayWeekDay().equals("Saturday")) {
			System.out.println(time.getTodayWeekDay());
			String message = createMessage(id);
			System.out.println(message);

			Socket socket;
			OutputStream outputStream;
			ObjectOutputStream objectOutputStream;
			// Initialisation du socket d'envoie
			try {
				socket = new Socket(serverAddress, serverPort);
				// Flux de donnees:
				outputStream = socket.getOutputStream();
				// Envoie des donnes via objectOutputStream:
				objectOutputStream = new ObjectOutputStream(outputStream);

				ArrayList<String> infos = readData(DATA_BACKUP_PATH);

				if (infos != null) {
					for (String info : infos) {
						objectOutputStream.writeObject(info);
					}
					deleteFile(DATA_BACKUP_PATH);
				}

				objectOutputStream.writeObject(message);

				objectOutputStream.close();
				outputStream.close();
				socket.close();
			} catch (IOException e) {

				saveData(message, DATA_BACKUP_PATH);
			}

			System.out.println(readData(DATA_BACKUP_PATH));
		}
	}

	public void setupSettings() {

        ArrayList<String> parameters = readData(SETTINGS_BACKUP_PATH);

        if (parameters != null) {

            try {
                int importedPort = Integer.parseInt(parameters.get(0));
                String importedServerAddress = parameters.get(1);
                if (importedPort != SERVER_DEFAULT_PORT && importedServerAddress != SERVER_DEFAULT_ADDRESS) {
                    serverPort = importedPort;
                    serverAddress = importedServerAddress;
                    System.out.println("Config file found, Port: " + serverPort + ", Address: "
                            + serverAddress + System.lineSeparator());
                } else {
                    serverPort = SERVER_DEFAULT_PORT;
                    serverAddress = SERVER_DEFAULT_ADDRESS;
                    System.out.println("Using default server settings, Port: " + serverPort + ", Address: "
                            + serverAddress + System.lineSeparator());
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                serverPort = SERVER_DEFAULT_PORT;
                serverAddress = SERVER_DEFAULT_ADDRESS;
                System.out.println("invalid Config file data. Using default server settings, Port: " + serverPort
                        + ", Address: " + serverAddress + System.lineSeparator());
            }

        } else {
            serverPort = SERVER_DEFAULT_PORT;
            serverAddress = SERVER_DEFAULT_ADDRESS;
            System.out.println("No Config file detected. Using default server settings: Port" + serverPort + "Address:"
                    + serverAddress + System.lineSeparator());
        }

    }

	public int getServerPort() {
		return serverPort;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void saveSettings() {
		deleteFile(SETTINGS_BACKUP_PATH);
		saveData(String.valueOf(serverPort), SETTINGS_BACKUP_PATH);
		saveData(serverAddress, SETTINGS_BACKUP_PATH);
	}

	public void changeServerPort(int portNumber) {
		this.serverPort = portNumber;
		saveSettings();
	}

	public void changeServerAddress(String serverAdress) {
		this.serverAddress = serverAdress;
		saveSettings();

	}

	public void deleteFile(String PATH) {
		try {
			File file = new File(PATH);
			if (file.exists())
				Files.delete(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> readData(String PATH) {
		ArrayList<String> infos = new ArrayList<String>();

		try {
			File file = new File(PATH);
			if (file.exists()) {
				Scanner reader = new Scanner(file);
				while (reader.hasNextLine()) {
					infos.add(reader.nextLine());
				}
				reader.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return infos;
	}

	public void saveData(String info, String Path) {
		try {
			File file = new File(Path);
			if (!file.exists())
				Files.createFile(file.toPath());
			Files.write(file.toPath(), (info + '\n').getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String createMessage(int id) {
		StringBuffer strBuff = new StringBuffer();

		strBuff.append(time.getCurrent_Date());
		strBuff.append("/");
		strBuff.append(time.getRounded_Time());
		strBuff.append("/");
		strBuff.append(id);

		return strBuff.toString();
	}

	public static void main(String args[]) {
		EmulatorController control = new EmulatorController();
	}

}
