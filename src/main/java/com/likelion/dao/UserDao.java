package com.likelion.dao;

import com.likelion.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;

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

    public void deleteAll() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = connectionMaker.makeConnection();
            pstmt = conn.prepareStatement("DELETE FROM users");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally { // error가 나도 실행되는 블럭
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public int getCount() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionMaker.makeConnection();

            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM users");

            rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }

    }

    public void add(User user) {
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
        Connection conn;
        try {
            conn = connectionMaker.makeConnection();
            PreparedStatement pstmt = conn.prepareStatement("SELECT id, name, password FROM users WHERE id =?");

            pstmt.setString(1, id);

            ResultSet rs = pstmt.executeQuery();

            User user = null;
            if (rs.next()) {
                user = new User(rs.getString("id"), rs.getString("name"), rs.getString("password"));
            }

            rs.close();
            pstmt.close();
            conn.close();

            if(user == null) throw new EmptyResultDataAccessException(1);

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
