package school.faang.user_service.service;

import org.mockito.Mock;
import school.faang.user_service.mapper.CareerMapper;
import school.faang.user_service.repository.CareerRepository;
import school.faang.user_service.repository.UserRepository;

public class CareerServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CareerRepository careerRepository;

    @Mock
    private CareerMapper careerMapper;

    }
