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
    HashMap<String, Block> blockChain;
    HashMap<String, Block> cache;
    ArrayList<Transaction> commulator;
    int headHash = -1;
    Block root;
	
    public User(int id) throws NoSuchAlgorithmException{
        this.id = "" + id;
        
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        keyPair = kpg.genKeyPair();  
        userSignatureType = Signature.getInstance("MD5WithRSA");
        root = new Block(0, null, "0");
        root.hash = "0";
        root.level = 0;
        blockChain = new HashMap<>();
        blockChain.put(root.hash, root);
        commulator = new ArrayList<>();
        cache = new HashMap<>();
        cache.put("0", root);
    }

    @Override
    public String toString() {
        String res = "user ID: " + id;
        for (int i = 0; i < neighbours.size(); i++) {
            res += '\n' + neighbours.get(i).id;
        }
        res += '\n' + "Number of transactions received --> " + commulator.size();
        res += '\n' + "Number of Blocks received --> " + cache.size();
        res += '\n' + "Ledger size --> " + blockChain.size();
        res += '\n';
        return res;
    }

    private void newEvent(Transaction t, User transationOwner) throws UnsupportedEncodingException, SignatureException, InvalidKeyException, NoSuchAlgorithmException {
        if(!commulator.contains(t))
        {
            commulator.add(t);
            NotifyNeighbours(t, transationOwner);
            if(commulator.size()>4){
//                System.out.println(Long.valueOf(nonce.hashCode()) + "    ********************************************");

                // Create a block and add it to the main block chain + change root
                Block newBlock = new Block(blockChain.size(), commulator, root.hash);
                String hash = Mine(newBlock);
                newBlock.hash = hash;
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

            // check if cache has a branch longer than the ledger
            if(newBlock.level > blockChain.size() - 1){
                root = newBlock;
                blockChain = new HashMap<>();
                Block cur = root;
                while(cur != null){
                    blockChain.put(cur.hash, cur);
                    cur = cur.previousBlock;
                }
            }

            // randomly choose a block of maximum levels
            if(newBlock.level == blockChain.size() - 1 && !newBlock.equals(root)){
                Random random = new Random();
                if(random.nextBoolean()){
                    root = newBlock;
                    blockChain = new HashMap<>();
                    Block cur = root;
                    while(cur != null){
                        blockChain.put(cur.hash, cur);
                        cur = cur.previousBlock;
                    }
                }
            }
        }
    }


    public void NotifyNeighbours(Transaction t, User transactionOwner) throws UnsupportedEncodingException, SignatureException, InvalidKeyException, NoSuchAlgorithmException {
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
        int randomNeighboursCount = random.nextInt((neighbours.size()-1)/2) + (neighbours.size()/3);
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

    public String Mine(Block block) throws NoSuchAlgorithmException{

        String nonce = randomUUID().toString().substring(0,5);
        ArrayList<Transaction> transactions = block.getTransactions();
        int transactionHash = transactions.hashCode();
        String previousHash = root.hash+"";

        String valueToBeHashed = nonce + transactionHash + previousHash;

        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
                sha1.update(valueToBeHashed.getBytes());

        byte byteData[] = sha1.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        while(!(sb.toString().startsWith("00"))){
            nonce = randomUUID().toString().substring(0,5);
            valueToBeHashed = nonce + transactionHash + previousHash;
            sha1.update(valueToBeHashed.getBytes());
            byte byteData2[] = sha1.digest();
            StringBuffer sb2 = new StringBuffer();
            for (int i = 0; i < byteData2.length; i++) {
                sb2.append(Integer.toString((byteData2[i] & 0xff) + 0x100, 16).substring(1));
            }
            sb = sb2;
//            System.out.println("Hash: " + sb2.toString());

        }

        return sb.toString();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
        User userTest = new User(101);
        User userTest2 = new User(102);
        User miningUser = new User(100);
        Transaction t1 = new Transaction(userTest);
        Transaction t2 = new Transaction(userTest2);
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(t1);
        transactions.add(t2);

        Block b = new Block(120,transactions,"" + 18216378);
        miningUser.Mine(b);
        System.out.println(">>>>> " + miningUser.Mine(b));
    }
}