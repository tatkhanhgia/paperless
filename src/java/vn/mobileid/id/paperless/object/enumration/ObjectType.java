/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.paperless.object.enumration;

/**
 *
 * @author GiaTK
 * In class TransactionID for manageme the type of source inside Transaction
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
        
        public static ObjectType valuesOf(int data){
            for(ObjectType type : values()){
                if(type.getNumber() == data){
                    return type;
                }
            }
            return ObjectType.PDF;
        }
    }
