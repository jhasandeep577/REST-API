package com.contactmanager.api.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.contactmanager.api.repositories.DepartmentRepo;
import com.contactmanager.api.repositories.UserFileRepo;
import com.contactmanager.api.repositories.UserRepository;
import com.contactmanager.api.repositories.UserTypeRepo;
import com.contactmanager.api.dto.ExcelData;
import com.contactmanager.api.dto.ExcelDataResponseDto;
import com.contactmanager.api.dto.PageResponse;
import com.contactmanager.api.dto.UserDto;
import com.contactmanager.api.exceptionhandler.customexceptions.NoContentFoundException;
import com.contactmanager.api.exceptionhandler.customexceptions.ResourceAlreadyExist;
import com.contactmanager.api.exceptionhandler.customexceptions.ResourceNotFoundException;
import com.contactmanager.api.models.User;
import com.contactmanager.api.models.UserFile;
import com.contactmanager.api.models.UserType;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserFileRepo userFileRepo;
    @Autowired
    DepartmentRepo departmentRepo;
    @Autowired
    UserTypeRepo userTypeRepo;
    @Autowired
    DtoUtility dtoUtility;
    @Autowired
    UserRepository User_repo ;
    @Autowired
    Map<String,String> message;
    @Autowired 
    FileService fileService;
    @Autowired
    RepoUtility repoUtil;
    public ResponseEntity<PageResponse> fetchAllUser(int PageNumber,int PageSize,String SortBy,String sortDir,String filter,String path){
    
        Sort sort=(sortDir.equalsIgnoreCase("asc"))?Sort.by(SortBy).ascending():Sort.by(SortBy).descending();
        Pageable PageData=PageRequest.of(PageNumber, PageSize,sort);
        Page<User> userPage=this.User_repo.findByFilter(PageData, filter);
        if(userPage.isEmpty())
        throw new NoContentFoundException();
        List<User> users=userPage.getContent();
        List<UserDto> dbUsers=users.stream().map((user)->{
            UserDto dtouser=this.dtoUtility.toUserDto(user);
            return dtouser;
        }).collect(Collectors.toList());

       PageResponse response=new PageResponse();
           response.setContent(dbUsers);
           response.setLastPage(userPage.isLast());
           response.setPageNumber(userPage.getNumber());
           response.setPageSize(userPage.getSize());
           response.setTotalElements(userPage.getTotalElements());
           response.setTotalPages(userPage.getTotalPages());
        return ResponseEntity.ok().body(response);
    }
    public ResponseEntity<UserDto> fetchUser(int id,String path){
        
         User dbUser=this.User_repo.findById(id).orElseThrow(()->{
            return new ResourceNotFoundException(id);
        }); 
        UserDto dtouser=this.dtoUtility.toUserDto(dbUser);
         return ResponseEntity.status(HttpStatus.OK).body(dtouser);
    }
    public ResponseEntity<UserDto> addUser(UserDto user,MultipartFile file,String path) throws IOException{
         User dbuser=this.dtoUtility.toUser(user);
         if(this.User_repo.findByMobile(dbuser.getMobile()).isPresent()){
            throw new ResourceAlreadyExist(dbuser.getMobile(), "Mobile Number");
         }else if(this.User_repo.findByEmail(dbuser.getEmail()).isPresent()){
            throw new ResourceAlreadyExist(dbuser.getEmail(), "Email");  
         }else{
         dbuser.setImage(uploadfile(file, path));
                    // ^ Saving file on server and getting its randomly generated name
         User NewDbUser=this.User_repo.save(dbuser);
         UserDto dtouser=this.dtoUtility.toUserDto(NewDbUser);
         return ResponseEntity.status(HttpStatus.CREATED).body(dtouser);
         }
    }
    @Transactional
    public ResponseEntity<UserDto> updateUser(UserDto user,int id,String path,MultipartFile file)throws IOException{    
        User oldUser=this.User_repo.findById(id).orElseThrow(()->{
            return new ResourceNotFoundException(id);
        });
        //  if(oldUser.getImage()!=null){
        //     fileService.deleteImage(path,oldUser.getImage());
        //  }
        oldUser.getUsertypes().stream().forEach((usertype)->{
             this.userTypeRepo.delete(usertype);
        });
        this.departmentRepo.delete(oldUser.getDepartment());
        User dbuser=this.dtoUtility.toUser(user);        
        dbuser.setID(oldUser.getID());
        this.User_repo.delete(oldUser);
        dbuser.setImage(uploadfile(file, path));
       // String ImageName= this.fileService.fileSave(file, path); 
                  // ^ Saving file and getting its randomly generated name
       // dbuser.setImage(ImageName);
        if(this.User_repo.findByMobile(dbuser.getMobile()).isPresent()){
            throw new ResourceAlreadyExist(dbuser.getMobile(), "Mobile Number");
         }else if(this.User_repo.findByEmail(dbuser.getEmail()).isPresent()){
            throw new ResourceAlreadyExist(dbuser.getEmail(), "Email");  
         }else{
        User newdbUser= this.User_repo.save(dbuser);
        UserDto userdto=this.dtoUtility.toUserDto(newdbUser);
        return ResponseEntity.status(HttpStatus.OK).body(userdto);
    }
    }
    public ResponseEntity<Map<String,String>> deleteUser(String path,int id){
        User dbUser=User_repo.findById(id).orElseThrow(()->{
            return new ResourceNotFoundException(id);
        });
        // if(dbUser.getImage()!=null){
        //     this.fileService.deleteImage(path, dbUser.getImage());
        // }
        User_repo.delete(dbUser);
        message.put("message","User Data has been deleted");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(message);
    }
    @Override
    public ResponseEntity<?> validateExcelData(MultipartFile file) {
        if(fileService.checkType(file)){
             List<UserDto> excelusers=fileService.getList(file);
             List<ExcelDataResponseDto> validUserData= new ArrayList<ExcelDataResponseDto>();
             List<ExcelDataResponseDto> InValidUserData= new ArrayList<ExcelDataResponseDto>();
             excelusers.stream().forEach((exceluser)->{
                if(dtoUtility.validateUserDtoBool(exceluser)){
                    ExcelDataResponseDto dto=dtoUtility.toExcelDataResponseDto(exceluser);
                    dto.setStatus(true);
                    validUserData.add(dto);
                }else{
                    Set<String>failedConstraint =dtoUtility.validateUserDto(exceluser);
                    ExcelDataResponseDto dto=dtoUtility.toExcelDataResponseDto(exceluser);
                    dto.setStatus(false);
                    dto.setMessages(failedConstraint);
                    InValidUserData.add(dto);
                }
             });
             ExcelData userdata=new ExcelData();
             if(!(validUserData.isEmpty())){
             userdata.setCorrectData(validUserData);}
             if(!(InValidUserData.isEmpty())){
             userdata.setIncorrectData(InValidUserData);
            }
             return ResponseEntity.status(HttpStatus.CONFLICT).body(userdata);
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Upload only excel file"));
        }
    }
    @Transactional
    public ResponseEntity<?> saveExcelData(List<ExcelDataResponseDto> dto){
         List<User> users=dto.stream().map((dtouser)->{
           return dtoUtility.toUser(dtouser);
         }).collect(Collectors.toList());
         List<User> dbValidUser = users.stream().map((user)->{
          this.User_repo.findByEmail(user.getEmail()).ifPresent((emailUser)->{
                  user.setID(emailUser.getID());
                  user.getDepartment().setId(emailUser.getDepartment().getId());
                  emailUser.getUsertypes().stream().forEach((usertype)->{
                     this.userTypeRepo.deleteById(usertype.getId());
                 });
           });
           return user;
         }).collect(Collectors.toList());
         List<User> dbusers=this.User_repo.saveAll(dbValidUser);
         List<UserDto> dbUserResponse=dbusers.stream().map((dbuser)->{
            return dtoUtility.toUserDto(dbuser);
         }).collect(Collectors.toList());
         return ResponseEntity.status(HttpStatus.CREATED).body(dbUserResponse);
    }
    @Override
    public UserFile uploadfile(MultipartFile file,String path) {
        try{
       String fileName= StringUtils.cleanPath(file.getOriginalFilename());
             if(fileName.contains("..")){
                throw new Exception("Invalid file Name");
             }
             UserFile userfile=new UserFile();
             userfile.setOriginalFileName(fileName);
             userfile.setGeneratedFileName(fileService.fileSave(file, path));
             userfile.setFileType(file.getContentType());
             return userfile;
             //return this.userFileRepo.save(userfile);
            }catch(Exception exception){
              exception.printStackTrace();
            }
            return new UserFile();
           
    }
    @Override
    public ResponseEntity<Resource> getFile(String fileName,String path) throws IOException {
        UserFile file= this.userFileRepo.findByGeneratedFileName(fileName).orElseThrow(()->{
            throw new ResourceNotFoundException();
        });
         return ResponseEntity
         .ok()
         .contentType(MediaType.parseMediaType(file.getFileType()))
         .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getOriginalFileName()+"\"")
         .body(new ByteArrayResource(fileService.getImage(path, file.getGeneratedFileName())));
    }
    public ResponseEntity<Resource> getExcelSheet(String keyword) throws Exception{
       
        List<User> users=this.repoUtil.SearchUser(keyword);
        String fileName="UserData.xlsx";
        ByteArrayInputStream actualData=fileService.getExcelData(users);
        InputStreamResource file=new InputStreamResource(actualData);
        return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,  "attachment; filename=\""+fileName+"\"")
        .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
        .body(file);
    } 
    public ResponseEntity<List<UserDto>> getUsersFromUserType(String name){
        List<UserType> Dbusers=this.userTypeRepo.findByUserTypeName(name);
        List<UserDto> Users=new ArrayList<UserDto>();
        Dbusers.stream().forEach((userType)->{
            if(!(Users.contains(dtoUtility.toUserDto(userType.getUser()))))
              Users.add(dtoUtility.toUserDto(userType.getUser()));
        });
        if(Users.isEmpty()){
         throw new NoContentFoundException();
        }else{
            return ResponseEntity.ok().body(Users);
        }
    }
}
