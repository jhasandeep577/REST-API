package com.contactmanager.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.contactmanager.api.models.UserType;

@Repository
public interface UserTypeRepo extends JpaRepository<UserType,Integer>{
    public List<UserType> findByUserTypeNameLike(String userTypeName);
    public List<UserType> findByUserTypeName(String userTypeName);
}
