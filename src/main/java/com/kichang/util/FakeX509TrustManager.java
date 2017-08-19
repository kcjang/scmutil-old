package com.kichang.util;



import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class FakeX509TrustManager implements X509TrustManager 
{
      public boolean isClientTrusted(java.security.cert 
             .X509Certificate[] chain) {

         return true;
     }

      public boolean isServerTrusted(java.security.cert 
             .X509Certificate[] chain) {

         return true;
     }

      public java.security.cert.X509Certificate[] getAcceptedIssuers() { 

         return null;
     }

  @Override
  public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
     
  }

  @Override
  public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    
  }
 }
