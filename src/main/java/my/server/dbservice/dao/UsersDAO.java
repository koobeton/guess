package my.server.dbservice.dao;

import my.server.dbservice.datasets.UsersDataSet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UsersDAO {

    private enum UsersQuery {
        SELECT_BY_NAME,
        INSERT_NEW
    }
    private static final Map<UsersQuery, String> statements = new ConcurrentHashMap<>();
    static {
        statements.put(UsersQuery.SELECT_BY_NAME, "select * from users where name=? limit 1");
        statements.put(UsersQuery.INSERT_NEW, "insert into users(name) values(?)");
    }

    private SessionFactory sessionFactory;

    public UsersDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public UsersDataSet get(final String name) {

        Session session = sessionFactory.openSession();
        return (UsersDataSet)session.createSQLQuery(statements.get(UsersQuery.SELECT_BY_NAME))
                .addEntity(UsersDataSet.class)
                .setString(0, name)
                .uniqueResult();
    }

    public void add(final UsersDataSet dataSet) {

        Session session = sessionFactory.openSession();
        Transaction trx = null;
        try {
            trx = session.beginTransaction();
            session.save(dataSet);
            trx.commit();
        } catch (Exception e) {
            if (trx != null) trx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}
