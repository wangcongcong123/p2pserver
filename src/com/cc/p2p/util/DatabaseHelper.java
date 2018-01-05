package com.cc.p2p.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.cc.p2p.bean.InfoBean;
import com.cc.p2p.bean.UserAndFile;

/**
 * this is an util class responsible for providing methods of operating database
 * 
 * @author wangcongcong
 *
 */
public class DatabaseHelper {

	private static Connection cnnConnection;
	private static PreparedStatement statement;
	private static ResultSet rsResultSet;

	public static boolean Login(InfoBean mb) {
		try {
			cnnConnection = JDBCUtils.getConnection();
			String sql = "SELECT * FROM users";
			statement = cnnConnection.prepareStatement(sql);
			rsResultSet = statement.executeQuery();
			boolean isCorrect = false;
			while (rsResultSet.next()) {
				if (mb.getAccount().equals(rsResultSet.getString("user_account"))
						&& mb.getPassword().equals(rsResultSet.getString("password"))) {
					isCorrect = true;
				}
			}
			if (!isCorrect) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	public static String Register(InfoBean mb) {
		try {
			cnnConnection = JDBCUtils.getConnection();
			String sql = "insert into users values(?,?)";
			statement = cnnConnection.prepareStatement(sql);
			statement.setString(1, mb.getAccount());
			statement.setString(2, mb.getPassword());
			statement.executeUpdate();
			return "registered";
		} catch (Exception e) {
			e.printStackTrace();
			return "register_error";
		} finally {
			JDBCUtils.release(statement, cnnConnection);
		}
	}

	public static void getFilePath(InfoBean mb) {

	}

	public static String uploadfile(InfoBean mb) {
		try {
			cnnConnection = JDBCUtils.getConnection();
			String sql = "insert into file_locations values(null,?,?)";
			statement = cnnConnection.prepareStatement(sql);
			statement.setString(1, mb.getFilepath());
			statement.setString(2, mb.getAccount());
			statement.executeUpdate();
			return "uploaded";
		} catch (Exception e) {
			e.printStackTrace();
			return "upload_error";
		} finally {
			JDBCUtils.release(statement, cnnConnection);
		}

	}

	public static UserAndFile searchFilePathsByName(String userName) {
		List<String> filepaths = new ArrayList<String>();
		try {
			cnnConnection = JDBCUtils.getConnection();
			String sql = "select file_path from file_locations where user_account=?";
			statement = cnnConnection.prepareStatement(sql);
			statement.setString(1, userName);
			rsResultSet = statement.executeQuery();
			while (rsResultSet.next()) {
				String filepath = rsResultSet.getString("file_path");
				filepaths.add(filepath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.release(statement, cnnConnection);
		}
		return new UserAndFile(userName, filepaths);
	}

	public static String deleteFile(InfoBean mb) {

		try {
			cnnConnection = JDBCUtils.getConnection();
			String sql = "delete from file_locations where user_account=? and file_path=?";
			statement = cnnConnection.prepareStatement(sql);
			statement.setString(1, mb.getAccount());
			statement.setString(2, mb.getFilepath());
			statement.executeUpdate();
			return "removed";

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "remove_error";
	}
}
