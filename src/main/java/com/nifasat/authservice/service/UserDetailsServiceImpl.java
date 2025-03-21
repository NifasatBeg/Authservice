package com.nifasat.authservice.service;

import com.nifasat.authservice.Producers.UserInfoProducers;
import com.nifasat.authservice.model.CustomUserDetails;
import com.nifasat.authservice.model.UserInfo;
import com.nifasat.authservice.model.UserInfoDto;
import com.nifasat.authservice.model.UserInfoEvent;
import com.nifasat.authservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService
{

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserInfoProducers userInfoProducers;


    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {

        log.debug("Entering in loadUserByUsername Method...");
        UserInfo user = userRepository.findByUsername(username);
        if(user == null){
            log.error("Username not found: " + username);
            throw new UsernameNotFoundException("could not found user..!!");
        }
        log.info("User Authenticated Successfully..!!!");
        return new CustomUserDetails(user);
    }

    public UserInfo checkIfUserAlreadyExist(UserInfoDto userInfoDto){
        return userRepository.findByUsername(userInfoDto.getUsername());
    }

    public String getUserId(String username){
        return Optional.ofNullable(userRepository.findByUsername(username)).map(user -> user.getUserId()).orElse(null);
    }

    public Boolean signupUser(UserInfoDto userInfoDto){
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if(Objects.nonNull(checkIfUserAlreadyExist(userInfoDto))){
            return false;
        }
        String userId = UUID.randomUUID().toString();
        UserInfo user = new UserInfo(userId, userInfoDto.getUsername(), userInfoDto.getPassword(), null, new HashSet<>());
        userRepository.save(user);
        userInfoProducers.sendEventToKafka(UserInfoEvent
                .builder()
                .userId(userId)
                .firstName(userInfoDto.getFirstName())
                .lastName(userInfoDto.getLastName())
                .phoneNumber(userInfoDto.getPhoneNumber())
                .email(userInfoDto.getEmail())
                .build());
        return true;
    }
}