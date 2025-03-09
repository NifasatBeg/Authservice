package com.nifasat.authservice.service;

import com.nifasat.authservice.model.RefreshToken;
import com.nifasat.authservice.model.UserInfo;
import com.nifasat.authservice.model.UserInfoDto;
import com.nifasat.authservice.repository.RefreshTokenRepository;
import com.nifasat.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    public RefreshToken createRefreshToken(String username) {
        UserInfo userInfoExtracted = userRepository.findByUsername(username);

        if (userInfoExtracted == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        if (userInfoExtracted.getRefreshToken() != null) {
            RefreshToken oldToken = userInfoExtracted.getRefreshToken();
            userInfoExtracted.setRefreshToken(null);
            refreshTokenRepository.delete(oldToken);
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(new Date(System.currentTimeMillis() + 60 * 10000))
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);

        refreshToken.setUserInfo(userInfoExtracted);
        refreshToken = refreshTokenRepository.save(refreshToken);

//        userInfoExtracted.setRefreshToken(refreshToken);
//        userRepository.save(userInfoExtracted);

        return refreshToken;
    }


    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(new Date())<0){
            UserInfo userInfo = userRepository.findByUserId(token.getUserInfo().getUserId()).get();
            userInfo.setRefreshToken(null);
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

}