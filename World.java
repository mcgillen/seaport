import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.table.DefaultTableModel;

/**
 * File: World.java 
 * Author: Brian McGillen 
 * Date: September 26, 2017 
 * Purpose:
 * Contains list of all SeaPorts. Extends scanner constructor and toString()
 * method of Thing class. Contains process method which adds ports to the list
 * of SeaPorts and assigns all other scanned objects to the appropriate port.
 * Contains search and sort methods to return requested data. Starts all job
 * threads after file has been read in.
 */

public class World extends Thing {

	ArrayList<SeaPort> ports = new ArrayList<>();
	ArrayList<Job> globalJobList = new ArrayList<>();

	PortTime time;
	
	// counter used to create an index of job threads created
	int jobCount = 0;

	public World(Scanner sc) {
		super(sc);
	}

	public World(String name, int index, int parent) {
		super(name, index, parent);
	}

	// calls the appropriate method below based on what is scanned
	void process(Scanner sc, DefaultTableModel jobTableModel) {
		HashMap<Integer, SeaPort> portMap = new HashMap<>();
		HashMap<Integer, Dock> dockMap = new HashMap<>();
		HashMap<Integer, Ship> shipMap = new HashMap<>();

		while (sc.hasNext()) {
			switch (sc.next()) {
			case "port":
				addPort(sc, portMap);
				break;
			case "dock":
				addDock(sc, portMap, dockMap);
				break;
			case "pship":
				addPShip(sc, portMap, dockMap, shipMap);
				break;
			case "cship":
				addCShip(sc, portMap, dockMap, shipMap);
				break;
			case "person":
				addPerson(sc, portMap);
				break;
			case "job":
				addJob(sc, dockMap, shipMap, jobTableModel);
				break;
			case "//":
				sc.nextLine();
				break;
			default:
				break;
			}
		}
		// after file has been read, start all job threads
		// globalJobList.sort(Job.jobShipComparator); perhaps implement in the future
		
		//first, release any ships without jobs from their docks
		for (SeaPort port: ports) {
			for (Dock dock: port.docks) {
				if (dock.currentShip.jobs.isEmpty()) dock.replaceCurrentShip(); 
			}
		}
		for (Job job : globalJobList) {
			job.startThread();
		}
	}

	// methods to add elements to data structure
	void addPort(Scanner sc, HashMap<Integer, SeaPort> portMap) {
		SeaPort port = new SeaPort(sc);
		ports.add(port);
		portMap.put(port.index, port);
	}

	void addDock(Scanner sc, HashMap<Integer, SeaPort> portMap, HashMap<Integer, Dock> dockMap) {
		Dock dock = new Dock(sc);
		getSeaPortByIndex(dock.parent, portMap).docks.add(dock);
		dock.parentPort = getSeaPortByIndex(dock.parent, portMap);
		dockMap.put(dock.index, dock);
	}

	void addPShip(Scanner sc, HashMap<Integer, SeaPort> portMap, HashMap<Integer, Dock> dockMap,
			HashMap<Integer, Ship> shipMap) {
		PassengerShip pShip = new PassengerShip(sc);
		assignShip(pShip, portMap, dockMap);
		shipMap.put(pShip.index, pShip);
	}

	void addCShip(Scanner sc, HashMap<Integer, SeaPort> portMap, HashMap<Integer, Dock> dockMap,
			HashMap<Integer, Ship> shipMap) {
		CargoShip cShip = new CargoShip(sc);
		assignShip(cShip, portMap, dockMap);
		shipMap.put(cShip.index, cShip);
	}

	void addPerson(Scanner sc, HashMap<Integer, SeaPort> portMap) {
		Person person = new Person(sc);
		getSeaPortByIndex(person.parent, portMap).persons.add(person);
		getSeaPortByIndex(person.parent, portMap).availableSkills.add(person.skill);
	}

	void addJob(Scanner sc, HashMap<Integer, Dock> dockMap, HashMap<Integer, Ship> shipMap,
			DefaultTableModel jobTableModel) {

		Job job = new Job(sc, jobTableModel, jobCount);
		jobCount++;
		// There is an inconsistency in the test files in regard to what the job's
		// parent
		// index refers to. In some cases it refers to a ship; in others, a dock. The
		// code
		// below deals with both possibilities.
		Dock parentDock = getDockByIndex(job.parent, dockMap);
		Ship parentShip = getShipByIndex(job.parent, shipMap);
		if (!(parentDock == null)) {
			getDockByIndex(job.parent, dockMap).currentShip.jobs.add(job);
			job.targetShip = getDockByIndex(job.parent, dockMap).currentShip;
		}
		if (!(parentShip == null)) {
			getShipByIndex(job.parent, shipMap).jobs.add(job);
			job.targetShip = getShipByIndex(job.parent, shipMap);
		}
		globalJobList.add(job);

	}

	// Assigns ship objects to the appropriate dock using the ship's parent index
	// data point. If the parent is not a dock, the ship is added to the que list.
	// All ships are added to the ships list.
	void assignShip(Ship ship, HashMap<Integer, SeaPort> portMap, HashMap<Integer, Dock> dockMap) {
		Dock dock = getDockByIndex(ship.parent, dockMap);
		if (dock == null) {
			getSeaPortByIndex(ship.parent, portMap).ships.add(ship);
			getSeaPortByIndex(ship.parent, portMap).que.add(ship);
			ship.port = getSeaPortByIndex(ship.parent, portMap);
			return;
		}
		dock.currentShip = ship;
		getSeaPortByIndex(dock.parent, portMap).ships.add(ship);
		ship.port = getSeaPortByIndex(dock.parent, portMap);
		ship.dock = dock;
	}

