package my.test;

import my.server.dbservice.DBServiceImpl;
import my.server.frontend.FrontendImpl;
import my.server.frontend.UserSession;
import my.server.messagesystem.MessageSystemImpl;
import my.test.mock.MockUserSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

public class FrontendToDBServiceTest {

    private static final String USER_NAME = "Serge";

    private MessageSystemImpl messageSystem;
    private FrontendImpl frontend;
    private DBServiceImpl dbService;

    @Before
    public void init() {

        messageSystem = new MessageSystemImpl();

        frontend = new FrontendImpl(messageSystem);
        messageSystem.addFrontend(frontend);

        dbService = new DBServiceImpl(messageSystem);
        messageSystem.addDBService(dbService);

        new Thread(frontend).start();
        new Thread(dbService).start();
    }

    @Test
    public void run() throws Exception {

        MockUserSession mockUserSession = new MockUserSession();
        mockUserSession.setUserName(USER_NAME);

        Method method = frontend.getClass().getDeclaredMethod("handleReplica", UserSession.class);
        method.setAccessible(true);
        String answer = (String)method.invoke(frontend, mockUserSession);
        method.setAccessible(false);

        Assert.assertTrue(answer.contains(USER_NAME));
    }
}
