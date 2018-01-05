package com.cc.p2p.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * this is an util class used for providing basic connection and release opeations
 * 
 * @author wangcongcong
 *
 */
public class JDBCUtils {
	public static final String DRIVERCLASS = "com.mysql.jdbc.Driver";
	public static final String URL = "jdbc:mysql://localhost:3306/p2p?userUnicode=True&characterEncoding=utf8";
	public static final String USER = "root";
	public static final String PSD = "123";

	public static Connection getConnection() throws Exception {
		loadDriver();
		Connection connection = DriverManager.getConnection(URL, USER, PSD);
		return connection;

	}

	private static void loadDriver() throws ClassNotFoundException {

		Class.forName(DRIVERCLASS);

	}

	public static void release(ResultSet rs, Statement statement, Connection cnn) {

		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			rs = null;
		}
		release(statement, cnn);

	}

	public static void release(Statement statement, Connection cnn) {

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			statement = null;
		}

		if (cnn != null) {
			try {
				cnn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			cnn = null;
		}

	}

}
