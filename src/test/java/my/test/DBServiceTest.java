package my.test;

import my.server.dbservice.DBServiceImpl;
import my.server.messagesystem.MessageSystemImpl;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DBServiceTest {

    private static final int USER_ID = 1;
    private static final String USER_NAME = "Serge";
    private static final int LIMIT = 3;

    private static MessageSystemImpl messageSystem;
    private static DBServiceImpl dbService;

    @BeforeClass
    public static void setUp() throws Exception {

        messageSystem = new MessageSystemImpl();

        dbService = new DBServiceImpl(messageSystem);
        messageSystem.addDBService(dbService);

        new Thread(dbService).start();
    }

    @Test
    public void testGetUserId() throws Exception {

        assertEquals(USER_ID, (int)dbService.getUserId(USER_NAME));
    }

    @Test
    public void testGetResults() throws Exception {

        assertEquals(LIMIT, dbService.getResults(LIMIT).size());
    }

    @Test
    public void testGetMessageSystem() throws Exception {

        assertEquals(messageSystem, dbService.getMessageSystem());
    }
}