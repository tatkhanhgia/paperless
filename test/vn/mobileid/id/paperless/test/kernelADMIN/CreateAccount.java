///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package vn.mobileid.id.paperless.test.kernelADMIN;
//
//import vn.mobileid.id.general.objects.InternalResponse;
//import vn.mobileid.id.paperless.kernelADMIN.CreateAccount.CreateUser;
//import vn.mobileid.id.paperless.kernelADMIN.CreateAccount.GetAuthenticatePassword;
//import vn.mobileid.id.paperless.objects.EmailTemplate;
//
//
///**
// *
// * @author GiaTK
// */
//public class CreateAccount {
//    
//    public static void testGetAuthenticatePassword(){
//        InternalResponse res = GetAuthenticatePassword.getAuthenticatePassword(
//                "giatk@mobile-id.vn",
//                2,
//                "email_send_password",
//                "transactionID");
//        EmailTemplate a = (EmailTemplate) res.getData();
//        System.out.println(a.getPassword());
//    }
//    
//    public static void testCreateUser(){
//        InternalResponse res = CreateUser.createUser(
//                "khanhpx@mobile-id.vn",
//                "giatk@mobile-id.vn",
//                "TAT KHANH GIA",
//                3, //enterprise-id
//                "OWNER",
//                0, //password - expired at
//                2, //business_type
//                "https://paperless.mobile-id.vn", 
//                "HMAC",
//                "transactionID");
//    }
//    
//    public static void testCreateAccount(){
//        InternalResponse res = vn.mobileid.id.paperless.kernelADMIN.CreateAccount.createAccount(
//                "giatk@mobile-id.vn",
//                "Tat Khanh Gia",
//                "079200011188",
//                3,
//                "khanhpx@mobile-id.vn",
//                "OWNER",
//                1000,
//                2,
//                "https://paperless.mobile-id.vn",                
//                "transactionID");
//        
//        System.out.println("Status:"+res.getStatus());
//    }   
//    
//    public static void main(String[] args) {
////        testCreateAccount();
//        testCreateUser();
//    }
//
//}
