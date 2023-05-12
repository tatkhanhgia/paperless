/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Mobile ID 22
 */
public class HttpUtils {

    static final Logger LOGGER = LogManager.getLogger(HttpUtils.class);

    
//    static {
//        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
//            @Override
//            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
//                    if (hostname.equals("id.mobile-id.vn") || hostname.equals("checkid.mobile-id.vn")) {
//                            return true;
//                    }
//                    return true;
//            }
//        });
//    }
    
//    public static String invokeHttpRequest(URL endpointUrl,
//            String httpMethod,
//            int timeout,
//            Map<String, String> headers,
//            String requestBody) {
//        HttpURLConnection connection = createHttpConnection(endpointUrl, httpMethod, timeout, headers);
//        try {
//            if (requestBody != null) {
//                DataOutputStream wr = new DataOutputStream(
//                        connection.getOutputStream());
//                wr.writeBytes(requestBody);
//                wr.flush();
//                wr.close();
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Request failed. " + e.getMessage(), e);
//        }
//        return executeHttpRequest(connection);
//    }
    public static String invokeHttpRequest(URL url,
            String httpMethod,
            int timeout,
            Map<String, String> headers,
            String requestBody) {

        HttpURLConnection connection = null;
        try {
            connection = createHttpConnection(url, httpMethod, timeout, headers);
            HttpURLConnection.setFollowRedirects(true);
            if (requestBody != null) {
                try ( BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"))) {
                    LOGGER.debug(">>> SEND: {}", requestBody);
                    wr.write(requestBody);
                    wr.flush();
                }
            }

            //HttpResponse response = new HttpResponse();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                    || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                return invokeHttpRequest(new URL(connection.getHeaderField("Location")), httpMethod, timeout, headers, requestBody);
            }
            InputStream is;
            if (responseCode > 299) {
                is = connection.getErrorStream();
                if (is == null) {
                    String responseMsg = connection.getResponseMessage();
                    LOGGER.debug("<<< RECEIVE: {}", responseMsg);
                    return responseMsg;
                }
            } else if (responseCode != HttpURLConnection.HTTP_OK) {
                String responseMsg = connection.getResponseMessage();
                LOGGER.debug("<<< RECEIVE: {}", responseMsg);
                return responseMsg;
            } else {
                try {
                    is = connection.getInputStream();
                } catch (IOException ex) {
                    LOGGER.debug("Error when read inputstream, caused by", ex);
                    is = connection.getErrorStream();
                }
            }

            StringBuilder msg = new StringBuilder();
            try ( BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    msg.append(line);
                    msg.append('\r');
//                    msg.append(System.lineSeparator());
                }
            }
            LOGGER.debug("<<< RECEIVE: {}", msg.toString());
            return msg.toString();
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

    public static String executeHttpRequest(HttpURLConnection connection) {
        try {
            InputStream is;
            try {
                is = connection.getInputStream();
            } catch (IOException e) {
                is = connection.getErrorStream();
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            throw new RuntimeException("Request failed. " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static HttpURLConnection createHttpConnection(URL endpointUrl,
            String httpMethod,
            int timeout,
            Map<String, String> headers) {
        try {
            HttpURLConnection connection = (HttpURLConnection) endpointUrl.openConnection();
            connection.setRequestMethod(httpMethod);

            if (headers != null) {
                for (String headerKey : headers.keySet()) {
                    connection.setRequestProperty(headerKey, headers.get(headerKey));
                }
            }

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);

            return connection;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create connection. " + e.getMessage(), e);
        }
    }

}
