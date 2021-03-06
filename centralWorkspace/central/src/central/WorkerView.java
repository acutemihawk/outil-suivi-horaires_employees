package central;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class WorkerView extends JPanel implements ListSelectionListener, ActionListener {

	private JTextField searchField;
	private JButton searchButton;
	private JButton addWorkerButton;
	private JButton addEntryFileButton;

	private JPanel menuPane;
	private JScrollPane workerScrollPane;
	private JScrollPane workerInfoPane;
	private JSplitPane infosSplitPane;

	private JPanel departPane;
	private JScrollPane departScrollPane;
	private JButton addDepartButton;
	private JButton delDepartButton;

	private JSplitPane mainSplitPane;

	private int lastDepartIndex = -1;
	private int lastWorkerIndex = -1;

	private ArrayList<Integer> departList;
	private ArrayList<ArrayList<Integer>> workerList;

	private JList departJList;
	private JList workerJList;

	private JTextField lastnameField;
	private JTextField nameField;

	private JComboBox<String> departCombo;

	private JButton addEntryButton;
	private JButton delButton;

	private ManagerController mc;
	private ManagerView mv;
	private Company comp;
	private Worker w;

	public WorkerView(ManagerController mc) {

		this.mc = mc;
		this.mv = mc.getManagerView();
		this.comp = mc.getCompany();

		searchField = new JTextField();
		searchField.setColumns(10);

		searchButton = new JButton("Search");
		searchButton.addActionListener(this);
		addWorkerButton = new JButton("Add worker");
		addWorkerButton.addActionListener(this);
		addEntryFileButton = new JButton("Add entry from file");
		addEntryFileButton.addActionListener(this);
		addEntryButton = new JButton("Add entry");
		addEntryButton.addActionListener(this);

		new BoxLayout(menuPane, BoxLayout.X_AXIS);
		menuPane = new JPanel();

		menuPane.add(searchField);
		menuPane.add(searchButton);
		menuPane.add(addWorkerButton);
		menuPane.add(addEntryFileButton);
		menuPane.add(addEntryButton);

		workerScrollPane = new JScrollPane();
		workerInfoPane = new JScrollPane();

		infosSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, workerScrollPane, workerInfoPane);
		infosSplitPane.setDividerLocation(150);

		departPane = new JPanel();
		departPane.setLayout(new BoxLayout(departPane, BoxLayout.Y_AXIS));

		departScrollPane = new JScrollPane();

		addDepartButton = new JButton("Add");
		addDepartButton.addActionListener(this);

		delDepartButton = new JButton("Delete");
		delDepartButton.addActionListener(this);

		JPanel buttonPane = new JPanel();
		BoxLayout buttonLayout = new BoxLayout(buttonPane, BoxLayout.X_AXIS);
		buttonPane.setLayout(buttonLayout);

		buttonPane.add(delDepartButton);
		buttonPane.add(addDepartButton);

		departPane.add(departScrollPane);
		departPane.add(buttonPane);

		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, departPane, infosSplitPane);
		mainSplitPane.setDividerLocation(150);

		setData();
		updateDList();

		this.setLayout(new BorderLayout());

		this.add(menuPane, BorderLayout.PAGE_START);
		this.add(mainSplitPane, BorderLayout.CENTER);
	}

	public void setData() {

		departList = new ArrayList<Integer>();
		workerList = new ArrayList<ArrayList<Integer>>();

		ArrayList<Department> departArray = comp.getDepartment_List();

		if(departArray != null) {

			int index = 0;

			for(Department depart : departArray) {
				departList.add(depart.getId_Department());
				workerList.add(new ArrayList<Integer>());

				ArrayList<Worker> workerArray = depart.getWorker_List();
				if(workerArray != null) {
					for(Worker worker : workerArray) {
						workerList.get(index).add(worker.getId_Worker());
					}
				}

				index++;
			}
		}
	}

	public void updateDList() {
		ArrayList<String> departNameList = new ArrayList<>();
		for(int idDepart : departList) {
			try {
				departNameList.add(comp.getDepartmentByID(idDepart).getName_Department());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		departJList = new JList(departNameList.toArray());

		departJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		departJList.setLayoutOrientation(JList.VERTICAL);

		departJList.getSelectionModel().addListSelectionListener(this);

		departScrollPane.setViewportView(departJList);
	}

	public void updateWList(int departID) {
		ArrayList<String> workerNameList = new ArrayList<>();
		for(int idWorker : workerList.get(departID)) {
			try {
				workerNameList.add(comp.getDepartmentByID(departList.get(departID)).getWorkerById(idWorker).getLastname_Worker() +" " +comp.getDepartmentByID(departList.get(departID)).getWorkerById(idWorker).getFirstname_Worker());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		workerJList = new JList(workerNameList.toArray());

		workerJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		workerJList.setLayoutOrientation(JList.VERTICAL);

		workerJList.getSelectionModel().addListSelectionListener(this);

		workerScrollPane.setViewportView(workerJList);
	}

	public void setInfo(int workerId) {

		JPanel infoPane = new JPanel();
		infoPane.setLayout(new BorderLayout());

		JPanel infoList = new JPanel();
		BoxLayout infoBoxLayout = new BoxLayout(infoList, BoxLayout.Y_AXIS);
		infoList.setLayout(infoBoxLayout);

		try {
			w = comp.whereIsWorker(workerId).getWorkerById(workerId);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		infoList.add(new JLabel("ID : " +w.getId_Worker()));

		JPanel lastnamePane = new JPanel();
		BoxLayout lastnameBoxLayout = new BoxLayout(lastnamePane, BoxLayout.X_AXIS);
		lastnamePane.setLayout(lastnameBoxLayout);

		lastnameField = new JTextField(w.getLastname_Worker());
		lastnameField.addActionListener(this);
		lastnamePane.add(new JLabel("Nom : "));
		lastnamePane.add(lastnameField);

		infoList.add(lastnamePane);

		JPanel namePane = new JPanel();
		BoxLayout nameBoxLayout = new BoxLayout(namePane, BoxLayout.X_AXIS);
		namePane.setLayout(nameBoxLayout);

		nameField = new JTextField(w.getFirstname_Worker());
		nameField.addActionListener(this);
		namePane.add(new JLabel("Prenom : "));
		namePane.add(nameField);

		infoList.add(namePane);

		infoList.add(new JLabel("Heures suppl?mentaires : " +w.getWorkingTimeOverflow_Worker()));

		String[] departmentsList = new String[comp.getDepartment_List().size()];
		int departmentIndex = 0;
		int workerDepartmentIndex = -1;

		for(Department d : comp.getDepartment_List()) {
			departmentsList[departmentIndex] = d.getName_Department();
			try {
				if(d.getName_Department() == comp.whereIsWorker(w.getId_Worker()).getName_Department()) {
					workerDepartmentIndex = departmentIndex;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			departmentIndex++;
		}

		departCombo = new JComboBox<>(departmentsList);
		departCombo.setSelectedIndex(workerDepartmentIndex);
		departCombo.addActionListener(this);

		JPanel departPane = new JPanel();
		BoxLayout departBoxLayout = new BoxLayout(departPane, BoxLayout.X_AXIS);
		departPane.setLayout(departBoxLayout);

		departPane.add(new JLabel("Department : "));
		departPane.add(departCombo);

		infoList.add(departPane);

		infoList.add(new JLabel("Horaires par defaut : "));

		DefaultTimeTableView defaultTimeTable = new DefaultTimeTableView(w);

		infoList.add(defaultTimeTable);

		infoList.add(new JLabel("Pointages :"));

		WorkingDaysTableView workedDaysTab = new WorkingDaysTableView(this, w);
		
		delButton = new JButton("delete");
		delButton.addActionListener(this);

		JPanel paramPane = new JPanel();
		BoxLayout paramLayout = new BoxLayout(paramPane, BoxLayout.X_AXIS);

		paramPane.add(delButton);

		infoPane.add(infoList, BorderLayout.PAGE_START);
		infoPane.add(workedDaysTab, BorderLayout.CENTER);
		infoPane.add(paramPane, BorderLayout.PAGE_END);

		workerInfoPane.setViewportView(infoPane);
	}

	public void clearInfos() {
		workerInfoPane.setViewportView(null);
	}

	public void clearWList() {
		workerScrollPane.setViewportView(null);
	}

	public void updateAll() {
		setData();
		updateDList();
		clearWList();
		clearInfos();
		lastDepartIndex = -1;
		lastWorkerIndex = -1;
	}

	public void updateInfos(int workerID) {
		setData();
		try {
			if(workerList.get(departList.indexOf(comp.whereIsWorker(workerID).getId_Department())).indexOf(workerID) == lastWorkerIndex) {
				setInfo(workerID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateWorkers(int departID) {
		setData();
		if(departList.indexOf(departID) == lastDepartIndex) {
			updateWList(departList.indexOf(departID));
			clearInfos();
			lastWorkerIndex = -1;
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int departIndex = ((JList)(((JViewport)departScrollPane.getComponents()[0]).getView())).getSelectedIndex();
		if(departIndex !=  lastDepartIndex) {
			lastDepartIndex = departIndex;
			updateWList(departIndex);
			workerInfoPane.setViewportView(null);
			lastWorkerIndex = -1;
		}
		int workerIndex = ((JList)(((JViewport)workerScrollPane.getComponents()[0]).getView())).getSelectedIndex();
		if(workerIndex != -1) {
			if(workerIndex != lastWorkerIndex) {
				lastWorkerIndex = workerIndex;
				setInfo(workerList.get(departIndex).get(workerIndex));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == searchButton) {
			int departIndex = 0;
			int workerIndex = -1;
			for(ArrayList<Integer> tempList : workerList) {
				try {
					workerIndex = tempList.indexOf(comp.getDepartmentByID(departList.get(departIndex)).getWorkerByFullName(searchField.getText()).getId_Worker());
				} catch (Exception e) {}
				if(workerIndex != -1)
					break;
				departIndex++;
			}
			if(workerIndex != -1) {
				searchField.setText("");
				departJList.setSelectedIndex(departIndex);
				workerJList.setSelectedIndex(workerIndex);
			}
			else {
				textError();
			}
		}
		else if(event.getSource() == lastnameField) {
			w.setLastname_Worker(lastnameField.getText());
			try {
				updateWorkers(comp.whereIsWorker(w.getId_Worker()).getId_Department());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(event.getSource() == nameField) {
			w.setFirstname_Worker(nameField.getText());
			try {
				updateWorkers(comp.whereIsWorker(w.getId_Worker()).getId_Department());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(event.getSource() == addWorkerButton) {
			AddWorkerView.display(mc);
		}
		else if(event.getSource() == addDepartButton) {
			AddDepartmentView.display(mc);
		}
		else if(event.getSource() == delDepartButton) {
			int departIndex = ((JList)(((JViewport)departScrollPane.getComponents()[0]).getView())).getSelectedIndex();
			try {
				if(comp.getDepartmentByID(departList.get(departIndex)).getWorker_List() != null) {
					int result = JOptionPane.showConfirmDialog(null, "This department contains workers, are you sure you want to delete it ?", "Delete department",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (result == JOptionPane.OK_OPTION) {
						comp.deleteDepartment(comp.getDepartmentByID(departList.get(departIndex)));
						mv.updateAll();
					}
				}
				else {
					comp.deleteDepartment(comp.getDepartmentByID(departList.get(departIndex)));
					mv.updateAll();
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		else if(event.getSource() == delButton) {
			int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this worker ?", "Delete worker",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				try {
					Department departTemp = comp.whereIsWorker(w.getId_Worker());
					departTemp.deleteWorker(w);
					updateWorkers(departTemp.getId_Department());
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
		else if(event.getSource() == departCombo) {
			try {
				comp.whereIsWorker(w.getId_Worker()).deleteWorker(w);
				comp.getDepartmentByName(departCombo.getSelectedItem().toString()).add_Worker(w);
				mv.updateAll();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		else if(event.getSource() == addEntryFileButton) {
			JFileChooser filechooser = new JFileChooser();

			int returnValue = filechooser.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String selectedFile = filechooser.getSelectedFile().getAbsolutePath();
				mc.importFromFile(selectedFile);
				mv.updateInfos(w.getId_Worker());
			}
		}
		else if(event.getSource() == addEntryButton) {
			AddEntryView.display(mc, w);
		}
	}

	public void textError() {
		searchField.setBorder(new LineBorder(Color.RED, 2));

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				searchField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
			}
		}, 2000);
	}

}
