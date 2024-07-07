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
import java.sql.*;

public class LeaveApplicationWebPlugin extends Element implements PluginWebSupport {
    public String renderTemplate(FormData formData, Map dataModel) {
        return "";
    }

    public String getName() {
        return "Leave Application Web";
    }

    public String getVersion() {
       return "1.0.0";
    }

    public String getDescription() {
        return "Leave Application Rest";
    }

    public String getLabel() {
        return "Leave Application Rest";
    }

    public String getClassName() {
        return this.getClass().getName();
    }

    public String getPropertyOptions() {
        return "";
    }

    public String dept, id, days,  hod;
    public int leave_balance;

    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, java.io.IOException {
        try {
            LogUtil.info("Leave Application Web Service", "Web-service Executed");
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            Connection con = ds.getConnection();
            //PreparedStatement ps = null;
            //ResultSet rs = null;
            //PreparedStatement ps1 = null;
            //ResultSet rs1 = null;

            //id = request.getParameter("username");
            //dept = request.getParameter("department");

            LogUtil.info("Leave Application Web Service", "Username: ");
            LogUtil.info("Leave Application Web Service", "Department: ");

            if (con != null) {

                try {
                    String query1 = "SELECT userId, leave_remaining FROM leave_balance;";
                    Statement statement1 = con.createStatement();
                    ResultSet resultSet1 = statement1.executeQuery(query1);

                    JsonArray jsonArray = new JsonArray();
                    while (resultSet1.next()) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("userId", resultSet1.getString("userId"));
                        obj.addProperty("balance", resultSet1.getString("leave_remaining"));
                        jsonArray.add(obj);
                    }

                    String query2 = "SELECT dir_employment.userId, dir_department.id FROM dir_employment INNER JOIN dir_department ON dir_employment.id = dir_department.hod;";
                    Statement statement2 = con.createStatement();
                    ResultSet resultSet2 = statement2.executeQuery(query2);

                    JsonArray jsonArray2 = new JsonArray();
                    while (resultSet2.next()) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("hodUserId", resultSet2.getString("userId"));
                        obj.addProperty("deptId", resultSet2.getString("id"));
                        jsonArray2.add(obj);
                    }

                    // Combine both results into a single JSON response
                    JsonObject combinedResponse = new JsonObject();
                    combinedResponse.add("leaveData", jsonArray);
                    combinedResponse.add("hodData", jsonArray2);

                    // Set response content type and write JSON to response
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(combinedResponse.toString());

                    // Close connections
                    con.close();
                    resultSet1.close();
                    statement1.close();
                    resultSet2.close();
                    statement2.close();
                } catch (SQLException e) {
                    // Handle SQL exceptions
                    e.printStackTrace();
                } catch (IOException e) {
                    // Handle IO exceptions
                    e.printStackTrace();
                }


                    /*
                    String query1 = "SELECT leave_remaining from leave_balance where userId = ?";
                    ps = con.prepareStatement(query1);
                    ps.setString(1, id);
                    rs = ps.executeQuery();

                    JsonArray jsonArray = new JsonArray();
                    int leave_balance = 0;

                    if (rs.next()) {
                        leave_balance = rs.getInt("leave_remaining");
                    } else {
                        LogUtil.info("Leave Application Web Service", "No leave balance found for user "+id);
                    }

                    String query2 = "select dir_employment.userId from dir_employment inner join dir_department on dir_employment.id = dir_department.hod where dir_department.id = ?";
                    ps1 = con.prepareStatement(query2);
                    ps1.setString(1, dept);
                    rs1 = ps1.executeQuery();
                    String hod = null;
                    if (rs1.next()) {
                        hod = rs1.getString("userId");
                    } else {
                        LogUtil.info("Leave Application Web Service", "No hod found for dept "+dept);
                    }

                    JsonObject obj = new JsonObject();
                    obj.addProperty("hod", hod);
                    obj.addProperty("leave_balance", leave_balance);
                    jsonArray.add(obj);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(jsonArray.toString());

                     */

            } else {
                LogUtil.info("webService", "JDBC Connection Failed");
            }
        } catch (Exception ex) {
            LogUtil.error("webService", ex, "Error in webService");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }





    @Override
    public String renderTemplate(FormData formData, java.util.Map map) {
        return null;
    }
}
