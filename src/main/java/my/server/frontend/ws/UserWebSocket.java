package my.server.frontend.ws;

import my.server.resourcesystem.FrontendResource;
import my.server.resourcesystem.ResourceFactory;
import my.server.utils.JSONHelper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.HashMap;
import java.util.Map;

@WebSocket
public class UserWebSocket {

    private static final FrontendResource FRONTEND_RESOURCE =
            (FrontendResource) ResourceFactory.instance().getResource("./data/FrontendResource.xml");
    private static final String SESSION_ID = FRONTEND_RESOURCE.getSessionId();
    private static final String TURN = FRONTEND_RESOURCE.getTurn();

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
    public void onMessage(Session session, String text) {

        Integer sessionIdValue = JSONHelper.readInt(SESSION_ID, text);
        String turnValue = JSONHelper.readString(TURN, text);

        if (sessionIdValue != null) {
            setSessionId(sessionIdValue);
            webSocketService.addWebSocket(this);
        }

        if (turnValue != null) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put(SESSION_ID, Integer.toString(getSessionId()));
            parameters.put(TURN, turnValue);
            webSocketService.handleMessage(parameters);
        }
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
