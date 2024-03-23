package com.contactmanager.api.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.contactmanager.api.models.User;
import com.contactmanager.api.repositories.DepartmentRepo;
import com.contactmanager.api.repositories.UserRepository;
import com.contactmanager.api.repositories.UserTypeRepo;

@Component
public class RepoUtility {
    @Autowired
    UserRepository User_repo;
    @Autowired
    DepartmentRepo departmentRepo;
    @Autowired
    UserTypeRepo userTypeRepo;
    public List<User> SearchUser(String keyword)throws Exception{
        final List<User>users=new ArrayList<User>();
        if(keyword==null||keyword==""){
            this.User_repo.findAll().stream().forEach((user)->{
                users.add(user);
            });
        }else{
            this.User_repo.findByNameLike("%"+keyword+"%").stream().forEach((user)->{
                users.add(user);
            });
            this.User_repo.findByEmailLike("%"+keyword+"%").stream().forEach((user)->{
                if(!(users.contains(user)))
                   users.add(user);
            });
            this.User_repo.findByMobileLike("%"+keyword+"%").stream().forEach((user)->{
                if(!(users.contains(user)))
                users.add(user);
            });
            this.departmentRepo.findByDepartmentCodeLike("%"+keyword+"%").stream().forEach((dep)->{
                if(!(users.contains(dep.getUser())))
                users.add(dep.getUser());
            });
            this.departmentRepo.findByDepartmentNameLike("%"+keyword+"%").stream().forEach((dep)->{
                if(!(users.contains(dep.getUser())))
                users.add(dep.getUser());
            });
            this.userTypeRepo.findByUserTypeName("%"+keyword+"%").stream().forEach((usertype)->{
                if(!(users.contains(usertype.getUser())))
                users.add(usertype.getUser());
            });
        }
        if(users.isEmpty()){
            throw new Exception("No Data Found");
        }else{
            return users;
        }
    }
}
