import java.util.Scanner;

/**
 * File: CargoShip.java
 * Author: Brian McGillen
 * Date: August 30, 2017
 * Purpose: Contains fields and methods for CargoShip objects. Extends scanner constructor
 * and toString() method of parent class.
 */

public class CargoShip extends Ship {

	double cargoWeight;
	double cargoVolume;
	double cargoValue;
	
	public CargoShip (Scanner sc) {
	      super (sc);
	      if (sc.hasNextDouble()) cargoWeight = sc.nextDouble();
	      if (sc.hasNextDouble()) cargoVolume = sc.nextDouble();
	      if (sc.hasNextDouble()) cargoValue = sc.nextDouble();
	      }

	public String toString() {
		return "Cargo Ship: " + super.toString();
	}
}
