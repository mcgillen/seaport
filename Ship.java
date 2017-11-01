import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * File: Ship.java 
 * Author: Brian McGillen 
 * Date: September 14, 2017 
 * Purpose: Contains fields and methods for Ship objects. Extends scanner constructor and
 * toString() method of parent class. Contains comparators for comparing ships by
 * draft, weight, length, or width.
 */

public class Ship extends Thing {

	PortTime arrivalTime;
	PortTime dockTime;
	SeaPort port;
	volatile Dock dock;
	
	Boolean busyFlag=false;

	double weight;
	double length;
	double width;
	double draft;
	ArrayList<Job> jobs = new ArrayList<>(); 

	public Ship(Scanner sc) {
		super(sc);
		if (sc.hasNextDouble())
			weight = sc.nextDouble();
		if (sc.hasNextDouble())
			length = sc.nextDouble();
		if (sc.hasNextDouble())
			width = sc.nextDouble();
		if (sc.hasNextDouble())
			draft = sc.nextDouble();
	}

	public String toString() {
		return super.toString() + "\n\t\tweight: " + this.weight + "\tlength: " + this.length + "\twidth: " + this.width
				+ "\tdraft: " + this.draft + "\n";
	}

	public static Comparator<Ship> draftComparator = new Comparator<Ship>() {

		public int compare(Ship a, Ship b) {
			return Double.compare(a.draft, b.draft);
		}

	};

	public static Comparator<Ship> weightComparator = new Comparator<Ship>() {

		public int compare(Ship a, Ship b) {
			return Double.compare(a.weight, b.weight);
		}

	};

	public static Comparator<Ship> lengthComparator = new Comparator<Ship>() {

		public int compare(Ship a, Ship b) {
			return Double.compare(a.length, b.length);
		}

	};

	public static Comparator<Ship> widthComparator = new Comparator<Ship>() {

		public int compare(Ship a, Ship b) {
			return Double.compare(a.width, b.width);
		}

	};
}
