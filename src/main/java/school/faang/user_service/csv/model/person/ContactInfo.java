package school.faang.user_service.csv.model.person;

import lombok.Data;

@Data
public class ContactInfo {
    private String email;
    private String phone;
    private Address address;
}
