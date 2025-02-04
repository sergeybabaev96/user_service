package school.faang.user_service.data;

import school.faang.user_service.entity.User;

public enum UserData {
    USER1(1L, "username", "email", "pass", "active", CountryData.RUSSIA);
    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final String active;
    private final CountryData country;

    UserData(Long id, String username, String email, String password, String active, CountryData country) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.active = active;
        this.country = country;
    }

    public User getUser() {
        return User.builder()
                .id(id)
                .username("username")
                .email("email")
                .password("password")
                .active(true)
                .country(country.toCountry())
                .build();
    }

    public Long getId() {
        return id;
    }
}
