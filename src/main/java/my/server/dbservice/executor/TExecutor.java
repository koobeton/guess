package my.server.dbservice.executor;

import my.server.dbservice.handlers.PSHandler;
import my.server.dbservice.handlers.TResultHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TExecutor {

    public <T> T execQuery(Connection connection,
                           String query,
                           PSHandler psHandler,
                           TResultHandler<T> handler)
            throws SQLException {

        PreparedStatement pstmt = null;
        ResultSet result = null;
        T value = null;

        try {
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(query);
            psHandler.setParameters(pstmt);
            pstmt.executeQuery();
            result = pstmt.getResultSet();
            value = handler.handle(result);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
            if (result != null) result.close();
            if (pstmt != null) pstmt.close();
        }

        return value;
    }

    public void execUpdate(Connection connection, String update, PSHandler handler)
            throws SQLException {

        PreparedStatement pstmt = null;

        try {
            connection.setAutoCommit(false);
            pstmt = connection.prepareStatement(update);
            handler.setParameters(pstmt);
            pstmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
        } finally {
            connection.setAutoCommit(true);
            if (pstmt != null) pstmt.close();
        }
    }
}
