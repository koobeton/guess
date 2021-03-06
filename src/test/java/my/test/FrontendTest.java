package my.test;

import my.server.frontend.FrontendImpl;
import my.server.frontend.UserSession;
import my.server.messagesystem.MessageSystemImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.*;

public class FrontendTest {

    private static final int USER_ID = 10;
    private static final long DURATION = 1000;

    private static MessageSystemImpl messageSystem;
    private static FrontendImpl frontend;

    private static Map<Integer, UserSession> sessionIdToUserSession;
    private static UserSession userSession;
    private static int sessionId;

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void setUp() throws Exception {

        messageSystem = new MessageSystemImpl();

        frontend = new FrontendImpl(messageSystem);
        messageSystem.addFrontend(frontend);

        new Thread(frontend).start();

        userSession = new UserSession();
        sessionId = userSession.getSessionId();

        Field field = frontend.getClass().getDeclaredField("sessionIdToUserSession");
        field.setAccessible(true);
        sessionIdToUserSession = (Map<Integer, UserSession>)field.get(frontend);
        sessionIdToUserSession.put(sessionId, userSession);
        field.setAccessible(false);
    }

    @Test
    public void testSetId() throws Exception {

        frontend.setId(sessionId, USER_ID);
        assertEquals(USER_ID, sessionIdToUserSession.get(sessionId).getUserId());
    }

    @Test
    public void testSetGameSessionDuration() throws Exception {

        frontend.setGameSessionDuration(sessionId, DURATION);
        assertEquals(DURATION, sessionIdToUserSession.get(sessionId).getGameSessionDuration());
    }
}