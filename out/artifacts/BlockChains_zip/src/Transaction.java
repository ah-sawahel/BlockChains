import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.UUID;
import java.util.Base64;


public class Transaction {

	String id;
	byte[] transactionSignature;
	PublicKey ownerPublicKey;
	String userId;

	public Transaction(User user) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException{
		this.id = UUID.randomUUID().toString();   
    	this.setSignature(user);
    	ownerPublicKey = user.keyPair.getPublic();
    	userId = user.id;
	} 
	
	
    private void setSignature(User user) throws SignatureException, UnsupportedEncodingException, InvalidKeyException{
    	byte[] data = user.getId().getBytes("UTF8");
         
        user.getUserSignatureType().initSign(user.getKeyPair().getPrivate());
        user.getUserSignatureType().update(data); 
        
        byte[] signatureBytes = user.getUserSignatureType().sign();  
        this.transactionSignature = signatureBytes;
//        System.out.println("Singature:" + Base64.getEncoder().encodeToString(signatureBytes));
    }
    
    public boolean verifySignature(User user) throws SignatureException, InvalidKeyException, UnsupportedEncodingException{
    	user.getUserSignatureType().initVerify(user.getKeyPair().getPublic()); 
    	byte[] data = user.getId().getBytes("UTF8");
        user.getUserSignatureType().update(data);
        return user.getUserSignatureType().verify(this.transactionSignature);
    }
    
    public byte[] getTransactionSignature() {
		return transactionSignature;
	}


	public static void main(String[] args) throws UnsupportedEncodingException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        User userTest = new User(101);
        User userTest2 = new User(102);
    	Transaction transTest = new Transaction(userTest);
    	userTest.id = "44";
		System.out.println(transTest.verifySignature(userTest));
	}
}
