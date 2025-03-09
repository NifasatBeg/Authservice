package com.nifasat.authservice.repository;

import com.nifasat.authservice.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer>
{
    Optional<RefreshToken> findByToken(String token);

}