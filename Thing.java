import java.util.Comparator;
import java.util.Scanner;

/**
 * File: CargoShip.java 
 * Author: Brian McGillen
 * Date: September 14, 2017 
 * Purpose: Contains fields and methods for Thing objects. These are fields common to all
 * the data processed by the program. Contains comparators for comparing by index or name.
 */

public class Thing implements Comparable<Thing> {

	int index;
	String name;
	int parent;

	@Override
	public int compareTo(Thing o) {
		return 0;
	}

	public static Comparator<Thing> nameComparator = new Comparator<Thing>() {

		public int compare(Thing a, Thing b) {

			String aName = a.name.toUpperCase();
			String bName = b.name.toUpperCase();
			// ascending order
			return aName.compareTo(bName);
		}
	};

	public static Comparator<Thing> indexComparator = new Comparator<Thing>() {

		public int compare(Thing a, Thing b) {

			if (a.index < b.index)
				return -1;
			else if (a.index > b.index)
				return 1;
			else
				return 0;
		}
	};

	public Thing(String name, int index, int parent) {
		this.index = index;
		this.name = name;
		this.parent = parent;
	}

	public Thing(Scanner sc) {
		if (sc.hasNext())
			name = sc.next();
		if (sc.hasNextInt())
			index = sc.nextInt();
		if (sc.hasNextInt())
			parent = sc.nextInt();
	}

	public String toString() {
		return name + " " + index;
	}
}
