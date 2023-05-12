/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

/**
 *
 * @author GiaTK
 */
public class ClassThread implements Runnable{

    @Override
    public void run() {
            int count = 10;
            for(int i = 0 ; i <count ; i ++){
                System.out.println("Count:"+i);
            }
            System.out.println("End");
    }
    
    public static void main(String[] args)
    {
        ClassThread system = new ClassThread();
        Thread thread = new Thread(system);
        thread.start();
    }
} 
