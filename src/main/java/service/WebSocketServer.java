package service;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/websocket/{sid}")
@Component
public class WebSocketServer {
    static Log log = LogFactory.getLog(WebSocketServer.class);

    private static int onlineCount = 0;

    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet
            = new CopyOnWriteArraySet<>();

    private javax.websocket.Session session;

    private String sid = "";

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();
        log.info("There is a new thread: " + sid + ", current online number is " + getOnlineCount());
        this.sid = sid;
        try {
            sendMessage("link start");
        } catch (IOException e) {
            log.error("IO EXCEPTION");
        }

    }

    @OnClose
    public void onClose(){
        webSocketSet.remove(this);
        subOnlineCount();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static void sendInfo(String message, @PathParam("sid") String sid) throws IOException {
        log.info("send message to thread " + sid + ": " + message);
        for(WebSocketServer item : webSocketSet){
            try {
                if(sid == null){
                    item.sendMessage(message);
                }else if(item.sid.equals(sid)){
                    item.sendMessage(message);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
