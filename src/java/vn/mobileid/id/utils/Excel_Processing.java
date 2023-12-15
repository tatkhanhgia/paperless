/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.mobileid.id.general.Resources;
import vn.mobileid.id.paperless.objects.WorkflowActivity;
import vn.mobileid.id.paperless.objects.WorkflowTemplateType;

/**
 *
 * @author Admin
 */
public class Excel_Processing {

    public static byte[] createExcel(
            List<WorkflowActivity> listObject
    ) throws Exception {
        //Create Excel
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Reports Workflow Activity");
        for (int i = 0; i <= 5; i++) {
            sheet.setColumnWidth(i, 4000);
        }

        //Set Header
        XSSFRow header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);

        XSSFCell headerCell = header.createCell(0);
        headerCell.setCellValue("ID");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Workflow Label");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Workflow Template Type Name");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Status");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Created By");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Created At");
        headerCell.setCellStyle(headerStyle);
        //Row
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);

        HashMap<Integer, WorkflowTemplateType> map = Resources.getListWorkflowTemplateType();
        if (map == null || map.isEmpty()) {
            Resources.reloadListWorkflowTemplateType();
            map = Resources.getListWorkflowTemplateType();
        }

        System.out.println("Total record:" + listObject.size());
        int row_count = 0;
        for (int i = 0; i < listObject.size(); i++) {
            //Check if workflow template type in Object not exist in RAM (in case that workflowtemplatetype is disable in DB) => Ignore that woAc
            //Kiểm tra workflow template type của Object có trong danh sách tồn tại hay không? (trường hợp disable trong DB) => bỏ qua woAc đó
            try {
                map.get(listObject.get(i).getWorkflow_template_type()).getName();
            } catch (Exception ex) {
                continue;
            }
//            if(listObject.get(i) == null || listObject.get(i).getCreated_by() == null || listObject.get(i).getCreated_by().isEmpty()){
//                continue;
//            }
            XSSFRow row = sheet.createRow(1 + row_count);
            row_count ++;

            //Cell
            XSSFCell cell = row.createCell(0);
            cell.setCellValue(listObject.get(i).getId());
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(listObject.get(i).getWorkflow_label());
            cell.setCellStyle(style);

            cell = row.createCell(2);

//            map.get(listObject.get(i).getWorkflow_template_type()).getName();
            cell.setCellValue(map.get(listObject.get(i).getWorkflow_template_type()).getName());
            cell.setCellStyle(style);

            cell = row.createCell(3);
            cell.setCellValue(listObject.get(i).getStatus_name());
            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue(listObject.get(i).getCreated_by());
            cell.setCellStyle(style);

            cell = row.createCell(5);
            cell.setCellValue(new SimpleDateFormat().format(listObject.get(i).getCreated_at()));
            cell.setCellStyle(style);
        }

        //Save        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        workbook.write(output);
        return output.toByteArray();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        List<WorkflowActivity> list = new ArrayList<>();
        WorkflowActivity temp1 = new WorkflowActivity();
        temp1.setId(1);
        temp1.setWorkflow_template_type(1);
        temp1.setWorkflow_template_type_name("TemplateType1");
        temp1.setWorkflow_label("Workflow Label 1");
        temp1.setStatus_name("Status1");
        temp1.setCreated_by("GiaTK1");

        WorkflowActivity temp2 = new WorkflowActivity();
        temp2.setId(2);
        temp2.setWorkflow_template_type(2);
        temp2.setWorkflow_template_type_name("TemplateType2");
        temp2.setWorkflow_label("Workflow Label 2");
        temp2.setStatus_name("Status2");
        temp2.setCreated_by("GiaTK2");

        list.add(temp1);
        list.add(temp2);

        String fileLocation = "C:\\Users\\Admin\\Downloads\\test.xlsx";
        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        outputStream.write(createExcel(list));
        outputStream.close();
    }
}
