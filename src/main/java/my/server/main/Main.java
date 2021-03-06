package my.server.main;

import my.server.dbservice.DBServiceImpl;
import my.server.frontend.FrontendImpl;
import my.server.gamemechanics.GameMechanicsImpl;
import my.server.messagesystem.MessageSystemImpl;
import my.server.resourcesystem.ResourceFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {

    public static void main(String... args) throws Exception {

        ResourceFactory.instance();
        MessageSystemImpl messageSystem = new MessageSystemImpl();

        FrontendImpl frontend = new FrontendImpl(messageSystem);
        messageSystem.addFrontend(frontend);

        DBServiceImpl dbService = new DBServiceImpl(messageSystem);
        messageSystem.addDBService(dbService);

        GameMechanicsImpl gameMechanics = new GameMechanicsImpl(messageSystem);
        messageSystem.addGameMechanics(gameMechanics);

        new Thread(frontend).start();
        new Thread(dbService).start();
        new Thread(gameMechanics).start();

        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(frontend), "");
        server.setHandler(context);

        server.start();
        server.join();
    }
}
