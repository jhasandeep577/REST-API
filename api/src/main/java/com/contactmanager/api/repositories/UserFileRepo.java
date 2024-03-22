package com.contactmanager.api.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.contactmanager.api.models.UserFile;
@Repository
public interface UserFileRepo extends JpaRepository<UserFile,Integer>{
 public Optional<UserFile> findByGeneratedFileName(String fileName);
    
}
