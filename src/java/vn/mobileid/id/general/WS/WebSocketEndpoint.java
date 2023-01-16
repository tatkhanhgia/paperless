/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.WS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.logging.Level;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vn.mobileid.id.general.LogHandler;
import vn.mobileid.id.eid.object.InterfaceCommunicationEID;
import vn.mobileid.id.eid.object.RequireBiometricEvidence;
import vn.mobileid.id.utils.Utils;

/**
 *
 * @author GiaTK
 */
public class WebSocketEndpoint {

    private final static Logger LOG = LogManager.getLogger(WebSocketEndpoint.class);
    private static HashMap<String, WebSocket> listSocket = new HashMap<>();

    private static WebSocketEndpoint instant;

    public static WebSocketEndpoint createWebSocket(String token, URI endpointURI) {
        try {
            if (instant == null) {
                instant = new WebSocketEndpoint();
                listSocket = new HashMap<>();
            }

            WebSocket socket = new WebSocket(endpointURI);
            if (listSocket.containsKey(token)) {
                return instant;
            }
            listSocket.put(token, socket);
            return instant;
        } catch (Exception e) {
            if (LogHandler.isShowErrorLog()) {
                LOG.error("Error while create websocket!");
            }
            System.out.println("ex:" + e);
            return null;
        }
    }

    public void sendMessage(String token, String message) throws Exception {
        if (listSocket == null || listSocket.isEmpty()) {
            throw new Exception("List Socket is empty");
        }
        if (!listSocket.containsKey(token)) {
            throw new Exception("This token doesn't contain in the list");
        }
        listSocket.get(token).sendMessage(message);
    }

    public String getMessage(String token) throws Exception {
        if (listSocket == null || listSocket.isEmpty()) {
            throw new Exception("List Socket is empty");
        }
        if (!listSocket.containsKey(token)) {
            throw new Exception("This token doesn't contain in the list");
        }
        return listSocket.get(token).getMessageHandler().getMessage();
    }

    /*
    public WebSocket getSocket(String token) {
        if (listSocket.isEmpty()) {
            return null;
        }
        return listSocket.get(token);
    } */

    public void closeSocket(String token) {
        WebSocket object = listSocket.get(token);
        object.closeConnection();
        listSocket.remove(token);
    }

    public static void main(String[] agrs) throws URISyntaxException, JsonProcessingException {
        try {
            URI uri = new URI("ws://127.0.0.1:9505/ISPlugin");
            WebSocketEndpoint socket = WebSocketEndpoint.createWebSocket("token1", uri);
//        WebSocketEndpoint endpoint = new WebSocketEndpoint(uri);
//        endpoint.addMessageHandler(new MessageHandler() {
//            @Override
//            public void handleMessage(String message) {
//                System.out.println("Message:"+message);
//            }
//        });           

            InterfaceCommunicationEID requests = new InterfaceCommunicationEID<RequireBiometricEvidence>();
            requests.setCmdType("BiometricEvidence");
            requests.setRequestID(Utils.generateTransactionID_noRP());
            requests.setTimeOutInterval(60);
            requests.setData(new RequireBiometricEvidence().setFaceID());            
            
//        System.out.println("Is WS Open?" + socket.userSession.isOpen());
            socket.sendMessage("token1", new ObjectMapper().writeValueAsString(requests));
            System.out.println("Messge:"+socket.getMessage("token1"));
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(WebSocketEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
