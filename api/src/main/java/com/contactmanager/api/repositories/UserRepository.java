package com.contactmanager.api.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.contactmanager.api.models.User;

@Repository
public interface UserRepository extends JpaRepository<User,Integer>{
    @Query("Select u from User u where u .name LIKE %:Key%")
    public Page<User> findByFilter(Pageable pageable,@Param("Key")String Key);
    public Optional<User> findByMobile(long mobile);
    public Optional<User> findByEmail(String email);
}
