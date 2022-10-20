package com.likelion.dao;

import com.likelion.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class UserDao {

    private ConnectionMaker connectionMaker;

    public UserDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = new AwsConnectionMaker();
    }

    public void deleteAll() {
        Map<String, String> env = System.getenv();
        try {
            Connection conn = connectionMaker.makeConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users");
            pstmt.executeUpdate();

            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getCount() {
        Map<String, String> env = System.getenv();
        try {
            Connection conn = connectionMaker.makeConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM users");

            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            rs.close();
            pstmt.close();
            conn.close();

            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(User user) {
        Map<String, String> env = System.getenv();
        try {
            Connection conn = connectionMaker.makeConnection();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users(id, name, password) " +
                    "VALUES(?, ? ,?)");

            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getPassword());

            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findById(String id) {
        Map<String, String> env = System.getenv();
        Connection conn;
        try {
            conn = connectionMaker.makeConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, name, password FROM users WHERE id =?");

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();

            User user = new User(rs.getString("id"), rs.getString("name"), rs.getString("password"));

            rs.close();
            pstmt.close();
            conn.close();

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
