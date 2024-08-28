package com.mldream.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelUtils {

    public static List<String[]> XLSHandle(MultipartFile file) throws IOException {
        List<String[]> result = new ArrayList<>();
        Workbook workbook = new HSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            String[] rowData = new String[row.getLastCellNum()];
            for (Cell cell : row) {
                rowData[cell.getColumnIndex()] = cell.toString();
            }
            result.add(rowData);
        }
        workbook.close();
        return result;
    }

    public static List<String[]> XLSXHandle(MultipartFile file) throws IOException {
        List<String[]> result = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            String[] rowData = new String[row.getLastCellNum()];
            for (Cell cell : row) {
                rowData[cell.getColumnIndex()] = cell.toString();
            }
            result.add(rowData);
        }
        workbook.close();
        return result;
    }
}
