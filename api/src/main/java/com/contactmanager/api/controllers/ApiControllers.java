package com.contactmanager.api.controllers;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import com.contactmanager.api.services.DtoUtility;
import com.contactmanager.api.services.FileService;
import com.contactmanager.api.services.UserService;
import com.contactmanager.api.dto.ExcelDataResponseDto;
import com.contactmanager.api.dto.PageResponse;
import com.contactmanager.api.dto.UserDto;

@RestController
@RequestMapping("/ContactManager")
@Tag(
    name = "User Controller"
    ,description = "To perform operation on User Data"
)
public class ApiControllers {
     @Value("${project.image}")
      String path;
    @Autowired UserService User_service;
    @Autowired FileService file_service;
    @Autowired DtoUtility utility;

    @Operation(
        summary = "GET Operation on User",
        description = "It is used to get all the users"
    )
    @GetMapping(path = "/fetchalluser")
    public ResponseEntity<PageResponse> getAllUser(
        @RequestParam(value = "pageNumber",defaultValue = "1",required = false) int pageNumber,
        @RequestParam(value = "pageSize",defaultValue = "1",required = false) int pageSize,
        @RequestParam(value = "sortBy",defaultValue = "name",required = false) String sortBy,
        @RequestParam(value = "sortDir",defaultValue = "asc",required = false)String dir,
        @RequestParam(value = "filter",defaultValue = "",required = false)String filter
    ){
        return User_service.fetchAllUser(pageNumber, pageSize, sortBy,dir,filter,path);
    }

    @Operation(
        summary = "GET Operation on User",
        description = "It is used to get user using userID"
    )
    @GetMapping(path = "/fetchuser/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable int id){
       return User_service.fetchUser(id,path);
    }

    @Operation(
        summary = "POST Operation on User",
        description = "It is used to get create user"
    )
    @PostMapping(path = "createuser",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> createUser(@Valid @RequestPart("UserDto") UserDto user,
    @RequestParam("image") MultipartFile file
    )throws IOException{
        return User_service.addUser(user,file,path);
    }
    @Operation(
        summary = "PATCH Operation on User",
        description = "It is used to get update user details using userID"
    )
    @PatchMapping(path="updateuser/{id}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestPart("UserDto") UserDto user,
    @RequestParam("image") MultipartFile file,@PathVariable int id)throws IOException{
       return User_service.updateUser(user,id,path,file);
    }
    @Operation(
        summary = "Delete Operation on User",
        description = "It is used to delete user using userID"
    )
    @DeleteMapping(path="deleteuser/{id}")
    public ResponseEntity<Map<String,String>> deleteUser(@PathVariable int id){
          return User_service.deleteUser(path,id);
    }
    // @Operation(
    //     summary = "Get Operation on User's Image",
    //     description = "It is used to fetch user's Image using Image Name"
    // )
    // @GetMapping(path = "getImage/{Image}")
    // public String downloadImage(@PathVariable("Image") String Image)throws FileNotFoundException,IOException{
    //     // String bytecode =this.file_service.getImage(path,Image);
    //     // return bytecode;
    //     return ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/").path("a11406c6-b263-4e79-87f0-087681ba7646.png").toUriString();
    // } 
    // @GetMapping(path = "/download")
    // public ResponseEntity<Resource> downloadfile() throws IOException{
    //     return ResponseEntity.ok().contentType(MediaType.parseMediaType("/png"))
    //     .header(HttpHeaders.CONTENT_DISPOSITION, "dewnload")
    //     .body(new ByteArrayResource(file_service.getImage(path,"a11406c6-b263-4e79-87f0-087681ba7646.png")));
    // }
    @Operation(
        summary = "Get Operation : Validation on Excel sheet Data",
        description = "It is used to fetch Valid Excel Data also Invalid Data with Invalidtion error messages"
    )
    @PostMapping(path = "validateExcelData",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> validateExcelData(@RequestParam("excel") MultipartFile exceldata){
        return this.User_service.validateExcelData(exceldata);
    }@Operation(
        summary = "Post Operation : Saving or updating bulk data",
        description = "Saving Valid data in bulk into database this api is for only validated data"
    )
    @PostMapping(path = "saveExcelData")
    public ResponseEntity<?> saveExcelData(@RequestBody List<ExcelDataResponseDto> listExcelData){
       return this.User_service.saveExcelData(listExcelData);
    }
    @Operation(
        summary = "API to dwonload an Image"
    )
    @GetMapping(path = "/download/{fileName}")
    public ResponseEntity<Resource> downloadfile(@PathVariable String fileName) throws IOException{
         return this.User_service.getFile(fileName,path);
    }
    @GetMapping(path="downloadExcelSheet")
    public ResponseEntity<Resource> downloadExcelSheet(
        @RequestParam(value = "keyword",required = false) String keyword) throws Exception{
        return this.User_service.getExcelSheet(keyword);
    }
    @GetMapping(path="getUsersFromUserType")
    public ResponseEntity<List<UserDto>> getUsersFromUserType(
        @RequestParam(value = "typeName",required = false,defaultValue = "user") String userTypeName ){
        return this.User_service.getUsersFromUserType(userTypeName);
    }
}
