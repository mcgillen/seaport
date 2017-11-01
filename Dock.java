import java.util.Comparator;
import java.util.Scanner;

/**
 * File: Dock.java 
 * Author: Brian McGillen 
 * Date: September 14, 2017 
 * Purpose: Contains fields and methods for Dock objects. Extends scanner constructor and
 * toString() method of parent class. Contains comparator for comparing docks by name.
 */

public class Dock extends Thing {

	Ship currentShip;
	SeaPort parentPort;

	public Dock(Scanner sc) {
		super(sc);
	}

	public String toString() {
		return "Dock: " + super.toString() + "\n\t" + currentShip.toString();
	}
	
	public static Comparator<Thing> dockNameComparator = new Comparator<Thing>() {

		public int compare(Thing a, Thing b) {

			//strip everything by pier number and convert to int
			int aNum = Integer.valueOf(a.name.substring(5));
			int bNum = Integer.valueOf(b.name.substring(5));

			//sorts by ascending order of pier number
			if (aNum < bNum)
				return -1;
			else if (aNum > bNum)
				return 1;
			else
				return 0;
		}
	};

	public void replaceCurrentShip() {
		currentShip.dock=null;
		//search for new ship in que
		if(!parentPort.que.isEmpty()) {
			currentShip=parentPort.que.get(0);
			currentShip.dock=this;
			parentPort.que.remove(currentShip);
			if (currentShip.jobs.isEmpty()) this.replaceCurrentShip();
		}
	}
	
}
