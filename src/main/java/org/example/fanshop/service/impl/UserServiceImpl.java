package org.example.fanshop.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fanshop.dto.user.UserAuthDto;
import org.example.fanshop.dto.user.UserRegistrationDto;
import org.example.fanshop.dto.user.UserResponseDto;
import org.example.fanshop.entity.User;
import org.example.fanshop.entity.UserImage;
import org.example.fanshop.exceptions.AuthenticationException;
import org.example.fanshop.exceptions.DifferentPassword;
import org.example.fanshop.exceptions.MappingException;
import org.example.fanshop.exceptions.UserAlreadyExistsException;
import org.example.fanshop.repository.UserImageRepository;
import org.example.fanshop.repository.UserRepository;
import org.example.fanshop.security.jwt.JwtService;
import org.example.fanshop.service.api.UserService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserImageRepository userImageRepository;


    //Вход для User и установка в куки
    @Override
    public UserResponseDto login(UserAuthDto userCredentialDto, HttpServletResponse response){
        log.info("Поиск пользователя по номеру телефона");
        String number = userCredentialDto.getPhoneNumber();
        String password = userCredentialDto.getPassword();
        Optional<User> userOpt = userRepository.findByPhoneNumber(number);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            log.debug("Пользователь с телефоном" + user.getPhoneNumber() + "найден");
            if (passwordEncoder.matches(password, user.getPassword())) {
                log.info("Пароли совпадают: генерируем JWT");
                String jwtToken = jwtService.generateJwtToken(user.getPhoneNumber(), user.getRoles());

                Cookie jwtCookie = new Cookie("jwt-auth-token", jwtToken);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(180 * 24 * 60 * 60);

                response.addCookie(jwtCookie);
                UserResponseDto dto = toDto(user);
                log.info("JWT добавлена в куки");
                return dto;
            }else {
                log.warn("Неверный пароль для пользователя {}", number);
                throw new DifferentPassword();
            }
        }
        log.error("Пользователь с номером телефона: {} не найден", number);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
    }

    //Регистрация нового пользователя через номер телефона или почту
    @Transactional
    public UserRegistrationDto addUser(UserRegistrationDto userDto) {
        String phoneNumber = userDto.getPhoneNumber();
        if (userRepository.findByPhoneNumber(userDto.getPhoneNumber()).isPresent()) {
            log.error("Пользователь с номером телефона: {} зарегистрирован!", phoneNumber);
            throw new UserAlreadyExistsException("Пользователь с номером телефона:" + phoneNumber + "зарегистрирован!");
        }
        User entity = RegistrationDtotoUser(userDto);
        if (entity == null) {
            String errorMessage = "Не получилось преобразовать RegistrationDto в User";
            log.error(errorMessage);
            throw new MappingException(errorMessage);
        }
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())){
            log.error("Пароли не совпадают!");
            throw new DifferentPassword();
        }
        entity.setRoles(userDto.getRoles());
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        User currentUser = userRepository.save(entity);
        setDefaultImage(currentUser);

        return toUserRegistrationDto(currentUser);
    }

    public byte[] getDefaultProfileImageBytes(){
        ClassPathResource resource = new ClassPathResource("images/default_profile.jpg");

        try (InputStream inputStream = resource.getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public void setDefaultImage(User user){
        //Сохранение в БД изображения пользователя по дефолту
        UserImage userImage = new UserImage();
        userImage.setProfileImage(getDefaultProfileImageBytes());
        userImage.setUser(user);
        userImageRepository.save(userImage);
        log.info("Дефолтная картинка пользователю установлена");
    }

    public User findByPhoneNumber(String number){
        return userRepository.findByPhoneNumber(number).orElseThrow(
                () ->{
                    String errorMessage = "Пользователь с номером телефона:  " + number + "не найден";
                    log.error(errorMessage, new UsernameNotFoundException(errorMessage));
                    return new UsernameNotFoundException("Пользователь не найден");
                }
        );
    }

    public User getCurrentUser(Object principal) {
        if (principal instanceof UserDetails userDetails) {
            String phoneNumber = userDetails.getUsername();
            return findByPhoneNumber(phoneNumber);
        }
        else {
            log.error("Пользователь не авторизован не найден JWT токен");
            throw new AuthenticationException("Пользователь не авторизован");
        }
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    @Transactional
    public UserResponseDto updateUser(UserResponseDto userResponseDto){
        return toDto(userRepository.save(toUser(userResponseDto)));
    }


    public User findById(Long userId){
        return userRepository.findById(userId).orElseThrow(
                () ->{
                    String errorMessage = "Пользователь с id:  " + userId + "не найден";
                    log.error(errorMessage, new UsernameNotFoundException(errorMessage));
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
                }
        );
    }

    public User RegistrationDtotoUser(UserRegistrationDto userDto){
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setRoles(userDto.getRoles());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        return  user;
    }

    public UserRegistrationDto toUserRegistrationDto(User user){
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRoles(user.getRoles());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public UserResponseDto toDto(User user){
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setRoles(user.getRoles());
        userResponseDto.setId(user.getId());
        userResponseDto.setPhoneNumber(user.getPhoneNumber());
        userResponseDto.setId(user.getId());
        userResponseDto.setPassword(user.getPassword());
        userResponseDto.setEmail(user.getEmail());
        return userResponseDto;
    }

    public User toUser(UserResponseDto userResponseDto){
        User user = new User();
        user.setFirstName(userResponseDto.getFirstName());
        user.setLastName(userResponseDto.getLastName());
        user.setRoles(userResponseDto.getRoles());
        user.setId(userResponseDto.getId());
        user.setPhoneNumber(userResponseDto.getPhoneNumber());
        user.setPassword(userResponseDto.getPassword());
        user.setEmail(userResponseDto.getEmail());
        return  user;
    }

}
