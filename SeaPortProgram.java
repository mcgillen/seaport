import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * File: SeaPortProgram.java 
 * Author: Brian McGillen 
 * Date: September 26, 2017
 * Purpose: Contains main method that calls for GUI to be constructed. Allows
 * user to open properly formatted .txt file, process data into a multi-tree,
 * display formatted data, and search or sort the data. Creates JTables used to
 * display job progress information and resource pools.
 */

public class SeaPortProgram extends JFrame {

	private static final long serialVersionUID = 1596896269692883573L;

	// used to permit the creation of multiple world object during one use of the
	// program
	static int worldCounter = 0;

	// JTree components that need to be accessed by multiple methods/listeners
	DefaultMutableTreeNode top = new DefaultMutableTreeNode("Seaports");
	DefaultTreeModel treeModel = new DefaultTreeModel(top);
	JTree tree = new JTree(treeModel);
	DefaultMutableTreeNode lastNodeSelected;

	// constructor that opens GUI
	public SeaPortProgram() {
		// list of world objects opened during one use of the program
		ArrayList<World> worlds = new ArrayList<>();

		// set window frame dimensions and functionality
		setTitle("Sea Port Program");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200, 700);
		setLocationRelativeTo(null);

		// establish 2 main panels side by side, menu on the left, display on the right
		JPanel menuPanel = new JPanel(new BorderLayout());
		JPanel menuPanelHelper = new JPanel();
		menuPanelHelper.setLayout(new GridLayout(0, 1));
		menuPanel.add(menuPanelHelper, BorderLayout.NORTH);
		JPanel displayPanel = new JPanel(new BorderLayout());

		// menu sub-panels within the main menu panel
		JPanel openFilePanel = new JPanel();
		JPanel searchPanel = new JPanel();
		JPanel sortPanel = new JPanel();
		JPanel jobPanel = new JPanel();
		JPanel resourcePanel = new JPanel(); // only added when jobDisplay is shown
		menuPanelHelper.add(openFilePanel);
		menuPanelHelper.add(searchPanel);
		menuPanelHelper.add(sortPanel);
		menuPanelHelper.add(jobPanel);

