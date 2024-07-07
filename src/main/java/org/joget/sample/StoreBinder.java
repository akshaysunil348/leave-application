package org.joget.sample;


import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StoreBinder extends FormBinder implements FormStoreBinder, FormStoreElementBinder {



    public String getName() {
        return "New Department StoreBinder";
    }

    public String getVersion() {
        return "1.0";
    }

    public String getDescription() {
        return "New Department Store Description";
    }

    public String getLabel() {
        return "New Department SB";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClassName(), "", null, true, "");
    }



    public FormRowSet store(Element element, FormRowSet rows, FormData formData) {
        Connection con = null;
        //ResultSet rs= null;
        PreparedStatement insertStmt = null;
        FormRow originalRow = rows.get(0);
        String id = originalRow.getProperty("id");
        String name = originalRow.getProperty("name");
        String description = originalRow.getProperty("description");
        String organizationId = originalRow.getProperty("organizationId");
        String hod = originalRow.getProperty("hod");
        LogUtil.info("Name", name);
        LogUtil.info("hod", hod);
        LogUtil.info("oid", organizationId);

        try {
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();

            //String selectQuery = "SELECT Sub1, Sub2, Sub3, percentage FROM result WHERE Name = ? ";
            String insertQuery = "INSERT INTO dir_department (id, name, description, organizationId, hod) VALUES (?, ?, ?, ?, ?)";
            insertStmt = con.prepareStatement(insertQuery);
            insertStmt.setString(1, id);
            insertStmt.setString(2, name);
            insertStmt.setString(3, description);
            insertStmt.setString(4, organizationId);
            insertStmt.setString(5, hod);

            insertStmt.executeUpdate();
        } catch (Exception e) {
            LogUtil.error("AccroJogetSamplePlugin", e, "Error executing plugin");
        } finally {
            // Close resources
            if (insertStmt != null) {
                try {
                    insertStmt.close();
                } catch (SQLException e) {
                    LogUtil.error("AccroJogetSamplePlugin", e, "Error closing PreparedStatement");
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    LogUtil.error("AccroJogetSamplePlugin", e, "Error closing Connection");
                }
            }
        }

        //normalStoring(element, rows, formData);
        return rows;
        //LogUtil.info("Messa",id);
    }


    public void normalStoring(Element element, FormRowSet rows, FormData formData) {
        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        FormStoreBinder binder = (FormStoreBinder) pluginManager.getPlugin("org.joget.apps.form.lib.WorkflowFormBinder");
        binder.store(element, rows, formData);
    }

}
