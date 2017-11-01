import java.util.Scanner;

/**
 * File: PassengerShip.java
 * Author: Brian McGillen
 * Date: August 30, 2017
 * Purpose: Contains fields and methods for PassengerShip objects. Extends scanner constructor
 * and toString() method of parent class.
 */

public class PassengerShip extends Ship {
	
	int numPassengers;
	int numRooms;
	int numOccupied;
	
	public PassengerShip (Scanner sc) {
	      super (sc);
	      if (sc.hasNextInt()) numPassengers = sc.nextInt();
	      if (sc.hasNextInt()) numRooms = sc.nextInt();
	      if (sc.hasNextInt()) numOccupied = sc.nextInt();
	}

	public String toString() {
		return "Passenger Ship: " + super.toString();
	}
}
