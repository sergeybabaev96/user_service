package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.ContactDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.mapper.ContactMapper;
import school.faang.user_service.repository.contact.ContactRepository;
import school.faang.user_service.service.user.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

    @InjectMocks
    private ContactService contactService;

    @Mock
    private ContactRepository contactRepository;
    @Mock
    private UserService userService;
    @Spy
    private ContactMapper contactMapper = Mappers.getMapper(ContactMapper.class);

    private ContactDto contactDto;
    private User user;
    private Contact contact;

    @BeforeEach
    public void setUp() {
        contactDto = ContactDto.builder()
                .id(1L)
                .userId(1L)
                .contact("12312")
                .type(ContactType.TELEGRAM)
                .build();
        contact = contactMapper.toContact(contactDto);
        user = User.builder()
                .id(1L)
                .build();
    }

    @Test
    void createContactTest() {
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(contactRepository.save(any())).thenReturn(contact);
        when(contactMapper.toContactDto(any())).thenReturn(contactDto);

        contactService.createContact(contactDto);

        verify(userService).getUserById(user.getId());
        verify(contactRepository).save(any());
        verify(contactMapper).toContactDto(any());
    }

    @Test
    void getByContactNumberTest() {
        when(contactRepository.findByContact(any())).thenReturn(contact);
        when(contactMapper.toContactDto(any())).thenReturn(contactDto);

        contactService.getByContactNumber(anyString());

        verify(contactRepository).findByContact(any());
        verify(contactMapper).toContactDto(any());

    }

    @Test
    void deleteByContactNumberTest() {
        contactService.deleteByContactNumber(anyString());

        verify(contactRepository).delete(any());
    }


}
