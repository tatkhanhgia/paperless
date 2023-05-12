/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package restful.sdk.API;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import vn.mobileid.id.general.LogHandler;

/**
 *
 * @author Tuan Pham
 */
public class HTTPUtils {

    public final static String X_CLUSTER_NAME = "X-Cluster-Name";    

    public static HttpResponse sendPost(
        String endpointUrl,
        String requestBody,
        String authorizationHeader
    ){
        return sendPost(endpointUrl, ContentType.JSON, requestBody, authorizationHeader);
    }
    
    public static HttpResponse sendPost(
            String endpointUrl,
            ContentType contentType,
            String requestBody,
            String authorizationHeader) {
        try {

            String httpMethod = "POST";
            int timeout = 50000;
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", authorizationHeader);
//            headers.put("X-RSSP-BACKEND", "rssp02");

            return invokeHttpRequest(
                    null,
                    endpointUrl,
                    httpMethod,
                    contentType,
                    timeout,
                    headers,
                    requestBody);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Request failed. " + e.getMessage(), e);
        }
    }

    private static HttpResponse invokeHttpRequest(
            String[] truststore,
            String endpointUrl,
            String httpMethod,
            ContentType contentType,
            int timeout,
            Map<String, String> headers,
            String requestBody) {

        HttpURLConnection connection = null;
        try {            
            URL url = new URL(endpointUrl);
            connection = createHttpConnection(
                    truststore,
                    url,
                    httpMethod,
                    contentType,
                    timeout,
                    timeout,
                    headers);            
            HttpURLConnection.setFollowRedirects(true);
            if (requestBody != null) {
                try (BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"))) {
//                    LOGGER.debug(">>> SEND: {}", requestBody);
                    wr.write(requestBody);
                    wr.flush();
                }
            }

            HttpResponse response = new HttpResponse();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                    || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                return invokeHttpRequest(
                        truststore,
                        connection.getHeaderField("Location"),
                        httpMethod,
                        contentType,
                        timeout,
                        headers,
                        requestBody);
            }

//            LOGGER.debug("Response Code: " + responseCode);
//            for (String key : connection.getHeaderFields().keySet()) {
//                LOGGER.debug("" + key + ": ");
//                for (String val : connection.getHeaderFields().get(key)) {
//                    LOGGER.debug("\t\t" + val + " ");
//                }
//            }

            InputStream is;
            if (responseCode > 299) {
                is = connection.getErrorStream();
                response.setStatus(false);
                if (is == null) {
                    String responseMsg = connection.getResponseMessage();
//                    LOGGER.debug("<<< RECEIVE: {}", responseMsg);
                    response.setMsg(responseMsg);
                    return response;
                }
            } else if (responseCode != HttpURLConnection.HTTP_OK) {
                String responseMsg = connection.getResponseMessage();
//                LOGGER.debug("<<< RECEIVE: {}", responseMsg);
                response.setMsg(responseMsg);
                response.setStatus(true);
                return response;
            } else {
                try {
                    is = connection.getInputStream();
                    response.setStatus(true);
                } catch (IOException ex) {
//                    LOGGER.debug("Error when read inputstream, caused by", ex);
                    is = connection.getErrorStream();
                    response.setStatus(false);
                }
            }

            StringBuilder msg = new StringBuilder();
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    msg.append(line);
                    msg.append('\r');
//                    msg.append(System.lineSeparator());
                }
            }
