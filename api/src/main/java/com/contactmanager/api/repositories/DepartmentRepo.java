package com.contactmanager.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.contactmanager.api.models.Department;

@Repository
public interface DepartmentRepo extends JpaRepository<Department,Integer>{
    
}
