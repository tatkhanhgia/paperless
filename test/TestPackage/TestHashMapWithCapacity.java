/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import java.util.HashMap;
import org.bouncycastle.jcajce.provider.digest.GOST3411;

/**
 *
 * @author Admin
 */
public class TestHashMapWithCapacity {
    public static void main(String[] arhs){
        HashMap<String, String> temp = new HashMap<>(5);
        temp.put("1", "1");
        temp.put("2", "2");
        temp.put("3", "3");
        
        System.out.println("Size:"+temp.size());
        for(String key: temp.keySet()){
            System.out.println("Key:"+key);
            System.out.println("Value:"+temp.get(key));
        }
        
        temp.put("4", "4");
        
        System.out.println("Size:"+temp.size());
        for(String key: temp.keySet()){
            System.out.println("Key:"+key);
            System.out.println("Value:"+temp.get(key));
        }
    }
}
