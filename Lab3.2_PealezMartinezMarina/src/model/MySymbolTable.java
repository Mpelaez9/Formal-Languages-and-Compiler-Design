package model;

public class MySymbolTable {
	private String[] table;
	private int numberOfElements;
	
	public MySymbolTable(int n) {
		this.numberOfElements=0;
		if(!isPrime(n)) {
			n=nextPositivePrime(n);
		}
		this.table=new String[n];
	}
	
	public String[] getSymbolTable() {
		return table;
	}
	
	public int getNumberOfElements() {
		return numberOfElements;
	}
	
	public int getSize() {
		return table.length;
	}
	
	public int insert(String i) {
		if(i==null) {
			return -2;
		}else if(find(i)!=-1) {
			return -1;
		}
		int position=hashFunction(i);
		int attempt=0;
		while(attempt<getSize() && table[position]!=null) {
			position=positionSearch(i,attempt);
			attempt++;
		}
		if(attempt==getSize()) {
			return -3;
		}else {
			table[position]=i;
			numberOfElements++;
			return 0;
		}
	}
	
	private int positionSearch(String i, int attempt) {
		return (hashFunction(i)+attempt)%getSize();
	}
	
	public int find(String i) {
		int position=0;
		int attempt=0;
		while(attempt<=getSize()) {
			position = positionSearch(i, attempt);
			if(table[position]!=null && table[position].equals(i)) {
				return position;
			}
			attempt++;
		}
		return -1;
	}

	public String toString() {
		StringBuilder stringChain = new StringBuilder();
		for(int i=0;i< getSize();i++){
			if(table[i]==null) {
				stringChain.append("-");
			}else {
				stringChain.append(table[i].toString());
			}
			stringChain.append(";");
		}
		stringChain.append("[Size: ");
		stringChain.append(getSize());
		stringChain.append(" Num.Elems.: ");
		stringChain.append(getNumberOfElements());
		stringChain.append("]");
		return stringChain.toString();
	}
	
 	private int hashFunction(String i) {
		int position = i.hashCode()%getSize();
		if(position<0) {
			return position + getSize();
		}else {
			return position;
		}
	}
	
	private boolean isPrime(int n) {
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
	
	private int nextPositivePrime(int n) {
		if(isPrime(n)) {
			n++;
		}
		while(!isPrime(n)) {
			n++;
		}
		return n;
	}
	
}