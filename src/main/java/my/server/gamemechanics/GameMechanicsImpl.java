package my.server.gamemechanics;

import my.server.base.Address;
import my.server.base.GameMechanics;
import my.server.base.GameReplica;
import my.server.base.MessageSystem;
import my.server.resourcesystem.GameResource;
import my.server.resourcesystem.ResourceFactory;
import my.server.utils.TimeHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameMechanicsImpl implements GameMechanics {

    private static final GameResource GAME_RESOURCE =
            (GameResource) ResourceFactory.instance().getResource("./data/GameResource.xml");
    private static final int LOWER_BOUND = GAME_RESOURCE.getLowerBound();
    private static final int UPPER_BOUND = GAME_RESOURCE.getUpperBound();
    private static final int SLEEP_TIME = GAME_RESOURCE.getSleepTime();

    private Address address;
    private MessageSystem ms;

    private Map<Integer, GameSession> games = new ConcurrentHashMap<>();

    public GameMechanicsImpl(MessageSystem ms) {
        this.ms = ms;
        this.address = new Address();
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void run() {
        while(true) {
            ms.execForAbonent(this);
            sendGameSessionsDuration();
            TimeHelper.sleep(SLEEP_TIME);
        }
    }

    private void sendGameSessionsDuration() {
        for (Integer sessionId : games.keySet()) {
            long duration = games.get(sessionId).getDuration();
            Address addressFrontend = ms.getAddressService().getAddressFrontend();
            ms.sendMessage(new MsgGameSessionDuration(getAddress(), addressFrontend, sessionId, duration));
        }
    }

    private void sendSaveResult(int sessionId) {
        GameSession gameSession = games.get(sessionId);
        Address addressDBS = ms.getAddressService().getAddressDBS();
        ms.sendMessage(new MsgSaveResult(
                getAddress(),
                addressDBS,
                gameSession.getUserId(),
                gameSession.getReplica().getAttempts(),
                gameSession.getDuration())
        );
    }

    @Override
    public GameReplica startNewGame(int sessionId, int userId) {
        games.put(sessionId, new GameSession(userId, LOWER_BOUND, UPPER_BOUND));
        return games.get(sessionId).getReplica();
    }

    @Override
    public GameReplica handleTurn(int sessionId, int turn) {
        GameReplica gameReplica = games.get(sessionId).handleTurn(turn);
        if (gameReplica.isGameOver()) {
            sendSaveResult(sessionId);
        }
        return gameReplica;
    }

    @Override
    public MessageSystem getMessageSystem() {
        return ms;
    }
}
