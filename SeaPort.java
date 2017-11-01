import java.util.ArrayList;
import java.util.Scanner;

/**
 * File: SeaPort.java 
 * Author: Brian McGillen 
 * Date: August 30, 2017 
 * Purpose:
 * Contains fields and methods for SeaPort objects. Extends scanner constructor
 * and toString() method of parent class. Contains lists of all associated
 * docks, que, ships, and persons and methods to search these lists. Contains methods
 * to assign and release persons from jobs.
 */

public class SeaPort extends Thing {

	ArrayList<Dock> docks = new ArrayList<>();
	ArrayList<Ship> que = new ArrayList<>();
	ArrayList<Ship> ships = new ArrayList<>();
	ArrayList<Person> persons = new ArrayList<>();
	ArrayList<String> availableSkills = new ArrayList<>();
	
	DataTableModel dtm=null;

	public SeaPort(Scanner sc) {
		super(sc);
	}

	public String toString() {
		String str = "\nSeaPort: " + super.toString() + "\n";
		str += toStringDock();
		str += toStringQue();
		str += toStringShip();
		str += toStringPerson();
		return str;
	}

	public String toStringDock() {
		String str = "\n";
		for (Dock dock : docks)
			str += dock;
		return str;
	}

	public String toStringQue() {
		String str = "\n --- List of all ships in que:\n";
		for (Ship ship : que)
			str += "   > " + ship;
		return str;
	}

	public String toStringShip() {
		String str = "\n --- List of all ships:\n";
		for (Ship ship : ships)
			str += "   > " + ship;
		return str;
	}

	public String toStringPerson() {
		String str = "\n --- List of all persons:\n";
		for (Person person : persons)
			str += "   > " + person;
		return str;
	}

	// search methods that return results from this seaport
	public String searchSkill(String searchTerm) {
		String result = "";
		for (Person person : persons) {
			if (person.skill.equalsIgnoreCase(searchTerm))
				result += "\n" + person.name + " " + person.index + "\tPort: " + this.name;
		}
		return result;
	}

	public String searchName(String searchTerm) {
		String result = "";
		for (Dock dock : docks) {
			if (dock.name.equalsIgnoreCase(searchTerm))
				result += "\n" + dock.toString();
		}
		for (Ship ship : ships) {
			if (ship.name.equalsIgnoreCase(searchTerm))
				result += "\n" + ship.toString();
		}
		for (Person person : persons) {
			if (person.name.equalsIgnoreCase(searchTerm))
				result += "\n" + person.toString();
		}
		return result;
	}

	public String searchIndex(int searchTerm) {
		String result = "";
		for (Dock dock : docks) {
			if (dock.index == searchTerm)
				result += "\n" + dock.toString();
		}
		for (Ship ship : ships) {
			if (ship.index == searchTerm)
				result += "\n" + ship.toString();
		}
		for (Person person : persons) {
			if (person.index == searchTerm)
				result += "\n" + person.toString();
		}
		return result;
	}
	
	// method to assign person resources to requesting jobs
	public synchronized Job.Resources getResources (Job job, ArrayList<String> requirements, Job.Resources resources) {
		
		ArrayList<Person> workers = new ArrayList<>();
		
		// search for available workers for each job requirement
		for (String requirement: requirements) {
			for (int i = 0; i < persons.size(); i++) {
				if (persons.get(i).skill.matches(requirement) && persons.get(i).currentJob==null) {
						persons.get(i).currentJob=job;
						persons.get(i).listLocation = i;
						workers.add(persons.get(i));
						break;
				}				
			}
		}
		
		// if not all workers are found tell job to keep waiting
		if (workers.size()!=requirements.size()) {
			for (Person worker: workers) worker.currentJob=null;
			return Job.Resources.WAITING;
		}
		
		// update resource pool table if currently showing for this port
		if (dtm!=null) {
			for (Person worker: workers) dtm.setValueAt(worker.currentJob.targetShip.name, worker.listLocation, 2);
		}
		
		// tell job that resources have been assigned
		return Job.Resources.ACQUIRED;
	}
	
	
	// method to remove persons from jobs after the job has been completed
	public synchronized void releaseResources (Job job) {
		for (int i = 0; i < persons.size(); i++) {
			if ((persons.get(i).currentJob!=null)) {
				if (persons.get(i).currentJob.equals(job)) persons.get(i).currentJob=null;
				
				// update resource pool table if currently showing for this port
				if (dtm!=null) {
					dtm.setValueAt("", i, 2);
				}
				
			}		
		}
	}
	
	// method called once for for each thread to check if required resources will ever be available at this port
	public synchronized Job.Resources pollResources (ArrayList<String> requirements, Job.Resources resources) {
		
		ArrayList<Person> workers = new ArrayList<>();
		
		for (String requirement: requirements) {
			if (!availableSkills.contains(requirement)) return Job.Resources.IMPOSSIBLE;	
			for (Person person: persons) {
				if (person.skill.matches(requirement)) {
					if (!workers.contains(person)) workers.add(person);
					break;
				}				
			}
		}
		if (requirements.size() > workers.size()) return Job.Resources.IMPOSSIBLE;
		return Job.Resources.WAITING;
	}	
	
}
