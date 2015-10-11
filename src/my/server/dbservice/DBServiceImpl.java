package my.server.dbservice;

import my.server.base.Address;
import my.server.base.DBService;
import my.server.base.MessageSystem;
import my.server.base.Results;
import my.server.dbservice.dao.ResultsDAO;
import my.server.dbservice.dao.UsersDAO;
import my.server.dbservice.datasets.ResultsDataSet;
import my.server.dbservice.datasets.UsersDataSet;
import my.server.resourcesystem.DBResource;
import my.server.resourcesystem.ResourceFactory;
import my.server.utils.TimeHelper;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.List;

public class DBServiceImpl implements DBService {

    private static final DBResource DB_RESOURCE =
            (DBResource) ResourceFactory.instance().getResource("./data/DBResource.xml");
    private static final int SLEEP_TIME = DB_RESOURCE.getSleepTime();
    private static final String DB_NAME = DB_RESOURCE.getDBName();
    private static final String LOGIN = DB_RESOURCE.getLogin();
    private static final String PASSWORD = DB_RESOURCE.getPassword();

    private Address address;
    private MessageSystem ms;
    private SessionFactory sessionFactory;
    private UsersDAO usersDAO;
    private ResultsDAO resultsDAO;

    public DBServiceImpl(MessageSystem ms) {
        this.ms = ms;
        this.address = new Address();
        sessionFactory = getSessionFactory(DB_NAME, LOGIN, PASSWORD);
        usersDAO = new UsersDAO(sessionFactory);
        resultsDAO = new ResultsDAO(sessionFactory);
    }

    @Override
    public void run() {
        while(true) {
            ms.execForAbonent(this);
            TimeHelper.sleep(SLEEP_TIME);
        }
    }

    @Override
    public Integer getUserId(String name) {
        int id;
        UsersDataSet dataSet = usersDAO.get(name);
        if (dataSet == null) {
            usersDAO.add(new UsersDataSet(name));
            id = getUserId(name);
        } else id = dataSet.getId();

        return id;
    }

    @Override
    public void addResult(int userId, int attempts, long time) {
        resultsDAO.add(new ResultsDataSet(userId, attempts, time));
    }

    @Override
    public List<Results> getResults(int limit) {
        return resultsDAO.get(limit);
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public MessageSystem getMessageSystem() {
        return ms;
    }

    private SessionFactory getSessionFactory(String dbName, String login, String password) {

        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UsersDataSet.class);
        configuration.addAnnotatedClass(ResultsDataSet.class);

        configuration.setProperty("hibernate.dialect",
                "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class",
                "com.mysql.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url",
                "jdbc:mysql://localhost:3306/" + dbName);
        configuration.setProperty("hibernate.connection.username",
                login);
        configuration.setProperty("hibernate.connection.password",
                password);
        configuration.setProperty("hibernate.hbm2ddl.auto",
                "validate");

        ServiceRegistryBuilder builder = new ServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.buildServiceRegistry();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}
