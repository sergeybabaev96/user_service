package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.ContactDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.mapper.ContactMapper;
import school.faang.user_service.repository.contact.ContactRepository;
import school.faang.user_service.service.user.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserService userService;
    private final ContactMapper contactMapper;

    public ContactDto createContact(ContactDto contactDto) {
        log.info("Creating new contact: {}", contactDto);

        Contact contact = contactMapper.toContact(contactDto);
        User user = userService.getUserById(contactDto.userId());
        contact.setUser(user);

        contact = contactRepository.save(contact);
        return contactMapper.toContactDto(contact);
    }

    public ContactDto getByContactNumber(String contactNumber) {
        log.info("Getting contact by contactNumber: {}", contactNumber);
        Contact contact = contactRepository.findByContact(contactNumber);
        return contactMapper.toContactDto(contact);
    }

    public void deleteByContactNumber(String contactNumber) {
        log.info("Deleting contact by contactNumber: {}", contactNumber);
        Contact contact = contactRepository.findByContact(contactNumber);
        contactRepository.delete(contact);
    }
}
