package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.ContactDto;
import school.faang.user_service.entity.contact.Contact;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactMapper {

    @Mapping(target = "user", ignore = true)
    Contact toContact(ContactDto contactDto);

    @Mapping(source = "user.id", target = "userId")
    ContactDto toContactDto(Contact contact);

    List<ContactDto> toContactDtoList(List<Contact> contacts);
}
