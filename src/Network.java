import java.util.HashMap;
import java.util.Random;

public class Network {
    HashMap<Integer, User> users = new HashMap<>();

    public Network(int numberOfUsers){
        for (int i = 0; i < numberOfUsers; i++) {
            User newUser = new User();
            users.put(i, newUser);
        }
    }

    public void connectUsers(){
        int networkSize = users.size();
        int numberOfNeighbours = networkSize / 10;
        numberOfNeighbours = numberOfNeighbours == 0? 2 : numberOfNeighbours;
        User currentUser;
        Random random = new Random();
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
            if(users.get(newNeighbour).neighbours.size()>=numberOfNeighbours)
                accepted=false;
            if(users.get(currentUser).neighbours.contains(users.get(newNeighbour)))
                accepted = false;
        }
        return users.get(newNeighbour);
    }

    public static void main(String[] args) {
        Network network = new Network(5);
        network.connectUsers();
        for (int i = 0; i < network.users.size(); i++) {
            System.out.println(network.users.get(i).toString());
        }
    }
}
