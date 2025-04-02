package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.csv.model.person.Address;
import school.faang.user_service.csv.model.person.ContactInfo;
import school.faang.user_service.csv.model.person.Person;
import school.faang.user_service.csv.model.person.PreviousEducation;
import school.faang.user_service.csv.model.person.Status;
import school.faang.user_service.csv.model.person.Education;
import school.faang.user_service.dto.csv.PersonCsvDto;

import java.util.Collections;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonCsvMapper {

    @Mapping(target = "contactInfo", expression = "java(mapContactInfo(dto))")
    @Mapping(target = "education", expression = "java(mapEducation(dto))")
    @Mapping(target = "status", expression = "java(mapStatus(dto))")
    Person toPerson(PersonCsvDto dto);

    default ContactInfo mapContactInfo(PersonCsvDto dto) {
        Address address = new Address();
        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(dto.getEmail());
        contactInfo.setPhone(dto.getPhone());
        contactInfo.setAddress(address);
        return contactInfo;
    }

    default Education mapEducation(PersonCsvDto dto) {
        Education edu = new Education();
        edu.setFaculty(dto.getFaculty());
        edu.setYearOfStudy(dto.getYearOfStudy());
        edu.setMajor(dto.getMajor());
        edu.setGpa(dto.getGpa());
        return edu;
    }

    default Status mapStatus(PersonCsvDto dto) {
        PreviousEducation prev = new PreviousEducation();
        prev.setDegree(dto.getDegree());
        prev.setInstitution(dto.getInstitution());
        prev.setCompletionYear(dto.getCompletionYear());

        Status status = new Status();
        status.setAdmissionDate(dto.getAdmissionDate());
        status.setGraduationDate(dto.getGraduationDate());
        status.setPreviousEducation(Collections.singletonList(prev));
        return status;
    }
}
