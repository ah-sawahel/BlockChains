import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.UUID;
import java.util.Base64;


public class Transaction {

	String id;
	byte[] transactionSignature; 

	public Transaction(User user) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException{
		this.id = UUID.randomUUID().toString();   
    	this.setSignature(user);
	} 
	
	
    private void setSignature(User user) throws SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException{
    	byte[] data = user.getId().getBytes("UTF8");
         
        user.getUserSignatureType().initSign(user.getKeyPair().getPrivate());
        user.getUserSignatureType().update(data); 
        
        byte[] signatureBytes = user.getUserSignatureType().sign();  
        this.transactionSignature = signatureBytes;
        System.out.println("Singature:" + Base64.getEncoder().encodeToString(signatureBytes));

    
    } 
    
    public void verifySignature(User user, byte[] signature) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, UnsupportedEncodingException{
    	user.getUserSignatureType().initVerify(user.getKeyPair().getPublic()); 
    	byte[] data = user.getId().getBytes("UTF8");
        user.getUserSignatureType().update(data);
        System.out.println(user.getUserSignatureType().verify(signature));
    }
    
    public byte[] getTransactionSignature() {
		return transactionSignature;
	}


	public static void main(String[] args) throws UnsupportedEncodingException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        User userTest = new User(101);
        User userTest2 = new User(102);
    	Transaction transTest = new Transaction(userTest); 
    	transTest.verifySignature(userTest,transTest.getTransactionSignature());
	}
}
