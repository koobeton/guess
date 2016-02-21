package my.server.frontend.ws;

import my.server.resourcesystem.FrontendResource;
import my.server.resourcesystem.ResourceFactory;
import my.server.utils.TimeHelper;

import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebSocketService implements Runnable {

    private static final FrontendResource FRONTEND_RESOURCE =
            (FrontendResource) ResourceFactory.instance().getResource("./data/FrontendResource.xml");
    private static final int SLEEP_TIME = FRONTEND_RESOURCE.getSleepTime();

    private Map<Integer, UserWebSocket> sockets = new ConcurrentHashMap<>();
    private Queue<Map<Integer, String>> queue = new ConcurrentLinkedQueue<>();

    public void addWebSocket(UserWebSocket webSocket) {
        sockets.put(webSocket.getSessionId(), webSocket);
    }

    public void removeWebSocket(UserWebSocket webSocket) {
        sockets.remove(webSocket.getSessionId());
    }

    public void sendMessage(int sessionId, String msg) {
        queue.add(Collections.singletonMap(sessionId, msg));
    }

    @Override
    public void run() {
        while (true) {
            while (!queue.isEmpty()) {
                Map<Integer, String> map = queue.poll();
                map.keySet().stream()
                        .filter(sessionId -> sockets.containsKey(sessionId) && sockets.get(sessionId).getSession().isOpen())
                        .forEach(sessionId -> sockets.get(sessionId).getSession().getRemote().sendStringByFuture(map.get(sessionId)));
            }
            TimeHelper.sleep(SLEEP_TIME);
        }
    }
}
