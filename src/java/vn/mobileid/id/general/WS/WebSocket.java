/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general.WS;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
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
public class WebSocket {
    private final static Logger LOG = LogManager.getLogger(WebSocket.class);
    
    private Session userSession;
    private MessageHandler messageHandler;

    public WebSocket(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.userSession = container.connectToServer(this, endpointURI);
            this.messageHandler = new MessageHandler();
            System.out.println("Is open:"+userSession.isOpen());
        } catch (DeploymentException ex) {
            if(LogHandler.isShowErrorLog()){
                LOG.error("Deployment Exception - Details:"+ex);
            }
        } catch (IOException ex) {
            LOG.error("IO Exception - Details:"+ex);
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
        System.out.println("Receive:"+message);
    }
    
    @OnMessage
    public void onMessage(ByteBuffer bytes){
        System.out.println("byte buffer");
    }
    
        
    public void sendMessage(String message){
        System.out.println("Send:"+message);
        this.userSession.getAsyncRemote().sendText(message);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(WebSocketEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeConnection(){
        try {
            this.userSession.close();
        } catch (IOException ex) {
            if(LogHandler.isShowErrorLog()){
                LOG.error("Error while closing connection - Details:"+ex);
            }
        }
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }
    
    
    public void update(MessageHandler hand) {
        update(null, hand);
    }

    public void update(Session session, MessageHandler hand) {
        userSession = (session == null ? userSession : session);
        messageHandler = hand;
    }
    
    public class MessageHandler {

        private String message;

        public MessageHandler() {
        }

        public void handleMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
    
    public static void main(String[] args) throws URISyntaxException{
        WebSocket ob = new WebSocket(new URI("ws://127.0.0.1:9505/ISPlugin"));
        ob.sendMessage("hello");
    }
}
