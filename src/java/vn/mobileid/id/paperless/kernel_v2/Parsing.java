/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.kernel_v2;

/**
 *
 * @author GiaTK
 */
public class Parsing {
    //<editor-fold defaultstate="collapsed" desc="Parse BoxCoodinare(percentage) to BoxCoordinate(Point)">
    public static String parseBoxCoordinate(
            String boxCoordinate, 
            int widthOfPDF, 
            int heightOfPDF,
            String transactionId){
        try{
            String[] tokens = boxCoordinate.split(",");
            String left_ = tokens[0];
            String top_ = tokens[1];
            String width_ = tokens[2];
            String height_ = tokens[3];
            float top = Float.parseFloat(top_);
            float left = Float.parseFloat(left_);
            int width = Integer.parseInt(width_);
            int height = Integer.parseInt(height_);
            width = width * widthOfPDF / 100;
            height = height * heightOfPDF / 100; 
            int finalTop = Math.round((1- top/100) * heightOfPDF - height);
            int finalLeft = Math.round(left * widthOfPDF/100);
            
            return finalLeft + "," + finalTop + "," + width + "," + height;
        } catch(Exception ex){
            return null;
        }
    }
    //</editor-fold>
    
    public static void main(String[] args) {
        System.out.println(parseBoxCoordinate("0,0,20,30", 200, 200, ""));
    }
}