		// openFilePanel sub-panel components
		openFilePanel.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED), "Import File"));
		openFilePanel.setLayout(new GridLayout(0, 1));
		JPanel openFileTopLine = new JPanel();
		JPanel openFileBottomLine = new JPanel();
		openFilePanel.add(openFileTopLine);
		openFilePanel.add(openFileBottomLine);
		JTextField fileNameArea = new JTextField(18);
		JButton browseButton = new JButton("Browse");
		JButton displayButton = new JButton("Display File");
		openFileTopLine.add(fileNameArea);
		openFileTopLine.add(browseButton);
		openFileBottomLine.add(displayButton);
		openFilePanel.setMaximumSize(openFilePanel.getPreferredSize());

		// searchPanel sub-panel components
		searchPanel.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED), "Search File"));
		searchPanel.setLayout(new GridLayout(0, 1));
		JPanel searchPanelTopLine = new JPanel();
		JPanel searchPanelBottomLine = new JPanel();
		searchPanel.add(searchPanelTopLine);
		searchPanel.add(searchPanelBottomLine);
		JTextField searchTermArea = new JTextField(18);
		String[] searchMenuOptions = { "Name", "Index", "Skill" };
		JComboBox<String> searchMenu = new JComboBox<>(searchMenuOptions);
		JButton searchButton = new JButton("Search");
		searchPanelTopLine.add(searchTermArea);
		searchPanelTopLine.add(searchMenu);
		searchPanelBottomLine.add(searchButton);

		// sortPanel sub-panel components
		sortPanel.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED), "Sort Data"));
		sortPanel.setLayout(new GridLayout(0, 1));
		JPanel sortPanelTopLine = new JPanel();
		JPanel sortPanelBottomLine = new JPanel();
		sortPanel.add(sortPanelTopLine);
		sortPanel.add(sortPanelBottomLine);
		JLabel sortWhatLabel = new JLabel("Sort: ");
		String[] sortWhatOptions = { "Select", "Seaports", "Docks", "All Ships", "Ships in Que", "Persons" };
		JComboBox<String> sortWhatMenu = new JComboBox<>(sortWhatOptions);
		JLabel byLabel = new JLabel("by: ");
		String[] defaultOptions = {};
		String[] seaportSortOptions = { "Name", "Index" };
		String[] dockSortOptions = { "Name", "Index" };
		String[] shipSortOptions = { "Name", "Index", "Draft", "Length", "Width", "Weight" };
		String[] personsSortOptions = { "Name", "Index", "Skill" };
		JComboBox<String> sortByMenu = new JComboBox<>(defaultOptions);
		JButton displaySortButton = new JButton("Display");
		sortPanelTopLine.add(sortWhatLabel);
		sortPanelTopLine.add(sortWhatMenu);
		sortPanelTopLine.add(byLabel);
		sortPanelTopLine.add(sortByMenu);
		sortPanelBottomLine.add(displaySortButton);

		// jobPanel sub-panel components
		jobPanel.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED), "Job Progress"));
		jobPanel.setLayout(new GridLayout(0, 1));
		JPanel jobTopLine = new JPanel();
		JPanel jobBottomLine = new JPanel();
		jobPanel.add(jobTopLine);
		jobPanel.add(jobBottomLine);
		JButton displayJobsButton = new JButton("Display Jobs");
		JButton pauseJobsButton = new JButton("Pause all Jobs");
		JButton resumeJobsButton = new JButton("Resume all Jobs");
		jobTopLine.add(displayJobsButton);
		jobBottomLine.add(pauseJobsButton);
		jobBottomLine.add(resumeJobsButton);
		
		//resource panel components
		resourcePanel.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED), "Seaport Resource Pools"));
		resourcePanel.setLayout(new BorderLayout());
		JPanel resourceMenuPanel = new JPanel();
		JPanel resourceDisplayPanel = new JPanel(new BorderLayout());
		resourcePanel.add(resourceMenuPanel, BorderLayout.NORTH);
		resourcePanel.add(resourceDisplayPanel, BorderLayout.CENTER);
		JLabel selectSeaportLabel = new JLabel ("Display Resources at: ");
		JComboBox<String> seaportSelectionBox = new JComboBox<String>();
		resourceMenuPanel.add(selectSeaportLabel);
		resourceMenuPanel.add(seaportSelectionBox);	

		// default displayPanel components
		JTextArea text = new JTextArea();
		text.setFont(new java.awt.Font("Monospaced", 0, 12));
		text.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(text);
		displayPanel.add(scrollPane);
		
		//Job display panel components
		JPanel jobDisplayPanel = new JPanel(new BorderLayout());

		// treeView components
		JScrollPane treeScroll = new JScrollPane(tree);
		treeScroll.setPreferredSize(new Dimension(200, 0));
		JPanel tableScrollPanel = new JPanel(new BorderLayout());
		JSplitPane treeView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, tableScrollPanel);
		
		// add main panels to window frame
		add(menuPanel, BorderLayout.WEST);
		add(displayPanel, BorderLayout.CENTER);

		
		// browseButton listener
		browseButton.addActionListener((ActionEvent e) -> {

			// open file chooser so user can selected file
			JFileChooser jfc = new JFileChooser(".");
			int returnVal = jfc.showOpenDialog(browseButton);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = jfc.getSelectedFile();
				fileNameArea.setText(file.getName());
				Scanner sc = null;
				try {
					sc = new Scanner(file);
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, "File not found!", "Alert", JOptionPane.ERROR_MESSAGE);
				}

				// create new world object, process data, advance worldCounter to permit
				// additional world objects to be created
				worlds.add(new World(file.getName(), worldCounter, 0));

				// reset all displays
				displayPanel.removeAll();
				jobDisplayPanel.removeAll();
				tableScrollPanel.removeAll();
				resourceDisplayPanel.removeAll();
				seaportSelectionBox.removeAllItems();
				menuPanel.remove(resourcePanel);
				menuPanel.validate();
				menuPanel.repaint();
				displayPanel.validate();
				displayPanel.repaint();

				// reset tree
				lastNodeSelected = null;
				top.removeAllChildren();
				treeModel.reload();

				// create table hold job progress data
				String[] columnNames = { "Seaport", "Dock", "Ship", "Resources Required", "Acquired?", "Duration", 
						"Progress", "Status", "Pause", "Cancel" };
				DefaultTableModel jobTableModel = new DefaultTableModel(null, columnNames) {
					private static final long serialVersionUID = 1L;
					@Override
					public boolean isCellEditable(int row, int column) {
						// all cells false
						return false;
					}
					
				};
				JTable jobTable = new JTable(jobTableModel);
				JScrollPane jobTableScroll = new JScrollPane(jobTable);
				jobDisplayPanel.add(jobTableScroll, BorderLayout.CENTER);

				// set custom renderers for job progress table
				jobTable.getColumn("Progress").setCellRenderer(new ProgressBarRenderer());
				jobTable.getColumn("Pause").setCellRenderer(new JButtonRenderer());
				jobTable.getColumn("Cancel").setCellRenderer(new JButtonRenderer());
				jobTable.addMouseListener(new JTableButtonMouseListener(jobTable));
				
				//set custom column widths for job progress table
				jobTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
				TableColumnModel colModel = jobTable.getColumnModel();
				colModel.getColumn(3).setPreferredWidth(200);

				// read in data
				worlds.get(worldCounter).process(sc, jobTableModel);

				// increase worldCounter to allow future instantiations of world object
				worldCounter++;		
			}
		});

		// displayButton listener
		displayButton.addActionListener((ActionEvent e) -> {
			// reset menu panel
			menuPanel.remove(resourcePanel);
			menuPanel.validate();
			menuPanel.repaint();
			
			// reset tree
			String expansionState = getExpansionState();
			top.removeAllChildren();
			treeModel.reload();
			displayTree(displayPanel, treeView, worlds, expansionState);
		});

		// searchButton listener
		searchButton.addActionListener((ActionEvent e) -> {
			if (!(searchTermArea.getText().isEmpty())) {
				try {
					// reset menu panel
					menuPanel.remove(resourcePanel);
					menuPanel.validate();
					menuPanel.repaint();
			
					// reset display panel
					displayPanel.removeAll();
					displayPanel.add(scrollPane);
					switch (searchMenu.getSelectedIndex()) {
					case 0:
						text.setText(worlds.get(worldCounter - 1).searchPorts("name", searchTermArea.getText()));
						break;
					case 1:
						text.setText(worlds.get(worldCounter - 1).searchPorts("index", searchTermArea.getText()));
						break;
					case 2:
						text.setText(worlds.get(worldCounter - 1).searchPorts("skill", searchTermArea.getText()));
						break;
					default:
						break;
					}
					text.setCaretPosition(0);
				} catch (ArrayIndexOutOfBoundsException e2) {
					JOptionPane.showMessageDialog(null, "Use browse button to open a new file first.", "Alert",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// sortWhatMenu listener
		sortWhatMenu.addActionListener((ActionEvent e) -> {
			String choice = (String) sortWhatMenu.getSelectedItem();
			switch (choice) {
			case "Seaports":
				sortByMenu.setModel(new DefaultComboBoxModel<String>(seaportSortOptions));
				break;
			case "Docks":
				sortByMenu.setModel(new DefaultComboBoxModel<String>(dockSortOptions));
				break;
			case "All Ships":
			case "Ships in Que":
				sortByMenu.setModel(new DefaultComboBoxModel<String>(shipSortOptions));
				break;
			case "Persons":
				sortByMenu.setModel(new DefaultComboBoxModel<String>(personsSortOptions));
				break;
			}
		});

		// displaySortButton listener
		displaySortButton.addActionListener((ActionEvent e) -> {
			String objectChoice = (String) sortWhatMenu.getSelectedItem();
			String sortCriteria = (String) sortByMenu.getSelectedItem();

			try {
				switch (objectChoice) {
				case "Seaports":
					worlds.get(worldCounter - 1).sortSeaports(sortCriteria);
					break;
				case "Docks":
					worlds.get(worldCounter - 1).sortDocks(sortCriteria);
					break;
				case "All Ships":
					worlds.get(worldCounter - 1).sortAllShips(sortCriteria);
					break;
				case "Ships in Que":
					worlds.get(worldCounter - 1).sortShipsInQue(sortCriteria);
					break;
				case "Persons":
					worlds.get(worldCounter - 1).sortPersons(sortCriteria);
					break;
				}

				// reset menu panel
				menuPanel.remove(resourcePanel);
				menuPanel.validate();
				menuPanel.repaint();
				
				// reset tree maintaining current expansion state
				String expansionState = getExpansionState();
				if (((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()) != null) {
					lastNodeSelected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				}

				// check is lastNodeSelected is null to avoid crash if sort is performed before
				// user clicks on any tree elements
				if (!(lastNodeSelected == null)) {
					// check if tree is open to leaf to determine whether table should be displayed
					// when tree is reset
					tableScrollPanel.removeAll();
					if (lastNodeSelected.isLeaf())
						constructTable(lastNodeSelected, tableScrollPanel, worlds.get(worldCounter - 1));
				}

				// reload tree model in case seaports have been sorted
				top.removeAllChildren();
				treeModel.reload();
				displayTree(displayPanel, treeView, worlds, expansionState);

			} catch (ArrayIndexOutOfBoundsException e2) {
				JOptionPane.showMessageDialog(null, "Use browse button to open a new file.", "Alert",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		// tree selection listener
		tree.addTreeSelectionListener((TreeSelectionEvent e) -> {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if ((node == null) && !(lastNodeSelected == null))
				node = lastNodeSelected;
			// second check to ensure lastNodeSelected isn't null because a new file was
			// opened
			if (node != null) {
				tableScrollPanel.removeAll();
				if (node.isLeaf())
					constructTable(node, tableScrollPanel, worlds.get(worldCounter - 1));
				displayPanel.validate();
				displayPanel.repaint();
			}

		});

		// displayJobsButton listener
		displayJobsButton.addActionListener((ActionEvent e) -> {
			try {
				displayPanel.removeAll();
			
			// add resource pool table to menu panel
			menuPanel.add(resourcePanel, BorderLayout.CENTER);
			menuPanel.repaint();
			
			// populate seaport selection jComboBox
			if (seaportSelectionBox.getItemCount()==0) {
				for (SeaPort port : (worlds.get(worldCounter - 1).ports)) seaportSelectionBox.addItem(port.name);
			}
			
			// display default jComboBox selection
			String portName = seaportSelectionBox.getSelectedItem().toString();
			SeaPort thisPort = null;
			for (SeaPort port : (worlds.get(worldCounter - 1).ports)) {
				if (portName.matches(port.name)) thisPort = port;
			}	
			JTable jobResourcesTable = new JTable(new DataTableModel("Resources", thisPort));
			JScrollPane resourceTableScroll = new JScrollPane(jobResourcesTable);
			resourceTableScroll.setPreferredSize(new Dimension(0, 150));
			
			//set custom column widths
			jobResourcesTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
			TableColumnModel colModel = jobResourcesTable.getColumnModel();
			colModel.getColumn(2).setPreferredWidth(110);
			
			resourceDisplayPanel.add(resourceTableScroll);
			
			displayPanel.add(jobDisplayPanel);
			displayPanel.validate();
			displayPanel.repaint();
			
			} catch (ArrayIndexOutOfBoundsException e2) {
				JOptionPane.showMessageDialog(null, "Use browse button to open a new file.", "Alert",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		// pauseJobs listener
		pauseJobsButton.addActionListener((ActionEvent e) -> {
			try {
				worlds.get(worldCounter - 1).pauseAllJobs(false);
			} catch (ArrayIndexOutOfBoundsException e2) {
				JOptionPane.showMessageDialog(null, "Use browse button to open a new file.", "Alert",
						JOptionPane.ERROR_MESSAGE);
			}

		});

		// resumeJobs listener
		resumeJobsButton.addActionListener((ActionEvent e) -> {
			try {
				worlds.get(worldCounter - 1).pauseAllJobs(true);
			} catch (ArrayIndexOutOfBoundsException e2) {
				JOptionPane.showMessageDialog(null, "Use browse button to open a new file.", "Alert",
						JOptionPane.ERROR_MESSAGE);
			}

		});
		
		// seaportSelectionBox listener
		seaportSelectionBox.addActionListener((ActionEvent e) -> {
			if (seaportSelectionBox.getItemCount() != 0) {
				String portName = seaportSelectionBox.getSelectedItem().toString();
				SeaPort thisPort = null;
				for (SeaPort port : (worlds.get(worldCounter - 1).ports)) {
					if (portName.matches(port.name)) thisPort = port;
				}
				
				JTable jobResourcesTable = new JTable(new DataTableModel("Resources", thisPort));
				JScrollPane resourceTableScroll = new JScrollPane(jobResourcesTable);
				resourceTableScroll.setPreferredSize(new Dimension(0, 150));
				
				//set custom column widths
				jobResourcesTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
				TableColumnModel colModel = jobResourcesTable.getColumnModel();
				colModel.getColumn(2).setPreferredWidth(110);
				
				resourceDisplayPanel.removeAll();
				resourceDisplayPanel.add(resourceTableScroll);
				resourceDisplayPanel.validate();
				resourceDisplayPanel.repaint();
			}			
		});
	}

	
	private void display() {
		setVisible(true);
	}

	
	// method to create and display table display data for ships or persons
	private void constructTable(DefaultMutableTreeNode node, JPanel tableScrollPanel, World world) {

		// find appropriate seaport for which to display data
		SeaPort currentSeaport = null;
		for (SeaPort port : world.ports) {
			if (port.name == node.getParent().toString())
				currentSeaport = port;
		}

		// construct table based on custom table model
		JTable table = new JTable(new DataTableModel(node.toString(), currentSeaport));
		JScrollPane tableScroll = new JScrollPane(table);
		tableScrollPanel.add(tableScroll, BorderLayout.CENTER);

		// add table and other components to display
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBorder(new EmptyBorder(5, 5, 5, 10));
		JLabel currentNodeLabel = new JLabel(node.toString());
		currentNodeLabel.setFont(currentNodeLabel.getFont().deriveFont(Font.BOLD, 14f));
		JLabel currentPortLabel = new JLabel("Seaport: " + node.getParent().toString());
		currentPortLabel.setFont(currentPortLabel.getFont().deriveFont(Font.BOLD, 14f));
		titlePanel.add(currentNodeLabel, BorderLayout.WEST);
		titlePanel.add(currentPortLabel, BorderLayout.EAST);
		tableScrollPanel.add(titlePanel, BorderLayout.NORTH);
	}
	
	
	// method to create and display JTree that displays hierarchy of elements in
	// seaport
	private void displayTree(JPanel displayPanel, JSplitPane treeView, ArrayList<World> worlds, String expansionState) {
		try {
			createNodes(top, worlds.get(worldCounter - 1));
			tree.expandPath(new TreePath(top.getPath()));
			if (!(expansionState == null))
				setExpansionState(expansionState);

			displayPanel.removeAll();
			displayPanel.add(treeView);
			displayPanel.validate();
			displayPanel.repaint();

		} catch (ArrayIndexOutOfBoundsException e2) {
			JOptionPane.showMessageDialog(null, "Use browse button to open a new file.", "Alert",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	
	// method that constructs jTree data structure
	private void createNodes(DefaultMutableTreeNode top, World world) {
		DefaultMutableTreeNode seaportNode = null;
		DefaultMutableTreeNode dockCategoryNode = null;
		DefaultMutableTreeNode queCategoryNode = null;
		DefaultMutableTreeNode allShipsCategoryNode = null;
		DefaultMutableTreeNode personsCategoryNode = null;

		for (SeaPort port : world.ports) {
			seaportNode = new DefaultMutableTreeNode(port.name);
			dockCategoryNode = new DefaultMutableTreeNode("Docks");
			queCategoryNode = new DefaultMutableTreeNode("Ships in Que");
			allShipsCategoryNode = new DefaultMutableTreeNode("All Ships in Port");
			personsCategoryNode = new DefaultMutableTreeNode("Persons");
			top.add(seaportNode);
			seaportNode.add(dockCategoryNode);
			seaportNode.add(queCategoryNode);
			seaportNode.add(allShipsCategoryNode);
			seaportNode.add(personsCategoryNode);
		}
	}

	
	// methods to get and maintain previous JTree expansion state
	private String getExpansionState() {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < tree.getRowCount(); i++) {
			TreePath tp = tree.getPathForRow(i);
			if (tree.isExpanded(i)) {
				sb.append(tp.toString());
				sb.append(",");
			}
		}

		return sb.toString();

	}

	private void setExpansionState(String s) {

		for (int i = 0; i < tree.getRowCount(); i++) {
			TreePath tp = tree.getPathForRow(i);
			if (s.contains(tp.toString())) {
				tree.expandRow(i);
			}
		}
	}

	
	// main method that displays GUI
	public static void main(String[] args) {
		SeaPortProgram SPP = new SeaPortProgram();
		SPP.display();
	}
}
