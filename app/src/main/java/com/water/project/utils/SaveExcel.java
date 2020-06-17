package com.water.project.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SaveExcel {


    public static CellStyle setFount(Workbook workBook){
        // 创建字体
        Font font = workBook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 11);// 字体大小
        // 设置字体的颜色
//        font.setColor(Font.COLOR_RED);
        CellStyle style = workBook.createCellStyle();
        // 垂直居中
//        style.setVerticalAlignment(VerticalAlignment.CENTER);
//        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);
        return style;
    }


    /**
     * 将数据存储在excel中
     * @param fileName
     * @param red3
     */
    public static void saveDataByExcel(String fileName,String red3){
        Workbook workBook = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            //创建xls文件
            String filePath=FileUtils.createFile2(fileName+".xls");
            // 读取指定路径下的excel
            fis = new FileInputStream(filePath);
            // 加载到workBook
            workBook = new HSSFWorkbook(fis);
            // 获取第一个sheet页
            org.apache.poi.ss.usermodel.Sheet sheetAt = workBook.getSheetAt(0);
            //创建边框及字体颜色等
            CellStyle style = setFount(workBook);
//            style.setBorderBottom(BorderStyle.THIN);
//            style.setBorderLeft(BorderStyle.THIN);

            // 获取第1行 第1列的单位格
            Row row = sheetAt.getRow(0);
            Cell cell = row.getCell(0);
            cell.setCellValue("采集时间");
            cell.setCellStyle(style);

            // 获取第2行 第1列的数据
            row = sheetAt.getRow(1);
            cell = row.getCell(0);
            cell.setCellValue("水位埋深(m)");
            cell.setCellStyle(style);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
