package hashTable;
/*
 * AUTHOR: Marina Peláez Martínez
 * 2023
 * Class that implements a basic Hash Table.
 * It represents a custom hash table for storing elements as strings. 
 * It handles collisions and includes methods for inserting, removing and finding elements, 
 * as well as utility functions for prime number checks and resizing the table.
 */


public class MyHashTable {
	
	// Attributes:
	private String[] myTable; // Array of strings representing the hash table.
	private int numberOfElements; // Counter for the number of elements in the table.
	
	/*
	 * Constructor:
	 * Initializes the numberOfElements and myTable array. 
	 * If the provided size is not a positive prime number, it finds the next consecutive prime number (private method).
	 */
	public MyHashTable(int size) {
		this.numberOfElements=0;
		if(!primeNumberPositive(size)) {
			size=consecutivePrime(size); // Couple
		}
		this.myTable=new String[size]; // Creates the hash table.
	}
	
	// GETTERS:
	
	/*
	 * Returns the hash table.
	 */
	public String[] getMySymbolTable() {
		return myTable;
	}
	
	/*
	 * Returns the number of elements.
	 */
	public int getNumberOfElements() {
		return numberOfElements;
	}
	
	/*
	 * Returns the size of the table.
	 */
	public int getSize() {
		return myTable.length;
	}
	
	/*
	 * This method takes a string i (identifier) as input and attempts to remove it from the hash table. 
	 * If the element is found and removed, it returns 0. 
	 * If the element is not found in the table, it returns -1. 
	 * If the element to be removed is null, it returns -2.
	 */
	public int removeElements(String i) {
	    if (i == null) {
	        return -2; 
	    }
	    int position = findElements(i);
	    if (position != -1) {
	        myTable[position] = null; // Element is marked as removed.
	        numberOfElements--;
	        return 0; // Element is removed.
	    }
	    return -1; // Element was not found.
	}

	
	
	/*
	 * It inserts an element i into the hash table. 
	 * It handles cases where the input is null or if the element already exists.
	 * If the element cannot be inserted due to collisions, it returns an error code.
	 * 0 If the element was correctly inserted, -1 if it was duplicated and -2 if it was null or not existed.
	 */
	public int insertElements(String i) {
		if(i==null) {
			return -2; // Null element
		}else if(findElements(i)!=-1) {
			return -1; //Not found
		}
		int position=hashTableFunction(i);
		int attempt=0;
		while(attempt<getSize() && myTable[position]!=null) {
			position=findPosition(i,attempt);
			attempt++;
		}
		if(attempt==getSize()) {
			return -3;
		}else {
			myTable[position]=i;
			numberOfElements++;
			return 0;
		}
	}
	
	
	
	
	/*
	 * This method calculates the position to insert or find an element in the hash table, 
	 * taking into account possible collisions.
	 */
	private int findPosition(String i, int attempt) {
		return (hashTableFunction(i)+attempt)%getSize();
	}
	
	/*
	 * This method finds the position of an element i in the hash table and returns it.
	 * Returns -1 if it was not found.
	 */
	public int findElements(String i) {
		int attempt=0;
		int position=0;
		while(attempt<=getSize()) {
			position = findPosition(i, attempt);
			if(myTable[position]!=null && myTable[position].equals(i)) {
				return position;
			}
			attempt++;
		}
		return -1;
	}

	/*
	 * This method generates a string representation of the hash table, 
	 * showing the elements and additional information like size and number of elements.
	 */
	public String toString() {
		StringBuilder string = new StringBuilder();
		for(int i=0;i< getSize();i++){
			if(myTable[i]==null) {
				string.append("-");
			}else {
				string.append(myTable[i].toString());
			}
			string.append(";");
		}
		string.append("[Size: ");
		string.append(getSize());
		string.append(" Num.Elems.: ");
		string.append(getNumberOfElements());
		string.append("]");
		return string.toString();
	}
	
	/*
	 * This method computes the hash code of the input element i and maps it to a valid position in the hash table.
	 */
 	private int hashTableFunction(String i) {
		int position = i.hashCode()%getSize();
		if(position<0) {
			return position + getSize();
		}else {
			return position;
		}
	}
	
 	/*
 	 * This method checks if a given number n is a positive prime number.
 	 */
	private boolean primeNumberPositive(int n) {
		if(n<0) {
			return false;
		}
		int counter = 2;
		boolean prime=true;
		while ((prime) && (counter!=n)){
			if (n % counter == 0) {
				prime = false;
			}
			counter++;
		}
		return prime;
	} 
	
	/*
	 * This method finds the next consecutive prime number after n.
	 */
	private int consecutivePrime(int n) {
		if(primeNumberPositive(n)) {
			n++;
		}
		while(!primeNumberPositive(n)) {
			n++;
		}
		return n;
	}
	
}