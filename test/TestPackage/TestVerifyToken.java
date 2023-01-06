/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TestPackage;

import vn.mobileid.id.general.keycloak.KeyCloakInvocation;

/**
 *
 * @author GiaTK
 */
public class TestVerifyToken {
    public static void main(String[] args){
        KeyCloakInvocation key = new KeyCloakInvocation(
                "http://192.168.198.120:8081/auth",
                "QryptoRealm");
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxSEFyRGRxRFFHYWRWX3dKaUpSLXItdjlUbUpVVldILURxSzRBM0xQT2xvIn0.eyJleHAiOjE2NzI5OTM2MTMsImlhdCI6MTY3Mjk5MzMxMywianRpIjoiOWYzNzlhNWMtNzI4Zi00NGQxLTljNjktMzNmNDM0MTZiYjQxIiwiaXNzIjoiaHR0cDovLzE5Mi4xNjguMTk4LjEyMDo4MDgxL2F1dGgvcmVhbG1zL1FyeXB0b1JlYWxtIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjVhZTkzYWNkLTk0OTMtNGVhNi1hYTkyLThhMTQyYWRiYzI3MSIsInR5cCI6IkJlYXJlciIsImF6cCI6InFyeXB0b1VzZXIiLCJzZXNzaW9uX3N0YXRlIjoiZmI4YjU4NTktYzQzMS00MzUzLThiOGUtNjZkNDRhZjkzM2I1IiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXFyeXB0b3JlYWxtIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6ImZiOGI1ODU5LWM0MzEtNDM1My04YjhlLTY2ZDQ0YWY5MzNiNSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IlRhdCBHaWEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyMiIsImdpdmVuX25hbWUiOiJUYXQiLCJmYW1pbHlfbmFtZSI6IkdpYSIsImVtYWlsIjoiZ2lhdGtAbW9iaWxlLWlkLnZuIn0.IvctOc5VoWASIhZsGqNMPZQ93zEP3PGZpsI3A2JZJZZISsl1T_QA7eVIDTFepnNvsxqJ6tXWUQH8T1pVFKnvHvNdSK_xViUQc7re67WlyKmSltEE6JG8BCFUNHAhs5JcRYC547r2GoDaFNMG0qT9NxNdmN303qqITXH9mZLhk0Yw-UslowSyLUTS7Vk2mYRE8AN3DLS-18pGz2zzMawrgidYKxvr4y_Pr3BvLfT23Wdtg39Q1snukrUVHQ9T4F6W63PSiAaV6SGRA3t_wqVGhQMtyA4Zh1g1Zfp5OLi9HlY4Q1BxJefChV4hpTkSGf9KxBZipmuvDlbkMXXOAOzvsw";
        key.verifyToken(token);
    }
}
