package org.joget.sample;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class SamplePluginTool extends DefaultApplicationPlugin {
    private Connection con = null;
    private PreparedStatement stmt = null;

    public String getName() {
        return "AccroJoget - Sample Process Tool Plugin";
    }

    public String getVersion() {
        return "1.0";
    }

    public String getDescription() {
        return "AccroJoget - Sample Tool Plugin part of Joget Training";
    }

    public String getLabel() {
        return "AccroJoget - Sample Tool Plugin";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClassName(), "/properties/student_details.json", null, true, "messages/CRRcreator");
    }

    public Object execute(Map map) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            LogUtil.info("AccroJogetSamplePlugin", "Plugin executed successfully");
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            QueryHandler qh = new QueryHandler();
            String name = getPropertyString("full_name");
            String roll = getPropertyString("roll");
            LogUtil.info("RevaluationPlugin", name);
            LogUtil.info("RevaluationPlugin", roll);
            qh.updateRevalTable(name, roll, con);

            LogUtil.info("AccroJogetSamplePlugin", "User name inserted into revaluation table successfully");

        } catch (Exception e) {
            LogUtil.error("AccroJogetSamplePlugin", e, "Error executing plugin");
        } finally {
            // Close resources
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    LogUtil.error("AccroJogetSamplePlugin", e, "Error closing preparedStatement");
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LogUtil.error("AccroJogetSamplePlugin", e, "Error closing connection");
                }
            }
        }
        return null;
    }
}
