package my.server.dbservice.handlers;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PSHandler {
    void setParameters(PreparedStatement pstmt) throws SQLException;
}
