package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.ResponseSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    @Value("${config.value.min.skill.offers}")
    private int minSkillOffers;

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseSkillDto create(CreateSkillDto skill) {
        if (skillRepository.existsByTitle(skill.title())) {
            throw new DataValidationException("The skill = " + skill.title() + " already exists!");
        }

        Skill skillEntity = skillMapper.toSkillEntity(skill);
        skillEntity = skillRepository.save(skillEntity);

        return skillMapper.toSkillDto(skillEntity);
    }

    @Override
    public List<ResponseSkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId).stream()
                .map(skillMapper::toSkillDto)
                .toList();
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        Map<Skill, Long> skillsOffers = skillRepository.findSkillsOfferedToUser(userId).stream()
                .collect(Collectors.groupingBy(x -> x, HashMap::new, Collectors.counting()));

        return skillsOffers.entrySet().stream()
                .map(skillLongEntry -> new SkillCandidateDto(skillMapper.toSkillDto(skillLongEntry.getKey()),
                        skillLongEntry.getValue()))
                .toList();
    }

    @Override
    public ResponseSkillDto acquireSkillFromOffers(long skillId, long userId) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill with id = " + skillId + " not found"));

        if (skill.getUsers().stream().anyMatch(user -> user.getId().equals(userId))) {
            return skillMapper.toSkillDto(skill);
        }

        User userToAddGuarantee = userRepository.findById(userId).orElse(null);
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);

        List<UserSkillGuarantee> userSkillGuarantees = skill.getGuarantees();

        if (skillOffers.size() >= minSkillOffers) {
            skillRepository.assignSkillToUser(skillId, userId);

            skillOffers.forEach(skillOffer -> {
                UserSkillGuarantee guarantee = addGuaranteeToUser(skillOffer);
                userSkillGuarantees.add(guarantee);
            });

            skill.setGuarantees(userSkillGuarantees);
            skillRepository.save(skill);
        }
        return skillMapper.toSkillDto(skill);
    }

    private UserSkillGuarantee addGuaranteeToUser(SkillOffer skillOffer) {
        Long userId = skillOffer.getRecommendation().getReceiver().getId();
        Skill skill = skillOffer.getSkill();
        User userToAddGuarantee = userRepository.findById(userId).orElse(null);
        User guarantorUser = userRepository.findById(skillOffer.getRecommendation().getAuthor().getId())
                .orElse(null);

        UserSkillGuarantee guarantee = new UserSkillGuarantee();
        guarantee.setUser(userToAddGuarantee);
        guarantee.setSkill(skill);
        guarantee.setGuarantor(guarantorUser);

        return guarantee;
    }
}
