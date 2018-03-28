import java.util.ArrayList;

import static java.util.UUID.randomUUID;

public class User {
    ArrayList<User> neighbours = new ArrayList<>();
    String id;

    public User(){
        id = randomUUID().toString();
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
}
