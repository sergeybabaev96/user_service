package school.faang.user_service.data;

import school.faang.user_service.entity.Country;

public enum CountryData {
    RUSSIA(1L, "Russia");

    private final Long id;
    private final String title;

    CountryData(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Country toCountry() {
        return Country.builder()
                .id(id)
                .title(title)
                .build();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
