package school.faang.user_service.utils.user;

import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

public class UserPrepareData {

    public static UserRegisterDto createUserRegisterDto() {
        return UserRegisterDto.builder()
                .username("username")
                .password("password")
                .email("email")
                .countryId(1L)
                .build();
    }

    public static User createUser() {
        return User.builder()
                .username("username")
                .password("password")
                .email("email")
                .country(Country.builder()
                        .id(1L)
                        .build())
                .build();
    }

    public static Country getCountry() {
        return Country.builder()
                .id(1L)
                .build();
    }
}
