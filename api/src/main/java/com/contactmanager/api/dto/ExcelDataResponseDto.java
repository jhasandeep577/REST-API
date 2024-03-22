package com.contactmanager.api.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelDataResponseDto {
        private String name;
        private DepartmentDto department;
        private Set<UserTypeResDto> usertypes;
        private long mobile;
        private String email;
        private boolean status;
        private Set<String> messages;
}
