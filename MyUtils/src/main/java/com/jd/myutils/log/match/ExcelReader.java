package com.jd.myutils.log.match;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;


public class ExcelReader {
    public static void writeExcel(String filePath, List<RuleInfo> writeInfo, int cellIdx) throws IOException {
        // 创建临时文件作为中间写入目标
        File tempFile = File.createTempFile("temp", ".xlsx");
        System.err.println(tempFile.getAbsolutePath());
        try(Workbook workbook = WorkbookFactory.create(new File(filePath))) {
            Sheet sheet = workbook.getSheetAt(0);

            for(RuleInfo ruleInfo : writeInfo) {
                Row row = sheet.getRow(ruleInfo.getRowIdx());
                System.err.println(ruleInfo.getRowIdx());

                if(row != null) {
                    // 使用 CREATE_NULL_AS_BLANK 避免覆盖问题
                    Cell cell = row.getCell(cellIdx, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    cell.setCellValue(ruleInfo.getMatchNum());
                }
            }

            // 写入临时文件
            try(FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
            }

            // 用临时文件替换原文件
            Files.copy(tempFile.toPath(), new File(filePath).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            tempFile.delete(); // 清理临时文件
        }
    }

    public static List<RuleInfo> loadMatchRules(String filePath) {
        List<RuleInfo> list = new ArrayList<>();

        try(FileInputStream fis = new FileInputStream(filePath);
            Workbook workbook = WorkbookFactory.create(fis)) {

            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);
            int rowNum = 0;
            // 遍历每一行
            for(Row row : sheet) {
                rowNum++;
                if(rowNum == 1) {
                    continue;
                }

                if(getCellValue(row.getCell(0)).length() == 0) {
                    break;
                }
                // 遍历行中的每个单元格
                RuleInfo ruleInfo = new RuleInfo();
                ruleInfo.setRowIdx(rowNum - 1);
                ruleInfo.setFirstType(getCellValue(row.getCell(0)));
                ruleInfo.setSecondType(getCellValue(row.getCell(1)));
                ruleInfo.setPattern(getCellValue(row.getCell(3)));
                double v = Double.parseDouble(getCellValue(row.getCell(4)));
                ruleInfo.setMatchType(new Double(v).intValue());
                list.add(ruleInfo);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // 根据单元格类型获取值
    private static String getCellValue(Cell cell) {
        CellType cellType = cell.getCellType();
        switch(cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if(DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // 日期类型
                }
                return String.valueOf(cell.getNumericCellValue()); // 数字类型
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula(); // 公式
            default:
                return "";
        }
    }

    public static void main(String[] args) throws IOException {
        String excelRulesfilePath = "/Users/liujianjia/3_data/error-analyse/match-rule.xlsx";
        List<RuleInfo> ruleInfos = ExcelReader.loadMatchRules(excelRulesfilePath);
        for(RuleInfo ruleInfo : ruleInfos) {
            ruleInfo.setMatchNum(100L);
        }
        ExcelReader.writeExcel(excelRulesfilePath, ruleInfos, 6);
    }
}