import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.ArrayList;

import static java.util.UUID.randomUUID;

public class User {
    ArrayList<User> neighbours = new ArrayList<>();
    String id;
    KeyPair keyPair; 
	Signature userSignatureType;  
	
    public User() throws NoSuchAlgorithmException{
        id = randomUUID().toString(); 
        
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        keyPair = kpg.genKeyPair();  
        userSignatureType = Signature.getInstance("MD5WithRSA");
    }

    @Override
    public String toString() {
        String res = "user ID: " + id;
        for (int i = 0; i < neighbours.size(); i++) {
            res += '\n' + neighbours.get(i).id;
        }
        res += '\n';
        return res;
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
	
	
    
    
}