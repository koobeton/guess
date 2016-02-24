package my.test;

import my.server.base.GameReplica;
import my.server.base.Results;
import my.server.dbservice.DBServiceImpl;
import my.server.frontend.FrontendImpl;
import my.server.frontend.UserSession;
import my.server.frontend.html.PageGenerator;
import my.server.messagesystem.MessageSystemImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FrontendToDBServiceTest {

    private static final String USER_NAME = "Serge";

    private MessageSystemImpl messageSystem;
    private FrontendImpl frontend;
    private DBServiceImpl dbService;
    @Mock
    private UserSession mockUserSession;
    @Mock
    private GameReplica mockGameReplica;

    @Before
    public void init() {

        messageSystem = new MessageSystemImpl();

        frontend = new FrontendImpl(messageSystem);
        messageSystem.addFrontend(frontend);

        dbService = new DBServiceImpl(messageSystem);
        messageSystem.addDBService(dbService);

        new Thread(frontend).start();
        new Thread(dbService).start();

        when(mockGameReplica.isGameOver()).thenReturn(true);
        when(mockUserSession.getGameReplica()).thenReturn(mockGameReplica);
        when(mockUserSession.getUserName()).thenReturn("");
    }

    @Test
    public void run() throws Exception {

        Method method = frontend.getClass().getDeclaredMethod("handleReplica",
                UserSession.class, Function.class, BiFunction.class);
        method.setAccessible(true);
        String answer = (String)method.invoke(frontend,
                mockUserSession,
                (Function<UserSession, String>) o -> "",
                (BiFunction<UserSession, List<Results>, String>) PageGenerator::getGameOverPage
        );
        method.setAccessible(false);

        Assert.assertTrue(answer.contains(USER_NAME));
    }
}