	// methods to find elements by index using HashMaps
	Dock getDockByIndex(int index, HashMap<Integer, Dock> dockMap) {
		if (dockMap.containsKey(index))
			return dockMap.get(index);
		else
			return null;
	}

	SeaPort getSeaPortByIndex(int index, HashMap<Integer, SeaPort> portMap) {
		if (portMap.containsKey(index))
			return portMap.get(index);
		else
			return null;
	}

	Ship getShipByIndex(int index, HashMap<Integer, Ship> shipMap) {
		if (shipMap.containsKey(index))
			return shipMap.get(index);
		else
			return null;
	}

	// toString method that calls on the toString method of children of this class
	public String toString() {
		String str = "File name: " + this.name + "\n\nList of Seaports";
		for (SeaPort port : ports) {
			str += "\n" + port;
		}
		return str;
	}

	// search methods that activate the search methods for each SeaPort object
	// contained in this world object's list of SeaPorts
	public String searchPorts(String searchItem, String searchTerm) {
		String result = "";
		switch (searchItem) {
		case "skill":
			result += "Persons with skill: " + searchTerm + "\n";
			for (SeaPort port : ports) {
				result += port.searchSkill(searchTerm);
			}
			return result;
		case "name":
			result += "Search results for: " + searchTerm + "\n";
			for (SeaPort port : ports) {
				if (port.name.equalsIgnoreCase(searchTerm))
					result += "\n" + port.toString();
				result += port.searchName(searchTerm);
			}
			return result;
		case "index":
			result += "Search results for index: " + searchTerm + "\n";
			for (SeaPort port : ports) {
				if (port.index == Integer.valueOf(searchTerm))
					result += "\n" + port.toString();
				result += port.searchIndex(Integer.valueOf(searchTerm));
			}
			return result;
		}
		return null;
	}

	// sort methods for each type of data that can be sorted. Methods sort the
	// data's arrayList by the sort criteria given. The toString() method of the
	// data type then returns the sorted data as a String.
	public String sortSeaports(String sortCriteria) {
		switch (sortCriteria) {
		case "Name":
			ports.sort(Thing.nameComparator);
			break;
		case "Index":
			ports.sort(Thing.indexComparator);
			break;
		}
		return toString();
	}

	public String sortDocks(String sortCriteria) {
		String result = "";
		for (SeaPort port : ports) {
			switch (sortCriteria) {
			case "Name":
				port.docks.sort(Dock.dockNameComparator);
				break;
			case "Index":
				port.docks.sort(Thing.indexComparator);
				break;
			}
			result += "   =====Seaport:" + port.name + "=====\n" + port.toStringDock() + "\n\n";
		}
		return result;
	}

	public String sortAllShips(String sortCriteria) {
		String result = "";
		for (SeaPort port : ports) {
			switch (sortCriteria) {
			case "Name":
				port.ships.sort(Thing.nameComparator);
				break;
			case "Index":
				port.ships.sort(Thing.indexComparator);
				break;
			case "Draft":
				port.ships.sort(Ship.draftComparator);
				break;
			case "Weight":
				port.ships.sort(Ship.weightComparator);
				break;
			case "Length":
				port.ships.sort(Ship.lengthComparator);
				break;
			case "Width":
				port.ships.sort(Ship.widthComparator);
				break;
			}
			result += "   =====Seaport:" + port.name + "=====\n" + port.toStringShip() + "\n";
		}
		return result;
	}

	public String sortShipsInQue(String sortCriteria) {
		String result = "";
		for (SeaPort port : ports) {
			switch (sortCriteria) {
			case "Name":
				port.que.sort(Thing.nameComparator);
				break;
			case "Index":
				port.que.sort(Thing.indexComparator);
				break;
			case "Draft":
				port.que.sort(Ship.draftComparator);
				break;
			case "Weight":
				port.que.sort(Ship.weightComparator);
				break;
			case "Length":
				port.que.sort(Ship.lengthComparator);
				break;
			case "Width":
				port.que.sort(Ship.widthComparator);
				break;
			}
			result += "   =====Seaport:" + port.name + "=====\n" + port.toStringQue() + "\n";
		}

		return result;
	}

	public String sortPersons(String sortCriteria) {
		String result = "";
		for (SeaPort port : ports) {
			switch (sortCriteria) {
			case "Name":
				port.persons.sort(Thing.nameComparator);
				break;
			case "Index":
				port.persons.sort(Thing.indexComparator);
				break;
			case "Skill":
				port.persons.sort(Person.skillComparator);
				break;
			}
			result += "   =====Seaport:" + port.name + "=====\n" + port.toStringPerson() + "\n";
		}
		return result;
	}

	// method that pauses every job in the global job list
	public void pauseAllJobs(Boolean b) {
		for (Job job : this.globalJobList) {
			job.pauseFlag = b;
			job.setPauseButton(b);
		}

	}
}
