package school.faang.user_service.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillReadDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {
    @Getter
    private static final int MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillMapper skillMapper;
    private final EventMapper eventMapper;

    public SkillReadDto create(SkillCreateDto skillCreateDto) {
        if (skillRepository.existsByTitle(skillCreateDto.getTitle())) {
            throw new BusinessException(
                    String.format("Умение с названием %s уже существует", skillCreateDto.getTitle())
            );
        }
        Skill skill = skillMapper.toEntity(skillCreateDto);
        skill = skillRepository.save(skill);
        return skillMapper.toSkillDto(skill);
    }

    public List<SkillReadDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(skillMapper::toSkillDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillRepository.findSkillsOfferedToUser(userId)
                .stream()
                .map(skill -> {
                    SkillCandidateDto dto = skillMapper.toSkillCandidateDto(skill);
                    List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skill.getId(), userId);
                    dto.setOffersAmount(skillOffers.size());
                    return dto;
                })
                .toList();
    }

    public SkillReadDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = getSkillById(skillId);
        User user = getUserById(userId);
        Optional<Skill> existingSkill = skillRepository.findUserSkill(skillId, userId);
        if (existingSkill.isPresent()) {
            throw new BusinessException(
                    String.format("Присвоение умения отклонено, так как умение %s уже существует у пользователя %s",
                            skill.getTitle(), user.getUsername())
            );
        }
        List<SkillOffer> skillOffers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (skillOffers.size() < MIN_SKILL_OFFERS) {
            throw new BusinessException(
                    String.format("Недостаточно предложений для присвоения умения %s. Необходимо %d вместо %d",
                            skill.getTitle(), MIN_SKILL_OFFERS, skillOffers.size())
            );
        }
        log.info("Умение {} присвоено пользователю {}, так как получено {} из {} предложений",
                skill.getTitle(), user.getUsername(), skillOffers.size(), MIN_SKILL_OFFERS);
        skillRepository.assignSkillToUser(skillId, userId);

        List<UserSkillGuarantee> userSkillGuarantees = skillOffers.stream()
                .map(skillOffer -> {
                    User guarantorUser = skillOffer.getRecommendation().getAuthor();
                    return new UserSkillGuarantee(null, user, skill, guarantorUser);
                })
                .toList();

        skill.setGuarantees(userSkillGuarantees);
        log.info("Обновлен список гарантов умения {} пользователя {}", skill.getTitle(), user.getUsername());
        skillRepository.save(skill);

        return skillMapper.toSkillDto(skill);
    }

    private Skill getSkillById(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Умение с ID %d не найдено", skillId)));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format("Пользователь с ID %d не найден", userId)));
    }

    public List<Skill> getAllSkills(List<Long> relatedSkills) {
        return skillRepository.findAllById(relatedSkills);
    }

    public List<Long> getSkillsIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }
}
