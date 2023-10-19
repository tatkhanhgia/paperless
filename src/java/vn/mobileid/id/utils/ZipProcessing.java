/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author GiaTK
 */
public class ZipProcessing {

    //<editor-fold defaultstate="collapsed" desc="Zip file">
    public static byte[] zipFile(List<String> name,List<byte[]> file) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(bos);

        int i=0;
        for (byte[] data : file) {
            ZipEntry entry = new ZipEntry(name.get(i));
            zip.putNextEntry(entry);
            
            zip.write(data);
            i++;
        }
        zip.close();
        return bos.toByteArray();
    }
    //</editor-fold>

    public static void main(String[] args) throws Exception{
        List<String> name = new ArrayList<>(); name.add("one.pdf"); name.add("two.pdf");
        List<byte[]> file = new ArrayList<>(); 
        file.add(Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\OT16974541390895_The_Sentry_x_TIA_Conference_2023 (2).pdf"))); 
        file.add(Files.readAllBytes(Paths.get("C:\\Users\\Admin\\Downloads\\result.pdf")));
        byte[] out = zipFile(name, file);
        FileOutputStream fileOs = new FileOutputStream("C:\\Users\\Admin\\Downloads\\result.zip");
        fileOs.write(out);
        fileOs.close();
    }
}
