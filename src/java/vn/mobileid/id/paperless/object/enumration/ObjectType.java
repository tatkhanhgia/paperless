/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 */
 public enum ObjectType{
        QR(1),
        CSV(2),
        PDF(3);
        
        private int number;

        private ObjectType(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }     

        public void setNumber(int number) {
            this.number = number;
        }                
    }
