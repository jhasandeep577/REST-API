package com.contactmanager.api.services;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.contactmanager.api.dto.ExcelDataResponseDto;
import com.contactmanager.api.dto.PageResponse;
import com.contactmanager.api.dto.UserDto;
import com.contactmanager.api.models.UserFile;

public interface UserService {
    public ResponseEntity<PageResponse> fetchAllUser(int PageNumber,int PageSize,String sortBy,String sortDir,String filter,String path);
    public ResponseEntity<UserDto> fetchUser(int id,String path);
    public ResponseEntity<UserDto> addUser(UserDto user,MultipartFile file,String path)throws IOException;
    public ResponseEntity<UserDto> updateUser(UserDto user,int id,String path,MultipartFile file)throws IOException;
    public ResponseEntity<Map<String,String>> deleteUser(String path,int id);
    public ResponseEntity<?> validateExcelData(MultipartFile file);
    public ResponseEntity<?> saveExcelData(List<ExcelDataResponseDto> dto);
    public UserFile uploadfile(MultipartFile file,String path);
    public ResponseEntity<Resource> getFile(String fileName,String path) throws IOException;
    public ResponseEntity<Resource> getExcelSheet(String keyword) throws Exception;
}
