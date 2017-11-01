import java.util.Comparator;
import java.util.Scanner;

/**
 * File: Person.java
 * Author: Brian McGillen
 * Date: August 30, 2017
 * Purpose: Contains fields and methods for Person objects. Extends scanner constructor
 * and toString() method of parent class. Contains comparator for sorting by skill.
 */

public class Person extends Thing {

	String skill;
	Job currentJob = null;
	int listLocation;
	
	public Person(int index, String name, int parent, String skill) {
		super(name, index, parent);
		this.skill=skill;
	}
	
	public Person(Scanner sc) {
		super(sc);
		if(sc.hasNext()) skill=sc.next();
	}
	
	public String toString() {
		return "Person: " + super.toString() + " " + skill + "\n";
	}
	
	public static Comparator<Person> skillComparator = new Comparator<Person>() {

		public int compare(Person a, Person b) {
			//sort alphabetically in ascending order
			return a.skill.compareTo(b.skill);
		}

	};

}
