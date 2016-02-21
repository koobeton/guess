package my.server.frontend.ws;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

public class UserWebSocketCreator implements WebSocketCreator {

    private WebSocketService webSocketService;

    public UserWebSocketCreator(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest servletUpgradeRequest, ServletUpgradeResponse servletUpgradeResponse) {
        return new UserWebSocket(webSocketService);
    }
}
