/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
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

/**
 *
 * @author GiaTK
 */
@ClientEndpoint
public class WebSocketEndpoint {
    private final static Logger LOG = LogManager.getLogger(WebSocketEndpoint.class);
    
    public Session userSession;
    private MessageHandler messageHandler;
    
    public WebSocketEndpoint(URI endpointURI) {
        try{
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e){
            if(LogHandler.isShowErrorLog()){
                LOG.error("Error while create websocket!");
            }
            System.out.println("ex:"+e);
        }
    }
    
    @OnOpen   
    public void onOpen(Session userSession){
        System.out.println("Open websocket");
        this.userSession = userSession;
    }
    
    @OnClose
    public void onClose(Session userSession, CloseReason reason){
        System.out.println("Close websocket");
        this.userSession = null;
    }
    
    @OnMessage
    public void onMessage(String message){
        if(this.messageHandler != null){
            this.messageHandler.handleMessage(message);
        }
    }
    
    @OnMessage
    public void onMessage(ByteBuffer bytes){
        System.out.println("byte buffer");
    }
    
    public void addMessageHandler(MessageHandler msgHand){
        this.messageHandler = msgHand;
    }
    
    public void sendMessage(String message){
          
    }
    
    public static interface MessageHandler {

        public void handleMessage(String message);
    }
    
    public static void main(String[] agrs) throws URISyntaxException{
        URI uri = new URI("ws://127.0.0.1:9505/ISPlugin");
        WebSocketEndpoint endpoint = new WebSocketEndpoint(uri);
        System.out.println(endpoint.userSession.isOpen());
    }
}
