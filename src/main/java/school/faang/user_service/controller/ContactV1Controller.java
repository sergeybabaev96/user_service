package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.ContactDto;
import school.faang.user_service.service.ContactService;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactV1Controller {

    private final ContactService contactService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactDto createContact(@Valid @RequestBody ContactDto contactDto) {
        return contactService.createContact(contactDto);
    }

    @GetMapping("/{contact_number}")
    public ContactDto getContact(@PathVariable("contact_number") String contactNumber) {
        return contactService.getByContactNumber(contactNumber);
    }

    @DeleteMapping("/{contact_number}")
    public void deleteContact(@PathVariable("contact_number") String contactNumber) {
        contactService.deleteByContactNumber(contactNumber);
    }

}
