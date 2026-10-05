package com.eventmanagement.dao.impl;

import com.eventmanagement.dao.UserDAO;
import com.eventmanagement.exception.DatabaseException;
import com.eventmanagement.model.*;
import com.eventmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    private User map(ResultSet rs) throws SQLException {
        User user = new User(rs.getLong("id"), rs.getString("name"), rs.getString("email"),
                rs.getString("password_hash"), UserRole.valueOf(rs.getString("role")),
                AccountStatus.valueOf(rs.getString("status")));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setNotificationPreference(rs.getBoolean("notification_preference"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) user.setCreatedAt(created.toLocalDateTime());
        return user;
    }

    @Override public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        } catch (SQLException e) { throw new DatabaseException("Could not find user.", e); }
    }

    @Override public User findById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        } catch (SQLException e) { throw new DatabaseException("Could not find user.", e); }
    }

    @Override public long create(User user) {
        String sql = "INSERT INTO users (name,email,password_hash,phone,role,status) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName()); ps.setString(2, user.getEmail()); ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getPhone()); ps.setString(5, user.getRole().name()); ps.setString(6, user.getStatus().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) { user.setId(rs.getLong(1)); return rs.getLong(1); } }
            throw new SQLException("No generated user id.");
        } catch (SQLException e) { throw new DatabaseException("Could not create user.", e); }
    }

    @Override public void updateProfile(User user) {
        String sql = "UPDATE users SET name=?, phone=?, address=?, notification_preference=? WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getName()); ps.setString(2, user.getPhone()); ps.setString(3, user.getAddress());
            ps.setBoolean(4, user.isNotificationPreference()); ps.setLong(5, user.getId()); ps.executeUpdate();
        } catch (SQLException e) { throw new DatabaseException("Could not update profile.", e); }
    }

    @Override public void updateAdmin(User user) {
        String sql = "UPDATE users SET name=?, email=?, role=?, status=? WHERE id=?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getName()); ps.setString(2, user.getEmail()); ps.setString(3, user.getRole().name());
            ps.setString(4, user.getStatus().name()); ps.setLong(5, user.getId()); ps.executeUpdate();
        } catch (SQLException e) { throw new DatabaseException("Could not update user.", e); }
    }

    @Override public List<User> findAll(String search, String role, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<Object> args = new ArrayList<>();
        if (search != null && !search.isBlank()) { sql.append(" AND (name LIKE ? OR email LIKE ?)"); args.add("%" + search.trim() + "%"); args.add("%" + search.trim() + "%"); }
        if (role != null && !role.isBlank()) { sql.append(" AND role = ?"); args.add(role); }
        if (status != null && !status.isBlank()) { sql.append(" AND status = ?"); args.add(status); }
        sql.append(" ORDER BY created_at DESC");
        List<User> users = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < args.size(); i++) ps.setObject(i + 1, args.get(i));
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) users.add(map(rs)); }
            return users;
        } catch (SQLException e) { throw new DatabaseException("Could not list users.", e); }
    }
}
