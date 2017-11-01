import javax.swing.table.AbstractTableModel;

/**
 * File: Job.java 
 * Author: Brian McGillen 
 * Date: September 26, 2017 
 * Purpose: Defines table models used when display data in jTables.
 */

public class DataTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private String[] columnNames;
	private String[][] data;

	public DataTableModel(String selection, SeaPort port) {
		switch (selection) {
		case "Docks":
			columnNames = new String[] { "Dock", "Index", "Current Ship", "Weight", "Length", "Width", "Draft" };
			data = new String[port.docks.size()][7];
			for (int i = 0; i < data.length; i++) {
				Dock currentDock = port.docks.get(i);
				Ship currentShip = port.docks.get(i).currentShip;
				data[i][0] = currentDock.name;
				data[i][1] = Integer.toString(currentDock.index);
				data[i][2] = currentShip.name;
				data[i][3] = Double.toString(currentShip.weight);
				data[i][4] = Double.toString(currentShip.length);
				data[i][5] = Double.toString(currentShip.width);
				data[i][6] = Double.toString(currentShip.draft);
			}
			break;
		case "All Ships in Port":
			columnNames = new String[] { "Ship", "Index", "Weight", "Length", "Width", "Draft" };
			data = new String[port.ships.size()][6];
			for (int i = 0; i < data.length; i++) {
				Ship currentShip = port.ships.get(i);
				data[i][0] = currentShip.name;
				data[i][1] = Integer.toString(currentShip.index);
				data[i][2] = Double.toString(currentShip.weight);
				data[i][3] = Double.toString(currentShip.length);
				data[i][4] = Double.toString(currentShip.width);
				data[i][5] = Double.toString(currentShip.draft);
			}
			break;
		case "Ships in Que":
			columnNames = new String[] { "Ship", "Index", "Weight", "Length", "Width", "Draft" };
			data = new String[port.que.size()][6];
			for (int i = 0; i < data.length; i++) {
				Ship currentShip = port.que.get(i);
				data[i][0] = currentShip.name;
				data[i][1] = Integer.toString(currentShip.index);
				data[i][2] = Double.toString(currentShip.weight);
				data[i][3] = Double.toString(currentShip.length);
				data[i][4] = Double.toString(currentShip.width);
				data[i][5] = Double.toString(currentShip.draft);
			}
			break;
		case "Persons":
			columnNames = new String[] { "Person", "Index", "Skill", "Current Job Location" };
			data = new String[port.persons.size()][4];
			for (int i = 0; i < data.length; i++) {
				Person currentPerson = port.persons.get(i);
				data[i][0] = currentPerson.name;
				data[i][1] = Integer.toString(currentPerson.index);
				data[i][2] = currentPerson.skill;
				String currentLocation = "";
				if (currentPerson.currentJob!=null) currentLocation = currentPerson.currentJob.targetShip.name;
				data[i][3] = currentLocation;
			}
			break;
			
		case "Resources":
			columnNames = new String[] { "Person", "Skill", "Current Job Location" };
			port.dtm = this;
			data = new String[port.persons.size()][3];
			for (int i = 0; i < data.length; i++) {
				Person currentPerson = port.persons.get(i);
				data[i][0] = currentPerson.name;
				data[i][1] = currentPerson.skill;
				String currentLocation = "";
				if (currentPerson.currentJob!=null) currentLocation = currentPerson.currentJob.targetShip.name;
				data[i][2] = currentLocation;
			}
			break;
		}
		
		
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];
	}
	
	public void setValueAt(String value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }

}
