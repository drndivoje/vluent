package rocks.drnd.vluent_example;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final Map<String, Address> addressesByUserName = new ConcurrentHashMap<>();

    private final UserService userService;

    public AddressService(UserService userService) {
        this.userService = userService;
    }

    public Optional<Address> addAddress(String userName, Address address) {
        if (userService.findByName(userName).isEmpty()) {
            return Optional.empty();
        }
        addressesByUserName.put(userName, address);
        return Optional.of(address);
    }
}
