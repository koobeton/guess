package my.server.dbservice.dao;

import my.server.base.Results;
import my.server.dbservice.datasets.ResultsDataSet;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ResultsDAO {

    private enum ResultsQuery {
        INSERT_NEW,
        GET
    }
    private static final Map<ResultsQuery, String> statements = new ConcurrentHashMap<>();
    static {
        statements.put(ResultsQuery.INSERT_NEW,
                "insert into results(user_id, attempts, time) values(?, ?, ?)");
        statements.put(ResultsQuery.GET,
                "select users.name, results.attempts, results.time " +
                    "from users, results " +
                    "where users.id = results.user_id " +
                    "order by results.attempts, results.time " +
                    "limit ?");
    }

    private SessionFactory sessionFactory;

    public ResultsDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void add(final ResultsDataSet dataSet) {

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

    public List<Results> get(final int limit) {

        Session session = sessionFactory.openSession();
        ScrollableResults scroll = session.createSQLQuery(statements.get(ResultsQuery.GET))
                .addScalar("name", StringType.INSTANCE)
                .addScalar("attempts", IntegerType.INSTANCE)
                .addScalar("time", LongType.INSTANCE)
                .setInteger(0, limit)
                .scroll();
        List<Results> list = new CopyOnWriteArrayList<>();
        while (scroll.next()) {
            list.add(new ResultsDataSet(
                            scroll.getString(0),
                            scroll.getInteger(1),
                            scroll.getLong(2)
                    )
            );
        }
        session.close();
        return list;
    }
}
