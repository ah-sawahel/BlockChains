import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Network {
    HashMap<Integer, User> users = new HashMap<>();
    static ArrayList<Block> ledger = new ArrayList<>();

    public Network(int numberOfUsers){
        for (int i = 0; i < numberOfUsers; i++) {
            User newUser = null;
            try {
                newUser = new User(i);
                users.put(i, newUser);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    public void connectUsers(){
        int networkSize = users.size();
        int numberOfNeighbours = networkSize / 10;
        numberOfNeighbours = numberOfNeighbours == 0? 2 : numberOfNeighbours;
        User currentUser;

        for (int i = 0; i < networkSize; i++) {
            currentUser = users.get(i);
            while(currentUser.neighbours.size() < numberOfNeighbours){
                User currentNeighbour = findNewNeighbour(i);
                currentUser.neighbours.add(currentNeighbour);
                currentNeighbour.neighbours.add(currentUser);
            }
        }
    }

    private User findNewNeighbour(int currentUser){
        int networkSize = users.size();
        Random random = new Random();
        boolean accepted = false;
        int numberOfNeighbours = networkSize / 10;
        numberOfNeighbours = numberOfNeighbours == 0? 2 : numberOfNeighbours;
        int newNeighbour = 0;
        while(!accepted){
            newNeighbour = random.nextInt(networkSize);
            if(newNeighbour!=currentUser)
                accepted=true;
            if(users.get(currentUser).neighbours.contains(users.get(newNeighbour)))
                accepted = false;
        }
        return users.get(newNeighbour);
    }

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // TEST IT :)
        int n = 100;
        Random random = new Random();
        Network network = new Network(n);
        network.connectUsers();
        for (int i = 0; i < 10; i++) {
            network.users.get(random.nextInt(n)).createTransaction();
        }

        for (int i = 0; i < network.users.size(); i++) {
            System.out.println(network.users.get(i).toString());
        }
        // BEST REGARDS ^_^
    }
}
