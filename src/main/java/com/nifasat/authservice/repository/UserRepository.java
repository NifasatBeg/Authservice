package com.nifasat.authservice.repository;


import com.nifasat.authservice.model.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserInfo, Long>
{
    public UserInfo findByUsername(String username);
    public Optional<UserInfo> findByUserId(String user_id);
}