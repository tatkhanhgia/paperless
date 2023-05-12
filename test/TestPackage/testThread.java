/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

/**
 *
 * @author GiaTK
 */
public class testThread extends Thread{
    private String name;

    public testThread(String name) {
        this.name = name;
    }
    
    @Override
    public void run(){
        int count = 10;
        for (int i = count; i > 0; i--) {
            System.out.println(name+":"+i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("Hết giờ");
    }
    public static void main (String[] args){
        testThread a = new testThread("a");
        a.start();
              testThread b = new testThread("c");
        b.start();
    }
}
