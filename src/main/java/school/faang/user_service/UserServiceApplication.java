package school.faang.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.RecommendationMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
@Component
@RequiredArgsConstructor
@EnableFeignClients("school.faang.user_service.client")
public class UserServiceApplication {
    private final RecommendationMapper mapper;

    public static void main(String[] args) {
//        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(UserServiceApplication.class);
//        UserServiceApplication usa = applicationContext.getBean(UserServiceApplication.class);
//        List<Long> list = List.of(1L, 2L, 3L);
//        RecommendationDto recommendationDto = new RecommendationDto(1L, 1L, 1L, "jopa",
//                list, LocalDateTime.now() );
//        Recommendation recommendation = usa.mapper.toEntity(recommendationDto);
//        log.info(recommendation.toString());
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}