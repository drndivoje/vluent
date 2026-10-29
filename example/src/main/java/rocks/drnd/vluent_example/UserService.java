package rocks.drnd.vluent_example;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final List<User> users = new CopyOnWriteArrayList<>();

    public User createUser(User user) {
        users.add(user);
        return user;
    }

    public List<User> getUsers() {
        return List.copyOf(users);
    }
}
