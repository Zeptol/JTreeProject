package com.company.dao;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class BaseDao {
    private static String userName;
    private static String pwd;
    private static String url;
    private static String driver;

    static {
        try {
            Properties prop = new Properties();
            InputStream in = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");//读取文件
            prop.load(in);//加载文件
            userName = prop.getProperty("userName");
            pwd = prop.getProperty("pwd");
            url = prop.getProperty("url");
            driver = prop.getProperty("driver");
            Class.forName(driver);// 驱动
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获得连接
    static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, userName, pwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭资源
     */
    static void closeAll(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
    }

    /**
     * 动态执行sql
     *
     * @param sql:预编译的sql
     * @param objs:可变参数
     */
    public void executeSql(String sql, Object[] objs) {
        //获得连接
        Connection conn = getConnection();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            //设置参数
            for (int i = 0; i < objs.length; i++) {
                ps.setObject(i + 1, objs[i]);
            }
            //执行sql
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeAll(conn, ps, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
