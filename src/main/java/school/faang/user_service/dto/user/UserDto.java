package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private boolean active;
    private String aboutMe;
    private String country;
    private String city;
    private Integer experience;
    private String createdAt;
    private String updatedAt;
    private List<Long> followers;
    private List<Long> followees;
    private List<Long> ownedEvents;
    private List<Long> mentees;
    private List<Long> mentors;
    private List<Long> receivedMentorshipRequests;
    private List<Long> sentMentorshipRequests;
    private List<Long> sentGoalInvitations;
    private List<Long> receivedGoalInvitations;
    private List<Long> setGoals;
    private List<Long> goals;
    private List<Long> skills;
    private List<Long> participatedEvents;
    private List<Long> recommendationsGiven;
    private List<Long> recommendationsReceived;
    private List<Long> contacts;
    private List<Long> ratings;
    private Long contactPreference;
    private Long premium;
    private Long tariff;
}
