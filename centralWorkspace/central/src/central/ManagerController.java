package central;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class ManagerController {
	private final String COMPANY_DATABASE = "src" + File.separator + "central" + File.separator + "assets"
			+ File.separator + "save.ser";

	private final String PARAMETERS_BACKUP = "src" + File.separator + "central" + File.separator + "assets"
			+ File.separator + "settings_save.ser";

	private final int APPLICATION_DEFAULT_PORT = 7700;

	private Company company;
	private DataTransferServer server;
	private DataManager<Company> dm;
	private DataManager<Integer> serverParameters;
	private Thread serverThread;
	private ManagerView view;

	public ManagerController(String CompanyName) {
		// Data settings:
		dm = new DataManager<Company>(COMPANY_DATABASE);
		company = dm.deserialiseObject();
		if (company == null)
			company = new Company("Company");

		////// server settings:
		serverParameters = new DataManager<Integer>(PARAMETERS_BACKUP);
		int parameters_backup = APPLICATION_DEFAULT_PORT;
		try {
			parameters_backup = serverParameters.deserialiseObject();
			System.out.println("ICII: " + parameters_backup);
		} catch (NullPointerException e) {
			System.out.println(
					"No server Config file found or the file is empty, trying using default server settings on port: "
							+ APPLICATION_DEFAULT_PORT + System.lineSeparator());
		}

		if (parameters_backup != APPLICATION_DEFAULT_PORT) {
			server = new DataTransferServer(this, parameters_backup);
			System.out.println(" Configured port found! Starting up server on port: " + APPLICATION_DEFAULT_PORT
					+ System.lineSeparator());
		} else
			server = new DataTransferServer(this, APPLICATION_DEFAULT_PORT);

		startServer();
		view = new ManagerView(this);
	}
	
	public ManagerView getManagerView() {
		return view;
	}

	public void serializeCompany() {
		try {
			dm.serialiseObject(company);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void serializeServerSettings() {
		try {
			serverParameters.serialiseObject(server.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void importFromFile(String filePath) {

        try {
            File file = new File(filePath);
            if (file.exists()) {
                Scanner reader = new Scanner(file);
                while (reader.hasNextLine()) {
                    parseEmulatorInput(reader.nextLine());
                }
                reader.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

	public void parseEmulatorInput(String input) {
		String[] strTmp = input.split("/");
		int id_Worker = Integer.parseInt(strTmp[2]);
		System.out.println(id_Worker);
		String datetmp = strTmp[0];
		String time = strTmp[1];

		////////////////////////////////////////////////////////////// on met le bon
		////////////////////////////////////////////////////////////// format de date

		try {
			DateFormat oldTMP = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			DateFormat newTMP = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
			Date date1 = oldTMP.parse(datetmp);

			String date = newTMP.format(date1);
			// System.out.println("DATEEE: " + date);
			////////////////////////////////////////////////////////////

			try {
				Worker signingIn_Worker = company.whereIsWorker(id_Worker).getWorkerById(id_Worker);

				try {
					WorkingDay wdTemp = signingIn_Worker.getLastWorkingDay();
					if (wdTemp.getTodaysDate().equals(date)) {

						if (wdTemp.getArrivalTime() == null) {
							wdTemp.setArrivalTime(time);
							//signingIn_Worker.addTimeOverflowArrival(time, wdTemp);
						} else {
							if (wdTemp.getDepartureTime() == null) {
								wdTemp.setDepartureTime(time);
								//signingIn_Worker.addTimeOverflowDepart(time, wdTemp);
								signingIn_Worker.addTimeOverflow(wdTemp);
							}
						}
					} else {

						signingIn_Worker.addWorkingDay(date, time);

					}
				} catch (Exception e) {

					signingIn_Worker.addWorkingDay(date, time); // on creer le premier jour

				}
				view.updateInfos(signingIn_Worker.getId_Worker());

			} catch (Exception e) {
				System.out.println("INVAILD WORKER ID");
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void startServer() {
		serverThread = new Thread(server);
		serverThread.start();
	}

	public void changeServerConfig(int newPort) {
		if (newPort != server.getPort()) {
			
			server.stopCurrentServer();
			DataTransferServer serverTMP = new DataTransferServer(this, newPort);
			server = serverTMP;
			startServer();
		}

	}
	public int getServerPort() {
		return server.getPort();
	}
	public Company getCompany() {
		return company;
	}

	public static void main(String[] args) {
		/*
		 * System.out.println("ETAPE1: --------------------"); TimeKeeper pointeuse =
		 * new TimeKeeper(1); TimeKeeper pointeuse2 = new TimeKeeper(2);
		 * System.out.println(pointeuse); System.out.println(pointeuse2);
		 * 
		 * try { DataManager dm = new DataManager(); dm.serialiseObject(pointeuse);
		 * 
		 * TimeKeeper pointeuse3 = (TimeKeeper) dm.deserialiseObject();
		 * System.out.println("////////////"); System.out.println(pointeuse3);
		 * 
		 * } catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
		 * 
		 * System.out.println("ETAPE 1 VALIDEE--------------------------------------");
		 * System.out.println("--------------------------------------");
		 * System.out.println("ETAPE2: Sockets:--------------------------------------");
		 * 
		 * try { DataTransferServer ds = new DataTransferServer(); ds.setupConnection();
		 * ds.connect(); ArrayList<Object> listofchanges = ds.getListOfUpdates();
		 * //System.out.println(listofchanges); } catch (IOException |
		 * ClassNotFoundException e) { e.printStackTrace(); }
		 */
		/*
		 * Company AledS6 = new Company(1, "AledS6");
		 * 
		 * Department bot = new Department(1, "Botlane"); Department mid = new
		 * Department(2, "Midlane"); Department jungl = new Department(3, "Jungle");
		 * Department top = new Department(4, "Toplane");
		 * 
		 * Worker Mah = new Worker(12346, "Mah", "----"); Worker Adrien = new
		 * Worker(12347, "Adrien", "----"); Worker Alexandre = new Worker(12348,
		 * "Alexandre", "-----"); Worker MohamadAli = new Worker(12349, "MohamadAli",
		 * "------"); Worker Tim = new Worker(12350, "Tim", "----");
		 * 
		 * bot.add_Worker(Mah); bot.add_Worker(Alexandre); mid.add_Worker(Adrien);
		 * jungl.add_Worker(MohamadAli); top.add_Worker(Tim);
		 * 
		 * AledS6.add_Department(bot); AledS6.add_Department(mid);
		 * AledS6.add_Department(jungl); AledS6.add_Department(top);
		 */
		// DataManager<Company> dm = new DataManager<Company>();

		ManagerController mg = new ManagerController("AledS6");
		//mg.changeServerConfig(7800);
		//mg.changeServerConfig(7700);

		/*
		 * new Thread(mg.server).start();
		 * 
		 * //TEST SI ON AJOUTE LE ARRIVAL TIME ET DEPARTURE TIME DE LEMPLOYEE: while
		 * (true) { try { if(mg.getCompany().whereIsWorker(12346).getWorkerById(12346).
		 * checkWorkingDayByDate("2020-05-21")) { System.out.println("PRESENT");
		 * System.out.println(mg.getCompany().whereIsWorker(12346).getWorkerById(12346).
		 * getLastWorkingDay().getArrivalTime());
		 * System.out.println(mg.getCompany().whereIsWorker(12346).getWorkerById(12346).
		 * getLastWorkingDay().getDepartureTime()); Thread.sleep(2000); } } catch
		 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); } }
		 */
		/*
		 * try { // System.out.println(mg.getCompany().getDepartmentByName("Botlane").
		 * getWorker_List()); } catch (Exception e1) { // TODO Auto-generated catch
		 * block e1.printStackTrace(); } try { //
		 * System.out.println(mg.getCompany().whereIsWorker(12346).getWorkerById(12346))
		 * ; } catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		/*
		 * 
		 * DataTransferServer DTT = new DataTransferServer(); new Thread(DTT).start();
		 * while (true) { System.out.println(DTT.getListOfUpdates()); try {
		 * Thread.sleep(2000); } catch (InterruptedException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); } System.out.println("ou?");
		 * 
		 * }
		 */
		/*
		 * try {
		 * 
		 * dm.serialiseObject(AledS6);
		 * 
		 * } catch (IOException e) { e.printStackTrace(); }
		 * 
		 * Company cp = dm.deserialiseObject(); System.out.println(cp.toString());
		 * System.out.println("///////"); System.out.println(cp.getDepartment_List());
		 * cp.showEveryDepartmentName();
		 * System.out.println("/////////////////////////////////////");
		 * 
		 * Department dpTest; Worker wTest; try {
		 * System.out.println("--Test:getDepartmentByName: Jungle "); dpTest =
		 * cp.getDepartmentByName("Jungle"); System.out.println(dpTest.toString());
		 * System.out.println("--Test:getDepartmentById: 2 "); dpTest =
		 * cp.getDepartmentById(2); System.out.println(dpTest.toString());
		 * System.out.println("/////////////////////////////////////");
		 * System.out.println("--getWorkerById: 12347 Adrien ");
		 * 
		 * wTest = dpTest.getWorkerById(12347); System.out.println(wTest.toString());
		 * 
		 * 
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 * 
		 * System.out.println("/////////////////////////////////////");
		 * System.out.println("--DeleteWorker: 12347 Adrien ");
		 * 
		 * try { System.out.println(bot.getWorkerById(12346).toString());
		 * bot.deleteWorker(bot.getWorkerById(12346));
		 * System.out.println(bot.getWorkerById(12346).toString()); bot.add_Worker(Mah);
		 * // on reset les tests
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 * System.out.println("/////////////////////////////////////");
		 */

	}

}
