package school.faang.user_service.dto.elastic_search;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Country;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "users")
public class UserDocument {

    @Id
    private long id;
    private String username;
    private String email;
    private String aboutMe;

    private String country;
    private String city;
    private String experience;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<GoalDto> setGoals;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<GoalDto> goals;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<SkillDto> skills;
}
