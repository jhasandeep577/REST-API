package com.contactmanager.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExcelData {
    private List<ExcelDataResponseDto> correctData;
    private List<ExcelDataResponseDto> IncorrectData;
}