//            LOGGER.debug("<<< RECEIVE: {}", msg.toString());
            response.setMsg(msg.toString());
            return response;
        } catch (RuntimeException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("Request failed. " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static InputStream invokeHttpRequestAsStream(
            String endpointUrl,
            String httpMethod,
            ContentType contentType,
            int timeout,
            Map<String, String> headers,
            byte[] requestBody) {
        try {
            return invokeHttpRequestAsStream(null, endpointUrl, httpMethod, contentType, timeout, headers, requestBody);
        } catch (Exception e) {
            throw new RuntimeException("Request failed. " + e.getMessage(), e);
        }
    }

    public static InputStream invokeHttpRequestAsStream(String[] truststore, String endpointUrl,
            String httpMethod,   
            ContentType contentType,
            int timeout,
            Map<String, String> headers,
            byte[] requestBody) {

        HttpURLConnection connection = null;
        try {
            URL url = new URL(endpointUrl);
            connection = createHttpConnection(truststore, url, httpMethod,contentType, timeout, timeout, headers);
            HttpURLConnection.setFollowRedirects(true);
            if (requestBody != null) {
                try (DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream())) {
//                    LOGGER.debug(">>> SEND: {}", Utils.toHexString(requestBody));
                    dataOut.write(requestBody);
                    dataOut.flush();
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                    || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                return invokeHttpRequestAsStream(truststore, connection.getHeaderField("Location"), httpMethod, contentType, timeout, headers, requestBody);
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("HTTP Response [" + responseCode + "]");
            } else {
                return (InputStream) connection.getContent();
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("Request failed. " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static HttpURLConnection createHttpConnection(
            String[] truststore,
            URL endpointUrl,
            String httpMethod,  
            ContentType contentType,
            int connTimeout,
            int readTimeOut,
            Map<String, String> headers) {
        try {
            HttpURLConnection connection = (HttpURLConnection) endpointUrl.openConnection();
            connection.setRequestMethod(httpMethod);            
            if (headers != null) {
//                LOGGER.debug("**************** Restful Request headers ****************");
                for (String headerKey : headers.keySet()) {
                    connection.setRequestProperty(headerKey, headers.get(headerKey));
                }
            }            
            connection.setUseCaches(true);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", contentType.getName());
            if (connTimeout > 0) {
                connection.setConnectTimeout(connTimeout);
            }
            if (readTimeOut > 0) {
                connection.setReadTimeout(readTimeOut);
            }

            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                setupSSL(https, truststore);
            }
            connection.connect();

            return connection;
        } catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot create connection. " + e.getMessage(), e);
        }
    }

    static class MySSLSocket extends SSLSocket {

        final private SSLSocket sslSocket;

        public MySSLSocket(SSLSocket sslSocket) {
            this.sslSocket = sslSocket;
        }

        @Override
        public String[] getSupportedCipherSuites() {
//            LOGGER.debug("getSupportedCipherSuites");
            return new String[]{"TLS_RSA_WITH_AES_128_GCM_SHA256"};
        }

        @Override
        public String[] getEnabledCipherSuites() {
//            LOGGER.debug("getEnabledCipherSuites");
            return new String[]{"TLS_RSA_WITH_AES_128_GCM_SHA256"};
        }

        @Override
        public void setEnabledCipherSuites(String[] strings) {
            for (String s : strings) {
//                LOGGER.debug("CipherSuites: " + s);
            }
            sslSocket.setEnabledCipherSuites(strings);
        }

        @Override
        public String[] getSupportedProtocols() {
            return sslSocket.getSupportedProtocols();
        }

        @Override
        public String[] getEnabledProtocols() {
//            LOGGER.debug("getEnabledProtocols");
            return sslSocket.getEnabledProtocols();
        }

        @Override
        public void setEnabledProtocols(String[] strings) {
            sslSocket.setEnabledProtocols(strings);
        }

        @Override
        public SSLSession getSession() {
            return sslSocket.getSession();
        }

        @Override
        public void addHandshakeCompletedListener(HandshakeCompletedListener hl) {
            sslSocket.addHandshakeCompletedListener(hl);
        }

        @Override
        public void removeHandshakeCompletedListener(HandshakeCompletedListener hl) {
            sslSocket.removeHandshakeCompletedListener(hl);
        }

        @Override
        public void startHandshake() throws IOException {
            sslSocket.startHandshake();
        }

        @Override
        public void setUseClientMode(boolean bln) {
            sslSocket.setUseClientMode(bln);
        }

        @Override
        public boolean getUseClientMode() {
            return sslSocket.getUseClientMode();
        }

        @Override
        public void setNeedClientAuth(boolean bln) {
            sslSocket.setNeedClientAuth(bln);
        }

        @Override
        public boolean getNeedClientAuth() {
            return sslSocket.getNeedClientAuth();
        }

        @Override
        public void setWantClientAuth(boolean bln) {
            sslSocket.setWantClientAuth(bln);
        }

        @Override
        public boolean getWantClientAuth() {
            return sslSocket.getWantClientAuth();
        }

        @Override
        public void setEnableSessionCreation(boolean bln) {
            sslSocket.setEnableSessionCreation(bln);
        }

        @Override
        public boolean getEnableSessionCreation() {
            return sslSocket.getEnableSessionCreation();
        }

    }

    static class MySSLSocketFactory extends SSLSocketFactory {

        final private SSLSocketFactory sslSocketFactory;

        public MySSLSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
        }

        @Override
        public String[] getDefaultCipherSuites() {
//            LOGGER.debug("getDefaultCipherSuites");
            return sslSocketFactory.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
//            LOGGER.debug("getSupportedCipherSuites");
            return sslSocketFactory.getDefaultCipherSuites();
        }

        @Override
        public Socket createSocket(Socket socket, String string, int i, boolean bln) throws IOException {
//            LOGGER.debug("createSocket [host:port] [{}:{}] result: {}", string, i, bln);
            Socket sk = this.sslSocketFactory.createSocket(socket, string, i, bln);
            if (sk instanceof SSLSocket) {
                ((SSLSocket) sk).setEnabledCipherSuites(new String[]{
                    "TLS_RSA_WITH_AES_128_CBC_SHA",
                    "TLS_RSA_WITH_AES_256_CBC_SHA256",
                    "TLS_RSA_WITH_AES_128_CBC_SHA256",
                    "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                    "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                    "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"
                });

                ((SSLSocket) sk).addHandshakeCompletedListener(new HandshakeCompletedListener() {
                    @Override
                    public void handshakeCompleted(HandshakeCompletedEvent hce) {
//                        LOGGER.debug("CipherSuite: " + hce.getCipherSuite());
                        //SSLSession session = hce.getSession();
                    }
                });
                // now do the TLS handshake
//                ((SSLSocket) sk).startHandshake();
//                SSLSession session = ((SSLSocket) sk).getSession();
//                if (session == null) {
//                    throw new SSLException("Cannot verify SSL socket without session");
//                }
//                LOGGER.debug("BufferSize: " + session.getApplicationBufferSize());
                // verify host name (important!)
//                if (!HttpsURLConnection.getDefaultHostnameVerifier().verify(string, session)) {
//                    throw new SSLPeerUnverifiedException("Cannot verify hostname: " + string);
//                }
            }
            return sk;
//            return this.sslSocketFactory.createSocket(new MySSLSocket((SSLSocket) sk), string, i, bln);
        }

        @Override
        public Socket createSocket(String string, int i) throws IOException, UnknownHostException {
//            LOGGER.debug("createSocket:::: " + string);
//            LOGGER.debug("createSocket:::: " + i);
            Socket sk = this.sslSocketFactory.createSocket(string, i);
            if (sk instanceof SSLSocket) {
                ((SSLSocket) sk).setEnabledCipherSuites(new String[]{
                    "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384",
                    "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
                    "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"
                });

                ((SSLSocket) sk).addHandshakeCompletedListener(new HandshakeCompletedListener() {
                    @Override
                    public void handshakeCompleted(HandshakeCompletedEvent hce) {
                        LogHandler.info(this.getClass(),"CipherSuite: " + hce.getCipherSuite());
                    }
                });
                // now do the TLS handshake
                ((SSLSocket) sk).startHandshake();
                SSLSession session = ((SSLSocket) sk).getSession();
                if (session == null) {
                    throw new SSLException("Cannot verify SSL socket without session");
                }

                // verify host name (important!)
                if (!HttpsURLConnection.getDefaultHostnameVerifier().verify(string, session)) {
                    throw new SSLPeerUnverifiedException("Cannot verify hostname: " + string);
                }
            }
            return sk;
        }

        @Override
        public Socket createSocket(String string, int i, InetAddress ia, int i1) throws IOException, UnknownHostException {
//            LOGGER.debug("createSocket");
            return this.sslSocketFactory.createSocket(string, i, ia, i1);
        }

        @Override
        public Socket createSocket(InetAddress ia, int i) throws IOException {
//            LOGGER.debug("createSocket");
            return this.sslSocketFactory.createSocket(ia, i);
        }

        @Override
        public Socket createSocket(InetAddress ia, int i, InetAddress ia1, int i1) throws IOException {
//            LOGGER.debug("createSocket");
            return this.sslSocketFactory.createSocket(ia, i, ia1, i1);
        }
    }

    private static void setupSSL(HttpsURLConnection conHttps, String[] truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException, FileNotFoundException, CertificateException {
        // Set our connection to use this SSL context, with the "Trust all" manager in place.
        SSLContext sslContext = wrapX500TrustManager(truststore);
        SSLSocketFactory sslSocket = new MySSLSocketFactory(sslContext.getSocketFactory());

        conHttps.setSSLSocketFactory(sslSocket);
//        String[] ciphers = sslSocket.getSupportedCipherSuites();
//        for (int i = 0; i < ciphers.length; i++) {
//            System.out.println(" " + i + ". " + ciphers[i]);
//        }

        // and set the hostname verifier.
        conHttps.setHostnameVerifier((String string, SSLSession ssls) -> {
            try {
                LogHandler.info(HTTPUtils.class,"------------ HostnameVerifier ------------");
                LogHandler.info(HTTPUtils.class,"\t\t String: " + string);
                LogHandler.info(HTTPUtils.class,"\t\t PeerHost: " + ssls.getPeerHost());
                LogHandler.info(HTTPUtils.class,"\t\t PeerPort: " + ssls.getPeerPort());
                LogHandler.info(HTTPUtils.class,"\t\t CipherSuite: " + ssls.getCipherSuite());

                LogHandler.info(HTTPUtils.class,"\t\t PeerPrincipal-Name: " + ssls.getPeerPrincipal().getName());
                LogHandler.info(HTTPUtils.class,"------------ |||||||||||||||| ------------");
            } catch (SSLPeerUnverifiedException ex) {
                LogHandler.info(HTTPUtils.class,"Error when verify hostname");
            }
            return true;
        });
    }

    public static SSLContext wrapX500TrustManager(String[] trusted) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, FileNotFoundException, IOException, CertificateException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // Using null here initialises the TMF with the default trust store.
        tmf.init((KeyStore) null);
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        // Get hold of the default trust manager
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                X509Certificate[] defauls = ((X509TrustManager) tm).getAcceptedIssuers();
                if (defauls != null) {
                    for (X509Certificate x509 : defauls) {
                        trustStore.setCertificateEntry(x509.getSerialNumber().toString(), x509);
                    }
                }
//                LOGGER.debug("Found X509TrustManager: " + tm.toString());
                break;
            }
        }

        if (trusted != null) {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            for (String f : trusted) {
                if (f == null) {
                    continue;
                }
                try (final FileInputStream inputStream = new FileInputStream(f)) {
                    X509Certificate cert = (X509Certificate) certFactory.generateCertificate(inputStream);
                    //trustStore.setCertificateEntry("MyTrust" + i, cert);
                    trustStore.setCertificateEntry(cert.getSerialNumber().toString(), cert);
                }
            }
        }
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, tmf.getTrustManagers(), new java.security.SecureRandom());
        return sslContext;
    }

    public static enum ContentType{
        JSON("application/json"),
        multipart_form_data("multipart/form-data");
        
        private String name;

        private ContentType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }                
    }
}
