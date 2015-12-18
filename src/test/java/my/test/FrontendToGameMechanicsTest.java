package my.test;

import my.server.base.Address;
import my.server.frontend.FrontendImpl;
import my.server.frontend.MsgStartGameSession;
import my.server.frontend.UserSession;
import my.server.gamemechanics.GameMechanicsImpl;
import my.server.messagesystem.MessageSystemImpl;
import my.server.utils.TimeHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

public class FrontendToGameMechanicsTest {

    private MessageSystemImpl messageSystem;
    private FrontendImpl frontend;
    private GameMechanicsImpl gameMechanics;

    private Map<Integer, UserSession> sessionIdToUserSession;
    private UserSession userSession;
    private int sessionId;

    @SuppressWarnings("unchecked")
    @Before
    public void init() throws Exception {

        messageSystem = new MessageSystemImpl();

        frontend = new FrontendImpl(messageSystem);
        messageSystem.addFrontend(frontend);

        gameMechanics = new GameMechanicsImpl(messageSystem);
        messageSystem.addGameMechanics(gameMechanics);

        new Thread(frontend).start();
        new Thread(gameMechanics).start();

        userSession = new UserSession();
        sessionId = userSession.getSessionId();

        Field field = frontend.getClass().getDeclaredField("sessionIdToUserSession");
        field.setAccessible(true);
        sessionIdToUserSession = (Map<Integer, UserSession>)field.get(frontend);
        sessionIdToUserSession.put(sessionId, userSession);
        field.setAccessible(false);
    }

    @Test
    public void run() {

        Address addressGM = messageSystem.getAddressService().getAddressGM();
        Address addressFrontend = messageSystem.getAddressService().getAddressFrontend();

        messageSystem.sendMessage(new MsgStartGameSession(
                        addressFrontend,
                        addressGM,
                        sessionId,
                        userSession.getUserId())
        );

        long firstTime = sessionIdToUserSession.get(sessionId).getGameSessionDuration();
        TimeHelper.sleep(300);

        Assert.assertTrue(firstTime < sessionIdToUserSession.get(sessionId).getGameSessionDuration());
    }
}
