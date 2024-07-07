package org.joget.sample;



import org.joget.commons.util.LogUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateResult {

    public void updateStudentResult(String name,String roll, Connection con) throws SQLException {

        String query = "INSERT INTO REVALUATION (Name, Roll, Reval) VALUES (?,?,'Yes')";

        PreparedStatement stmt = con.prepareStatement(query);

        stmt.setString(1, name);
        stmt.setString(2, roll);
        stmt.executeUpdate();
        LogUtil.info("", "===== Revaluation Table Updated ======");

    }
}

