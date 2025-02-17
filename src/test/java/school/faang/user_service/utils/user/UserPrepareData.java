package school.faang.user_service.utils.user;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegisterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

public class UserPrepareData {

    public static UserRegisterDto getUserRegisterDto() {
        return UserRegisterDto.builder()
                .username("username")
                .password("password")
                .email("email")
                .countryId(1L)
                .build();
    }

    public static User getUser() {
        return User.builder()
                .id(1L)
                .username("username")
                .password("password")
                .email("email")
                .country(Country.builder()
                        .id(1L)
                        .build())
                .phone("phone")
                .build();
    }

    public static Country getCountry() {
        return Country.builder()
                .id(1L)
                .build();
    }

    public static UserDto getUserDto() {
        return new UserDto(1L, "username", "email", "phone");
    }
}
