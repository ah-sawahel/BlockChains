import java.security.Timestamp;
import java.util.ArrayList;

public class Block {
    
	int index; 
	int hash;
	int previousHash;
	ArrayList<Transaction> transactions; 
	Timestamp createdAt;
	Block previousBlock;
	int level;
	
	public Block(int i, ArrayList<Transaction> t, int ph){
		this.index = i;
		this.transactions = t;
		this.previousHash = ph;
		this.hash = hashCode();
	}

	public int getIndex() {
		return index;
	}

	public int getHash() {
		return hash;
	}

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public int getPreviousHash() {
		return previousHash;
	}
}
