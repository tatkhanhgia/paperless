/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.mobileid.id.general;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import vn.mobileid.id.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ADMIN
 */
public class HttpRequest {

    final private static Logger LOG = LogManager.getLogger(HttpRequest.class);
    final public static int RETRIES = 3;

    private final boolean postMethod;
    private final String postBody;
    private final String url;
    private final HashMap<String, String> headers;

    public HttpRequest(boolean postMethod, String postBody, String url, HashMap<String, String> headers) {
        this.postMethod = postMethod;
        this.postBody = postBody;
        this.url = url;
        this.headers = headers;
    }

    public Response sendRequest() {
        String response = null;
        HashMap<String, String> responseHeader = new HashMap<>();
        int httpCode = -1;
        int numOfRetry = RETRIES;
        while (numOfRetry > 0) {
            CloseableHttpClient client = null;
            CloseableHttpResponse httpresponse = null;
            try {
                client = HttpClients.createDefault();
                if (postMethod) {
                    HttpPost httpPost = new HttpPost(url);
                    if (!Utils.isNullOrEmpty(postBody)) {
                        StringEntity entity = new StringEntity(postBody);
                        httpPost.setEntity(entity);
                    }
                    if (headers != null) {
                        for (Map.Entry<String, String> entry : headers.entrySet()) {
                            httpPost.setHeader(entry.getKey(), entry.getValue());
                        }
                    }
                    httpresponse = client.execute(httpPost);
                } else {
                    HttpGet httpGet = new HttpGet(url);
                    if (headers != null) {
                        for (Map.Entry<String, String> entry : headers.entrySet()) {
                            httpGet.setHeader(entry.getKey(), entry.getValue());
                        }
                    }
                    httpresponse = client.execute(httpGet);
                }

                if (httpresponse.getEntity() != null) {
                    response = EntityUtils.toString(httpresponse.getEntity());
                }

                Header[] responseHeaders = httpresponse.getAllHeaders();
                if (responseHeaders != null) {
//                    if (LogHandler.isShowDebugLog()) {
//                        LOG.debug("Headers in IAM response");
//                    }
//                    for (Header header : responseHeaders) {
//                        if (LogHandler.isShowDebugLog()) {
//                            LOG.debug("\t" + header.getName() + ":" + header.getValue());
//                        }
//                        responseHeader.put(header.getName(), header.getValue());
//                    }
                } else {
                    if (LogHandler.isShowDebugLog()) {
                        LOG.debug("No header in IAM response");
                    }
                }

                httpCode = httpresponse.getStatusLine().getStatusCode();

                if (LogHandler.isShowDebugLog()) {
                    LOG.debug("Received response from server " + url + ". HTTP status code: " + httpresponse.getStatusLine().getStatusCode()
                            + "\nBody: " + response);
                }
                break;
            } catch (Exception e) {
                e.printStackTrace();
                if (LogHandler.isShowErrorLog()) {
                    LOG.error("Error while calling provider server. Details: " + Utils.printStackTrace(e));
                }
                numOfRetry--;
            } finally {
                if (client != null) {
                    try {
                        client.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                if (httpresponse != null) {
                    try {
                        httpresponse.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return new Response(response, httpCode, responseHeader);
    }

    public static class Response {

        private String body;
        private int httpCode;
        private HashMap<String, String> responseHeader;

        public Response(String body, int httpCode, HashMap<String, String> responseHeader) {
            this.body = body;
            this.httpCode = httpCode;
            this.responseHeader = responseHeader;
        }

        public HashMap<String, String> getResponseHeader() {
            return responseHeader;
        }

        public void setResponseHeader(HashMap<String, String> responseHeader) {
            this.responseHeader = responseHeader;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public int getHttpCode() {
            return httpCode;
        }

        public void setHttpCode(int httpCode) {
            this.httpCode = httpCode;
        }

    }
}
