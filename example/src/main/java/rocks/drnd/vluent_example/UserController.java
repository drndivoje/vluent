package rocks.drnd.vluent_example;

import io.github.drndivoje.vluent.api.VluentSpringValidator;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Spring validator example: UserValidator runs for every @Valid User argument, next to the
    // Bean Validation annotations. See AddressController for the JSR-303 (@ValidateBeanWith) example.
    @InitBinder("user")
    void initBinder(WebDataBinder binder) {
        binder.addValidators(new VluentSpringValidator<>(User.class, new UserValidator()));
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
       
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}