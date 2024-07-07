package org.joget.sample;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.*;
import org.joget.commons.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.joget.sample.JDBCBinder.MESSAGE_PATH;

public class DataListBinder extends DataListBinderDefault {

    public static int MAXROWS = 10000;
    public static String ALIAS = "temp";
    private DataListColumn[] columns;

    public String getName() {
        return "Data List Binder";
    }

    public String getVersion() {
        return "1.0.0";
    }

    public String getDescription() {
        return "Data List Binder";
    }

    public String getLabel() {
        return "Data List Binder";
    }

    public String getClassName() {
        return this.getClass().getName();
    }

    public String getPrimaryKeyColumnName() {
        return "id";
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClassName(), "/properties/datalistbinder.json", null, true, MESSAGE_PATH);
    }

    @Override
    public DataListColumn[] getColumns() {
        if (this.columns == null) {
            List<DataListColumn> columns = new ArrayList<>();
            try {
                DataSource ds = createDataSource();
                Connection con = ds.getConnection();
                String tableName = getPropertyString("tableName");
                PreparedStatement pstmt = con.prepareStatement("SHOW COLUMNS FROM " + tableName);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String columnName = rs.getString("Field");
                    columns.add(new DataListColumn(columnName, columnName, true));
                }
                rs.close();
                pstmt.close();
                con.close();
            } catch (SQLException e) {
                LogUtil.error(DataListBinder.class.getName(), e, "Error retrieving columns for table " + getPropertyString("tableName"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.columns = columns.toArray(new DataListColumn[0]);
        }
        return this.columns;

    }

    protected String getQuerySelect(DataList dataList, Map properties) {
        String selectColumns = "";
        DataListColumn[] columns = dataList.getColumns();
        for (DataListColumn column : columns) {
            selectColumns += column.getName() + ",";
        }
        selectColumns = selectColumns.substring(0, selectColumns.length() - 1);
        String tableName = getPropertyString("tableName");
        String sql = "SELECT " + selectColumns + " FROM " + tableName + " " + ALIAS;
        return sql;
    }

    @Override
    public DataListCollection getData(DataList dataList, Map properties, DataListFilterQueryObject[] filterQueryObjects, String sort, Boolean desc, Integer start, Integer rows) {
        try {
            DataSource ds = createDataSource();
            String sql = getQuerySelect(dataList, properties);
            DataListCollection results = executeQuery(dataList, ds, sql);
            return results;
        } catch (Exception ex) {
            LogUtil.error(DataListBinder.class.getName(), ex, "Error getting data");
            return null;
        }
    }

    protected DataListCollection executeQuery(DataList dataList, DataSource ds, String sql) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        DataListCollection<Map> results = new DataListCollection<>();
        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            DataListColumn[] columns = dataList.getColumns();
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                for (DataListColumn column : columns) {
                    String columnName = column.getName();
                    String columnValue = rs.getString(columnName);
                    row.put(columnName, columnValue);
                }
                results.add(row);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return results;
    }

    protected DataSource createDataSource() throws Exception {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        return ds;
    }

    

    @Override
    public int getDataTotalRowCount(DataList dataList, Map properties, DataListFilterQueryObject[] filterQueryObjects) {
        try {
            DataSource ds = createDataSource();
            String sqlCount = getQueryCount(dataList, properties);
            return executeQueryCount(ds, sqlCount);
        } catch (Exception ex) {
            LogUtil.error(DataListBinder.class.getName(), ex, "Error getting total row count");
            return 0;
        }
    }

    protected String getQueryCount(DataList dataList, Map properties) {
        String tableName = getPropertyString("tableName");
        String sql = "SELECT COUNT(*) FROM " + tableName + " " + ALIAS;
        return sql;
    }

    protected int executeQueryCount(DataSource ds, String sql) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = ds.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }
}
