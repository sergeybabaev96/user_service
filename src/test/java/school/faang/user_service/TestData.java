package school.faang.user_service;

import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recomendation.RequestFilterDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventRequestDto;
import school.faang.user_service.dto.recomendation.RecommendationRequestRcvDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.service.event.filter.MaxAttendeesLessThanFilter;
import school.faang.user_service.service.event.filter.StartDateLaterThanFilter;
import school.faang.user_service.service.event.filter.TitleContainsFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class TestData {
    public static Stream<MentorshipRequest> getMentorshipRequestsStream() {
        return getListOfRequests().stream();
    }

    public static List<MentorshipRequest> getListOfRequests() {
        User userBob = User.builder()
                .id(1L)
                .username("Bob")
                .build();
        User userAlice = User.builder()
                .id(2L)
                .username("Alice")
                .build();
        User userJohn = User.builder()
                .id(3L)
                .username("John")
                .build();
        User userJack = User.builder()
                .id(4L)
                .username("Jack")
                .build();

        MentorshipRequest request1 = MentorshipRequest.builder()
                .id(1L)
                .requester(userBob)
                .status(RequestStatus.ACCEPTED)
                .description("description")
                .receiver(userAlice)
                .build();
        MentorshipRequest request2 = MentorshipRequest.builder()
                .id(2L)
                .requester(userAlice)
                .status(RequestStatus.PENDING)
                .description("description")
                .receiver(userJohn)
                .build();
        MentorshipRequest request3 = MentorshipRequest.builder()
                .id(3L)
                .requester(userAlice)
                .status(RequestStatus.REJECTED)
                .description("description")
                .receiver(userJack)
                .build();
        MentorshipRequest request4 = MentorshipRequest.builder()
                .id(4L)
                .requester(userAlice)
                .status(RequestStatus.ACCEPTED)
                .description("specification")
                .receiver(userJack)
                .build();
        MentorshipRequest request5 = MentorshipRequest.builder()
                .id(5L)
                .requester(userAlice)
                .status(RequestStatus.ACCEPTED)
                .description("description")
                .receiver(userJack)
                .build();

        return List.of(request1, request2, request3, request4, request5);
    }

    public static List<User> getUsers() {
        List<User> users = new ArrayList<>();

        Map<String, Country> countries = getCountries();
        Map<String, Skill> skills = getSkills();
        Map<String, Contact> contacts = getContacts();

        List<Skill> skillSet1 = new ArrayList<>(List.of(skills.get("Skill 1")));
        List<Skill> skillSet23 = new ArrayList<>(List.of(skills.get("Skill 2"), skills.get("Skill 3")));
        List<Skill> skillSet3 = new ArrayList<>(List.of(skills.get("Skill 3")));

        List<Contact> contacts1 = new ArrayList<>(List.of(contacts.get("Contact 1")));
        List<Contact> contacts23 = new ArrayList<>(List.of(contacts.get("Contact 2"), contacts.get("Contact 3")));
        List<Contact> contacts3 = new ArrayList<>(List.of(contacts.get("Contact 3")));

        User user1 = User.builder()
                .city("Moscow")
                .active(true)
                .id(1L)
                .goals(new ArrayList<>())
                .contacts(contacts1)
                .aboutMe("I'm Misha")
                .email("misha@mail.ru")
                .username("misha")
                .phone("123")
                .country(countries.get("Russia"))
                .skills(skillSet1)
                .experience(10)
                .build();


        User user2 = User.builder()
                .city("Piter")
                .active(false)
                .id(2L)
                .goals(new ArrayList<>())
                .contacts(contacts23)
                .aboutMe("I'm Masha")
                .email("masha@mail.ru")
                .username("masha")
                .phone("456")
                .country(countries.get("USA"))
                .skills(skillSet23)
                .experience(20)
                .build();

        User user3 = User.builder()
                .city("Kazan")
                .active(true)
                .id(3L)
                .goals(new ArrayList<>())
                .contacts(contacts3)
                .aboutMe("I'm Kesha")
                .email("kesha@mail.ru")
                .username("kesha")
                .phone("789")
                .country(countries.get("China"))
                .skills(skillSet3)
                .experience(30)
                .build();

        users.add(user3);
        users.add(user1);
        users.add(user2);

        users.sort((u1, u2) -> (int) (u1.getId() - u2.getId()));

        return users;
    }

    public static Map<String, Country> getCountries() {
        Map<String, Country> countryMap = new HashMap<>();
        Country countryRussia = Country.builder().id(1).title("Russia").build();
        Country countryUsa = Country.builder().id(2).title("USA").build();
        Country countryChina = Country.builder().id(3).title("China").build();
        countryMap.put("Russia", countryRussia);
        countryMap.put("USA", countryUsa);
        countryMap.put("China", countryChina);
        return countryMap;
    }

    public static Map<String, Skill> getSkills() {
        Map<String, Skill> skillMap = new HashMap<>();
        Skill skill1 = Skill.builder().id(1).title("Skill 1").build();
        Skill skill2 = Skill.builder().id(2).title("Skill 2").build();
        Skill skill3 = Skill.builder().id(3).title("Skill 3").build();
        skillMap.put("Skill 1", skill1);
        skillMap.put("Skill 2", skill2);
        skillMap.put("Skill 3", skill3);
        return skillMap;
    }

    public static Map<String, Contact> getContacts() {
        Map<String, Contact> contactMap = new HashMap<>();
        Contact contact1 = Contact.builder().contact("Contact 1").build();
        Contact contact2 = Contact.builder().contact("Contact 2").build();
        Contact contact3 = Contact.builder().contact("Contact 3").build();
        contactMap.put("Contact 1", contact1);
        contactMap.put("Contact 2", contact2);
        contactMap.put("Contact 3", contact3);
        return contactMap;
    }

    public static SkillRequest createSkillRequest(long id, RecommendationRequest recommendationRequest, Skill skill) {
        return SkillRequest.builder()
                .id(id)
                .request(recommendationRequest)
                .skill(skill)
                .build();
    }

    public static RecommendationRequestRcvDto createRequestRcvDto(User requester,
                                                                  User receiver,
                                                                  RecommendationRequest request,
                                                                  List<Long> skillIdsList) {
        return RecommendationRequestRcvDto.builder()
                .message(request.getMessage())
                .skillIds(skillIdsList)
                .requesterId(requester.getId())
                .receiverId(receiver.getId())
                .build();
    }

    public static RecommendationRequest createRequest(Long id, User requester, User receiver, RequestStatus status) {
        return RecommendationRequest.builder()
                .id(id)
                .requester(requester)
                .receiver(receiver)
                .status(status)
                .createdAt(LocalDateTime.now())
                .message("Please confirm my skills")
                .build();
    }

    public static RejectionDto createRejectDto(String reason) {
        return RejectionDto.builder()
                .reason(reason)
                .build();
    }

    public static RequestFilterDto createFilterDto(RequestStatus status, Long requesterId, Long receiverId) {
        return RequestFilterDto.builder()
                .status(status)
                .requesterId(requesterId)
                .receiverId(receiverId)
                .build();
    }

    public static Event createEvent(Long id, String title, String date, int maxAttendees) {
        return Event.builder()
                .id(id)
                .title(title)
                .startDate(LocalDateTime.parse(date))
                .maxAttendees(maxAttendees)
                .build();
    }

    public static EventRequestDto createEventRequestDto(String title, String date, Long id) {
        return EventRequestDto.builder()
                .title(title)
                .startDate(LocalDateTime.parse(date))
                .ownerId(id)
                .build();
    }

    public static EventRequestDto createEventRequestDto(String title, Long id, List<Long> ids) {
        return EventRequestDto.builder()
                .title(title)
                .ownerId(id)
                .relatedSkillsIds(ids)
                .build();
    }

    public static EventFilterDto createEventFilterDto(String charSequence, String date, int attendees) {
        return EventFilterDto.builder()
                .titleContains(charSequence)
                .startDateLaterThan(LocalDateTime.parse(date))
                .maxAttendeesLessThan(attendees)
                .build();
    }

    public static List<EventFilter> createFilters() {
        TitleContainsFilter titleContainsFilter = new TitleContainsFilter();
        StartDateLaterThanFilter startDateLaterThanFilter = new StartDateLaterThanFilter();
        MaxAttendeesLessThanFilter maxAttendeesLessThanFilter = new MaxAttendeesLessThanFilter();

        return new ArrayList<>(List.of(titleContainsFilter, startDateLaterThanFilter, maxAttendeesLessThanFilter));
    }

    public static User createUser(Long id, List<Skill> skills) {
        return User.builder()
                .id(id)
                .skills(skills)
                .build();
    }
}
