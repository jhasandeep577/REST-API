package com.contactmanager.api.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.contactmanager.api.dto.DepartmentDto;
import com.contactmanager.api.dto.UserDto;
import com.contactmanager.api.dto.UserTypeResDto;
import com.contactmanager.api.models.User;


@Service
public class FileService {
    
    public String fileSave(MultipartFile file,String path) throws IOException{
        String fileName=file.getOriginalFilename();
        file.getContentType();
        String fileExtension=fileName.substring(fileName.lastIndexOf('.'));
        String id=UUID.randomUUID().toString();
        String newFileName=id+fileExtension;
        String filePath=path+newFileName;
        File directory=new File(path);
        if(!directory.exists()){
           directory.mkdir();
        }
           Files.copy(file.getInputStream(),Paths.get(filePath));
        return newFileName;
    }
    public byte[] getImage(String path,String imageName)throws IOException{
        String fullpath=path+imageName;
        InputStream stream=new FileInputStream(fullpath);
        byte [] bytes=stream.readAllBytes();
        //String encoded=Base64.getEncoder().encodeToString(bytes);
        stream.close();
        //  String uri=ServletUriComponentsBuilder.fromCurrentContextPath().path("/download").toUriString();
        return bytes;
    }
    
    public boolean deleteImage(String path,String fileName){
        Path filePath=Paths.get(path,fileName);
        try{
            Files.delete(filePath);
            return true;
        }catch(IOException exception){
            exception.printStackTrace();
            return false;
        }
    }
    public boolean checkType(MultipartFile file){
        if(file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
            return true;
        }else{
            return false;
        }
    }
    public List<UserDto> getList(MultipartFile file){
        List<UserDto> listUserDto=new ArrayList<UserDto>();
        try{
         @SuppressWarnings("resource")
                   // Converting Excel Data into UserDto
         XSSFWorkbook workbook=new XSSFWorkbook(file.getInputStream());
         XSSFSheet sheet=workbook.getSheet( workbook.getSheetName(0));
         int rowNumber=0;
         int rowid=sheet.getLastRowNum();
         while(rowNumber<=rowid){
            Row row=sheet.getRow(rowNumber);
            if(rowNumber==0||row==null||rowNumber==1||rowNumber==2){
                rowNumber++;
                continue;
            }
             UserDto userdto=new UserDto();
             DepartmentDto dep_dto=new DepartmentDto();
             Set<UserTypeResDto> listTypeDto=new HashSet<UserTypeResDto>();
             int lastcellindex=row.getLastCellNum();
            for(int cid=0;cid<lastcellindex;cid++){
                switch (cid) {
                        case 0:
                            userdto.setName((row.getCell(cid)==null)?null:row.getCell(cid).getStringCellValue());
                            break;
                        case 1:
                            userdto.setEmail((row.getCell(cid)==null)?null:row.getCell(cid).getStringCellValue());
                            break;
                        case 2:
                            if(!(row.getCell(cid)==null))
                             userdto.setMobile((row.getCell(cid).getCellType()==CellType.NUMERIC)?Math.round(row.getCell(cid).getNumericCellValue()):0);
                            break;
                        case 3:
                            dep_dto.setDepartmentName((row.getCell(cid)==null)?null:row.getCell(cid).getStringCellValue());
                            break;
                        case 4:
                             if(!(row.getCell(cid)==null))
                               dep_dto.setDepartmentCode((row.getCell(cid).getCellType()==CellType.NUMERIC)?(int)Math.round(row.getCell(cid).getNumericCellValue()):0);
                            break;
                        case 5:
                        if(row.getCell(cid)!=null){
                            String type=row.getCell(cid).getStringCellValue();
                            String tyeplist[]=type.split(",");
                            for(int i=0;i<tyeplist.length;i++){
                                UserTypeResDto userType=new UserTypeResDto();
                                userType.setUserTypeName(tyeplist[i]);
                                listTypeDto.add(userType);
                            }}
                            break;
                       }
            }
            userdto.setDepartment(dep_dto);
            userdto.setUsertypes(listTypeDto);
            listUserDto.add(userdto);
            rowNumber++;
         }
        }catch(NumberFormatException|IOException exception){
             exception.printStackTrace();
        }
        return listUserDto;
    }
    public static String[] RowNames={
        "Name","Email","MobileNumber","Department_Name","Department_ID","UserType"
    };
    public static String[] RowExample={
        "john doe","example123@gmail.com","9372838234","Example","837382","example"
    };

     public ByteArrayInputStream getExcelData(List<User> userList) throws Exception{
        // Creating Excel Sheet
        Workbook workbook= new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try{
          // Creating a Sheet
          Sheet sheet=workbook.createSheet("Users");
          // Creating font for desired color
          Font fontRed=workbook.createFont();
          fontRed.setColor(IndexedColors.RED.getIndex());
          fontRed.setBold(true);
          CellStyle styleRed=workbook.createCellStyle();
          styleRed.setFont(fontRed);
          // Creating another font for Opacity
          Font OpacityBlack=workbook.createFont();
          OpacityBlack.setColor(IndexedColors.BLACK.getIndex());
          OpacityBlack.setItalic(true);
          OpacityBlack.setBold(true);
          CellStyle styleOpacity=workbook.createCellStyle();
          styleOpacity.setFont(OpacityBlack);

          Row row=sheet.createRow(0);
           for(int i=0;i<RowNames.length;i++){
              Cell cell=row.createCell(i);
              cell.setCellValue("Mandatory");
              cell.setCellStyle(styleRed);   
          }
          Row row2=sheet.createRow(1);
           for(int i=0;i<RowNames.length;i++){
              Cell cell=row2.createCell(i);
              cell.setCellValue(RowExample[i]);
              cell.setCellStyle(styleOpacity);
          }
          Row row3=sheet.createRow(2);
          for(int i=0;i<RowNames.length;i++){
              Cell cell=row3.createCell(i);
              cell.setCellValue(RowNames[i]);
              cell.setCellStyle(styleOpacity);
          }
          int RowIndex=3;
          for(User exceldata: userList){
            Row dataRow=sheet.createRow(RowIndex);
            RowIndex++;
             dataRow.createCell(0).setCellValue(exceldata.getName());
             dataRow.createCell(1).setCellValue(exceldata.getEmail());
             dataRow.createCell(2).setCellValue(exceldata.getMobile());
             dataRow.createCell(3).setCellValue(exceldata.getDepartment().getDepartmentName());
             dataRow.createCell(4).setCellValue(exceldata.getDepartment().getDepartmentCode());
           
           StringBuffer usertypeString=new StringBuffer("");
           exceldata.getUsertypes().stream().forEach((userType)->{
               usertypeString.append(userType.getUserTypeName()+",");
           });
            dataRow.createCell(5).setCellValue(usertypeString.toString());
          }
          workbook.write(out);
          return new ByteArrayInputStream(out.toByteArray());
        }catch(Exception exception){
            exception.printStackTrace();
            throw new Exception("Could Not Convert User Data to Excel Sheet");
        }finally{
            workbook.close();
            out.close();
        }
    }
}
