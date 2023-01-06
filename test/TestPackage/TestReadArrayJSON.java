/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.awt.List;
import java.io.File;
import java.io.IOException;
import vn.mobileid.id.qrypto.objects.QryptoItemWorkflowDetailJSNObject;
import vn.mobileid.id.qrypto.objects.QryptoWorkflowDetailJSNObject;

/**
 *
 * @author GiaTK
 */
public class TestReadArrayJSON {
    public static void main(String[] args) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        String a = "[{\"name\": \"Java\", \"description\": \"Java is a class-based, object-oriented programming language.\"},{\"name\": \"Python\", \"description\": \"Python is an interpreted, high-level and general-purpose programming language.\"}, {\"name\": \"JS\", \"description\": \"JS is a programming language that conforms to the ECMAScript specification.\"}]";
        String b = "{\"item\":[{\"c\":1001,\"mandatory_enable\":\"jhon\",\"t\":\"t2\"}, {\"c\":1002,\"mandatory_enable\":\"jhon\",\"t\":\"t2\"}]}";
        String c = "{\n" +
"    \"items\":[\n" +
"        {\n" +
"            \"c\":\"hello\",\n" +
"            \"mandatory_enable\":\"false\",\n" +
"            \"t\":\"t2\"\n" +
"        },\n" +
"        {\n" +
"            \"c\":\"111\",\n" +
"            \"mandatory_enable\":\"false\",\n" +
"            \"t\":\"t2\"\n" +
"        }" + 
"    ]\n" +
"}";
//                String c = "{\n" +
//"\"items\":[\n" +
//"{\n" +
//"\"c\":\"hello\",\n" +
//"\"mandatory_enable\":\"false\",\n" +
//"\"t\":\"t2\"\n" +
//"},\n" +
//"{\n" +
//"\"c\":\"111\",\n" +
//"\"mandatory_enable\":\"false\",\n" +
//"\"t\":\"t2\"\n" +
//"}" + 
//"]\n" +
//"}";
//        Language[] langList = mapper.readValue(a,Language[].class);
//        for(Language temp : langList){
//            System.out.println("Name:"+temp.getName());
//            System.out.println("Des:"+temp.getDescription());
//        }
//        Item langList = mapper.readValue(b,Item.class);
//        for(Language temp : langList.getItem()){
//            System.out.println("Name:"+temp.getName());
//            System.out.println("Des:"+temp.getDescription());
//        }
c = c.replaceAll("\t", "");
c = c.replaceAll("\n", "");
c = c.replaceAll("[ ]{2,10}", "");
        System.out.println("c:"+c);        
QryptoItemWorkflowDetailJSNObject langList = mapper.readValue(c,QryptoItemWorkflowDetailJSNObject.class);
        for(QryptoWorkflowDetailJSNObject temp : langList.getItem()){
            System.out.println("T:"+temp.getT());
        }
    }
    
    
}
