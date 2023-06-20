/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author GiaTK
 */
public class TestConvertStringToDate {
    public static void main(String[] args) throws ParseException{
//        String b = "2023-05-15 15:40 PM UTC";
//        String a = "2023-05-23 02:59:07 PM +07:00";
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a XXX");
//        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));        
//        Date q = formatter.parse(a);
//        System.out.println(q);
//        System.out.println(formatter.format(q));
        
        
        String a = "2023-05-23 02:59:07 PM +07:00";
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a XXX");
        ZonedDateTime zdtInstanceAtOffset = ZonedDateTime.parse(a, DATE_TIME_FORMATTER);
        ZonedDateTime zdtInstanceAtUTC = zdtInstanceAtOffset.withZoneSameInstant(ZoneOffset.UTC);
        System.out.println(zdtInstanceAtOffset);
        System.out.println(zdtInstanceAtUTC);
//        Date w = formatter2.parse(formatter.format(q));
//        System.out.println(w);
    }
}
