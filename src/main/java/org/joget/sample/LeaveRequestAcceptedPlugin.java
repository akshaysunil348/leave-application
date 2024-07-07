package org.joget.sample;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class LeaveRequestAcceptedPlugin extends DefaultApplicationPlugin {


    public String getName() {
        return "Leave Application - Save Employee Leave Data";
    }

    public String getVersion() {
        return "1.0";
    }

    public String getDescription() {
        return "Leave Application - Plugin to save the approved leave requests";
    }

    public String getLabel() {
        return "Leave Application - Leave Data Plugin";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClassName(), "/properties/leave_data.json", null, true, "messages/CRRcreator");
    }
    public Object execute(Map map) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            LogUtil.info("Leave Application", "Plugin executed successfully");
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            String username = getPropertyString("username");
            String days = getPropertyString("days");
            String start = getPropertyString("startDate");
            String end = getPropertyString("endDate");
            String dept = getPropertyString("department");
            String approver = getPropertyString("approver");
            LogUtil.info("Leave Application plugin", username);
            LogUtil.info("RevaluationPlugin", days);

            int balance = Integer.parseInt(days);

            String query1 = "update leave_balance set leave_remaining=leave_remaining-? where userId=?;";
            PreparedStatement stmt = con.prepareStatement(query1);

            stmt.setInt(1, balance);
            stmt.setString(2, username);
            stmt.executeUpdate();


            LogUtil.info("Leave Application Plugin", "Leave balance Updated");

            String query2 = "insert into leave_approved values(?,?,?,?,?,?);";
            PreparedStatement stmt2 = con.prepareStatement(query2);

            stmt2.setString(1, username);
            stmt2.setString(2, dept);
            stmt2.setString(3, start);
            stmt2.setString(4, end);
            stmt2.setInt(5, balance);
            stmt2.setString(6, approver);
            stmt2.executeUpdate();

            LogUtil.info("Leave Application Plugin", "Leave Approved table Updated");


        } catch (Exception e) {
            LogUtil.error("Leave Application Plugin", e, "Error executing plugin");
        } finally {
            // Close resources
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    LogUtil.error("leave Application Plugin", e, "Error closing preparedStatement");
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    LogUtil.error("leave Application Plugin", e, "Error closing connection");
                }
            }
        }
        return null;
    }
}
