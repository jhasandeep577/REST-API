package com.contactmanager.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.contactmanager.api.models.UserType;

@Repository
public interface UserTypeRepo extends JpaRepository<UserType,Integer>{
    
}
