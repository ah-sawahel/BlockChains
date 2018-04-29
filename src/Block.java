import java.security.Timestamp;
import java.util.ArrayList;

public class Block {
    
	int index; 
	String hash;
	String previousHash;
	ArrayList<Transaction> transactions; 
	Timestamp createdAt;
	Block previousBlock;
	int level;
	
	public Block(int i, ArrayList<Transaction> t, String ph){
		this.index = i;
		this.transactions = t;
		this.previousHash = ph;
		this.hash = "";
	}

	public int getIndex() {
		return index;
	}

	public String getHash() {
		return hash;
	}

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public String getPreviousHash() {
		return previousHash;
	}
}
