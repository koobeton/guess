package my.server.frontend.ws;

import my.server.resourcesystem.FrontendResource;
import my.server.resourcesystem.ResourceFactory;
import my.server.utils.JSONHelper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.Reader;

@WebSocket
public class UserWebSocket {

    private static final FrontendResource FRONTEND_RESOURCE =
            (FrontendResource) ResourceFactory.instance().getResource("./data/FrontendResource.xml");
    private static final String SESSION_ID = FRONTEND_RESOURCE.getSessionId();

    private WebSocketService webSocketService;
    private Session session;
    private int sessionId;

    public UserWebSocket(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        setSession(session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        webSocketService.removeWebSocket(this);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, Reader reader) {
        setSessionId(JSONHelper.readInt(SESSION_ID, reader));
        webSocketService.addWebSocket(this);
    }

    public Session getSession() {
        return session;
    }

    private void setSession(Session session) {
        this.session = session;
    }

    public int getSessionId() {
        return sessionId;
    }

    private void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }
}
