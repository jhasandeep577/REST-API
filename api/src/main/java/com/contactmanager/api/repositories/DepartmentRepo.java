package com.contactmanager.api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.contactmanager.api.models.Department;


@Repository
public interface DepartmentRepo extends JpaRepository<Department,Integer>{
      public List<Department> findByDepartmentNameLike(String dep_name);
   @Query(value="Select * from Department where department_code  LIKE %:Key%",nativeQuery=true)
      public List<Department> findByDepartmentCodeLike(String Key);
}
