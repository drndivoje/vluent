package rocks.drnd.vluent_example.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import rocks.drnd.vluent_example.dto.User;

@Service
public class UserService {

  private final Map<String, User> users = new ConcurrentHashMap<>();

  public User createUser(User user) {
    String id = String.join("", UUID.randomUUID().toString().split("-"));
    User newUser = new User(id, user.firstName(), user.lastName(), user.birthDate());
    users.put(id, newUser);
    return newUser;
  }

  public Optional<User> findById(String id) {
    return Optional.ofNullable(users.get(id));
  }

  public List<User> getUsers() {
    return users.values().stream().toList();
  }
}
