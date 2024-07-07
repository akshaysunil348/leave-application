package org.joget.sample;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hibernate.mapping.Map;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SampleWebRequest extends Element implements PluginWebSupport {
    public String renderTemplate(FormData formData, Map dataModel) {
        return "";
    }

    public String getName() {
        return "Student Details Rest- Sample";
    }

    public String getVersion() {
        return "1.0.0";
    }

    public String getDescription() {
        return "Student Details Rest";
    }

    public String getLabel() {
        return "Student Details Rest";
    }

    public String getClassName() {
        return this.getClass().getName();
    }

    public String getPropertyOptions() {
        return "";
    }

    public String name, roll, year, reval;

    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        try {
            LogUtil.info("AccorSampleWebservice","Web-service Executed");
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            Connection con = ds.getConnection();

            if (con!=null) {
                try {
                    String query = "SELECT firstName, lastName, leave_remaining FROM leave_balance INNER JOIN dir_user ON leave_balance.userId COLLATE utf8mb4_unicode_ci = dir_user.id;\n;";
                    Statement statement = con.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);

                    JsonArray jsonArray = new JsonArray();
                    while (resultSet.next()) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("fname", resultSet.getString("firstName"));
                        obj.addProperty("lname", resultSet.getString("lastName"));
                        obj.addProperty("balance", resultSet.getString("leave_remaining"));
                        //obj.addProperty("reval", resultSet.getString("Reval"));
                        jsonArray.add(obj);
                    }

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(jsonArray.toString());
                    con.close();

                } catch (SQLException e) {
                    LogUtil.info("webService", e.getMessage());
                }
            } else {
                LogUtil.info("webService","JDBC Connection Failed");
            }
        } catch (Exception ex) {
            LogUtil.info("webService", ex.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }






    @Override
    public String renderTemplate(FormData formData, java.util.Map map) {
        return null;
    }
}

