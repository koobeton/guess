package my.test;

import my.server.base.Frontend;
import my.server.gamemechanics.GameMechanicsImpl;
import my.server.messagesystem.MessageSystemImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GameMechanicsTest {

    private static final int SESSION_ID = 10;
    private static final int USER_ID = 25;
    private static final int UPPER_BOUND = 100;
    private static final int TURN = 37;

    private MessageSystemImpl messageSystem;
    private GameMechanicsImpl gameMechanics;
    @Mock
    private Frontend mockFrontend;

    @Before
    public void setUp() throws Exception {

        messageSystem = new MessageSystemImpl();

        gameMechanics = new GameMechanicsImpl(messageSystem);
        messageSystem.addGameMechanics(gameMechanics);

        messageSystem.addFrontend(mockFrontend);

        new Thread(gameMechanics).start();
        new Thread(mockFrontend).start();
    }

    @Test
    public void testStartNewGame() throws Exception {

        assertEquals(UPPER_BOUND, gameMechanics.startNewGame(SESSION_ID, USER_ID).getUpperBound());
    }

    @Test
    public void testHandleTurn() throws Exception {

        gameMechanics.startNewGame(SESSION_ID, USER_ID);
        assertEquals(TURN, gameMechanics.handleTurn(SESSION_ID, TURN).getLastTurn());
    }

    @Test
    public void testGetMessageSystem() throws Exception {

        assertEquals(messageSystem, gameMechanics.getMessageSystem());
    }
}