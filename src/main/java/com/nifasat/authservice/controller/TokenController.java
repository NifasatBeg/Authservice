package com.nifasat.authservice.controller;

import com.nifasat.authservice.model.AuthRequestDto;
import com.nifasat.authservice.model.JwtResponseDto;
import com.nifasat.authservice.model.RefreshToken;
import com.nifasat.authservice.model.RefreshTokenRequestDto;
import com.nifasat.authservice.service.JwtService;
import com.nifasat.authservice.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class TokenController
{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("auth/v1/login")
    public ResponseEntity AuthenticateAndGetToken(@RequestBody AuthRequestDto authRequestDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if(authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
            return new ResponseEntity<>(JwtResponseDto.builder()
                    .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
                    .token(refreshToken.getToken())
                    .build(), HttpStatus.OK);

        } else {
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("auth/v1/refreshToken")
    public ResponseEntity<JwtResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDTO){
        JwtResponseDto responseDto =  refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.GenerateToken(userInfo.getUsername());
                    JwtResponseDto jwtResponseDto = JwtResponseDto.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken()).build();
                    return jwtResponseDto;
                }).orElseThrow(() ->new RuntimeException("Refresh Token is not in DB..!!"));
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("auth/v1/ping")
    public ResponseEntity<String> ping(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()){
            return new ResponseEntity<>("Authorized", HttpStatus.OK);
        }
        return new ResponseEntity<>("Unauthorizeed", HttpStatus.UNAUTHORIZED);
    }

}