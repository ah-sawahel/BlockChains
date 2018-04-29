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
    HashMap<Integer, Block> cache;
    ArrayList<Transaction> commulator;
    int headHash = -1;
    Block root;
	
    public User(int id) throws NoSuchAlgorithmException{
        this.id = "" + id;
        
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        keyPair = kpg.genKeyPair();  
        userSignatureType = Signature.getInstance("MD5WithRSA");
        root = new Block(0, null, 0);
        root.level = 0;
        blockChain = new HashMap<>();
        blockChain.put(0, root);
        commulator = new ArrayList<>();
        cache = new HashMap<>();
        cache.put(0, root);
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

    private void newEvent(Transaction t, User transationOwner) throws UnsupportedEncodingException, SignatureException, InvalidKeyException {
        if(!commulator.contains(t))
        {
            commulator.add(t);
            NotifyNeighbours(t, transationOwner);
            if(commulator.size()>4){
                String nonce = Mine();
                System.out.println(Long.valueOf(nonce.hashCode()) + "    ********************************************");
                //TODO use nonce in hashing :)

                // Create a block and add it to the main block chain + change root
                Block newBlock = new Block(blockChain.size(), commulator, root.hash);
                newBlock.previousBlock = root;
                newBlock.level = root.level + 1;
                root = newBlock;
                cache.put(newBlock.hash, root);
                blockChain.put(newBlock.hash, newBlock);

                // propagate the new block to neighbours
                NotifyNeighbours(newBlock);
            }
        }
    }

    private void newEvent(Block newBlock) {
        // add any block received to the cache if its previous block is already found
        if(cache.containsKey(newBlock.previousHash)){
            cache.put(newBlock.getHash(), newBlock);

//            previous block + level should already be set, else use cloning
//            newBlock.previousBlock = cache.get(newBlock.previousHash);
//            newBlock.level = cache.get(newBlock.previousHash).level + 1;

            // check to add in blockchain
            if(root.hash == newBlock.previousHash){
                blockChain.put(newBlock.getHash(), newBlock);
                root = newBlock;
            }
        }
    }


    public void NotifyNeighbours(Transaction t, User transactionOwner) throws UnsupportedEncodingException, SignatureException, InvalidKeyException {
        Random random = new Random();
        int randomNeighboursCount = random.nextInt((neighbours.size()-1)/2) + (neighbours.size()/4);
        Collections.shuffle(neighbours);
        if(t.verifySignature(transactionOwner)){
            for (int i = 0; i < randomNeighboursCount; i++) {
                neighbours.get(i).newEvent(t, transactionOwner);
            }
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
        NotifyNeighbours(newTransaction, this);
    }

    public String Mine(){
        String nonce = randomUUID().toString().substring(0,5) + headHash;
        while(!(nonce.startsWith("010") && Long.valueOf(nonce.hashCode()) > 1e9))
            nonce = randomUUID().toString().substring(0,5) + headHash;
        System.out.println(nonce + " ------------------------------");
        return nonce;
    }
}