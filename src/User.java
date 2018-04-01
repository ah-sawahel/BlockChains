import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.util.UUID.randomUUID;

public class User {
    ArrayList<User> neighbours = new ArrayList<>();
    String id;

    public User(int id){
        this.id = "" + id;//randomUUID().toString();
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

    public void newEvent(){
        // handle new comming event
    }

    public void NotifyNeighbours(){
        Random random = new Random();
        int randomNeighboursCount = random.nextInt((neighbours.size()-1)/2) + (neighbours.size()/4);
        Collections.shuffle(neighbours);
        for (int i = 0; i < randomNeighboursCount; i++) {
            neighbours.get(i).newEvent();
        }
    }
}
