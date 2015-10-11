package my.test.mock;

import my.server.base.MessageSystem;
import my.server.frontend.FrontendImpl;

public class MockFrontend extends FrontendImpl {

    public MockFrontend(MessageSystem ms) {
        super(ms);
    }

    @Override
    public void setGameSessionDuration(int sessionId, long duration) {
        //nop
    }
}
