/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package restful.sdk.API;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
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
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClients;
import vn.mobileid.id.general.LogHandler;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.InputStreamBody;

/**
 *
 * @author GiaTK
 */
public class HttpPostMultiPart2 {

    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private PrintWriter writer;

    public HttpResponse sendPost(
            String baseURL,
            Map<String, String> headers,
            Map<String, Object> bodypart
    ) throws Exception {
//        URL url = new URL(baseURL);
//        httpConn = createConnection(
//                baseURL,
//                headers,
//                0,
//                0,
//                null);
//        HttpURLConnection.setFollowRedirects(true);

//      USING APACHE HTTP COMPONENT
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.setCharset(Charset.forName("UTF-8"));

        for (String key : bodypart.keySet()) {
            if (bodypart.get(key) instanceof byte[]) {
                ByteArrayInputStream bis = new ByteArrayInputStream((byte[])bodypart.get(key));
//                InputStreamBody fileBody = new InputStreamBody(bis, ContentType.DEFAULT_BINARY);
//                builder.addPart(key, fileBody);
                builder.addBinaryBody(key, bis, ContentType.DEFAULT_BINARY, key);
            }
            if (bodypart.get(key) instanceof String) {
                builder.addTextBody(key, (String) bodypart.get(key), ContentType.APPLICATION_JSON);
            }
        }

        HttpEntity entity = builder.build();

        HttpPost httpPost = new HttpPost(baseURL);
        httpPost.setEntity(entity);
        
        if (headers != null && !headers.isEmpty()) {
            for (String headerKey : headers.keySet()) {
                httpPost.addHeader(headerKey, headers.get(headerKey));
            }
        }

        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse response = httpClient.execute(httpPost);

        return response;
    }

    private HttpURLConnection createConnection(
            String baseURL,
            Map<String, String> headers,
            int connTimeout,
            int readTimeout,
            String[] truststore
    )
            throws Exception {
        URL url = new URL(baseURL);

        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(true);
        httpConn.setDoInput(true);
        httpConn.setDoOutput(true);
        httpConn.setRequestMethod("POST");

        if (headers != null && !headers.isEmpty()) {
            for (String headerKey : headers.keySet()) {
                httpConn.setRequestProperty(headerKey, headers.get(headerKey));
            }
        }

        if (connTimeout > 0) {
            httpConn.setConnectTimeout(connTimeout);
        }
        if (readTimeout > 0) {
            httpConn.setReadTimeout(readTimeout);
        }

        if (httpConn instanceof HttpsURLConnection) {
            HttpsURLConnection https = (HttpsURLConnection) httpConn;
            setupSSL(https, truststore);
        }
        httpConn.connect();

        return httpConn;
    }

    private static void setupSSL(HttpsURLConnection conHttps, String[] truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException, FileNotFoundException, CertificateException {
        // Set our connection to use this SSL context, with the "Trust all" manager in place.
        SSLContext sslContext = wrapX500TrustManager(truststore);
        SSLSocketFactory sslSocket = new HttpPostMultiPart2.MySSLSocketFactory(sslContext.getSocketFactory());

        conHttps.setSSLSocketFactory(sslSocket);
//        String[] ciphers = sslSocket.getSupportedCipherSuites();
//        for (int i = 0; i < ciphers.length; i++) {
//            System.out.println(" " + i + ". " + ciphers[i]);
//        }

        // and set the hostname verifier.
        conHttps.setHostnameVerifier((String string, SSLSession ssls) -> {
            try {
                LogHandler.info(HTTPUtils.class, "------------ HostnameVerifier ------------");
                LogHandler.info(HTTPUtils.class, "\t\t String: " + string);
                LogHandler.info(HTTPUtils.class, "\t\t PeerHost: " + ssls.getPeerHost());
                LogHandler.info(HTTPUtils.class, "\t\t PeerPort: " + ssls.getPeerPort());
                LogHandler.info(HTTPUtils.class, "\t\t CipherSuite: " + ssls.getCipherSuite());

                LogHandler.info(HTTPUtils.class, "\t\t PeerPrincipal-Name: " + ssls.getPeerPrincipal().getName());
                LogHandler.info(HTTPUtils.class, "------------ |||||||||||||||| ------------");
            } catch (SSLPeerUnverifiedException ex) {
                LogHandler.info(HTTPUtils.class, "Error when verify hostname");
            }
            return true;
        });
    }

    private static SSLContext wrapX500TrustManager(String[] trusted) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, FileNotFoundException, IOException, CertificateException {
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
                        LogHandler.info(this.getClass(), "CipherSuite: " + hce.getCipherSuite());
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
}
