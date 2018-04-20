import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import static java.util.UUID.randomUUID;

public class User {
    ArrayList<User> neighbours = new ArrayList<>();
    String id;
    KeyPair keyPair; 
	Signature userSignatureType;
	HashMap<Integer, Block> blockChain;
    ArrayList<Transaction> commulator;
    int headHash = -1;
	
    public User(int id) throws NoSuchAlgorithmException{
        this.id = "" + id;//randomUUID().toString();
        
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        keyPair = kpg.genKeyPair();  
        userSignatureType = Signature.getInstance("MD5WithRSA");
        blockChain = new HashMap<>();
        commulator = new ArrayList<>();
    }

    @Override
    public String toString() {
        String res = "user ID: " + id;
        for (int i = 0; i < neighbours.size(); i++) {
            res += '\n' + neighbours.get(i).id;
        }
        res += '\n' + "Number of transactions received --> " + commulator.size();
        res += '\n' + "Number of Blocks received --> " + blockChain.size();
        res += '\n';
        return res;
    }

    public void newEvent(Transaction t){
        if(!commulator.contains(t))
        {
            commulator.add(t);
            NotifyNeighbours(t);
            if(commulator.size()>4){
                String nonce = Mine();
                System.out.println(Long.valueOf(nonce.hashCode()) + "    ********************************************");
                //TODO use nonce in hashing :)

                Block newBlock =new Block(blockChain.size(), commulator, headHash);
                headHash = newBlock.getHash();
                blockChain.put(newBlock.getHash(), newBlock);
                NotifyNeighbours(newBlock);
            }
        }
    }

    public void newEvent(Block b){
        //TODO implement block receiving
//        if(!blockChain.containsValue(b));
//        {
//            blockChain.put(b.getHash(), b);
//        }
    }

    public void NotifyNeighbours(Transaction t){
        Random random = new Random();
        int randomNeighboursCount = random.nextInt((neighbours.size()-1)/2) + (neighbours.size()/4);
        Collections.shuffle(neighbours);
        for (int i = 0; i < randomNeighboursCount; i++) {
            neighbours.get(i).newEvent(t);
        }
    }

    public void NotifyNeighbours(Block b){
        Random random = new Random();
        int randomNeighboursCount = random.nextInt((neighbours.size()-1)/2) + (neighbours.size()/4);
        Collections.shuffle(neighbours);
        for (int i = 0; i < randomNeighboursCount; i++) {
            neighbours.get(i).newEvent(b);
        }
    }

	public ArrayList<User> getNeighbours() {
		return neighbours;
	}

	public String getId() {
		return id;
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public Signature getUserSignatureType() {
		return userSignatureType;
	}

	public void createTransaction() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, UnsupportedEncodingException {
        Transaction newTransaction = new Transaction(this);
        commulator.add(newTransaction);
        NotifyNeighbours(newTransaction);
    }

    public String Mine(){
        String nonce = randomUUID().toString().substring(0,5) + headHash;
        while(!(nonce.startsWith("010") && Long.valueOf(nonce.hashCode()) > 1e9))
            nonce = randomUUID().toString().substring(0,5) + headHash;
        System.out.println(nonce + " ------------------------------");
        return nonce;
    }
}