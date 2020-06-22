package com.water.project.utils;

import android.content.Context;
import android.view.View;

import com.water.project.view.DialogView;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SaveExcel {

    private static   DialogView dialogView;

    public static CellStyle setFount(Workbook workBook){
        // 创建字体
        Font font = workBook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 11);// 字体大小
        // 设置字体的颜色
//        font.setColor(Font.COLOR_RED);
        CellStyle style = workBook.createCellStyle();
        // 垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);
        return style;
    }


    /**
     * 将数据存储在excel中
     * @param fileName
     */
    public static void saveDataByExcel(Context context,String red2,String fileName, String totalMsg){
        red2=red2.replace("GDIDR","").replace(">OK","");
        final String templateXLS=FileUtils.getSdcardPath()+"zkgd_temp.xls";
        final File file=new File(templateXLS);
        if(!file.isFile()){
            DialogUtils.closeProgress();
            ToastUtil.showLong("xls模板不存在");
            return;
        }
        Workbook workBook = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            String filePath=FileUtils.getSdcardPath()+"zkgd_temp.xls";
            // 读取指定路径下的excel
            fis = new FileInputStream(filePath);
            // 加载到workBook
            workBook = new HSSFWorkbook(fis);
            // 获取第一个sheet页
            org.apache.poi.ss.usermodel.Sheet sheetAt = workBook.getSheetAt(0);
            //创建边框及字体颜色等
            CellStyle style = setFount(workBook);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);

            List<String> arrayMsg=BleUtils.getSendData(totalMsg,123);
            for (int i=0,len=arrayMsg.size();i<len;i++){
                 final String msg=arrayMsg.get(i);
                 if(msg.length()!=123){
                     continue;
                 }


                // 设备id
                Row row = sheetAt.getRow(i+1);
                Cell cell = row.getCell(0);
                if(cell==null){
                    cell=row.createCell(0);
                }
                cell.setCellValue(red2);
                cell.setCellStyle(style);


                // 采集时间
                row = sheetAt.getRow(i+1);
                cell = row.getCell(1);
                if(cell==null){
                    cell=row.createCell(1);
                }
                String time=msg.substring(0, 2)+"年"+msg.substring(2,4)+"月"+msg.substring(4,6)+"日"+msg.substring(6,8)+"时"+msg.substring(8,10)+"分"+msg.substring(10,12)+"秒";
                cell.setCellValue(time);
                cell.setCellStyle(style);


                // 水位埋深(m)
                if(msg.substring(12,13).equals("L")){
                    row = sheetAt.getRow(i+1);
                    cell = row.getCell(2);
                    if(cell==null){
                        cell=row.createCell(2);
                    }
                    cell.setCellValue(msg.substring(13,23));
                    cell.setCellStyle(style);
                }


                // 探头温度(℃)
                if(msg.substring(23,24).equals("T")){
                    row = sheetAt.getRow(i+1);
                    cell = row.getCell(3);
                    if(cell==null){
                        cell=row.createCell(3);
                    }
                    cell.setCellValue(msg.substring(24,33));
                    cell.setCellStyle(style);
                }


                // 探头电量 (%)
                if(msg.substring(33,34).equals("B")){
                    row = sheetAt.getRow(i+1);
                    cell = row.getCell(4);
                    if(cell==null){
                        cell=row.createCell(4);
                    }
                    final String dian_liang=msg.substring(34,40);
                    if(dian_liang.equals("999999")){
                        cell.setCellValue(dian_liang);
                    }else{
                        cell.setCellValue(dian_liang+"%");
                    }
                    cell.setCellStyle(style);
                }


                // 电导率（uS/cm）
                if(msg.substring(40,41).equals("C")){
                    row = sheetAt.getRow(i+1);
                    cell = row.getCell(5);
                    if(cell==null){
                        cell=row.createCell(5);
                    }
                    cell.setCellValue(msg.substring(41,50));
                    cell.setCellStyle(style);
                }



                // RTU 电压(V)
                if(msg.substring(50,51).equals("V")){
                    row = sheetAt.getRow(i+1);
                    cell = row.getCell(6);
                    if(cell==null){
                        cell=row.createCell(6);
                    }
                    cell.setCellValue(msg.substring(51,56));
                    cell.setCellStyle(style);
                }



                // CSQ
                if(msg.substring(56,59).equals("CSQ")){
                    row = sheetAt.getRow(i+1);
                    cell = row.getCell(7);
                    if(cell==null){
                        cell=row.createCell(7);
                    }
                    cell.setCellValue(msg.substring(59,61));
                    cell.setCellStyle(style);
                }



                // RTU温度(℃)
                if(msg.substring(61,62).equals("R")){
                    row = sheetAt.getRow(i+1);
                    cell = row.getCell(8);
                    if(cell==null){
                        cell=row.createCell(8);
                    }
                    cell.setCellValue(msg.substring(62,68));
                    cell.setCellStyle(style);
                }


                // 故障代码
                if(msg.substring(68,69).equals("E")){
                    row = sheetAt.getRow(i+1);
                    cell = row.getCell(9);
                    if(cell==null){
                        cell=row.createCell(9);
                    }
                    cell.setCellValue(msg.substring(69,73));
                    cell.setCellStyle(style);
                }


                // 探头压力(mH2O)
                if(msg.substring(93,94).equals("P")){
                    row = sheetAt.getRow(i+1);
                    cell = row.getCell(10);
                    if(cell==null){
                        cell=row.createCell(10);
                    }
                    cell.setCellValue(msg.substring(94,103));
                    cell.setCellStyle(style);
                }



                // 大气压(mH2O)
                if(msg.substring(103,104).equals("B")){
                    row = sheetAt.getRow(i+1);
                    cell = row.getCell(11);
                    if(cell==null){
                        cell=row.createCell(11);
                    }
                    cell.setCellValue(msg.substring(104,111));
                    cell.setCellStyle(style);
                }


                // 探头埋深(m)
                if(msg.substring(111,112).equals("C")){
                    row = sheetAt.getRow(i+1);
                    cell = row.getCell(12);
                    if(cell==null){
                        cell=row.createCell(12);
                    }
                    cell.setCellValue(msg.substring(112,122));
                    cell.setCellStyle(style);
                }
            }

            fos = new FileOutputStream(FileUtils.getSdcardPath()+red2+"_"+fileName+".xls");
            // 写入
            workBook.write(fos);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (workBook != null) {
                try {
                    workBook.cloneSheet(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        DialogUtils.closeProgress();
        dialogView = new DialogView(dialogView,context, "数据.xls文件已创建成功，目录是：" + FileUtils.getSdcardPath()+red2+"_"+fileName+".xls", "确定", null, new View.OnClickListener() {
            public void onClick(View v) {
                dialogView.dismiss();
            }
        }, null);
        dialogView.show();
    }
}
