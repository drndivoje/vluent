package rocks.drnd.vluent_example.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rocks.drnd.vluent_example.service.AddressService;
import rocks.drnd.vluent_example.dto.Address;

@RestController
@RequestMapping("/users/{name}/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // No @InitBinder needed: @Valid triggers Bean Validation, which runs @ValidateBeanWith on Address
    @PostMapping
    public ResponseEntity<Address> addAddress(
            @PathVariable String name, @RequestBody @Valid Address address) {
        return addressService
                .addAddress(name, address)
                .map(created -> new ResponseEntity<>(created, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
