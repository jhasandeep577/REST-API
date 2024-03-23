package com.contactmanager.api.services;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.contactmanager.api.dto.DepartmentDto;
import com.contactmanager.api.dto.ExcelDataResponseDto;
import com.contactmanager.api.dto.UserDto;
import com.contactmanager.api.dto.UserTypeDto;
import com.contactmanager.api.dto.UserTypeResDto;
import com.contactmanager.api.models.Department;
import com.contactmanager.api.models.User;
import com.contactmanager.api.models.UserFile;
import com.contactmanager.api.models.UserType;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

@Component
public class DtoUtility {
    private Validator validator;
    public Set<String> validateUserDto(UserDto dto){
        this.validator=Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserDto>> violations=validator.validate(dto);
            Set<String> failedValidations= violations.stream().map((violation)->{
               return violation.getMessage();
            }).collect(Collectors.toSet());
            
        return failedValidations;
    }
    public boolean validateUserDtoBool(UserDto dto){
        this.validator=Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserDto>> violations=validator.validate(dto);
        if(violations.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    public UserType toUserType(UserTypeDto dto,User user){
        UserType usertype= new UserType();
        usertype.setUser(user);
        BeanUtils.copyProperties(dto, usertype);
        return usertype;
    }
    public UserType toUserTypeRes(UserTypeResDto dto,User user){
        UserType usertype= new UserType();
        usertype.setUser(user);
        BeanUtils.copyProperties(dto, usertype);
        return usertype;
    }
    public UserType toUserTypeRes(UserTypeResDto dto){
        UserType usertype= new UserType();
        BeanUtils.copyProperties(dto, usertype);
        return usertype;
    }
    public UserTypeResDto toUserTypeDto(UserType user){
        UserTypeResDto usertypedto= new UserTypeResDto();
        BeanUtils.copyProperties(user, usertypedto);
        return usertypedto;
    }
    public Department toDepartment(DepartmentDto dto){
        Department department= new Department();
        BeanUtils.copyProperties(dto, department);
        return department;
    }
    public DepartmentDto tDepartmentDto(Department department){
        DepartmentDto dto=new DepartmentDto();
        BeanUtils.copyProperties(department, dto);
        return dto;
    }
    public User toUser(UserDto dto){
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setDepartment(this.toDepartment(dto.getDepartment()));
        Set<UserType> usertype=dto.getUsertypes().stream().map((usertypedto)->{
             UserType userr=this.toUserTypeRes(usertypedto,user);
              return userr;
        }).collect(Collectors.toSet());
        user.setUsertypes(usertype);
        return user;
    }
    public UserDto toUserDto(User user){
        UserDto dto=new UserDto();
        BeanUtils.copyProperties(user, dto);
        if(user.getImage()!=null){
           dto.setFileUrl(toUrl(user.getImage()));
        }
        if(user.getDepartment()!=null){
        dto.setDepartment(this.tDepartmentDto(user.getDepartment()));}
        if(user.getUsertypes()!=null){
        Set<UserTypeResDto> usertypedto=user.getUsertypes().stream().map((usertype)->{
           UserTypeResDto dtousertype=this.toUserTypeDto(usertype);
           return dtousertype;
        }).collect(Collectors.toSet());
        dto.setUsertypes(usertypedto);
    }
        return dto;
    }
    public ExcelDataResponseDto toExcelDataResponseDto(UserDto userdto){
        ExcelDataResponseDto exceldto=new ExcelDataResponseDto();
       BeanUtils.copyProperties(userdto,exceldto);
        return exceldto;
    }
    public User toUser(ExcelDataResponseDto dto){
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setDepartment(this.toDepartment(dto.getDepartment()));
        Set<UserType> usertype=dto.getUsertypes().stream().map((usertypedto)->{
             UserType userr=this.toUserTypeRes(usertypedto,user);
              return userr;
        }).collect(Collectors.toSet());
        user.setUsertypes(usertype);
        return user;   
    }
    public String toUrl(UserFile file){
       String url=ServletUriComponentsBuilder.fromCurrentContextPath()
        .path("/ContactManager/download/")
        .path(""+file.getGeneratedFileName())
        .toUriString();
        return url;
    }
}
